package blindcommand;

import android.view.accessibility.AccessibilityNodeInfo;

import lombok.AllArgsConstructor;

public class NodeInfoFinder {
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

    private static ReturnValue next(AccessibilityNodeInfo father, String path){
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
            System.out.println("actual " + father.getClassName());
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
