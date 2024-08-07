package com.example.kinga.core.NetworkConnection.VolleyUtils;

import static com.example.kinga.core.NetworkConnection.VolleyUtils.RequestTimeOut.VOLLEY_INITIAL_TIMEOUT_MS;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.kinga.core.NetworkConnection.ConnectionHelpers.ConnectionMonitor;

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private MySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }

        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        // If we are not connected to the internet don't add request into the queue, instead deliver
        // no connection error
//        if (!ConnectionMonitor.isConnected(mContext)){
//            req.deliverError(new NoConnectionError());
//            return;
//        }

        req.setRetryPolicy(new DefaultRetryPolicy(
                VOLLEY_INITIAL_TIMEOUT_MS,
                -1,
                0)
        );
        /*
        // Since linux is very fast, we delay every request intentionally to test our loaders,
        //  but when we are done, we should remove this handler
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                getRequestQueue().add(req);

            }
        }, 1000);  //the time is in milliseconds

         */
        getRequestQueue().add(req);
    }
}