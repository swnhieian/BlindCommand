package com.shiweinan.BlindCommand.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
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
    private KeyboardView keyboardView;
    //private EditText editText;
    Toast toast;

    public KBView(Context context, WindowManager.LayoutParams params){
        super(context);

        //inflate(context, R.layout.service, this);
       // editText = findViewById(R.id.input);
        /*
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(editText.hasFocus()){
                    //用来初始化我们的软键盘
                    new KeyBoardUtil(keyboardView,editText).showKeyboard();
                }
                return false;
            }
        });
        */

        /*
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

*/
        //kb = (GridView)findViewById(R.id.nine_block);

        setBackgroundColor(ContextCompat.getColor(context, R.color.back));
        setAlpha(0.4f);
        SimpleParser.getInstance().setKeyboardInfo(params.width, params.height);


        this.setOnTouchListener(listener);

        /*
        */
    }
    private View.OnTouchListener listener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_UP:
                    TouchPoint a = new TouchPoint(0, motionEvent.getX(), motionEvent.getY(), motionEvent.getRawX(), motionEvent.getRawY());
                    //Log.i("touch info", "onTouch: " + a.info());
                    String pressResult = SimpleParser.getInstance().press(a);
                    if(toast != null){
                        toast.cancel();
                    }
                    toast = Toast.makeText(getContext(), pressResult, Toast.LENGTH_SHORT);
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
