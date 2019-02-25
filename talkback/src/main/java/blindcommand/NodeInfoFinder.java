package blindcommand;

import android.util.Pair;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;

public class NodeInfoFinder {
    public static AccessibilityNodeInfo find(AccessibilityNodeInfo root, NodePath path) {
        AccessibilityNodeInfo target = path.getNodeFromRoot(root);
        if (target == null) return null;
        while (!target.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)) {
            target = target.getParent();
            if (target == null) return null;
        }
        return target;
    }
    public static AccessibilityNodeInfo find(AccessibilityNodeInfo root, String path){
        String[] splitPath = path.split("\\s*;\\s*");
        AccessibilityNodeInfo cur = root;
        for(int i = 0; i < splitPath.length - 1; i ++){
            ReturnValue returnValue = next(cur, splitPath[i]);
            if(!returnValue.matchClassName || returnValue.nextNode == null){
                return null;
            }
            cur = returnValue.nextNode;
        }
        return cur;
    }

    public static String getTextFromNode(AccessibilityNodeInfo node) {
        if (node.getText() != null && node.getText().length() > 0) {
            return node.getText().toString();
        } else {
            for (int i=0; i<node.getChildCount(); i++) {
                String res = getTextFromNode(node.getChild(i));
                if (res != null && res.length() > 0) {
                    return res;
                }
            }
        }
        return null;
    }

    public static Pair<String, AccessibilityNodeInfo> getTextFromNode(AccessibilityNodeInfo root, String[] paths) {
        AccessibilityNodeInfo cur = root;
        for(int i = 0; i < paths.length - 1; i ++){
            ReturnValue returnValue = next(cur, paths[i]);
            if(!returnValue.matchClassName || returnValue.nextNode == null){
                return null;
            }
            cur = returnValue.nextNode;
        }
        return new Pair<>(getTextFromNode(cur), cur);
    }

    public static List<Pair<String, AccessibilityNodeInfo>> getParameterList(AccessibilityNodeInfo root, String path) {
        List<Pair<String, AccessibilityNodeInfo>> ret = new ArrayList<>();
        String[] splitPath = path.split("\\s*;\\s*");
        AccessibilityNodeInfo cur = root;
        for (int i=0; i<splitPath.length - 1; i++) {
            String[] pos = splitPath[i].split("\\|");
            if (pos[1].contains("*")) {
                Matcher m = Pattern.compile("(\\d+)\\*(\\d+)").matcher(pos[1]);
                if (m.find()) {
                    int start = Integer.parseInt(m.group(1));
                    int end = Integer.parseInt(m.group(2));
                    String[] restPath = Arrays.copyOfRange(splitPath, i + 1, splitPath.length);
                    for (int j = start; j < cur.getChildCount()-end; j++) {
                        ret.add(getTextFromNode(cur.getChild(j), restPath));
                    }
                }
                break;
            } else {
                ReturnValue returnValue = next(cur, splitPath[i]);
                if(!returnValue.matchClassName || returnValue.nextNode == null){
                    break;
                }
                cur = returnValue.nextNode;
            }

        }
        return ret;
    }

    private static ReturnValue next(AccessibilityNodeInfo father, String path){
        String[] splitPath = path.split("\\|");
//        System.out.println("split path: " + splitPath[0] + " " + splitPath[1]);
        int childCount = father.getChildCount();
        int childIndex = splitPath[1].equals("$") ? childCount - 1 : Integer.parseInt(splitPath[1]);
//        System.out.println("" + childIndex + " " + childCount);
        boolean childOutOfIndex = childIndex < 0 || childIndex >= childCount;
        boolean matchClassName = false;
        AccessibilityNodeInfo nextNode = null;
        if(!childOutOfIndex) {
            nextNode = father.getChild(childIndex);
//            System.out.println("actual " + father.getClassName());
            if (father.getClassName().toString().equals(splitPath[0])) {
                matchClassName = true;
            } else {
                try {
                    Class nodeClass = Class.forName(father.getClassName().toString());
                    Class thatClass = Class.forName(splitPath[0]);
                    matchClassName = nodeClass.equals(thatClass) || thatClass.isAssignableFrom(nodeClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ReturnValue(nextNode, matchClassName, childOutOfIndex);
    }

    private static ReturnValue next_ori(AccessibilityNodeInfo father, String path){
        String[] splitPath = path.split("\\|");
        System.out.println("split path: " + splitPath[0] + " " + splitPath[1]);
        int childCount = father.getChildCount();
        int childIndex = splitPath[1].equals("$") ? childCount - 1 : Integer.parseInt(splitPath[1]);
        System.out.println("" + childIndex + " " + childCount);
        boolean childOutOfIndex = childIndex < 0 || childIndex >= childCount;
        boolean matchClassName = false;
        AccessibilityNodeInfo nextNode = null;
        if(!childOutOfIndex) {
            nextNode = father.getChild(childIndex);
            System.out.println("actual " + nextNode.getClassName());
            try {
                Class nodeClass = Class.forName(nextNode.getClassName().toString());
                Class thatClass = Class.forName(splitPath[0]);
                matchClassName = nodeClass.equals(thatClass) || thatClass.isAssignableFrom(nodeClass);
            } catch(ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        return new ReturnValue(nextNode, matchClassName, childOutOfIndex);
    }


    @AllArgsConstructor
    static class ReturnValue{
        public AccessibilityNodeInfo nextNode;
        public boolean matchClassName;
        public boolean childOutOfIndex;
    }
}
