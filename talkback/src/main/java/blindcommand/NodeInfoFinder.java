package blindcommand;

import android.view.accessibility.AccessibilityNodeInfo;

public class NodeInfoFinder {
    public static AccessibilityNodeInfo find(AccessibilityNodeInfo root, String path){
        String[] splitPath = path.split("\\s*;\\s*");
        AccessibilityNodeInfo cur = root;
        System.out.println(root.getClassName());
        int childCount = root.getChildCount();
        for(int i = 0; i < childCount; i ++){
            System.out.println("" + i + " " + root.getChild(i).getClassName());
        }
        for(String subPath: splitPath){
            cur = next(cur, subPath);
            if(cur == null) return null;
        }
        return cur;
    }

    private static AccessibilityNodeInfo next(AccessibilityNodeInfo father, String path){
        String[] splitPath = path.split("\\|");
        if(splitPath.length != 2) return null;
        System.out.println("split path: " + splitPath[0] + " " + splitPath[1]);
        int childCount = father.getChildCount();
        int childIndex = splitPath[1].equals("$") ? childCount - 1 : Integer.parseInt(splitPath[1]);
        AccessibilityNodeInfo child = father.getChild(childIndex);
        return child.getClassName().toString().equals(splitPath[0]) ? child : null;
    }
}
