package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Node {

    public String pageName;
    public String pageId;
    public String pagePinyin;
    public boolean canDirectReach;
    public JsonAppInfo meta;
    public List<Feature> pageFeatures;
    public Map<Node, Edge> neighbours;
    public boolean visited;

    public Instruction getInstruction() {
        return new Instruction(pageId, pageName, pagePinyin, meta);
    }

    public Node pre;

    public Node(String id, String name, String pinyin, boolean directReach, JsonAppInfo meta) {
        pageId = id;
        pageName = name;
        pagePinyin = pinyin;
        canDirectReach = directReach;
        this.meta = meta;
        pageFeatures = new ArrayList<>();
        neighbours = new Hashtable<>();
        pre = null;
        visited = false;
    }
    public Node(String id, String name, String pinyin, JsonAppInfo meta) {
        this(id, name, pinyin, true, meta);
    }
    public Node(JsonNode jsonNode, JsonAppInfo meta){
        pageId = jsonNode.pageId;
        pageName = jsonNode.pageName;
        pagePinyin = jsonNode.pagePinyin;
        canDirectReach = jsonNode.canDirectReach;
        this.meta = meta;
        pageFeatures = new ArrayList<>();
        for(JsonFeature feature: jsonNode.features){
            pageFeatures.add(new Feature(feature));
        }
        neighbours = new Hashtable<>();
        pre = null;
        visited = false;
    }
    public boolean represent(AccessibilityWindowInfo window, AccessibilityService service){
        System.out.println(pageName + " :feature num: " + pageFeatures.size());
        if (pageFeatures.size() == 0) return false;
        for(Feature feature: pageFeatures){
            if(!feature.correspondTo(window, service)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return this.pageId.equals(((Node)obj).pageId);
    }

    public void addEdge(Edge edge){
        if (edge.to.equals(this)) {
            return;
        }
        neighbours.put(edge.to, edge);
    }
}
