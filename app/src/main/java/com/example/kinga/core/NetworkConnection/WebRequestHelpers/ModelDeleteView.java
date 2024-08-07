package com.example.kinga.core.NetworkConnection.WebRequestHelpers;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.kinga.core.Dialogs.displayAlertToast;
import com.example.kinga.core.RedirectResolver;
import com.example.kinga.core.userUtils.SessionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ModelDeleteView {
    private static final String KEY_EMPTY = "";

    /**
     * Helps in handling volley error
     */
    public static void handleError(Context context, Activity activity, SessionHandler session, VolleyError error) {

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            showAlertToast(context, StatusCodeDescriptor.codeMessage(600));

        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            // Every time a user receives an authentication error, delete the user details, log them
            // out and redirect them to the login page
            logoutUserAndRedirectToLogin(context, session);

        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            handleServerError(context, activity, error);

        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request

            showAlertToast(context, StatusCodeDescriptor.codeMessage(598));

        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            showAlertToast(context, StatusCodeDescriptor.codeMessage(601));
        }else{
            showAlertToast(context, StatusCodeDescriptor.codeMessage(700));
        }
    }


    /**
     * Handles Server errors
     */
    public static void handleServerError(Context context, Activity activity, VolleyError error) {

        try {
            if (error.networkResponse != null){

                String status_code = String.valueOf(error.networkResponse.statusCode);

                // Check for server or not found errors
                if (status_code.equals("503")){
                    // Display server maintenance error
                    showAlertToast(context, StatusCodeDescriptor.codeMessage(503));

                    return;

                } else if (status_code.startsWith("5")){
                    // Display server unknown error
                    showAlertToast(context, StatusCodeDescriptor.codeMessage(500));

                    return;

                }else if (status_code.equals("404")){
                    // Display server unknown error
                    showAlertToast(context, StatusCodeDescriptor.codeMessage(404));

                    return;
                }



                // Get response body and check if there was an error
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);

                for(int i = 0; i<data.names().length(); i++){

                    switch (status_code){
                        case "400":
                            showAlertToast(context, StatusCodeDescriptor.codeMessage(400));
                            break;

                        case "429":
                            showAlertToast(context, StatusCodeDescriptor.codeMessage(429));
                            break;

                        default:
                            // Here we put a non http error number so that an Unknown error is raised
                            showAlertToast(context, StatusCodeDescriptor.codeMessage(700));
                    }
                }

            }else{
                // Here we put a non http error number so that an Unknown error is raised
                showAlertToast(context, StatusCodeDescriptor.codeMessage(700));
            }

        } catch (JSONException e) {
            // Here we put a non http error number so that an Unknown error is raised
            showAlertToast(context, StatusCodeDescriptor.codeMessage(700));
        } catch (UnsupportedEncodingException uee_error) {
            // Here we put a non http error number so that an Unknown error is raised
            showAlertToast(context, StatusCodeDescriptor.codeMessage(700));
        }catch(Exception e) {
            // Here we put a non http error number so that an Unknown error is raised
            showAlertToast(context, StatusCodeDescriptor.codeMessage(700));
        }
    }


    private static void showAlertToast(Context context, String error){
        displayAlertToast.showLongDeterminedError(context, error);
    }

    private static void logoutUserAndRedirectToLogin(Context context, SessionHandler session) {
        RedirectResolver.logoutUserAndRedirectToLogin(context, session);
    }

    private static void redirectBackToVisitIndex() {

    }
}