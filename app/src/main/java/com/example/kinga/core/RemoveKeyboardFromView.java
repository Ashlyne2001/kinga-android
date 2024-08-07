package com.example.kinga.core;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class RemoveKeyboardFromView {

    public static void clearKeyboard(View view, Activity activity) {

        try {
            InputMethodManager mImm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mImm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mImm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            // Do nothing

        }
    }

}
