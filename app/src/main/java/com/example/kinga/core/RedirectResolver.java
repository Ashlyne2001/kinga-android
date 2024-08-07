package com.example.kinga.core;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.kinga.core.NetworkConnection.VolleyUtils.MySingleton;
import com.example.kinga.core.Urls.SharedUrls;
import com.example.kinga.core.userUtils.SessionHandler;
import com.example.kinga.core.userUtils.User;
import com.example.kinga.ui.accounts.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class RedirectResolver {
    /**
     * Redirects to broweser
     */
    public static void redirectToBrowser(String url, Activity activity) {
        try {
            Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);

        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed
        }
    }

    /**
     * Logs out the user and then redirects to the login page
     */
    public static void logoutUserAndRedirectToLogin(Context context, SessionHandler session) {

        User user = session.getUserDetails();

        // First logout user locally
        session.logoutUser();

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.GET, SharedUrls.LogoutUrl(), null, response -> {
                    // If we receive a successful response, we process the returned data


                }, error -> {
                    // In case of a error, the code below is executed

                })
        {
            // Authorise with current user token
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String auth = "Token " + user.getToken();
                params.put("Authorization", auth);
                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsArrayRequest);

        Intent i = new Intent(context.getApplicationContext(), LoginActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        ((Activity)context).finish();

    }


}
