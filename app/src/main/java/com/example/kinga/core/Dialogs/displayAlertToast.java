package com.example.kinga.core.Dialogs;

import static com.example.kinga.core.NetworkConnection.ConnectionHelpers.ConnectionMonitor.isConnected;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.kinga.R;

public class displayAlertToast {

    public static  void showLongDeterminedError(Context context, String error){

        // If there is no internet connection, notify the user.

        if (!isConnected(context )){
            error = context.getResources().getString(R.string.app_no_internet_connection_message);
        }

        // Sometimes when the back button is pressed and this process is ongoing,
        // a "NullPointerException" might be raised.

        // (java.lang.NullPointerException: Attempt to invoke virtual method
        // 'boolean android.app.Activity.isFinishing()' on a null object reference)
        try{

            // Before displaying the message, we check if the Activity is finishing so that we
            // avoid Error : BinderProxy@45d459c0 is not valid; is your activity running?
            if (!((Activity)context).isFinishing()){
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            // Do nothing
        }
    }

    public static  void showShortDeterminedError(Context context, String error){

        // If there is no internet connection, notify the user.

        if (!isConnected(context )){
            error = context.getResources().getString(R.string.app_no_internet_connection_message);
        }

        // Sometimes when the back button is pressed and this process is ongoing,
        // a "NullPointerException" might be raised.

        // (java.lang.NullPointerException: Attempt to invoke virtual method
        // 'boolean android.app.Activity.isFinishing()' on a null object reference)
        try{

            // Before displaying the message, we check if the Activity is finishing so that we
            // avoid Error : BinderProxy@45d459c0 is not valid; is your activity running?
            if (!((Activity)context).isFinishing()){
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            // Do nothing
        }
    }
}
