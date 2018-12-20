package com.example.fly.anyrtcdemo.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;


public class SoftKeyboardUtil {

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private InputMethodManager imm;
    private int keyboardHeight;//鍵盤的高度
    private boolean isShowKeyboard;// 鍵盤的顯示狀態
    private boolean isVKeyMap;//虛擬按键，華為手機
    int vHeight = 0;

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow);
    }

    public void observeSoftKeyboard(Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        final int statusBarHeight = getStatusBarHeight(activity);// 狀態欄的高度
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                onGlobalLayoutListener = this;
                Rect r = new Rect();
                decorView.getWindowVisibleDisplayFrame(r);
                // 螢幕高度不含虛擬按键的高度
                int screenHeight = decorView.getRootView().getHeight();

                if ((screenHeight - r.bottom) < screenHeight / 4) {
                    isVKeyMap = true;
                    vHeight = screenHeight - r.bottom;
                } else if ((screenHeight - r.bottom) == 0) {
                    vHeight = 0;
                    isVKeyMap = false;
                }

                int heightDiff = screenHeight - (r.bottom - r.top);
                if (keyboardHeight == 0 && heightDiff > (screenHeight / 4)) {
                    if (isVKeyMap) {
                        keyboardHeight = heightDiff - vHeight;
                    } else {
                        keyboardHeight = heightDiff;
                    }
                }

                if (isVKeyMap) {
                    if (isShowKeyboard) {
                        if (heightDiff <= (statusBarHeight + vHeight)) {
                            isShowKeyboard = false;
                            listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                        }
                    } else {
                        if (heightDiff > (statusBarHeight) && heightDiff > (screenHeight / 4)) {
                            isShowKeyboard = true;
                            listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                        }
                    }
                } else {
                    if (isShowKeyboard) {
                        if (heightDiff <= statusBarHeight) {
                            isShowKeyboard = false;
                            listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                        }
                    } else {
                        if (heightDiff > (statusBarHeight)) {
                            isShowKeyboard = true;
                            listener.onSoftKeyBoardChange(keyboardHeight, isShowKeyboard);
                        }
                    }
                }
            }
        });
    }

    public void removeGlobalOnLayoutListener(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        if (onGlobalLayoutListener != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                decorView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
            } else {
                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        }
    }

    // 獲取狀態欄高度
    public int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void showKeyboard(Context context, View view) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }
}