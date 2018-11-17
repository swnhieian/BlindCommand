package com.shiweinan.BlindCommand.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.shiweinan.BlindCommand.R;
import com.shiweinan.BlindCommand.touch.TouchPoint;
import com.shiweinan.BlindCommand.util.SimpleParser;

import java.util.ArrayList;
import java.util.List;


public class KBView extends GridLayout {
    //private EditText editText;
    private TextView candidateView;
    Toast toast;

    public KBView(Context context, WindowManager.LayoutParams params, TextView candidate){
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, R.color.back));
        setAlpha(0.4f);
        SimpleParser.getInstance().setKeyboardInfo(params.width, params.height);
        candidateView = candidate;
        this.setOnTouchListener(listener);
    }
    private View.OnTouchListener listener = new OnTouchListener() {
        private double beginPosX = 0.0;
        private double beginPosY = 0.0;
        private double curPosX = 0.0;
        private double curPosY = 0.0;

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    beginPosX = (double)motionEvent.getX();
                    beginPosY = (double)motionEvent.getY();
                    //Log.i("touch down","down " + beginPosX + " " + beginPosY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //curPosX = (double)motionEvent.getX();
                    //curPosY = (double)motionEvent.getY();
                    //Log.i("touch move","move " + curPosX + " " + curPosY);
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
                           //Log.i("swipe","right " + hshift);
                           result = SimpleParser.getInstance().performSwipeRight();
                        }
                        else if(hshift < -25) { // swipe left
                            //Log.i("swipe", "left " + (-hshift));
                            result = SimpleParser.getInstance().performSwipeLeft();
                        }
                        else{
                            //Log.i("click0","click at " + curPosX + " " + curPosY);
                            result = SimpleParser.getInstance().performTouch(tp);
                        }
                    }
                    else if(Math.abs(hshift) < Math.abs(vshift)) {
                        if (vshift > 25) { // swipe down
                            //Log.i("swipe", "down " + vshift);
                            result = SimpleParser.getInstance().performSwipeDown();
                        } else if (vshift < -25) { // swipe up
                            //Log.i("swipe", "up " + (-vshift));
                            result = SimpleParser.getInstance().performSwipeUp();
                        }
                        else{
                            //Log.i("click1","click at " + curPosX + " " + curPosY);
                            result = SimpleParser.getInstance().performTouch(tp);
                        }
                    }
                    else{
                        //Log.i("click2","click at " + curPosX + " " + curPosY);
                        result = SimpleParser.getInstance().performTouch(tp);
                    }
                    beginPosX = 0.0;
                    beginPosY = 0.0;
                    curPosX = 0.0;
                    curPosY = 0.0;

                    //Log.i("touch info", "onTouch: " + a.info());

                    if(toast != null){
                        toast.cancel();
                    }
                    toast = Toast.makeText(getContext(), result, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 200);
                    toast.show();
                    //SimpleParser.getInstance().add(a);
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    @Override
    public boolean performClick(){
        return super.performClick();
    }
}
