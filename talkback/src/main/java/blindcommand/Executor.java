package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

public class Executor {
    final String LOGTAG = "Executor";
    private AccessibilityService service;
    private HashMap<String, NodeGraph> graphs;
    public Executor(AccessibilityService service){
        this.service = service;
        graphs = new HashMap<>();
        init();
    }
    public List<Instruction> getInstructions() {
        List<Instruction> ret = new ArrayList<>();
        for (NodeGraph nodeGraph:graphs.values()) {
            ret.addAll(nodeGraph.getInstructions());
//            Instruction appIns = new Instruction(nodeGraph.meta.appName, nodeGraph.meta.appName, nodeGraph.meta.appPinyin, nodeGraph.meta);
//            if (!ret.contains(appIns)) {
//                ret.add(appIns);
//            }

        }
        JsonAppInfo sysInfo = new JsonAppInfo("System", "系统", "xitong", new Integer[]{});
        ret.add(new Instruction("返回", "返回", "FanHui", sysInfo, 1));
        ret.add(new Instruction("桌面", "桌面", "ZhuoMian", sysInfo, 1));
        ret.add(new Instruction("手电筒", "手电筒", "ShouDianTong", sysInfo, 2));
        ret.add(new Instruction("相机", "相机", "XiangJi", sysInfo, 2));
        ret.add(new Instruction("截屏", "截屏", "JiePing", sysInfo, 2));
        ret.add(new Instruction("电话", "电话", "DianHua", sysInfo, 1));
        ret.add(new Instruction("设置", "设置", "SheZhi", sysInfo, 2));
        return ret;
    }

    public AccessibilityNodeInfo getRoot() {
        List<AccessibilityWindowInfo> windows = service.getWindows();
        AccessibilityWindowInfo currentWindow = null;
        for (AccessibilityWindowInfo window : windows) {
            if (window.isActive()) {
                currentWindow = window;
                break;
            }
        }
        if (currentWindow == null)
            return null;
        AccessibilityNodeInfo root = currentWindow.getRoot();
        if (root == null) {
            root = service.getRootInActiveWindow();
        }
        return root;
    }

