package blindcommand;

import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Node {

    public String pageName;
    public List<Feature> pageFeatures;
    public Map<Node, Edge> neighbours;

    public Node pre;
    public Node(String name) {
        pageName = name;
        pageFeatures = new ArrayList<>();
        neighbours = new Hashtable<>();
    }
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
        System.out.println(pageName + " :feature num: " + pageFeatures.size());
        if (pageFeatures.size() == 0) return false;
        for(Feature feature: pageFeatures){
            if(!feature.correspondTo(window)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return this.pageName.equals(((Node)obj).pageName);
    }

    public void addEdge(Edge edge){
        if (edge.to.equals(this)) {
            return;
        }
        neighbours.put(edge.to, edge);
    }
}
