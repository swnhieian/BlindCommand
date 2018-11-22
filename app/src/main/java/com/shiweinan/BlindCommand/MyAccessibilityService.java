package com.shiweinan.BlindCommand;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import android.widget.TextView;
import com.shiweinan.BlindCommand.keyboard.KBView;
import com.shiweinan.BlindCommand.util.InstructionSet;
import com.shiweinan.BlindCommand.util.SoundPlayer;


public class MyAccessibilityService extends AccessibilityService {

    DisplayMetrics metrics;

    public MyAccessibilityService() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);

        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);


        int touchPadHeight = metrics.heightPixels / 3;
        int candidateHeight = metrics.heightPixels / 30;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                metrics.widthPixels,
                touchPadHeight ,
                0,
                metrics.heightPixels - touchPadHeight,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;


        SoundPlayer.setContext(this);
        InstructionSet.init();
      

        /*
        TextView candidateView = new TextView(this);
        candidateView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        candidateView.setText("empty");
        WindowManager.LayoutParams candidateParams = new WindowManager.LayoutParams(
                metrics.widthPixels,
                candidateHeight,
                0,
                metrics.heightPixels - touchPadHeight - 2 * candidateHeight,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        candidateParams.gravity = Gravity.START | Gravity.TOP;
        wm.addView(candidateView, candidateParams);
        */

        View kbdView = new KBView(this, params);
        wm.addView(kbdView, params);

        /*kbdView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        TouchPoint a = new TouchPoint(motionEvent.getX(), motionEvent.getY(), motionEvent.getRawX(), motionEvent.getRawY());
                        System.out.println(a.info());
                        break;
                    default:
                        break;
                }

                return true;

            }

        });*/

        /*
        kbdView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println(motionEvent.getX() + " " + motionEvent.getY());
                TouchPoint a = new TouchPoint(motionEvent.getX(), motionEvent.getY(),motionEvent.getRawX(), motionEvent.getRawY());



                return true;
            }
        });
        */
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //System.out.println(AccessibilityEvent.eventTypeToString(event.getEventType()));
    }

    @Override
    public void onInterrupt() {
    }
}
