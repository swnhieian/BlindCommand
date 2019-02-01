package blindcommand;

import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.List;

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
    private void traverse(AccessibilityNodeInfo node, int level) {
        String space = "";
        for (int i=0; i<level; i++)
            space = space + "   ";
        System.out.println(space + "=======in traverse======");
        System.out.println(space + "---" + node.getClassName() + "," + node.getText() + "," + node.getContentDescription() + "---");
        for (int i=0; i<node.getChildCount(); i++) {
            System.out.println(space + "===child " + i + " ===");
            traverse(node.getChild(i), level+1);
        }
        System.out.println(space + "=======out traverse======");

    }
    public boolean correspondTo(AccessibilityWindowInfo window){
        // TODO
        return true;
    }


}