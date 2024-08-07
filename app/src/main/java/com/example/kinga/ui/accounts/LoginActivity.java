package com.example.kinga.ui.accounts;

import static com.example.kinga.core.AppSettings.CUSTOMER_USER_TYPE;
import static com.example.kinga.core.AppSettings.DRIVER_USER_TYPE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kinga.PrimaryNavigationActivity;
import com.example.kinga.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.kinga.core.Dialogs.MqProgressLoader;
import com.example.kinga.core.Dialogs.displayAlertToast;
import com.example.kinga.core.AppSettings;
import com.example.kinga.core.NetworkConnection.VolleyUtils.MySingleton;
import com.example.kinga.core.NetworkConnection.WebRequestHelpers.CleanApiErrorMessages;
import com.example.kinga.core.NetworkConnection.WebRequestHelpers.StatusCodeDescriptor;
import com.example.kinga.core.PermissionUtils;
import com.example.kinga.core.RedirectResolver;
import com.example.kinga.core.Urls.SharedUrls;
import com.example.kinga.core.userUtils.SessionHandler;
import com.example.kinga.core.Validators.FieldValidator;

public class LoginActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver = null;
    private static final String KEY_EMPTY = "";

    private EditText mEtEmail;
    private EditText mEtPassword;

    private String mLoginEmail;
    private String mLoginPassword;
    private String mLoginUrl;
    private SessionHandler mSession;

    private AlertDialog mDialog;
    ActivityResultLauncher<Intent> mActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_login);

        // Hide default title bar
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar

        // Get user session
        mSession = new SessionHandler(getApplicationContext());

        // Get login url
        mLoginUrl = SharedUrls.loginUrl();


        // Set views
        mEtEmail = findViewById(R.id.login_email_et);
        mEtPassword = findViewById(R.id.login_password_et);

        Button login_btn = findViewById(R.id.login_btn);
        Button login_sign_up_customer_btn = findViewById(R.id.sign_customer_btn);
        Button login_sign_up_rider_btn = findViewById(R.id.sign_rider_btn);

        if (!AppSettings.PRODUCTION){
            // Todo Remember to remove this
            //mEtEmail.setText("jack@gmail.com");
            //mEtPassword.setText("secretpass");

//            mEtEmail.setText("driver1@gmail.com");
//            mEtPassword.setText("secretpass");
        }

        login_btn.setOnClickListener((View v) -> {
            //Retrieve the data entered in the edit texts
            mLoginEmail = mEtEmail.getText().toString().toLowerCase().trim();
            mLoginPassword = mEtPassword.getText().toString().trim();
            if (validateInputs()){
                loginUser();
            }
        });

        login_sign_up_customer_btn.setOnClickListener((View v) -> navigateToSignUpActivity(CUSTOMER_USER_TYPE));
        login_sign_up_rider_btn.setOnClickListener((View v) -> navigateToSignUpActivity(DRIVER_USER_TYPE));

        // Requests the required permissions
        new PermissionUtils(this).checkPermissions();
    }

    private void navigateToSignUpActivity(int signupType) {
        Intent signup_intent = new Intent(LoginActivity.this, SignUpActivity.class);
        signup_intent.putExtra("signupType", signupType);
        startActivity(signup_intent);
        finish();
    }

    /**
     * Validates user inputs
     * @return - Returns true when all the provided user inputs are valid
     */
    private boolean validateInputs() {

        boolean validity;

        // Email validation
        validity = FieldValidator.validateEmailField(this, mEtEmail, true);
        if (!validity) return false;

        // Check if password is not empty
        if(KEY_EMPTY.equals(mLoginPassword)){
            mEtPassword.setError(getResources().getString(
                    R.string.field_password_empty));
            mEtPassword.requestFocus();
            return false;
        }

        return true;
    }


    /**
     * Connects to the server to login the user.
     * Upon success from the server, it stores the returned credentials from the server in the
     * current user's session
     */
    public void loginUser() {

        // Display progress dialog while connecting to the server
        mDialog = MqProgressLoader.showLoader(this,
                getResources().getString(R.string.login_dialog_text));

        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("username", mLoginEmail);
            request.put("password", mLoginPassword);

        } catch (JSONException e) {
            // If we get an error, show an error message and then stop any further code execution
            showAlertToast(getResources().
                    getString(R.string.error_message_when_parameters_are_wrong));
            return;
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, this.mLoginUrl, request, response -> {

                    // If we receive a successful response, we retrieve user credentials and store
                    // them in the user's session

                    Log.d("mq-log", "Logged in successfull");

                    mDialog.dismiss(); // Dismiss loader

                    // If login is successful start store choosing activity
                    if (LoginUtils.loginUser(getApplicationContext(), mSession, response)){

                        Intent i= new Intent(LoginActivity.this, PrimaryNavigationActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        // If login is not successful, we display an error message
                        showAlertToast(getResources().getString(R.string.error_message_when_parameters_are_wrong));
                    }

                }, error -> {
                    // In case of a error, at this point is where we handle the error

                    mDialog.dismiss(); // Dismiss loader

                    handleError(error);

                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);

    }

    /**
     * Helps in handling volley error
     * This method is public since it's called directly during testing to test error handling
     * mechanisms
     */
    public void handleError(VolleyError error) {


        Log.d("mq-log", "Status ");
        Log.d("mq-log", error.toString());

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            showAlertToast(StatusCodeDescriptor.codeMessage(600));

        } else if (error instanceof AuthFailureError) {
            //Error indicating that there was an Authentication Failure while performing the request
            showAlertToast(StatusCodeDescriptor.codeMessage(401));

        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            showAlertToast(StatusCodeDescriptor.codeMessage(598));

        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            showAlertToast(StatusCodeDescriptor.codeMessage(601));

        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            handleServerError(error);

        }else{
            // Indicates unknown error
            showAlertToast(StatusCodeDescriptor.codeMessage(700));
        }
    }

    /**
     * Handles Server errors
     */
    private void handleServerError(VolleyError error) {

        try {
            if (error.networkResponse != null){

                String status_code = String.valueOf(error.networkResponse.statusCode);

                if (status_code.equals("503")){
                    // Display server maintenance error
                    showAlertToast(StatusCodeDescriptor.codeMessage(503));
                    return;

                } else if (status_code.startsWith("5")){
                    // Display server unknown error
                    showAlertToast(StatusCodeDescriptor.codeMessage(500));
                    return;

                }else if (status_code.equals("404")){
                    // Display not found error
                    showAlertToast(StatusCodeDescriptor.codeMessage(404));
                    return;

                }else if (status_code.equals("412")){
                    // Display not found error
//                    showAlertDialog();
                    return;

                }else if (status_code.equals("429")){
                    // Display too many request error
                    showAlertToast(StatusCodeDescriptor.codeMessage(429));
                    return;
                }

                // Get response body and check if there was an error
                String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject(responseBody);

                String result_key;
                String result_value;

                for(int i = 0; i< Objects.requireNonNull(data.names()).length(); i++){

                    result_key = Objects.requireNonNull(data.names()).getString(i);
                    result_value = data.get(Objects.requireNonNull(data.names()).getString(i)).toString();


                    // Only clean result_value if it's not empty:
                    if (!KEY_EMPTY.equals(result_value)){
                        result_value = CleanApiErrorMessages.removeBrackets(result_value);
                    }

                    switch (result_key){

                        case "username":
                            mEtEmail.setError(result_value);
                            mEtEmail.requestFocus();
                            break;

                        case "password":
                            mEtPassword.setError(result_value);
                            mEtPassword.requestFocus();
                            break;

                        case "non_field_errors":
                            showAlertToast(result_value);
                            break;

                        default:

                            if (status_code.equals("400")){
                                // Display bad request error
                                showAlertToast(StatusCodeDescriptor.codeMessage(400));

                            }else{
                                // Display unknown error
                                showAlertToast(StatusCodeDescriptor.codeMessage(700));
                            }
                    }
                }

            }else{
                // Display unknown error
                showAlertToast(StatusCodeDescriptor.codeMessage(700));
            }

        } catch (Exception uee_error) {
            // Display unknown error
            showAlertToast(StatusCodeDescriptor.codeMessage(700));
        }
    }

    /**
     * Displays a dialog with the an error message
     * @param error - The error message to be displayed in the dialog
     */
    private void showAlertToast(String error){
        displayAlertToast.showLongDeterminedError(this, error);
    }
}