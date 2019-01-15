package blindcommand;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.accessibility.talkback.TalkBackService;

import java.util.ArrayList;

public class KbdView extends View {
    public KbdView(Context context) {
        super(context);
        initKeyboard();
    }

    public KbdView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public KbdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context,attrs,defStyleAttr,0);
    }

    public KbdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initKeyboard();
    }


    public enum SwipeAction { SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT, SWIPE_UP_TWO_FINGERS,
        SWIPE_DOWN_TWO_FINGERS, SWIPE_LEFT_TWO_FINGERS, SWIPE_RIGHT_TWO_FINGERS};
    public void performAction(SwipeAction action) {
        System.out.println("Detect:" + action.toString());
        switch (action) {
            case SWIPE_LEFT:
                parser.previous();
                SoundPlayer.tts(parser.getCurrent().name);
                break;
            case SWIPE_RIGHT:
                parser.next();
                SoundPlayer.tts(parser.getCurrent().name);
                break;
            case SWIPE_DOWN: //confirm
                instructionSet.execute(parser.getCurrent().name);
                SoundPlayer.execute(parser.getCurrent());
                ((TalkBackService)getContext()).triggerBCMode();
                parser.clear();
                break;
            case SWIPE_LEFT_TWO_FINGERS:
                parser.clear();
                SoundPlayer.ding();
                break;
            default:
                break;
        }
    }

    float downx, downy, downx2, downy2;
    long downTime, downTime2;
    final int SWIPE_DIST = 40;
    final int SWIPE_TIME = 400;
    final int NO_FEEDBACK_TIME = 40;
    boolean judgingTwoFingers = false;
    SimpleParser parser;
    InstructionSet instructionSet;
    public void upTouch(MotionEvent event) { //confirm
        parser.addTouchPoint(event.getEventTime(), event.getX(), event.getY());
        if (event.getEventTime() - downTime < NO_FEEDBACK_TIME) {
            SoundPlayer.click();
        }
        SoundPlayer.tts(parser.getCurrent().name);
    }
    private char lastKey = ' ';
    float lastX, lastY;
    long lastTime;
    public void explore(MotionEvent event) {

        for (Key key: keys) {
            if (key.contains(event.getX(), event.getY())) {
                if (key.name != lastKey && (event.getEventTime() - downTime)>SWIPE_TIME) {
                    SoundPlayer.readKey(Utility.speed, key.name);
                    lastKey = key.name;
                }
                break;
            }
        }
        lastX = event.getX();
        lastY = event.getY();
        lastTime = event.getEventTime();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long time = event.getEventTime();
        if (event.getPointerCount() == 2) {
            judgingTwoFingers = true;
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    downx2 = x;
                    downy2 = y;
                    downTime2 = time;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if (time < downTime2 + SWIPE_TIME) {
                        if (y < downy2 - SWIPE_DIST &&
                                Math.abs(y-downy2) > Math.abs(x-downx2)) {
                            performAction(SwipeAction.SWIPE_UP_TWO_FINGERS);
                        } else if (y > downy2 + SWIPE_DIST &&
                                Math.abs(y-downy2) > Math.abs(x-downx2)) {
                            performAction(SwipeAction.SWIPE_DOWN_TWO_FINGERS);
                        } else if (x < downx2 - SWIPE_DIST &&
                                Math.abs(x - downx2) > Math.abs(y - downy2)) {
                            performAction(SwipeAction.SWIPE_LEFT_TWO_FINGERS);
                        } else if (x > downx2 + SWIPE_DIST &&
                                Math.abs(x - downx2) > Math.abs(y - downy2)) {
                            performAction(SwipeAction.SWIPE_RIGHT_TWO_FINGERS);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        if (!judgingTwoFingers && event.getPointerCount() == 1) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    downx = x;
                    downy = y;
                    downTime = time;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    if (time < downTime + SWIPE_TIME) {
                        if (y < downy - SWIPE_DIST &&
                                Math.abs(y-downy) > Math.abs(x-downx)) {
                            performAction(SwipeAction.SWIPE_UP);
                        } else if (y > downy + SWIPE_DIST &&
                                Math.abs(y-downy) > Math.abs(x-downx)) {
                            performAction(SwipeAction.SWIPE_DOWN);
                        } else if (x < downx - SWIPE_DIST &&
                                Math.abs(x - downx) > Math.abs(y - downy)) {
                            performAction(SwipeAction.SWIPE_LEFT);
                        } else if (x > downx + SWIPE_DIST &&
                                Math.abs(x - downx) > Math.abs(y - downy)) {
                            performAction(SwipeAction.SWIPE_RIGHT);
                        } else {
                            upTouch(event);
                        }
                    } else {
                        upTouch(event);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    explore(event);
                    break;
                default:
                    break;
            }
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            judgingTwoFingers = false;
        }
        return super.onTouchEvent(event);
    }

    public ArrayList<Key> keys;
    Paint backgroundPaint;
    Paint textPaint;

    public void initKeyboard() {
        //this.setBackgroundColor(Color.parseColor("#89cff0"));
        //this.setAlpha(0.4f);

        this.backgroundPaint=new Paint();
        this.backgroundPaint.setColor(Color.rgb(239,239,239));
        this.backgroundPaint.setStrokeJoin(Paint.Join.ROUND);
        this.backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        this.backgroundPaint.setStrokeWidth(3);
        this.backgroundPaint.setStrokeWidth(3);

        this.textPaint=new Paint();
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setStrokeJoin(Paint.Join.ROUND);
        this.textPaint.setStrokeCap(Paint.Cap.ROUND);
        this.textPaint.setStrokeWidth(3);
        this.textPaint.setTextSize(Math.round(70));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setStrokeJoin(Paint.Join.ROUND);
        this.textPaint.setStrokeCap(Paint.Cap.ROUND);


        int keyWidth = Utility.getKeyboardWidth(getContext()) / 10;
        int keyHeight = Utility.getkeyboardHeight(getContext()) / 4;
        int startY = Utility.getScreenHeight(getContext())/2 - Utility.getkeyboardHeight(getContext());
        System.out.println("startY" + startY+","+getHeight());
        keys = new ArrayList<>();
        String line = "QWERTYUIOP";
        for (int i=0; i<line.length(); i++) {
            keys.add(new Key(line.charAt(i), (i+0.5f)*keyWidth, startY+0.5f*keyHeight, keyWidth, keyHeight));
        }
        line = "ASDFGHJKL";
        for (int i=0; i<line.length(); i++) {
            keys.add(new Key(line.charAt(i), (i+1)*keyWidth, startY+1.5f*keyHeight, keyWidth, keyHeight));
        }
        line = "ZXCVBNM";
        for (int i=0; i<line.length(); i++) {
            keys.add(new Key(line.charAt(i), (i+2)*keyWidth, startY+2.5f*keyHeight, keyWidth, keyHeight));
        }

        this.invalidate();
        this.instructionSet = new InstructionSet(getContext());
        parser = new SimpleParser(keys, this.instructionSet);


    }


    @Override
    public void draw(Canvas canvas) {
        System.out.println("ondraw");
        //this.setBackgroundColor(Color.parseColor("#89cff0"));
        canvas.drawARGB(100, 137, 207, 240);
        if (keys != null) {
            for (Key key : keys) {
                System.out.println("key:" + key.name + ",x:" + key.x + ",y:" + key.y);
                canvas.drawRect(key.getRect(), backgroundPaint);
                canvas.drawText(key.name+"", key.x, key.y, textPaint);
            }
        }

        super.draw(canvas);
    }
}
