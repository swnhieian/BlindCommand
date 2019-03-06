package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Executor {
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
            ret.add(new Instruction(nodeGraph.meta.appName, nodeGraph.meta.appName, nodeGraph.meta.appPinyin, nodeGraph.meta));
            ret.addAll(nodeGraph.getInstructions());
        }
        ret.add(new Instruction("返回", "返回", "FanHui", new JsonAppInfo()));
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
                    singleSteps(parameterEdges, parameterEdgesIndex);
                }
            }
        }
    }

    public void continueSteps(String para) {
        executeForParameter = false;
        if (continueNode != null) {
            if (continueNode.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT)) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, para);
                continueNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                sleep(500);
            }
        } else {
          //
        }
        singleSteps(parameterEdges, parameterEdgesIndex);
    }
    private void endExecute() {
        SoundPlayer.interrupt();
        SoundPlayer.success();
        SoundPlayer.tts("执行完毕");
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
            //continueNode = NodeInfoFinder.find(getRoot(), edge.path);


//             Instruction[] names = new Instruction[]
//             {
//                     new Instruction("shiweinan", "石伟男", "ShiWeiNan", edge.from.meta),
//                     new Instruction("sunke", "孙科", "SunKe", edge.from.meta),
//                     new Instruction("weiyi", "唯一", "WeiYi",edge.from.meta),
//                     new Instruction("PenguinGG", "PenguinGG", "PenguinGG",edge.from.meta)
//             };
            //((TalkBackService)(service)).triggerBCMode(Parser.ParserType.NO_DICT);
            //((TalkBackService)(service)).triggerBCMode(Parser.ParserType.LIST, Arrays.asList(names));


        } else {
            singleStep(edge);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    singleSteps(edges, index + 1);
                }
            }, 500);

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
        PackageInfo packageInfo = null;
        if (getRoot().getPackageName().equals(packageName)) {
            return;
        }
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
    private int loopCount = 0; //to prevent dead loop
    public void execute(final Instruction instruction) {
        if (instruction.id == "返回") {
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            endExecute();
            return;
        }
        //TODO: find app graph
        jumpToApp(instruction.meta.packageName);
        final AccessibilityNodeInfo rootNode = getRoot();
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText("跳过");
        List<AccessibilityNodeInfo> snodes = rootNode.findAccessibilityNodeInfosByText("关爱出行");
        long delayTime = 4000;
        if (nodes.size() == 1) {
            nodes.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            delayTime = 1000;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                    execute(instruction, graphs.get(instruction.meta.appName));
            }
        }, delayTime);
    }
    List<Edge> edges;

    public void clearContinue() {
        executeForParameter = false;
    }
    public void execute(Instruction ins, NodeGraph graph) {
        String commandId = ins.id;
        if (commandId == "null") {
            return;
        }
        if (graph.meta.appName.equals(ins.id)) {
            singleSteps(new ArrayList<Edge>(), 1);
        }
        System.out.println("execute: " + commandId);
        List<AccessibilityWindowInfo> windows = service.getWindows();
        AccessibilityWindowInfo currentWindow = null;
        for (AccessibilityWindowInfo window : windows) {
            if (window.isActive()) {
                currentWindow = window;
                break;
            }
        }
        Node currentNode = graph.getCurrentWindowNode(currentWindow, service);
        if (currentNode == null) {
            loopCount += 1;
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            //service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            System.out.println("back action, loop:" + loopCount);
            sleep(500);
            if (loopCount < 3) {
                execute(ins);
            }
            return;
        }
        // TODO ? 用 commandName 得出目标窗口的name
        Node targetNode = graph.getTargetWindowNode(commandId);
        System.out.println("\tfrom: " + (currentNode == null ? "null" : currentNode.pageId));
        System.out.println("\tto  : " + (targetNode == null ? "null" : targetNode.pageId));
        if(currentNode == null || targetNode == null) return;
        edges = graph.findPath(currentNode, targetNode);
        if(edges != null) {
            singleSteps(edges, 0);
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
        String[] fileNames = new String[] {"Alipay_10.1.58.json", "Didi_V5.2.40_505.json", "Eleme_v8.12.0.json", "Wechat.json"};
        // 从asset读配置文件
        for (String fileName:fileNames) {
            System.out.println("Loading " + fileName);
            StringBuilder stringBuilder = new StringBuilder();
            try {
                AssetManager assetManager = service.getAssets();
                BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
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
