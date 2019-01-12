package com.shiweinan.BlindCommand;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Utility {
    public static int speed = 80;
    public static int getkeyboardHeight(Context service) {
        return 680;
    }
    public static int getKeyboardWidth(Context service) {
        WindowManager wm = (WindowManager)service.getSystemService(service.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
    public static int getScreenHeight(Context service) {
        WindowManager wm = (WindowManager)service.getSystemService(service.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }
    public static AccessibilityService service;
    public static String getLanguage() {
        return Utility.service.getResources().getConfiguration().locale.getCountry();
    }
}