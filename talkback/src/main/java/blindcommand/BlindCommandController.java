package blindcommand;

import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.accessibility.talkback.TalkBackService;


public class BlindCommandController {
    final private int STAY_THRESHOLD = 150;
    public enum State {Idle, Input, Select};
    private TalkBackService service;
    public BlindCommandController(TalkBackService service) {
        this.service = service;
        this.state = State.Idle;
        InstructionSet.init();
    }

    private long enterTime = -1;
    private State state;
    AccessibilityNodeInfo lastNode = null;
    AccessibilityNodeInfo currentNode = null;
    public void performMotionEvent(MotionEvent event) {
        //System.out.println("************************************************get event");
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                System.out.println("enter time" + System.currentTimeMillis());
                enterTime = event.getEventTime();
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                long time = event.getEventTime() - enterTime;
                //System.out.println("Stay Time:" + time);
                if (time > STAY_THRESHOLD && getState() == State.Idle) {
                    currentNode = getClickNode((int) event.getRawX(), (int) event.getRawY());
                    if (lastNode != null && currentNode != lastNode) {
                        dropClick();
                    }
                    lastNode = currentNode;

                }
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                System.out.println("exit time" + System.currentTimeMillis());
                time = event.getEventTime() - enterTime;
                //System.out.println("Stay Time:" + time);
                if (time > STAY_THRESHOLD && getState() == State.Idle) {
                    currentNode = getClickNode((int) event.getRawX(), (int) event.getRawY());
                    if (lastNode != null && currentNode != lastNode) {
                        dropClick();
                    }
                    lastNode = currentNode;
                } else {
                    if (getState() != State.Input) {
                        setState(State.Input);
                    }
                    SimpleParser.getInstance().addTouchPoint(new TouchPoint(0, event));
                }
                lastNode = null;
                enterTime = -STAY_THRESHOLD - 1000;
                break;
            default:
                break;
        }
    }

    public AccessibilityNodeInfo getClickNode(int x, int y) {
        return service.perform(x, y);
    }

    public void dropClick() {
        currentNode.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
    }
    public State getState() {
        return state;
    }
    public void setState(State s) {
        this.state = s;
    }
    public void toast(String info) {
        service.kbdView.toast(info);
    }
    public boolean performGesture(int gestureId) {
        switch (gestureId) {
            case TalkBackService.GESTURE_SWIPE_LEFT:
                switch(state) {
                    case Input:
                        SimpleParser.getInstance().delete();
                        if (SimpleParser.getInstance().getSize() == 0) {
                            this.setState(State.Idle);
                        }
                        break;
                    case Select:
                        this.setState(State.Input);
                        break;
                    default:
                        break;
                }
                break;
            case TalkBackService.GESTURE_SWIPE_RIGHT:
                switch(state) {
                    case Input:
                        String result = SimpleParser.getInstance().parseInput();
                        SoundPlayer.tts(result);
                        //toast("parse:" + result);
                        this.setState(State.Select);
                        break;
                    case Select:
                        result = SimpleParser.getInstance().current();
                        SoundPlayer.tts("执行" + result);
                        //toast("accept:" + result);
                        SimpleParser.getInstance().clear();
                        this.setState(State.Idle);
                        break;
                    default:
                        break;
                }
                break;
            case TalkBackService.GESTURE_SWIPE_UP:
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
            case TalkBackService.GESTURE_SWIPE_DOWN:
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
            default:
                break;
        }
        return true;
    }

}
