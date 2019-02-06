package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.google.android.accessibility.talkback.R;
import com.google.android.accessibility.talkback.TalkBackService;
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
            ret.addAll(nodeGraph.getInstructions());
        }
        return ret;
    }

    public void singleStep(Edge edge) {
        List<AccessibilityWindowInfo> windows = service.getWindows();
        AccessibilityWindowInfo currentWindow = null;
        for (AccessibilityWindowInfo window : windows) {
            if (window.isActive()) {
                currentWindow = window;
                break;
            }
        }
        if (currentWindow == null)
            return;
        AccessibilityNodeInfo root = currentWindow.getRoot();
        if (root == null) {
            root = service.getRootInActiveWindow();
        }
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
    public void execute(final Instruction instruction) {
        //TODO: find app graph
        jumpToApp(instruction.meta.packageName);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                execute(instruction.id, graphs.get(instruction.meta.appName));
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
    public void execute(String commandId, NodeGraph graph) {
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
        Node currentNode = graph.getCurrentWindowNode(currentWindow);
        // TODO ? 用 commandName 得出目标窗口的name
        Node targetNode = graph.getTargetWindowNode(commandId);
        if(currentNode == null || targetNode == null) return;
        edges = graph.findPath(currentNode, targetNode);
        // 起一个新线程
        if(edges != null) {
            for (int i=0; i<edges.size(); i++) {
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
        }
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