    HashMap<String, Parameter> parameterMap = new HashMap<>();
    boolean executeForParameter = false;
    List<Edge> parameterEdges = null;
    int parameterEdgesIndex = -1;
    AccessibilityNodeInfo continueNode = null;
    Node parameterResumeNode = null;

    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (executeForParameter) {
            AccessibilityWindowInfo activeWindow = null;
            for (AccessibilityWindowInfo window: service.getWindows()) {
                if (window.isActive())  {
                    activeWindow = window;
                    break;
                }
            }
            if (activeWindow != null) {
                if (parameterResumeNode.represent(activeWindow, service)) {
                    executeForParameter = false;
                    executeSteps(edges.subList(parameterEdgesIndex, edges.size()));
                    //singleSteps(parameterEdges, parameterEdgesIndex);
                }
            }
        }
    }


    private void endExecute() {
        SoundPlayer.interrupt();
        SoundPlayer.success();
        SoundPlayer.tts("执行完毕");
    }

    public void executeSteps(final List<Edge> edges) {
        if (edges == null) {
            endExecute();
            Log.i(LOGTAG, "endExecute");
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                while (index < edges.size()) {
                    long lastTime = System.currentTimeMillis();
                    Edge edge = edges.get(index);
                    while (System.currentTimeMillis() - lastTime < 1000) {
                        if (edge.from.represent(getCurrentWindow(), service)) {
                            executeStep(edge, index);
                        }
                    }
                    index ++;
                }
                if (index == edges.size()) {
                    endExecute();
                    Log.i(LOGTAG, "endExecute");
                }
            }
        }, 0);
    }
    public void executeStep(Edge edge, int index) {
        if (edge.needParameter) {
            SoundPlayer.tts("请输入" + (edge.parameterName.length() == 0 ? "参数" : edge.parameterName));
            executeForParameter = true;
            parameterResumeNode = edge.to;
            parameterEdges = edges;
            parameterEdgesIndex = index + 1;
        } else {
            AccessibilityNodeInfo root = getRoot();
            AccessibilityNodeInfo operatedNode = NodeInfoFinder.find(root, edge.path);
            if (operatedNode != null) {
                operatedNode.performAction(edge.action);
            }
        }

    }

    public void singleSteps(final List<Edge> edges, final int index) {
        if (index >= edges.size()) {
            endExecute();
            return;
        }
        Edge edge = edges.get(index);
        if (edge.needParameter) {
            SoundPlayer.tts("请输入" + (edge.parameterName.length() == 0?"参数":edge.parameterName));
//            List<Pair<String, AccessibilityNodeInfo>> validParameters = NodeInfoFinder.getParameterList(getRoot(), edge.path);
//            parameterMap.clear();
//            for (Pair<String, AccessibilityNodeInfo> pair: validParameters) {
//                parameterMap.put(pair.first, new Parameter(pair.first, pair.first, pair.second));
//                System.out.println("get:" + pair.first);
//            }
//            String[] dict = parameterMap.keySet().toArray(new String[] {});
            //System.out.println(dict);
            executeForParameter = true;
            parameterResumeNode = edge.to;
            parameterEdges = edges;
            parameterEdgesIndex = index + 1;
        } else {
            singleStep(edge);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    singleSteps(edges, index + 1);
                }
            }, 500);
            edge.to.represent(getCurrentWindow(), service);
        }
    }

    public void singleStep(Edge edge) {
        AccessibilityNodeInfo root = getRoot();
        ////
        AccessibilityNodeInfo operatedNode = NodeInfoFinder.find(root, edge.path);
        if (operatedNode != null) {
            //operatedNode.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
            operatedNode.performAction(edge.action);
        }

    }
    public void jumpToApp(String packageName) {
        PackageManager packageManager = service.getPackageManager();
        System.out.println("package:" + packageName);
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo != null) {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                service.startActivity(intent);
            }
        }
    }
    public void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //reference: https://blog.csdn.net/xiongwei3673605/article/details/42875017
    public void expandStatusBar() {
        Object ser = service.getSystemService("statusbar");
        if (ser == null) {
            return;
        }
        try {
            Class<?> clazz = Class.forName("android.app.StatusBarManager");
            int sdkVersion = android.os.Build.VERSION.SDK_INT;
            Method expand = null;
            if (sdkVersion <= 16) {
                expand = clazz.getMethod("expand");
            } else {
                expand = clazz.getMethod("expandSettingsPanel");
            }
            expand.setAccessible(true);
            expand.invoke(ser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    boolean lightStatus = false;
    private void executeSystemFunctions(Instruction instruction) {
        if (instruction.id.equals("返回")) {
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            SoundPlayer.success();
        } else if (instruction.id.equals("桌面")) {
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            SoundPlayer.success();
        } else if (instruction.id.equals("电话")) {
            Intent intent =  new Intent(Intent.ACTION_CALL_BUTTON);
            this.service.startActivity(intent);
            SoundPlayer.success();
        } else if (instruction.id.equals("相机")) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            this.service.startActivity(intent);
            SoundPlayer.success();
        } else if (instruction.id.equals("手电筒")) {
            try {
                CameraManager manager = (CameraManager) this.service.getSystemService(Context.CAMERA_SERVICE);
                this.lightStatus = !this.lightStatus;
                manager.setTorchMode("0", this.lightStatus);
                SoundPlayer.tts("手电筒已"+ (this.lightStatus?"打开":"关闭"));
            } catch (Exception e) {
                this.lightStatus = false;
            }
        } else if (instruction.id.equals("截屏")) {
            expandStatusBar();
            sleep(500);
            List<AccessibilityNodeInfo> nodes = getRoot().findAccessibilityNodeInfosByText("截屏");
            if (nodes.size() == 1) {
                AccessibilityNodeInfo target = nodes.get(0);
                while (!target.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)) {
                    target = target.getParent();
                    if (target == null) return;
                }
                target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                SoundPlayer.success();
            }
        } else if (instruction.id.equals("设置")) {
            Intent intent =  new Intent(Settings.ACTION_SETTINGS);
            this.service.startActivity(intent);
            SoundPlayer.success();
        }

    }
    private int loopCount = 0; //to prevent dead loop
    private boolean isBackgroundRunning(String processName) {
        ActivityManager activityManager = (ActivityManager) service.getSystemService(ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) service.getSystemService(KEYGUARD_SERVICE);

        if (activityManager == null) return false;
        // get running application processes
        List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processList) {
            if (process.processName.startsWith(processName)) {
                boolean isBackground = process.importance != IMPORTANCE_FOREGROUND && process.importance != IMPORTANCE_VISIBLE;
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackground || isLockedState) return true;
                else return false;
            }
        }
        return false;
    }
    public void execute(final Instruction instruction) {
        Log.i(LOGTAG, "startExecute:"+instruction.toString());
        if (instruction.meta.packageName.equals("System")) {
            executeSystemFunctions(instruction);
            Log.i(LOGTAG, "endExecute:" + "system function");
            return;
        }

        AccessibilityNodeInfo rootNode = getRoot();
        final NodeGraph appGraph = graphs.get(instruction.meta.appName);
        if (rootNode.getPackageName().equals(instruction.meta.packageName)) {
            execute(instruction, appGraph);
            return;
        }
        final boolean isBackground = isBackgroundRunning(instruction.meta.packageName);
        jumpToApp(instruction.meta.packageName);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                execute(instruction, appGraph);
            }
        }, 4000);
