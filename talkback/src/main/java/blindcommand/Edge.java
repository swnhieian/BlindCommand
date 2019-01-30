package blindcommand;

import android.view.accessibility.AccessibilityNodeInfo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Edge {
    public Node from;
    public Node to;
    public String path;
    // 对找到的AccessibilityNodeInfo执行何种操作能从from到to
    public int action;

    public Edge(Node from, Node to, JsonClickable jc){
        this.from = from;
        this.to = to;
        this.path = jc.path;
        this.action = AccessibilityNodeInfo.ACTION_CLICK;
    }

}
