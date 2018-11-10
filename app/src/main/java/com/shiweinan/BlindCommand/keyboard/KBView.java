package com.shiweinan.BlindCommand.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.shiweinan.BlindCommand.R;
import com.shiweinan.BlindCommand.touch.TouchPoint;
import com.shiweinan.BlindCommand.util.SimpleParser;

import java.util.ArrayList;
import java.util.List;


public class KBView extends GridLayout {
    private List<View> kbButton;
    private GridLayout kb;

    public KBView(Context context, WindowManager.LayoutParams params){
        super(context);

        inflate(context, R.layout.nine_block, this);
        kb = findViewById(R.id.nine_block);
        Log.i("count", "Count : " + kb.getChildCount());
        kbButton = new ArrayList<>();

        for(int i = 0; i < 9; i ++){
            kbButton.add(kb.getChildAt(i));
        }
        for(int i = 0; i < 9; i ++){
            final int keyNumber = i + 1;
            kbButton.get(i).setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent) {
                    switch(motionEvent.getAction()) {
                        case MotionEvent.ACTION_UP:
                            TouchPoint a = new TouchPoint(keyNumber, v.getLeft() + motionEvent.getX(), v.getTop() + motionEvent.getY(), motionEvent.getRawX(), motionEvent.getRawY());
                            SimpleParser.getInstance().add(a);
                            //Log.i("Touch from Key", "onTouch: " + a.info());
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }

        //kb = (GridView)findViewById(R.id.nine_block);

        setBackgroundColor(ContextCompat.getColor(context, R.color.back));
        setAlpha(0.6f);

        SimpleParser.getInstance().setKeyboardInfo(params.width, params.height);


        /*
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        TouchPoint a = new TouchPoint(0, motionEvent.getX(), motionEvent.getY(), motionEvent.getRawX(), motionEvent.getRawY());
                        SimpleParser.getInstance().add(a);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        */
    }
    @Override
    public boolean performClick(){
        return super.performClick();
    }
}
