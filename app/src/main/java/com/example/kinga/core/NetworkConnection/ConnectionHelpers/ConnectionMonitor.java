package com.example.kinga.core.NetworkConnection.ConnectionHelpers;

import android.content.Context;

/**
 * A class used for notifications about network changes
 */
public class ConnectionMonitor {

    /**
     * Starts a network monitoring callback if one is not already running and then checks if we
     * have a connection
     * @return - Returns true if we have a network connection
     */
    public static boolean isConnected(Context context) {

        MyConnectionChangeSingleton singleton = MyConnectionChangeSingleton.getInstance(context);

        singleton.startConnectionNetworkCallback();

        return singleton.getIsConnected();

    }
}
