package blindcommand;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.MotionEvent;
// import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.google.android.accessibility.talkback.TalkBackService;

import java.util.Collections;
import java.util.List;

public class BlindCommandController {
    final private boolean ENABLE_QUICK_INPUT = true;
    final private int STAY_THRESHOLD = 150;
    public enum State {Idle, Input, Select};
    private TalkBackService service;
    public final String TAG = "Controller.";
    public BlindCommandController(TalkBackService service) {
        this.service = service;
        this.state = State.Idle;

    }

    private long enterTime = -1;
    private State state;
    AccessibilityNodeInfo lastNode = null;
    AccessibilityNodeInfo currentNode = null;
    public void performMotionEvent(MotionEvent event) {
        //System.out.println("************************************************get event");
        final String SUBTAG = "motionEvent";
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                Log.d(TAG + SUBTAG, "action hover enter.");
                enterTime = event.getEventTime();
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                Log.d(TAG + SUBTAG, "action hover move.");
                long time = event.getEventTime() - enterTime;
                //System.out.println("Stay Time:" + time);
                if (time > STAY_THRESHOLD && getState() == State.Idle) {
                    currentNode = getClickNode((int) event.getRawX(), (int) event.getRawY());
                    if ((lastNode != null && !lastNode.equals(currentNode)) || (lastNode == null && currentNode != null)) {
                        dropClick();
                    }
                    lastNode = currentNode;
                }
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                Log.d(TAG + SUBTAG, "action hover exit.");
                time = event.getEventTime() - enterTime;
                //System.out.println("Stay Time:" + time);
                if (time > STAY_THRESHOLD && getState() == State.Idle) {
                    currentNode = getClickNode((int) event.getRawX(), (int) event.getRawY());
                    if (lastNode != null && currentNode != lastNode) {
                        dropClick(true);
                    }
                    lastNode = currentNode;
                } else {
                    performClick(new TouchPoint(0, event));
                    if (ENABLE_QUICK_INPUT) {
                        Log.d(TAG + SUBTAG, "Touch Exploration Disabled.");
                        service.disableTouchExploration();
                    }
                }
                lastNode = null;
                enterTime = -STAY_THRESHOLD - 1000;
                break;
            default:
                break;
        }
    }
    public void performClick(TouchPoint tp) {
        final String SUBTAG = "performClick";
        if (getState() != State.Input) {
            setState(State.Input);
        }
        Log.d(TAG + SUBTAG, "click at " + tp.toString());
        SimpleParser.getInstance().addTouchPoint(tp);
    }

    public AccessibilityNodeInfo getClickNode(int x, int y) {
        return service.perform(x, y);
    }

    public void dropClick() {
        dropClick(false);
    }
    public void dropClick(boolean exit) {
        final String SUBTAG = "dropClick";
        if (currentNode != null) {
            if (currentNode.getWindow().getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD && exit) {
                Log.d(TAG + SUBTAG, "click on " + currentNode.getViewIdResourceName() + ", text: " + currentNode.getText());
                currentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.d(TAG + SUBTAG, "focus on " + currentNode.getViewIdResourceName() + ", text: " + currentNode.getText());
                currentNode.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
            }
        }
    }
    public State getState() {
        return state;
    }
    public void setState(State s) {
        final String SUBTAG = "setState";
        Log.d(TAG + SUBTAG, String.format("State: %s -> %s.", this.state, s));
        this.state = s;
        if (this.state == State.Idle && ENABLE_QUICK_INPUT) {
            Log.d(TAG + SUBTAG, "Touch Exploration Enabled.");
            service.enableTouchExploration();
        }
    }
    public void toast(String info) {
        service.kbdView.toast(info);
    }
    public boolean performGesture(int gestureId) {
        final String SUBTAG = "pfmGesture";
        switch (gestureId) {
            case TalkBackService.GESTURE_SWIPE_LEFT:
                Log.d(TAG + SUBTAG, "Gesture Swipe Left.");
                switch(state) {
                    case Input:
                        SimpleParser.getInstance().delete();
                        if (SimpleParser.getInstance().getSize() == 0) {
                            this.setState(State.Idle);
                        }
                        break;
                    case Select:
                        this.setState(State.Idle);
                        SimpleParser.getInstance().clear();
                        break;
                    default:
                        break;
                }
                break;
            case TalkBackService.GESTURE_SWIPE_RIGHT:
                Log.d(TAG + SUBTAG, "Gesture Swipe Right.");
                switch(state) {
                    case Input:
                        String result = SimpleParser.getInstance().parseInput();
                        SoundPlayer.tts(result);
                        //toast("parse:" + result);
                        this.setState(State.Select);
                        break;
                    case Select:
                        result = SimpleParser.getInstance().current();
                        //System.out.println("111111111" + Utility.getLanguage());
                        if (Utility.getLanguage().equals("CN")) {
                            SoundPlayer.tts("执行" + result);
                        } else {
                            SoundPlayer.tts("Execute " + result);
                        }
                        //toast("accept:" + result);
                        InstructionSet.execute(result);
                        SimpleParser.getInstance().clear();
                        this.setState(State.Idle);
                        if (ENABLE_QUICK_INPUT) {
                            service.enableTouchExploration();
                        }
                        break;
                    default:
                        break;
                }
                break;
            case TalkBackService.GESTURE_SWIPE_UP:
                Log.d(TAG + SUBTAG, "Gesture Swipe Up.");
                switch (state) {
                    case Input:
                        break;
                    case Select:
                        String result = SimpleParser.getInstance().previous();
                        //toast("parse:" + result);
                        SoundPlayer.tts(result);
                        break;
                    default:
                        break;
                }
                break;
            case TalkBackService.GESTURE_SWIPE_DOWN:
                Log.d(TAG + SUBTAG, "Gesture Swipe Down.");
                switch (state) {
                    case Input:
                        SimpleParser.getInstance().clear();
                        SoundPlayer.tts("清空输入");
                        //toast("clear input!");
                        setState(State.Idle);
                        break;
                    case Select:
                        String result = SimpleParser.getInstance().next();
                        //toast("parse:" + result);
                        SoundPlayer.tts(result);
                        break;
                    default:
                        break;
                }
                break;
            default:
                Log.e(TAG + SUBTAG, "****Unknown Gesture****");
                break;
        }
        return true;
    }

}
