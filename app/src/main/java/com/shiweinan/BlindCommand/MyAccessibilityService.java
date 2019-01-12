package com.shiweinan.BlindCommand;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.INotificationSideChannel;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;


public class MyAccessibilityService extends AccessibilityService {

    DisplayMetrics metrics;
    KbdView kbdView;

    public MyAccessibilityService() {
        Utility.service = this;
    }

    /////////////////////////double click judging
    ////////////////////////source: https://blog.csdn.net/L_mixiu/article/details/51792927
        private boolean isVolumeDown = false;
        private boolean isVolumeUp = false;
        private boolean isMenu = false;
        private int currentKeyCode = 0;

        private static Boolean isDoubleClick = false;
        private static Boolean isLongClick = false;

        CheckForLongPress mPendingCheckForLongPress = null;
        CheckForDoublePress mPendingCheckForDoublePress = null;
        Handler mHandler = new Handler();


        public void dispatchKeyEvent(KeyEvent event) {
            int keycode = event.getKeyCode();
            System.out.println("in dispatch key event");


            // 有不同按键按下，取消长按、短按的判断
            if (currentKeyCode != keycode) {
                removeLongPressCallback();
                isDoubleClick = false;
            }

            // 处理长按、单击、双击按键
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                checkForLongClick(event);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                checkForDoubleClick(event);
            }

