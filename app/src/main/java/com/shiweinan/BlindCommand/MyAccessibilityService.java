package com.shiweinan.BlindCommand;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.shiweinan.BlindCommand.touch.TouchPoint;
import com.shiweinan.BlindCommand.util.CommandParser;

public class MyAccessibilityService extends AccessibilityService {

    DisplayMetrics metrics;
    CommandParser cp;

    public MyAccessibilityService() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);

        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                metrics.widthPixels,
                metrics.heightPixels / 2,
                0,
                metrics.heightPixels / 2,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;

        final View kbdView = new View(this);

        kbdView.setBackgroundColor(Color.RED);
        kbdView.setAlpha(0.6f);
        wm.addView(kbdView, params);
        kbdView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println(motionEvent.getX() + " " + motionEvent.getY());
                TouchPoint a = new TouchPoint(motionEvent.getX(), motionEvent.getY(),motionEvent.getRawX(), motionEvent.getRawY());


                return false;
            }
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //System.out.println(AccessibilityEvent.eventTypeToString(event.getEventType()));
    }

    @Override
    public void onInterrupt() {
    }
}
