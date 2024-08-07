package com.example.kinga.core.NetworkConnection.ConnectionHelpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

/**
 * A class used for notifications about network changes
 */
public class MyConnectionChangeSingleton {

    // static variable single_instance of type Singleton
    @SuppressLint("StaticFieldLeak")
    private static MyConnectionChangeSingleton single_instance=null;

    private Context mContext;
    private ConnectivityManager mConnectivityManager = null;
    private boolean mIsConnected = false;

    /**
     *private constructor restricted to this class itself
     */
    private MyConnectionChangeSingleton(Context context) {
        mContext = context;
    }

    /**
     * static method to create instance of Singleton class
     */
    public static MyConnectionChangeSingleton getInstance(Context context) {
        if (single_instance == null){

            single_instance = new MyConnectionChangeSingleton(context);
        }

        return single_instance;
    }

    /**
     * Starts a network monitoring callback if one is not already running
     */
    void startConnectionNetworkCallback() {

        if (mConnectivityManager == null){

            mConnectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            mConnectivityManager.registerNetworkCallback(
                    builder.build(),
                    new ConnectivityManager.NetworkCallback() {

                        @Override
                        public void onAvailable(@NonNull Network network) {
                            mIsConnected = true;
                        }

                        @Override
                        public void onLost(@NonNull Network network) {
                            mIsConnected = false;
                        }
                    }
            );
        }
    }

    /**
     * @return - Returns true if we have a network connection
     */
    public boolean getIsConnected(){
        return mIsConnected;
    }

}