            if (keycode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    isVolumeDown = true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    isVolumeDown = false;
                }
            } else if (keycode == KeyEvent.KEYCODE_VOLUME_UP) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    isVolumeUp = true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    isVolumeUp = false;
                }
            } else if (keycode == KeyEvent.KEYCODE_MENU) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    isMenu = true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    isMenu = true;
                }
            }

            // 判断组合按键
            if (isVolumeDown
                    && isVolumeUp
                    && isMenu
                    && (keycode == KeyEvent.KEYCODE_VOLUME_UP
                    || keycode == KeyEvent.KEYCODE_VOLUME_DOWN || keycode == KeyEvent.KEYCODE_MENU)
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                //组合按键事件处理；
                isVolumeDown = false;
                isVolumeUp = false;
                isMenu = false;
            }
        }

        private void removeLongPressCallback() {
            if (mPendingCheckForLongPress != null) {
                mHandler.removeCallbacks(mPendingCheckForLongPress);
            }
        }

        private void checkForLongClick(KeyEvent event) {
            int count = event.getRepeatCount();
            int keycode = event.getKeyCode();
            if (count == 0) {
                currentKeyCode = keycode;
            } else {
                return;
            }
            if (mPendingCheckForLongPress == null) {
                mPendingCheckForLongPress = new CheckForLongPress();
            }
            mPendingCheckForLongPress.setKeycode(event.getKeyCode());
            mHandler.postDelayed(mPendingCheckForLongPress, 1000);
        }

        class CheckForLongPress implements Runnable {

            int currentKeycode = 0;

            public void run() {
                isLongClick = true;
                longPress(currentKeycode);
            }

            public void setKeycode(int keycode) {
                currentKeycode = keycode;
            }
        }

        private void longPress(int keycode) {
            //Log.i(TAG, "--longPress 长按事件--" + keycode);
        }

        private void singleClick(int keycode) {
            //Log.i(TAG, "--singleClick 单击事件--" + keycode);
            KeyEvent kdown = new KeyEvent(KeyEvent.ACTION_DOWN, keycode);
            super.onKeyEvent(kdown);
            KeyEvent kup = new KeyEvent(KeyEvent.ACTION_UP, keycode);
            super.onKeyEvent(kup);
        }

        private void doublePress(int keycode) {
            //Log.i(TAG, "---doublePress 双击事件--" + keycode);
            triggerBCMode();
        }



        private void checkForDoubleClick(KeyEvent event) {
            System.out.println("Checking for double click");
            // 有长按时间发生，则不处理单击、双击事件
            removeLongPressCallback();
            if (isLongClick) {
                isLongClick = false;
                return;
            }

            if (!isDoubleClick) {
                isDoubleClick = true;
                if (mPendingCheckForDoublePress == null) {
                    mPendingCheckForDoublePress = new CheckForDoublePress();
                }
                mPendingCheckForDoublePress.setKeycode(event.getKeyCode());
                mHandler.postDelayed(mPendingCheckForDoublePress, 500);
            } else {
                // 500ms内两次单击，触发双击
                isDoubleClick = false;
                doublePress(event.getKeyCode());
            }
        }

        class CheckForDoublePress implements Runnable {

            int currentKeycode = 0;

            public void run() {
                if (isDoubleClick) {
                    singleClick(currentKeycode);
                }
                isDoubleClick = false;
            }

            public void setKeycode(int keycode) {
                currentKeycode = keycode;
            }
        }

        private void removeDoublePressCallback() {
            if (mPendingCheckForDoublePress != null) {
                mHandler.removeCallbacks(mPendingCheckForDoublePress);
            }
        }
    /////////////////////////////////////////
    private static final String TALKBACK_SERVICE_NAME = "com.google.android.marvin.talkback/.TalkBackService";

    private void enableAccessibilityService(String name) {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, name);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, "");
    }

    private void disableAccessibilityServices() {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "");
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, "");
    }

    public void ALT_CTRL_Z() {
        KeyEvent kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ALT_LEFT);
        super.onKeyEvent(kdown);
        kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT);
        super.onKeyEvent(kdown);
        kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z);
        super.onKeyEvent(kdown);
        kdown = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_Z);
        super.onKeyEvent(kdown);
        KeyEvent kup = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT);
        super.onKeyEvent(kup);
        kdown = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ALT_LEFT);
        super.onKeyEvent(kdown);

    }
    public void ALT_CTRL_Z_2() {
        new Thread() {
        public void run() {
            try {
                Instrumentation inst = new Instrumentation();
                KeyEvent kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ALT_LEFT);
                inst.sendKeySync(kdown);
                kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT);
                inst.sendKeySync(kdown);
                kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z);
                inst.sendKeySync(kdown);
                kdown = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_Z);
                inst.sendKeySync(kdown);
                KeyEvent kup = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT);
                inst.sendKeySync(kup);
                kdown = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ALT_LEFT);
                inst.sendKeySync(kdown);
            } catch (Exception e) {
                System.out.println("Exception when sendPointerSync " + e.toString());
            }
        }
    }.start();
        }


    public void triggerBCMode() {


        Toast.makeText(this, "Trigger BC Mode", Toast.LENGTH_SHORT).show();
        if (kbdView.getVisibility() == View.VISIBLE) {
            kbdView.setVisibility(View.INVISIBLE);
            enableTouchExploration();
            //disableAccessibilityServices();
        } else {
            kbdView.setVisibility(View.VISIBLE);
            disableTouchExploration();
            System.out.println("CTRL_ALT_Z start");
            ALT_CTRL_Z();
            System.out.println("CTRL_ALT_Z end");
            //enableAccessibilityService(TALKBACK_SERVICE_NAME);
        }
    }

    public void enableTouchExploration() {
        AccessibilityServiceInfo info = getServiceInfo();

        info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;

        setServiceInfo(info);
    }

    public void disableTouchExploration() {
        AccessibilityServiceInfo serviceInfo = getServiceInfo();

        serviceInfo.flags &= ~AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;

        setServiceInfo(serviceInfo);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        System.out.println("on key event");
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            System.out.println("entrance of dispatch");
            dispatchKeyEvent(event);
            return true;
        }
        return super.onKeyEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        kbdView = new KbdView(this);
        SoundPlayer.setContext(this);
        Utility.service = this;
        enableTouchExploration();
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);

        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                metrics.widthPixels,
                metrics.heightPixels / 2,
                0,
                metrics.heightPixels / 2,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;



        wm.addView(kbdView, params);
        kbdView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        System.out.println(AccessibilityEvent.eventTypeToString(event.getEventType()));
    }

    @Override
    public void onInterrupt() {
    }
}
