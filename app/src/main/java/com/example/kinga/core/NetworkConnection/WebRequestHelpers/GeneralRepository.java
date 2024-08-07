package com.example.kinga.core.NetworkConnection.WebRequestHelpers;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.kinga.core.NetworkConnection.VolleyUtils.MySingleton;
import com.example.kinga.core.Urls.SharedUrls;
import com.example.kinga.core.userUtils.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class GeneralRepository {

    /**
     * Feeds the returned response data to the repository's model
     * @param response - Request response data
     */
    public abstract void processResponse(JSONObject response);

    /**
     * Feeds the returned pagination response data to the repository's model
     * @param response - Request response data
     */
    public abstract void processPaginationResponse(JSONObject response);

    /**
     * Sets the error status code ot the model
     * @param statusCode - Error status code from the resposne
     */
    public abstract void setErrorCode(int statusCode);

    /**
     * Connects to the server to get the required data
     * @param application - The current {@link Application} context
     * @param currentUser - The current logged in {@link User}
     * @param requestUrl - A string representing the request url to be used
     */
    public void getData(Application application, User currentUser, String requestUrl) {

        // When there is no url, requestUrl holds a string "null". So if requestUrl
        // has a valid url, requestUrl's length will be more than 10.
        if (requestUrl == null || requestUrl.length() < 10){
            return;
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.GET, requestUrl, null,
                        this::processResponse,
                        error -> {

                            Log.d("mq-log", "Set 11 > "+ requestUrl);

                            // handleError is defined in GeneralRepository
                            int statusCode = handleError(error);

                            setErrorCode(statusCode);
                        }
                ) {
            // Authorise with current user token
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String auth = "Token " + currentUser.getToken();
                params.put("Authorization", auth);
                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(application).addToRequestQueue(jsArrayRequest);

    }

    /**
     * Connects to the server to get the required data
     * @param application - The current {@link Application} context
     * @param currentUser - The current logged in {@link User}
     * @param nextPageUrl - A string representing the next page request url to be used
     */
    public void getPaginationData(Application application, User currentUser, String nextPageUrl) {

        // When there is no next page url from the server, nextUrl holds a string "null". So if nextPageUrl
        // has a valid url, nextPageUrl's length will be more than 5.
        if (nextPageUrl == null || nextPageUrl.length() < 5){
            return;
        }

        // If we are using https, make sure pagination urls are using https
        if (SharedUrls.getBaseUrl().contains("https:")){
            nextPageUrl = nextPageUrl.replace("http:","https:");
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.GET, nextPageUrl, null,
                        this::processPaginationResponse,
                        error -> {

                            // handleError is defined in GeneralRepository
                            int statusCode = handleError(error);

                            setErrorCode(statusCode);
                        }
                ) {
            // Authorise with current user token
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String auth = "Token " + currentUser.getToken();
                params.put("Authorization", auth);
                return params;
            }
        };

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(application).addToRequestQueue(jsArrayRequest);

    }

    /**
     * @param error - A {@link VolleyError]} error
     * @return - Returns the status code of the received error
     */
    public int handleError(VolleyError error) {

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            return 600;

        } else if (error instanceof AuthFailureError) {
            //Error indicating that there was an Authentication Failure while performing the request
            return 401;

        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            return handleServerError(error);

        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            return 598;

        } else if (error instanceof ParseError) {
            return 601;
        }else{
            return 700;

        }
    }

    /**
     * @param error - A {@link VolleyError]} error
     * @return - Returns the status code of the received error
     */
    public int handleServerError(VolleyError error) {

        try {
            if (error.networkResponse != null){

                String status_code = String.valueOf(error.networkResponse.statusCode);

                if (status_code.equals("503")){
                    return 503;

                }else if (status_code.equals("404")){
                    return 404;

                }else if (status_code.equals("204")){
                    return 204;

                }else{
                    return 502;
                }

            }else{
                return 502;
            }

        }catch(Exception e) {
            return 501;
        }
    }
}
