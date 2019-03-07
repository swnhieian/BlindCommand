package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

public class Utility {
    public static int speed = 80;
    public static int screenHeight = -1;
    public static int screenWidth = -1;
    public static void init(Context service) {
        WindowManager wm = (WindowManager)service.getSystemService(service.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        Utility.screenHeight = metrics.heightPixels;
        Utility.screenWidth = metrics.widthPixels;
    }
    public static int getScreenHeight() {
        return Utility.screenHeight;
    }
    public static int getScreenWidth() {
        return Utility.screenWidth;
    }
    public static void vibrate() {
        Vibrator vib = (Vibrator)service.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(200);
    }
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
    public static AccessibilityNodeInfo getRoot() {
        List<AccessibilityWindowInfo> windows = service.getWindows();
        AccessibilityWindowInfo currentWindow = null;
        for (AccessibilityWindowInfo window : windows) {
            if (window.isActive()) {
                currentWindow = window;
                break;
            }
        }
        if (currentWindow == null)
            return null;
        AccessibilityNodeInfo root = currentWindow.getRoot();
        if (root == null) {
            root = service.getRootInActiveWindow();
        }
        return root;
    }
    public static String getPackageName() {
        AccessibilityNodeInfo root = Utility.getRoot();
        if (root == null) return null;
        return root.getPackageName().toString();
    }
    public static Parser.ParserType parserType = Parser.ParserType.DEFAULT;
    public static boolean useDiffNav = true;
}