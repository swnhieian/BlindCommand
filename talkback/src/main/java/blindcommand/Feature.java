package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Feature {
    public String contentReg;
    public boolean mustBeVisible;
    public boolean mustLast;
    public String nodeId;
    public String textReg;


    public Feature(JsonFeature feature){
        contentReg = feature.contentReg;
        mustBeVisible = feature.mustBeVisible;
        mustLast = feature.mustLast;
        nodeId = feature.nodeId;
        textReg = feature.textReg;
    }
    public String nodeId(AccessibilityNodeInfo node) {
        if (node == null) return "not exist";
        String ret = "";
        AccessibilityNodeInfo pNode = node.getParent();
        if (pNode == null) return (""+node.getClassName());
        int num = 0;
        for (int i=0; i<pNode.getChildCount(); i++) {
            if (pNode.getChild(i).equals(node)) {
                num = i;
                break;
            }
        }
        String pre = nodeId(pNode);
        ret += (pre + "|" + num + ";" + node.getClassName());
        return ret;
    }
    List<AccessibilityNodeInfo> nodes = new ArrayList<>();
    String idsss = "";
    private void traverse(AccessibilityNodeInfo node) {
        nodes.add(node);
        if (node.getText() != null && node.getText().equals("微信")) {
            idsss = nodeId(node);
        }
        for (int i=0; i<node.getChildCount(); i++) {
            AccessibilityNodeInfo n = node.getChild(i);
            traverse(n);
        }
        nodes.add(null);

    }
    public boolean correspondTo(AccessibilityWindowInfo window, AccessibilityService service){
        AccessibilityNodeInfo node = window.getRoot();
        if (node == null) {
            node = service.getRootInActiveWindow();
        }
        List<AccessibilityNodeInfo> wnode = node.findAccessibilityNodeInfosByText("设置");
//        nodes.clear();
//        traverse(node);
        if (wnode.size() > 0) {
            System.out.println("=========");
            System.out.println(nodeId(wnode.get(0)));
        }

        AccessibilityNodeInfo featureNode = NodeInfoFinder.find(node, nodeId);
        if(featureNode == null) return false;
        System.out.println("feature found");
        CharSequence text = featureNode.getText();
        boolean match = true;
        if(text != null){
            match = Pattern.matches(textReg, text);
        }
        CharSequence content = featureNode.getContentDescription();
        if(content != null){
            match = match && Pattern.matches(contentReg, content);
        }
        System.out.println("match: " + match);
        return match;
    }
}
