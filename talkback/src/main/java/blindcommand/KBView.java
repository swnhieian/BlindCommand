package blindcommand;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
// import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.android.accessibility.talkback.R;
import com.google.android.accessibility.talkback.TalkBackService;

public class KBView extends GridLayout {
    Toast toast;
    TalkBackService service;
    BlindCommandController controller;

    public static final String TAG = "KBView.";
    public KBView(Context context, @NonNull WindowManager.LayoutParams params){
        super(context);
        this.service = (TalkBackService)context;
        this.controller = service.blindCommandController;

        setBackgroundColor(ContextCompat.getColor(context, R.color.back));
        setAlpha(0.4f);
        SimpleParser.getInstance().setKeyboardInfo(params.width, params.height);
        this.setOnTouchListener(listener);
        Log.d(TAG, "keyboard view init.");
    }

    @Override
    public boolean dispatchGenericMotionEvent (MotionEvent event) {
        service.performMotionEvent(event);
        return true;
    }
    public void toast(String info) {
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), info, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.show();
    }

    private View.OnTouchListener listener = new OnTouchListener() {
        private double beginPosX = 0.0;
        private double beginPosY = 0.0;
        private double curPosX = 0.0;
        private double curPosY = 0.0;
        private final String SUBTAG = TAG + "onTouch";

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            // System.out.println("HHHHHHHHHHHHHHHHHHHHH"+ MotionEvent.actionToString(motionEvent.getAction()));
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    beginPosX = (double)motionEvent.getX();
                    beginPosY = (double)motionEvent.getY();
                    Log.d(SUBTAG, String.format("Action down: (%.2f, %.2f)", beginPosX, beginPosY));
                    break;
                case MotionEvent.ACTION_MOVE:
                    curPosX = (double)motionEvent.getX();
                    curPosY = (double)motionEvent.getY();
                    Log.d(SUBTAG, String.format("Action move: (%.2f, %.2f)", curPosX, curPosY));
                    break;
                case MotionEvent.ACTION_UP:
                    curPosX = (double)motionEvent.getX();
                    curPosY = (double)motionEvent.getY();
                    double hshift = curPosX - beginPosX;
                    double vshift = curPosY - beginPosY;
                    Log.d(SUBTAG, String.format("Action up: (%.2f, %.2f)", curPosX, curPosY));

                    TouchPoint tp = new TouchPoint(0, motionEvent.getX(), motionEvent.getY(), motionEvent.getRawX(), motionEvent.getRawY());
                    if(Math.abs(hshift) > Math.abs(vshift)){
                        if(hshift > 25){  // swipe right
                            controller.performGesture(TalkBackService.GESTURE_SWIPE_RIGHT);
                        }
                        else if(hshift < -25) { // swipe left
                            controller.performGesture(TalkBackService.GESTURE_SWIPE_LEFT);
                        }
                        else{
                            controller.performClick(tp);
                        }
                    }
                    else if(Math.abs(hshift) < Math.abs(vshift)) {
                        if (vshift > 25) { // swipe down
                            controller.performGesture(TalkBackService.GESTURE_SWIPE_DOWN);
                        } else if (vshift < -25) { // swipe up
                            controller.performGesture(TalkBackService.GESTURE_SWIPE_UP);
                        }
                        else{
                            controller.performClick(tp);
                        }
                    }
                    else{
                        controller.performClick(tp);
                    }
                    beginPosX = 0.0;
                    beginPosY = 0.0;
                    curPosX = 0.0;
                    curPosY = 0.0;
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    @Override
    public boolean performClick(){
        return super.performClick();
    }
}
