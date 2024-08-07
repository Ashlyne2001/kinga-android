package com.example.kinga.core.NetworkConnection.ConnectionHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MyConnectionChangeReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        if (intent.getAction() != null){

            boolean status = ConnectionMonitor.isConnected(mContext);

            if(status) {
                performActionsWhenConnectionIsBack();

            }else {
                performActionsWhenConnectionIsLost();
            }
        }
    }

    /**
     * When connection is regained, we send broadcast notifying whoever is listening that connection
     * has been regained
     */
    public void performActionsWhenConnectionIsBack(){

        // Send an INTERNET_IS_BACK broadcast that can be received by any listening party and
        // do additional processes that may be needed
        mContext.sendBroadcast(new Intent("INTERNET_IS_BACK"));

    }

    /**
     * When connection is lost, we send broadcast notifying whoever listening that connection is
     * gone
     */
    private void performActionsWhenConnectionIsLost(){

        mContext.sendBroadcast(new Intent("INTERNET_IS_GONE"));

    }
}