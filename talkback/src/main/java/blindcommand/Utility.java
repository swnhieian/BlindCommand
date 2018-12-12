package blindcommand;

import android.accessibilityservice.AccessibilityService;

public class Utility {
    public static AccessibilityService service;
    public static String getLanguage() {
        return Utility.service.getResources().getConfiguration().locale.getCountry();
    }
}
