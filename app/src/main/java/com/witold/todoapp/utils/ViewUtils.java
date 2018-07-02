package com.witold.todoapp.utils;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ViewUtils {
    public static void setUpHidingKeyboard(final View view, final Activity activity){
        if(!(view instanceof EditText)){
            view.setFocusableInTouchMode(true);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    view.requestFocus();
                    return false;
                }
            });
        }
    }

    public static void hideSoftKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus()!=null){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
        }
    }
}
