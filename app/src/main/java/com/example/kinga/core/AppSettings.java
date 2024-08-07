package com.example.kinga.core;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.kinga.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class AppSettings {
    public static final boolean PRODUCTION = true;

    // User type constants
    public static final int DRIVER_USER_TYPE = 1;
    public static final int CUSTOMER_USER_TYPE = 2;

    // Date formatters
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat FULL_DATE_FORMATTER = new SimpleDateFormat(
            "EEEE, d MMMM yyyy"
    );

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm aa");


}
