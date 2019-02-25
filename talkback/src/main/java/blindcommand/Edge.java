package blindcommand;

import android.view.accessibility.AccessibilityNodeInfo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Edge {
    public Node from;
    public Node to;
    public NodePath path;
    // 对找到的AccessibilityNodeInfo执行何种操作能从from到to
    public int action;
    public boolean needParameter = false;
    public String parameterName = "";
    public JsonAppInfo meta;

    public Edge(Node from, Node to, JsonClickable jc, JsonAppInfo meta){
        this.from = from;
        this.to = to;
        this.path = new NodePath(jc.path, meta);
        this.needParameter = jc.needParameter;
        this.parameterName = jc.parameterName;
        this.action = AccessibilityNodeInfo.ACTION_CLICK;
        this.meta = meta;
    }

}
