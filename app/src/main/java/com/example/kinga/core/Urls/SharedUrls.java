package com.example.kinga.core.Urls;

import android.util.Log;

import com.example.kinga.core.AppSettings;

public class SharedUrls {

    public static String getBaseUrl() {

//        return "http://192.168.100.10:8000";
        return "https://kinga.traqsale.com";

//        if (AppSettings.PRODUCTION) {
//            Log.d("mylog", "Using Production");
//            return "https://kinga.traqsale.com";
//        } else {
//            Log.d("mylog", "Using Dev");
//            return "http://192.168.100.10:8000";
//        }
    }



    public static String loginUrl() {
        return String.format("%s%s", getBaseUrl(), "/api/api-token-auth/");
    }

    public static String LogoutUrl() {
        return String.format("%s%s", getBaseUrl(), "/api/logout/");
    }

    public static String signupUrl() {
        return String.format("%s%s", getBaseUrl(), "/api/signup/");
    }

    // Profile urls
    public static String profileViewUrl(Boolean isDriver) {
        if (isDriver){
            return String.format("%s%s", getBaseUrl(), "/api/driver/profile/");
        }else{
            return String.format("%s%s", getBaseUrl(), "/api/customer/profile/");
        }
    }

    public static String tripIndexUrl() {
        return String.format("%s%s", getBaseUrl(), "/api/trips/");
    }













}
