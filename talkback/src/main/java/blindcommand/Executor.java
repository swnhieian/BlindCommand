package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.google.android.accessibility.talkback.TalkBackService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
            ret.addAll(nodeGraph.getInstructions());
        }
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
            AccessibilityNodeInfo node = parameterMap.get(para).node; //get node from para
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        singleSteps(parameterEdges, parameterEdgesIndex);
    }

    public void singleSteps(final List<Edge> edges, final int index) {
        if (index >= edges.size()) return;
        Edge edge = edges.get(index);
        if (edge.needParameter) {
            SoundPlayer.tts("请输入参数");
//            List<Pair<String, AccessibilityNodeInfo>> validParameters = NodeInfoFinder.getParameterList(getRoot(), edge.path);
//            parameterMap.clear();
//            for (Pair<String, AccessibilityNodeInfo> pair: validParameters) {
//                parameterMap.put(pair.first, new Parameter(pair.first, pair.first, pair.second));
//                System.out.println("get:" + pair.first);
//            }
//            String[] dict = parameterMap.keySet().toArray(new String[] {});
            //System.out.println(dict);
            executeForParameter = true;
            parameterEdges = edges;
            parameterEdgesIndex = index + 1;
            continueNode = NodeInfoFinder.find(getRoot(), edge.path);
            continueSteps("sk");
//            ((TalkBackService)(service)).triggerBCMode(dict);
        } else {
            singleStep(edge);
            if (index < edges.size() - 1) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        singleSteps(edges, index + 1);
                    }
                }, 500);
            }
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
        //TODO: find app graph
        jumpToApp(instruction.meta.packageName);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!executeForParameter) {
                    execute(instruction, graphs.get(instruction.meta.appName));
                } else {
                    continueSteps(instruction.id);
                }
            }
        }, 1000);
    }
    List<Edge> edges;
    class ExecuteAsyncTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            System.out.println("ssssswwwwwnnnnn," + integers);
            singleStep(edges.get(integers[0].intValue()));
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public void execute(Instruction ins, NodeGraph graph) {
        String commandId = ins.id;
        //jumpToApp(graph.meta.packageName);
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
            System.out.println("back action");
            try {
                System.out.println("sleeping for back action");
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            windows = service.getWindows();
//            currentWindow = null;
//            for (AccessibilityWindowInfo window : windows) {
//                if (window.isActive()) {
//                    currentWindow = window;
//                    break;
//                }
//            }
            if (loopCount < 3) {
                execute(ins);
            }
            return;
            //currentNode = graph.getCurrentWindowNode(currentWindow, service);
        }
        loopCount = 0;
        // TODO ? 用 commandName 得出目标窗口的name
        Node targetNode = graph.getTargetWindowNode(commandId);
        System.out.println("\tfrom: " + (currentNode == null ? "null" : currentNode.pageId));
        System.out.println("\tto  : " + (targetNode == null ? "null" : targetNode.pageId));
        if(currentNode == null || targetNode == null) return;
        edges = graph.findPath(currentNode, targetNode);
        // 起一个新线程
        if(edges != null) {
            singleSteps(edges, 0);
/*            for (int i=0; i<edges.size(); i++) {
                //new ExecuteAsyncTask().execute(new Integer(i));
                final Edge edge = edges.get(i);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        singleStep(edge);
                    }
                }, i*500);
                //TODO: 是否可以不使用延时的方式处理
            }
*/
        } else {
            loopCount += 1;
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            //service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            System.out.println("back action");
            try {
                System.out.println("sleeping for back action");
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            windows = service.getWindows();
//            currentWindow = null;
//            for (AccessibilityWindowInfo window : windows) {
//                if (window.isActive()) {
//                    currentWindow = window;
//                    break;
//                }
//            }
            if (loopCount < 3) {
                execute(ins);
            }
            return;

        }
        loopCount = 0;
//        if(edges != null){
//            Runnable runnable = new Runnable(){
//                @Override
//                public void run(){
//
//                    for(int i=0; i<edges.size(); i++){
//                        final Edge edge = edges.get(i);
//                        singleStep(edge);
//                        //singleStep(edge);
//                    }
//                }
//            };
//            Thread thread = new Thread(runnable);
//            thread.start();
//        }
    }

    public void init(){
        // 从asset读配置文件
        StringBuilder stringBuilder = new StringBuilder();
        try{
            AssetManager assetManager = service.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("Wechat.json")));
            String line;
            while((line = bf.readLine()) != null){
                stringBuilder.append(line);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        // 反序列化
        String jsonString = stringBuilder.toString();
        Gson gson = new Gson();
        JsonAppNode jsonappNode = gson.fromJson(jsonString, new TypeToken<JsonAppNode>(){}.getType());
        // 建图
        NodeGraph graph = new NodeGraph();
        graph.loadGraph(jsonappNode);
        graphs.put(jsonappNode.meta.appName, graph);
    }
}
