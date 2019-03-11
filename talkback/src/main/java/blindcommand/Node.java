package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Node {

    public String pageName;
    public String pageId;
    public String pagePinyin;
    public boolean canDirectReach;
    public JsonAppInfo meta;
    public List<NodePath> pageNodePaths;
    public Map<Node, Edge> neighbours;
    public boolean visited;
    public int frequency;
    private static int DEFAULT_FREQUENCY = 4;

    public Instruction getInstruction() {
        return new Instruction(pageId, pageName, pagePinyin, meta, frequency);
    }

    public Node pre;

    public Node(String id, String name, String pinyin, boolean directReach, JsonAppInfo meta, int frequency) {
        pageId = id;
        pageName = name;
        pagePinyin = pinyin;
        canDirectReach = directReach;
        this.meta = meta;
        pageNodePaths = new ArrayList<>();
        neighbours = new Hashtable<>();
        pre = null;
        visited = false;
        if(frequency == 0)
            this.frequency = DEFAULT_FREQUENCY;
        else
            this.frequency = frequency;
    }
    public Node(String id, String name, String pinyin, JsonAppInfo meta, int frequency) {
        this(id, name, pinyin, true, meta, frequency);
    }
    public Node(JsonNode jsonNode, JsonAppInfo meta){
        pageId = jsonNode.pageId;
        pageName = jsonNode.pageName;
        pagePinyin = jsonNode.pagePinyin;
        canDirectReach = jsonNode.canDirectReach;
        if(jsonNode.frequency == 0)
            frequency = DEFAULT_FREQUENCY;
        else
            frequency = jsonNode.frequency;
        this.meta = meta;
        pageNodePaths = new ArrayList<>();
        for(JsonNodePath feature: jsonNode.features){
            pageNodePaths.add(new NodePath(feature, meta));
        }
        neighbours = new Hashtable<>();
        pre = null;
        visited = false;
    }
    public boolean represent(AccessibilityWindowInfo window, AccessibilityService service){
        System.out.println(pageName + " :feature num: " + pageNodePaths.size());
        if (pageNodePaths.size() == 0) return false;
        for(NodePath nodePath : pageNodePaths){
            if(!nodePath.correspondTo(window, service)){
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
