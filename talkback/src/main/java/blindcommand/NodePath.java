package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NodePath {
    public String text;
    public String contentReg;
    public String textReg;
    Integer[] boundsInScreen;
    Integer[] resolution;
    public JsonAppInfo meta;


    public NodePath(JsonNodePath feature, JsonAppInfo meta){
        contentReg = feature.contentReg;
        textReg = feature.textReg;
        text = feature.text;
        boundsInScreen = feature.boundsInScreen;
        resolution = feature.resolution;
        this.meta = meta;
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
    public boolean isSameBound(Rect target, int resx, int resy) {
        final int X_THRESHOLD = 20;
        final int Y_THRESHOLD = 148;
        double ratioX = ((double)meta.resolution[0]) / resx;
        double ratioY = ((double)meta.resolution[1]) / resy;
        if ((Math.abs(this.boundsInScreen[0] - target.left * ratioX) >= X_THRESHOLD) ||
                (Math.abs(this.boundsInScreen[1] - target.top * ratioY) >= Y_THRESHOLD) ||
                (Math.abs(this.boundsInScreen[2] - target.right * ratioX) >= X_THRESHOLD) ||
                (Math.abs(this.boundsInScreen[3] - target.bottom * ratioY) >= Y_THRESHOLD))
            return false;
        double xRange = Math.abs(Math.abs(this.boundsInScreen[2] - this.boundsInScreen[0]) -
                Math.abs(target.right * ratioX - target.left * ratioX));
        double yRange = Math.abs(Math.abs(this.boundsInScreen[3] - this.boundsInScreen[1]) -
                Math.abs(target.bottom * ratioY - target.top * ratioY));
        return ((xRange < X_THRESHOLD) && (yRange < X_THRESHOLD));
//        return (Math.abs(this.boundsInScreen[0] - target.left * ratioX) < X_THRESHOLD) &&
//                (Math.abs(this.boundsInScreen[1] - target.top * ratioY) < Y_THRESHOLD) &&
//                (Math.abs(this.boundsInScreen[2] - target.right * ratioX) < X_THRESHOLD) &&
//                (Math.abs(this.boundsInScreen[3] - target.bottom * ratioY) < Y_THRESHOLD) &&
//                ( < X_THRESHOLD)
//                &&  < X_THRESHOLD);
    }
    public AccessibilityNodeInfo getNodeFromRoot(AccessibilityNodeInfo root) {
        if (this.text.length() != 0) {
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(this.text);
            for (AccessibilityNodeInfo node: nodes) {
                Rect bounds = new Rect();
                node.getBoundsInScreen(bounds);
                CharSequence text = node.getText();
                if (text != null && !Pattern.matches(this.textReg, text.toString().trim())) {
                    continue;
                }
                text = node.getContentDescription();
                if (text != null && !Pattern.matches(this.contentReg, text.toString().trim())) {
                    continue;
                }
                boolean bound = isSameBound(bounds, Utility.getScreenWidth(), Utility.getScreenHeight());
                if (bound) { return node; }
            }
        } else {
            Rect bounds = new Rect();
            root.getBoundsInScreen(bounds);
            if (isSameBound(bounds, Utility.getScreenWidth(), Utility.getScreenHeight())) return root;
            for (int i=0; i<root.getChildCount(); i++) {
                AccessibilityNodeInfo found = getNodeFromRoot(root.getChild(i));
                return found;
            }
        }
        return null;
    }
    public boolean correspondTo(AccessibilityWindowInfo window, AccessibilityService service){
        AccessibilityNodeInfo node = window.getRoot();
        if (node == null) {
            node = service.getRootInActiveWindow();
        }
        return (getNodeFromRoot(node) != null);
    }
}
