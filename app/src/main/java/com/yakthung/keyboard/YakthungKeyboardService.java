package com.yakthung.keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.os.Handler;
import android.view.KeyEvent;  // Add this line

public class YakthungKeyboardService extends InputMethodService {
    private static final String TAG = "YakthungKeyboardService";
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private boolean isSymbols = false;
    private boolean isLayout2 = false;
    private Handler handler = new Handler();
    private Runnable backspaceRunnable;
    private boolean isDeleting = false;

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.input_view, null);
        if (keyboardView != null) {
            loadKeyboard(R.xml.keyboard); // Load main keyboard by default
            keyboardView.setOnKeyboardActionListener(new MyKeyboardActionListener());
        } else {
            Log.e(TAG, "Keyboard view is null.");
        }
        return keyboardView;
    }

    private void loadKeyboard(int xmlLayoutResId) {
        keyboard = new Keyboard(this, xmlLayoutResId);
        if (keyboardView != null) {
            keyboardView.setKeyboard(keyboard);
        } else {
            Log.e(TAG, "Keyboard view is not initialized.");
        }
    }

    private class MyKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (getCurrentInputConnection() == null) {
                Log.e(TAG, "Input connection is null.");
                return;
            }

            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE: // Backspace
                    getCurrentInputConnection().deleteSurroundingText(1, 0);
                    break;
                case Keyboard.KEYCODE_DONE: // Enter
                    getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
                    );
                    getCurrentInputConnection().sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
                    );
                    break;
                case -1: // Switch to layout 2 (Shift key)
                    switchToLayout2Keyboard();
                    break;
                case -2: // Switch to symbols layout (Globe key)
                    switchToSymbolsKeyboard();
                    break;
                case -6: // Open input method picker (Settings gear icon)
                    openInputMethodPicker();
                    break;
                default:
                    // Handle character input
                    getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
                    break;
            }
        }

        @Override
        public void onPress(int primaryCode) {
            if (primaryCode == Keyboard.KEYCODE_DELETE) {
                startDeleting();
            }
        }

        @Override
        public void onRelease(int primaryCode) {
            if (primaryCode == Keyboard.KEYCODE_DELETE) {
                stopDeleting();
            }
        }

        @Override
        public void onText(CharSequence text) {}
        @Override
        public void swipeLeft() {}
        @Override
        public void swipeRight() {}
        @Override
        public void swipeDown() {}
        @Override
        public void swipeUp() {}
    }

    private void startDeleting() {
        if (!isDeleting) {
            isDeleting = true;
            backspaceRunnable = new Runnable() {
                @Override
                public void run() {
                    if (getCurrentInputConnection() != null && isDeleting) {
                        getCurrentInputConnection().deleteSurroundingText(1, 0);
                        handler.postDelayed(this, 50); // Adjust speed as needed
                    }
                }
            };
            handler.post(backspaceRunnable);
        }
    }

    private void stopDeleting() {
        isDeleting = false;
        if (backspaceRunnable != null) {
            handler.removeCallbacks(backspaceRunnable);
        }
    }

    private void switchToLayout2Keyboard() {
        if (isLayout2) {
            loadKeyboard(R.xml.keyboard); // Switch to main layout
        } else {
            loadKeyboard(R.xml.layout2); // Switch to layout 2
        }
        isLayout2 = !isLayout2; // Toggle state
    }

    private void switchToSymbolsKeyboard() {
        if (isSymbols) {
            loadKeyboard(R.xml.keyboard); // Switch to main layout
        } else {
            loadKeyboard(R.xml.symbols); // Switch to symbols layout
        }
        isSymbols = !isSymbols; // Toggle state
    }

    private void openInputMethodPicker() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showInputMethodPicker();
        }
    }
}