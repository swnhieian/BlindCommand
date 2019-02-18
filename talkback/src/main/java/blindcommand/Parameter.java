package blindcommand;

import android.view.accessibility.AccessibilityNodeInfo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Parameter {
    String id;
    String name;
    AccessibilityNodeInfo node;
}
