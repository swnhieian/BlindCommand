package blindcommand;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

    public KBView(Context context, @NonNull WindowManager.LayoutParams params){
        super(context);
        this.service = (TalkBackService)context;
        this.controller = service.blindCommandController;

        setBackgroundColor(ContextCompat.getColor(context, R.color.back));
        setAlpha(0.4f);
        SimpleParser.getInstance().setKeyboardInfo(params.width, params.height);
        this.setOnTouchListener(listener);
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

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            System.out.println("HHHHHHHHHHHHHHHHHHHHH"+ MotionEvent.actionToString(motionEvent.getAction()));
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    beginPosX = (double)motionEvent.getX();
                    beginPosY = (double)motionEvent.getY();
                    System.out.println("touch down" + "down " + beginPosX + " " + beginPosY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    curPosX = (double)motionEvent.getX();
                    curPosY = (double)motionEvent.getY();
                    Log.i("touch move","move " + curPosX + " " + curPosY);
                    break;
                case MotionEvent.ACTION_UP:
                    curPosX = (double)motionEvent.getX();
                    curPosY = (double)motionEvent.getY();
                    double hshift = curPosX - beginPosX;
                    double vshift = curPosY - beginPosY;

                    TouchPoint tp = new TouchPoint(0, motionEvent.getX(), motionEvent.getY(), motionEvent.getRawX(), motionEvent.getRawY());
                    String result = "";
                    if(Math.abs(hshift) > Math.abs(vshift)){
                        if(hshift > 25){  // swipe right
                            Log.i("swipe","right " + hshift);
                            //result = SimpleParser.getInstance().performSwipeRight();
                            controller.performGesture(TalkBackService.GESTURE_SWIPE_RIGHT);
                        }
                        else if(hshift < -25) { // swipe left
                            Log.i("swipe", "left " + (-hshift));
                            //result = SimpleParser.getInstance().performSwipeLeft();
                            controller.performGesture(TalkBackService.GESTURE_SWIPE_LEFT);
                        }
                        else{
                            //Log.i("click0","click at " + curPosX + " " + curPosY);
                            //result = SimpleParser.getInstance().performTouch(tp);
                            controller.performClick(tp);
                            //controller.performTouch(TalkBackService.GESTURE_SWIPE_RIGHT);
                        }
                    }
                    else if(Math.abs(hshift) < Math.abs(vshift)) {
                        if (vshift > 25) { // swipe down
                            Log.i("swipe", "down " + vshift);
                            //result = SimpleParser.getInstance().performSwipeDown();
                            controller.performGesture(TalkBackService.GESTURE_SWIPE_DOWN);
                        } else if (vshift < -25) { // swipe up
                            Log.i("swipe", "up " + (-vshift));
                            //result = SimpleParser.getInstance().performSwipeUp();
                            controller.performGesture(TalkBackService.GESTURE_SWIPE_UP);
                        }
                        else{
                            Log.i("click1","click at " + curPosX + " " + curPosY);
                            //result = SimpleParser.getInstance().performTouch(tp);
                            controller.performClick(tp);
                            //controller.performTouch(TalkBackService.GESTURE_SWIPE_RIGHT);
                        }
                    }
                    else{
                        //Log.i("click2","click at " + curPosX + " " + curPosY);
                        //result = SimpleParser.getInstance().performTouch(tp);
                        controller.performClick(tp);
                    }
                    beginPosX = 0.0;
                    beginPosY = 0.0;
                    curPosX = 0.0;
                    curPosY = 0.0;

                    //Log.i("touch info", "onTouch: " + a.info());

//                    if(toast != null){
//                        toast.cancel();
//                    }
                    //String result = "test";
//                    toast = Toast.makeText(getContext(), result, Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.TOP, 0, 200);
//                    toast.show();
                    //SimpleParser.getInstance().add(a);
                    //SoundPlayer.click();
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
