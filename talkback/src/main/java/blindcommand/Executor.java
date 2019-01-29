package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.content.res.AssetManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Executor {
    private AccessibilityService service;
    private NodeGraph graph;
    public Executor(AccessibilityService service){
        this.service = service;
        init();
    }

    public void singleStep(Edge edge){
        List<AccessibilityWindowInfo> windows = service.getWindows();
        for(AccessibilityWindowInfo w: windows){
            if(w.isActive()){
                List<AccessibilityNodeInfo> nodes = w.getRoot().findAccessibilityNodeInfosByText(edge.text);
                if(nodes.size() > 0){
                    nodes.get(0).performAction(edge.action);
                }
                else{
                    System.out.println("find no node");
                }
            }
        }
    }

    public void execute(String commandName){
        System.out.println(commandName);
        List<AccessibilityWindowInfo> windows = service.getWindows();
        AccessibilityWindowInfo currentWindow = null;
        for(AccessibilityWindowInfo window: windows){
            if(window.isActive()){
                currentWindow = window;
                break;
            }
        }
        Node currentNode = graph.getCurrentWindowNode(currentWindow);
        // TODO ? 用 commandName 得出目标窗口的name
        Node targetNode = graph.getTargetWindowNode(commandName);
        List<Edge> edges = graph.findPath(currentNode, targetNode);
        if(edges != null){
            for(Edge edge: edges){
                singleStep(edge);
            }
        }
    }

    public void init(){
        // 从asset读配置文件
        StringBuilder stringBuilder = new StringBuilder();
        try{
            AssetManager assetManager = service.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("WeChatHomepage.json")));
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
        List<JsonNode> jsonNodes = gson.fromJson(jsonString, new TypeToken<List<JsonNode>>(){}.getType());
        // 建图
        graph = new NodeGraph();
        graph.loadGraph(jsonNodes);
    }
}
