package blindcommand;

import android.view.accessibility.AccessibilityNodeInfo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Edge {
    public Node from;
    public Node to;
    // text 和 viewId 能找到可操作的AccessibilityNodeInfo
    public String text;
    public String viewId;
    // 对找到的AccessibilityNodeInfo执行何种操作能从from到to
    public int action;

    public Edge(Node from, Node to, JsonClickable jc){
        // TODO 修改寻找AccessibilityNodeInfo 的信息
        this.from = from;
        this.to = to;
        this.viewId = jc.path;
        this.text = jc.path;
        this.action = AccessibilityNodeInfo.ACTION_CLICK;
    }

}
