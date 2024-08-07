package com.example.kinga.core;

import android.content.res.Resources;

public class MyCalculatorUtil {

    public static int pxToDp(int px) {
        return (int) (px * Resources.getSystem().getDisplayMetrics().density);
    }

}
