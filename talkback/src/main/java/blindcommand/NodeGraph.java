package blindcommand;

import android.content.res.AssetManager;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NodeGraph {

    // 存储结点列表，结点里存邻接表
    public List<Node> nodes;

    public NodeGraph(){
        nodes = new ArrayList<>();
    }

    public Node getCurrentWindowNode(AccessibilityWindowInfo activeWindow){
        System.out.println("node: " +  nodes.size());
        for(Node node: nodes){
            if(node.represent(activeWindow)){
                System.out.println("current window found!");
                return node;
            }
        }
        return null;
    }
    public Node getTargetWindowNode(String name){
        for(Node node: nodes){
            if(node.pageName.equals(name)){
                return node;
            }
        }
        return null;
    }
    public List<Edge> findPath(Node from, Node to){
        List<Edge> path = new ArrayList<>();
        if(from == to){
            return path;
        }
        // bfs 找最短路
        LinkedList<Node> queue = new LinkedList<>();
        queue.offer(from);
        while(!queue.isEmpty()){
            Node first = queue.poll();
            if(first == null) continue;
            boolean found = false;
            for(Node neighbour: first.neighbours.keySet()){
                neighbour.pre = first;
                queue.offer(neighbour);
                if(neighbour == to){
                    found = true;
                    break;
                }
            }
            if(found){
                break;
            }
        }

        // 从Node的pre结点信息生成一条路径
        Node currentFrom = to;
        Node currentTo = to;
        while(currentTo != from){
            if(currentFrom == null){
                return null;
            }
            currentFrom = currentFrom.pre;
            path.add(0, currentFrom.neighbours.get(currentTo));
            currentTo = currentTo.pre;
        }
        return path;
    }

    /*
        从jsonObject中建图
        整个配置文件为一个列表
        列表中每项元素(JsonNode)表示一个页面，对应图中一个结点(Node)
        pageId为页面的名字，用于区分和查找页面
        features为页面的特征(JsonFeature)，用于判断当前在哪个页面
        buttons为页面上Clickable的元素(JsonClickable)，每个button对应图中一条有向边(Edge)
    */

    public void loadGraph(List<JsonNode> jsonNodes){
        Map<String, Node> map = new Hashtable<>();
        for(JsonNode jsonNode: jsonNodes){
            Node newNode = new Node(jsonNode);
            map.put(newNode.pageName, newNode);
            nodes.add(newNode);
        }
        int size = jsonNodes.size();
        for(int i = 0; i < size; i ++){
            List<JsonClickable> jsonClickables = jsonNodes.get(i).buttons;
            for(JsonClickable button: jsonClickables){
                Node targetNode = map.get(button.target);
                if(targetNode != null) {
                    nodes.get(i).addEdge(new Edge(nodes.get(i), targetNode, button));
                }
            }
        }
    }

}
