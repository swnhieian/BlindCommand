package blindcommand;

import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

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
    public boolean correspondTo(AccessibilityWindowInfo window){
        AccessibilityNodeInfo featureNode = NodeInfoFinder.find(window.getRoot(), nodeId);
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
