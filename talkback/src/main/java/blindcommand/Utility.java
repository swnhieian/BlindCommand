package blindcommand;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.WindowManager;

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
    public static Parser.ParserType parserType = Parser.ParserType.DEFAULT;
}