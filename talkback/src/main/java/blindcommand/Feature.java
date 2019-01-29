package blindcommand;

import android.view.accessibility.AccessibilityWindowInfo;

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
        // TODO
        return true;
    }


}
