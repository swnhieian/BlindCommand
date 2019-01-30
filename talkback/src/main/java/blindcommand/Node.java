package blindcommand;

import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Node {

    public String pageName;
    public List<Feature> pageFeatures;
    public Map<Node, Edge> neighbours;

    public Node pre;
    public Node(JsonNode jsonNode){
        pageName = jsonNode.pageId;
        pageFeatures = new ArrayList<>();
        for(JsonFeature feature: jsonNode.features){
            pageFeatures.add(new Feature(feature));
        }
        neighbours = new Hashtable<>();
        pre = null;
    }
    public boolean represent(AccessibilityWindowInfo window){
        System.out.println("feature: " + pageFeatures.size());
        for(Feature feature: pageFeatures){
            if(!feature.correspondTo(window)){
                return false;
            }
        }
        return true;
    }

    public void addEdge(Edge edge){
        neighbours.put(edge.to, edge);
    }
}