//        long lastTime = System.currentTimeMillis();
//        while (System.currentTimeMillis() - lastTime <= 4000) {
//            if (rootNode != null && rootNode.getPackageName().equals(instruction.meta.packageName)) {
//                break;
//            }
//            rootNode = getRoot();
//            System.out.println("app loop:" + (System.currentTimeMillis() - lastTime));
//        }
//        execute(instruction, appGraph);
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                long lastTime = System.currentTimeMillis();
//                long delayTime = isBackground?200:5000;
//                outer:
//                while (System.currentTimeMillis() - lastTime < delayTime) {
//                    if (appGraph.nodes.get(0).represent(getCurrentWindow(), service)) {
//                        break outer;
//                    }
//                }
//                execute(instruction, appGraph);
//            }
//        }, 0);

    }
    List<Edge> edges;

    public void clearContinue() {
        executeForParameter = false;
    }
    private AccessibilityWindowInfo getCurrentWindow() {
        List<AccessibilityWindowInfo> windows = service.getWindows();
        AccessibilityWindowInfo currentWindow = null;
        for (AccessibilityWindowInfo window : windows) {
            if (window.isActive()) {
                currentWindow = window;
                break;
            }
        }
        return currentWindow;
    }
    public void execute(Instruction ins, NodeGraph graph) {
        String commandId = ins.id;
        if (commandId == "null") {
            Log.i(LOGTAG, "endExecute:null command");
            return;
        }
        System.out.println("execute: " + commandId);
        AccessibilityWindowInfo currentWindow = getCurrentWindow();
        Node currentNode = graph.getCurrentWindowNode(currentWindow, service);
        if (currentNode == null) {
            loopCount += 1;
            System.out.println("in back loop, loopCount:" + loopCount);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            sleep(500);
            if (loopCount < 3) {
                execute(ins);
            }
            return;
        }
        Node targetNode = graph.getTargetWindowNode(commandId);
        System.out.println("\tfrom: " + (currentNode == null ? "null" : currentNode.pageId));
        System.out.println("\tto  : " + (targetNode == null ? "null" : targetNode.pageId));
        if(currentNode == null || targetNode == null) return;
        edges = graph.findPath(currentNode, targetNode);
        if(edges != null) {
            //singleSteps(edges, 0);
            executeSteps(edges);
        } else {
            loopCount += 1;
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            System.out.println("back action");
            sleep(500);
            if (loopCount < 3) {
                execute(ins);
            }
            return;
        }
        loopCount = 0;
    }

    public void init(){
        AssetManager assetManager = service.getAssets();
        String[] fileNames = new String[] {"apps/Wechat_7.0.3.json"};
        try {
            fileNames = assetManager.list("apps");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 从asset读配置文件
        for (String fileName:fileNames) {
            System.out.println("Loading " + fileName);
            StringBuilder stringBuilder = new StringBuilder();
            try {

                BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("apps/"+fileName)));
                String line;
                while ((line = bf.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 反序列化
            String jsonString = stringBuilder.toString();
            Gson gson = new Gson();
            JsonAppNode jsonappNode = gson.fromJson(jsonString, new TypeToken<JsonAppNode>() {
            }.getType());
            // 建图
            NodeGraph graph = new NodeGraph();
            graph.loadGraph(jsonappNode);
            graphs.put(jsonappNode.meta.appName, graph);
        }
    }
}
