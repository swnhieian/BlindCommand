package blindcommand;

import android.accessibilityservice.AccessibilityService;
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
import java.util.List;

import blindcommand.speech.SpeechCallback;
import blindcommand.speech.SpeechHelper;
import blindcommand.speech.SpeechParser;
import blindcommand.speech.SpeechResult;

public class KbdView extends View{
    public Executor executor;
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
        SWIPE_DOWN_TWO_FINGERS, SWIPE_LEFT_TWO_FINGERS, SWIPE_RIGHT_TWO_FINGERS, No_Action,
        SWIPE_DOWN_UP, SWIPE_UP_DOWN, SWIPE_LEFT_RIGHT, SWIPE_RIGHT_LEFT, SWIPE_DOWN_LEFT,
        SWIPE_DOWN_RIGHT, SWIPE_UP_LEFT, SWIPE_UP_RIGHT, SWIPE_LEFT_UP, SWIPE_LEFT_DOWN,
        SWIPE_RIGHT_UP, SWIPE_RIGHT_DOWN}
    public void performAction(SwipeAction action) {
        tempAction = SwipeAction.No_Action;
        System.out.println("Detect:" + action.toString());
        ParseResult parseResult;
        switch (action) {
            case SWIPE_LEFT:
                if (Utility.useDiffNav) {
                    parser.previousDiff();
                } else {
                    parser.previous();
                }
                readParseResult(parser.getCurrent());
                break;
            case SWIPE_RIGHT:
                if (Utility.useDiffNav) {
                    parser.nextDiff();
                } else {
                    parser.next();
                }
                readParseResult(parser.getCurrent());
                break;
            case SWIPE_DOWN: //confirm
                parseResult = parser.getCurrent();
                executor.execute(parseResult.instruction);
                SoundPlayer.execute(parseResult.instruction);
                ((TalkBackService)getContext()).triggerBCMode();
                parser.clear();
                break;
            case SWIPE_LEFT_TWO_FINGERS:
                parser.clear();
                SoundPlayer.ding();
                break;
            case SWIPE_UP_TWO_FINGERS:
                if (Utility.useDiffNav) {
                    parser.previous();
                    readParseResult(parser.getCurrent());
                }
                break;
            case SWIPE_DOWN_TWO_FINGERS:
                if (Utility.useDiffNav) {
                    parser.next();
                    readParseResult(parser.getCurrent());
                }
                break;
            case SWIPE_DOWN_UP:
                ((TalkBackService)getContext()).triggerBCMode();
            default:
                break;
        }
    }

    float downx, downy, downx2, downy2, tempx, tempy;
    long downTime, downTime2, tempTime;
    final int SWIPE_DIST = 40;
    final int SWIPE_TIME = 400;
    final int NO_FEEDBACK_TIME = 40;
    boolean judgingTwoFingers = false;
    SimpleParser defaultParser;
    Parser parser;
    Parser.ParserType parserType;
    InstructionSet instructionSet;
    public void upTouch(MotionEvent event) { //confirm
        if(parserType != Parser.ParserType.SPEECH) {
            parser.addTouchPoint(event.getEventTime(), event.getX(), event.getY());
            if (event.getEventTime() - downTime < NO_FEEDBACK_TIME) {
                SoundPlayer.click();
            }
            readParseResult(parser.getCurrent());
        }
        else{
            if(parser instanceof  SpeechParser) {
                ((SpeechParser)parser).startRecognizing();
            }
            System.out.println("uptouch end");
        }
    }
    public void readParseResult(ParseResult parseResult){
        SoundPlayer.tts(parseResult.instruction.name + parseResult.instruction.meta.appName + ". 当前第" + (parseResult.index + 1) + "项, 共" + (parseResult.size) +"项");
        //SoundPlayer.tts((parseResult.hasSameName ? parseResult.instruction.meta.appName : "" ) + parseResult.instruction.name +
        //        ". 当前第" + (parseResult.index + 1) + "项, 共" + (parseResult.size) +"项");
    }
    private char lastKey = ' ';
    float lastX, lastY;
    SwipeAction tempAction = SwipeAction.No_Action;
    long lastTime;
    public void explore(MotionEvent event) {
        if(parserType != Parser.ParserType.SPEECH) {
            for (Key key : keys) {
                if (key.contains(event.getX(), event.getY())) {
                    if (key.name != lastKey && (event.getEventTime() - downTime) > SWIPE_TIME) {
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
    }
    private SwipeAction concatGesture(SwipeAction action1, SwipeAction action2) {
        if (action1 == SwipeAction.No_Action) {
            return action2;
        }
        switch (action1) {
            case SWIPE_LEFT:
                switch (action2) {
                    case SWIPE_RIGHT:
                        return SwipeAction.SWIPE_LEFT_RIGHT;
                    case SWIPE_DOWN:
                        return SwipeAction.SWIPE_LEFT_DOWN;
                    case SWIPE_UP:
                        return SwipeAction.SWIPE_LEFT_UP;
                    default:
                        return SwipeAction.No_Action;
                }
            case SWIPE_RIGHT:
                switch (action2) {
                    case SWIPE_LEFT:
                        return SwipeAction.SWIPE_RIGHT_LEFT;
                    case SWIPE_DOWN:
                        return SwipeAction.SWIPE_RIGHT_DOWN;
                    case SWIPE_UP:
                        return SwipeAction.SWIPE_RIGHT_UP;
                    default:
                        return SwipeAction.No_Action;
                }
            case SWIPE_UP:
                switch (action2) {
                    case SWIPE_RIGHT:
                        return SwipeAction.SWIPE_UP_RIGHT;
                    case SWIPE_DOWN:
                        return SwipeAction.SWIPE_UP_DOWN;
                    case SWIPE_LEFT:
                        return SwipeAction.SWIPE_UP_LEFT;
                    default:
                        return SwipeAction.No_Action;
                }
            case SWIPE_DOWN:
                switch (action2) {
                    case SWIPE_RIGHT:
                        return SwipeAction.SWIPE_DOWN_RIGHT;
                    case SWIPE_LEFT:
                        return SwipeAction.SWIPE_DOWN_LEFT;
                    case SWIPE_UP:
                        return SwipeAction.SWIPE_DOWN_UP;
                    default:
                        return SwipeAction.No_Action;
                }
            default:
                return SwipeAction.No_Action;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch(event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                System.out.println("down");
//                startRecognizing();
//                break;
//            case MotionEvent.ACTION_UP:
//                System.out.println("up");
//                stopRecognizing();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                break;
//            default:
//                break;
//        }
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
                    tempx = x;
                    tempy = y;
                    tempTime = time;
                    downTime = time;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    if (time < tempTime + SWIPE_TIME) {
                        if (y < tempy - SWIPE_DIST &&
                                Math.abs(y-tempy) > Math.abs(x-tempx)) {
                                performAction(concatGesture(tempAction, SwipeAction.SWIPE_UP));
                        } else if (y > tempy + SWIPE_DIST &&
                                Math.abs(y-tempy) > Math.abs(x-tempx)) {
                            performAction(concatGesture(tempAction, SwipeAction.SWIPE_DOWN));
                        } else if (x < tempx - SWIPE_DIST &&
                                Math.abs(x - tempx) > Math.abs(y - tempy)) {
                            performAction(concatGesture(tempAction, SwipeAction.SWIPE_LEFT));
                        } else if (x > tempx + SWIPE_DIST &&
                                Math.abs(x - tempx) > Math.abs(y - tempy)) {
                            performAction(concatGesture(tempAction, SwipeAction.SWIPE_RIGHT));
                        } else if (tempAction != SwipeAction.No_Action) {
                            performAction(concatGesture(SwipeAction.No_Action, tempAction));
                        } else {
                            upTouch(event);
                        }

                    } else {
                        upTouch(event);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (time < downTime + SWIPE_TIME && time > downTime + SWIPE_TIME / 2) {
                        if (y < downy - SWIPE_DIST &&
                                Math.abs(y - downy) > Math.abs(x - downx)) {
                            if (tempAction == SwipeAction.No_Action || tempAction == SwipeAction.SWIPE_UP) {
                                tempx = x;
                                tempy = y;
                                tempTime = time;
                                tempAction = SwipeAction.SWIPE_UP;
                            }
                        } else if (y > downy + SWIPE_DIST &&
                                Math.abs(y - downy) > Math.abs(x - downx)) {
                            if (tempAction == SwipeAction.No_Action || tempAction == SwipeAction.SWIPE_DOWN) {
                                tempx = x;
                                tempy = y;
                                tempTime = time;
                                tempAction = SwipeAction.SWIPE_DOWN;
                            }
                        } else if (x < downx - SWIPE_DIST &&
                                Math.abs(x - downx) > Math.abs(y - downy)) {
                            if (tempAction == SwipeAction.No_Action || tempAction == SwipeAction.SWIPE_LEFT) {
                                tempx = x;
                                tempy = y;
                                tempTime = time;
                                tempAction = SwipeAction.SWIPE_LEFT;
                            }
                        } else if (x > downx + SWIPE_DIST &&
                                Math.abs(x - downx) > Math.abs(y - downy)) {
                            if (tempAction == SwipeAction.No_Action || tempAction == SwipeAction.SWIPE_RIGHT) {
                                tempx = x;
                                tempy = y;
                                tempTime = time;
                                tempAction = SwipeAction.SWIPE_RIGHT;
                            }
                        }
                    } else {
                            explore(event);
                        }
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
    private List<Instruction> instructions;

    public void initKeyboard() {
        executor = new Executor(((AccessibilityService)(getContext())));
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
        int startY = Utility.getScreenHeight(getContext()) - Utility.getkeyboardHeight(getContext());
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
        this.instructionSet = new InstructionSet(executor.getInstructions());
        defaultParser = new SimpleParser(keys, this.instructionSet);
    }
    public void setParser(Parser.ParserType type, List<Instruction> paras) {
        this.parserType = type;
        switch (type) {
            case DEFAULT:
                parser = defaultParser;
                break;
            case NO_DICT:
                parser = new NoDictParser(keys);
                break;
            case LIST:
                parser = new SimpleParser(keys, new InstructionSet(paras));
            case SPEECH:
                parser = new SpeechParser(this, this.instructionSet);
            default:
                break;
        }
    }


    @Override
    public void draw(Canvas canvas) {
        System.out.println("ondraw");
        //this.setBackgroundColor(Color.parseColor("#89cff0"));
        canvas.drawARGB(100, 137, 207, 240);
        if(parserType != Parser.ParserType.SPEECH) {
            if (keys != null) {
                for (Key key : keys) {
                    System.out.println("key:" + key.name + ",x:" + key.x + ",y:" + key.y);
                    canvas.drawRect(key.getRect(), backgroundPaint);
                    canvas.drawText(key.name+"", key.x, key.y, textPaint);
                }
            }
        }
        super.draw(canvas);
    }
}
