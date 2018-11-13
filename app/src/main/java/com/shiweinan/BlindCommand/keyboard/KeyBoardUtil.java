package com.shiweinan.BlindCommand.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.shiweinan.BlindCommand.R;

public class KeyBoardUtil {
    private KeyboardView keyboardView;
    private EditText editText;
    private Keyboard k;

    public KeyBoardUtil(KeyboardView keyboardView, EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        k = new Keyboard(editText.getContext(), R.xml.qwerty);
        this.keyboardView = keyboardView;
        this.editText = editText;
        this.keyboardView.setOnKeyboardActionListener(listener);
        this.keyboardView.setKeyboard(k);
        this.keyboardView.setEnabled(true);
        this.keyboardView.setPreviewEnabled(false);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
           Editable editable = editText.getText();
            int start = editText.getSelectionStart();
            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE:
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                    break;
                case Keyboard.KEYCODE_CANCEL:
                    keyboardView.setVisibility(View.GONE);
                    break;
                default:
                    editable.insert(start, Character.toString((char) primaryCode));
                    break;
            }

        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };
    // Activity中获取焦点时调用，显示出键盘
    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    // 隐藏键盘
    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.GONE);
        }
    }

}
