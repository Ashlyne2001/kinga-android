package com.example.kinga.ui.accounts;

import static com.example.kinga.core.AppSettings.CUSTOMER_USER_TYPE;
import static com.example.kinga.core.AppSettings.DRIVER_USER_TYPE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.kinga.PrimaryNavigationActivity;
import com.example.kinga.R;
import com.example.kinga.core.Dialogs.MqProgressLoader;
import com.example.kinga.core.Dialogs.displayAlertToast;
import com.example.kinga.core.NetworkConnection.ConnectionHelpers.MyConnectionChangeReceiver;
import com.example.kinga.core.NetworkConnection.VolleyUtils.MySingleton;
import com.example.kinga.core.NetworkConnection.WebRequestHelpers.CleanApiErrorMessages;
import com.example.kinga.core.NetworkConnection.WebRequestHelpers.StatusCodeDescriptor;
import com.example.kinga.core.RedirectResolver;
import com.example.kinga.core.Validators.FieldErrorChecker;
import com.example.kinga.core.Validators.FieldValidator;
import com.example.kinga.core.userUtils.SessionHandler;
import com.example.kinga.core.Urls.SharedUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private static final String KEY_EMPTY = "";

    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtEmail;
    private EditText mEtPhone;
    private EditText mEtLocation;

    private EditText mEtVehicleModelName;
    private EditText mEtVehicleColor;
    private EditText mEtVehicleRegistrationNumber;
    private EditText mEtVehicleYearOfManufacture;
    private EditText mEtPassword1;
    private EditText mEtPassword2;

    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mPhone;
    private String mLocation;

    private String mVehicleModelName;
    private String mVehicleColor;
    private String mVehicleRegistrationNumber;
    private String mVehicleYearOfManufacture;
    private String mPassword1;

    private Spinner mGenderSpinner;
    private int mGenderIndex = 0; // Defaults to 0

    private AlertDialog mDialog;
    private String mSignUpUrl;
    private SessionHandler mSession;

    private BroadcastReceiver mReceiver = null;

    private int signupType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Hide default title bar
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar

        // Get passed intent
        Intent intent = getIntent();
        signupType = intent.getIntExtra("signupType", 0);

        Log.d("mq-log", "signupType: " + signupType);

        // Get user session
        mSession = new SessionHandler(getApplicationContext());

        // Logout user since they are already trying to create another account
        mSession.logoutUser();

        // Get sign up url
        mSignUpUrl = SharedUrls.signupUrl();

        TextView subtitle = findViewById(R.id.sign_up_subtitle);
        if (signupType == CUSTOMER_USER_TYPE) {
            subtitle.setText("Customer Signup");
        }else{
            subtitle.setText("Rider Signup");
        }

        // Set views
        mEtFirstName = findViewById(R.id.sign_up_et_first_name);
        mEtLastName = findViewById(R.id.sign_up_et_last_name);
        mEtEmail = findViewById(R.id.sign_up_et_email);
        mEtPhone = findViewById(R.id.sign_up_et_phone);
        mEtLocation = findViewById(R.id.sign_up_et_location);
        mEtVehicleModelName = findViewById(R.id.sign_up_et_vehicle_model_name);
        mEtVehicleColor = findViewById(R.id.sign_up_et_vehicle_color);
        mEtVehicleRegistrationNumber = findViewById(R.id.sign_up_et_vehicle_registration_number);
        mEtVehicleYearOfManufacture = findViewById(R.id.sign_up_et_vehicle_year_of_manufacture);
        mGenderSpinner = findViewById(R.id.sign_up_gender_spinner);
        mEtPassword1 = findViewById(R.id.sign_up_et_password1);
        mEtPassword2 = findViewById(R.id.sign_up_et_password2);

//        mEtFirstName.setText("Elvin");
//        mEtLastName.setText("Lamos");
//        mEtEmail.setText("email2@gmail.com");
//        mEtPhone.setText("254718371810");
//        mEtLocation.setText("Nair");
//        mEtPassword1.setText("secretpass");
//        mEtPassword2.setText("secretpass");

        if (signupType == DRIVER_USER_TYPE){

            // Make vehicle fields visible
            findViewById(R.id.sign_up_tv_vehicle_model_name_title).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_up_tv_vehicle_color_title).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_up_tv_vehicle_registration_number_title).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_up_tv_vehicle_year_of_manufacture_title).setVisibility(View.VISIBLE);

            mEtVehicleModelName.setVisibility(View.VISIBLE);
            mEtVehicleColor.setVisibility(View.VISIBLE);
            mEtVehicleRegistrationNumber.setVisibility(View.VISIBLE);
            mEtVehicleYearOfManufacture.setVisibility(View.VISIBLE);

//            mEtVehicleModelName.setText("Toyota");
//            mEtVehicleColor.setText("Black");
//            mEtVehicleRegistrationNumber.setText("KCA 123A");
//            mEtVehicleYearOfManufacture.setText("2010");
        }



        // Create a sign up button listener
        Button signup_btn = findViewById(R.id.sign_up_btn);
        signup_btn.setOnClickListener((View v) -> {

            //Retrieve the data entered in the edit texts
            mFirstName = mEtFirstName.getText().toString().trim();
            mLastName = mEtLastName.getText().toString().trim();
            mEmail = mEtEmail.getText().toString().toLowerCase().trim();
            mPhone = mEtPhone.getText().toString().trim();
            mVehicleModelName = mEtVehicleModelName.getText().toString().trim();
            mVehicleColor = mEtVehicleColor.getText().toString().trim();
            mVehicleRegistrationNumber = mEtVehicleRegistrationNumber.getText().toString().trim();
            mVehicleYearOfManufacture = mEtVehicleYearOfManufacture.getText().toString().trim();
            mLocation = mEtLocation.getText().toString().trim();
            mPassword1 = mEtPassword1.getText().toString().trim();

            if (validateInputs()) {
                signUp();
            }

        });

        // Gender spinner
        setGenderSpinner();
    }

    /**
     * Validates user inputs
     * @return - Returns true when all the provided user inputs are valid
     */
    private boolean validateInputs() {

        boolean[] fieldValidators;


        if (signupType == CUSTOMER_USER_TYPE){

            fieldValidators = new boolean[]{
                    FieldValidator.validateFirstNameField(this, mEtFirstName),
                    FieldValidator.validateLastNameField(this, mEtLastName),
                    FieldValidator.validateEmailField(this, mEtEmail, true),
                    FieldValidator.validatePhoneField(this, mEtPhone, true),
                    FieldValidator.validateLocationField(this, mEtLocation),
                    FieldValidator.validatePassword1Field(this, mEtPassword1),
                    FieldValidator.validatePassword2Field(this, mEtPassword2, mPassword1)
            };

        }else{

            fieldValidators = new boolean[]{
                    FieldValidator.validateFirstNameField(this, mEtFirstName),
                    FieldValidator.validateLastNameField(this, mEtLastName),
                    FieldValidator.validateEmailField(this, mEtEmail, true),
                    FieldValidator.validatePhoneField(this, mEtPhone, true),
                    FieldValidator.validateLocationField(this, mEtLocation),
                    FieldValidator.validateVehicleModelNameField(this, mEtVehicleModelName),
                    FieldValidator.validateVehicleColorField(this, mEtVehicleColor),
                    FieldValidator.validateVehicleRegistrationNumberField(this, mEtVehicleRegistrationNumber),
                    FieldValidator.validateVehicleYearOfManufactureField(this, mEtVehicleYearOfManufacture),
                    FieldValidator.validatePassword1Field(this, mEtPassword1),
                    FieldValidator.validatePassword2Field(this, mEtPassword2, mPassword1)
            };

        }
        return FieldErrorChecker.areAllFieldsValid(fieldValidators);
    }

    /**
     * Creates gender's spinner
     */
    private void setGenderSpinner(){

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_create_gender_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(adapter);

        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mGenderIndex = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Connects to the server to sign up the user.
     * Upon success, it stores the returned credentials from the server in the current user's
     * session
     */
    private void signUp() {

        // Display progress dialog while connecting to the server
        mDialog = MqProgressLoader.showLoader(this,
                getResources().getString(R.string.dialog_please_wait_text,
                        "Signing up"));

        JSONObject request = new JSONObject();
        try {

            // Calculate proper gender index
            int genderIndex ;
            if (mGenderIndex == 0){
                genderIndex = -1;
            }else {
                genderIndex = mGenderIndex - 1;
            }

            //Populate the request parameters
            request.put("first_name", mFirstName);
            request.put("last_name", mLastName);
            request.put("email", mEmail);
            request.put("phone", mPhone);

            request.put("current_location_name", mLocation);
            request.put("gender", genderIndex);
            request.put("user_type", signupType);
            request.put("password", mPassword1);

            if (signupType == DRIVER_USER_TYPE){
                request.put("vehicle_model_name", mVehicleModelName);
                request.put("vehicle_color", mVehicleColor);
                request.put("vehicle_registration_number", mVehicleRegistrationNumber);
                request.put("vehicle_year_of_manufacture", mVehicleYearOfManufacture);
            }

        } catch (JSONException e) {
            // If we get an error, show an error message and then stop any further code execution
            showAlertToast(getResources().
                    getString(R.string.error_message_when_parameters_are_wrong));
            return;
        }

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, this.mSignUpUrl, request, response -> {

                    // If we receive a successful response, we retrieve user credentials and store
                    // them in the user's session

                    mDialog.dismiss(); // Dismiss loader

                    // If login is successful start store choosing activity
                    if (LoginUtils.loginUser(getApplicationContext(), mSession, response)){
                        Intent intent = new Intent(this, PrimaryNavigationActivity.class);
                        startActivity(intent);
                        finish();
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

                // Check for server or not found errors
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

                }else if (status_code.equals("423")){
                    // Display no new sign ups message
                    showAlertToast(getResources().getString(R.string.new_not_allowed, "accounts"));
                    return;

                }else if (status_code.equals("429")){
                    // Display too many request error
                    showAlertToast(StatusCodeDescriptor.codeMessage(429));
                    return;
                }

                // Get response body and check if there was an error
                String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject(responseBody);

                Log.d("mq-log", "Error " + data);

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

                        case "first_name":
                            mEtFirstName.setError(result_value);
                            mEtFirstName.requestFocus();
                            break;

                        case "last_name":
                            mEtLastName.setError(result_value);
                            mEtLastName.requestFocus();
                            break;

                        case "email":
                            mEtEmail.setError(result_value);
                            mEtEmail.requestFocus();
                            break;

                        case "phone":
                            mEtPhone.setError(result_value);
                            mEtPhone.requestFocus();
                            break;

                        case "location":
                            mEtLocation.setError(result_value);
                            mEtLocation.requestFocus();
                            break;

                        case "vehicle_model_name":
                            mEtVehicleModelName.setError(result_value);
                            mEtVehicleModelName.requestFocus();
                            break;

                        case "vehicle_color":
                            mEtVehicleColor.setError(result_value);
                            mEtVehicleColor.requestFocus();
                            break;

                        case "vehicle_registration_number":
                            mEtVehicleRegistrationNumber.setError(result_value);
                            mEtVehicleRegistrationNumber.requestFocus();
                            break;

                        case "vehicle_year_of_manufacture":
                            mEtVehicleYearOfManufacture.setError(result_value);
                            mEtVehicleYearOfManufacture.requestFocus();
                            break;

                        case "gender":
                            showAlertToast("Pick a valid gender");
                            break;

                        case "password":
                            mEtPassword1.setError(result_value);
                            mEtPassword1.requestFocus();
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

        } catch (Exception e) {
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

    /**
     * Registers connection checker receiver
     */
    public void activateConnectionBroadcast(){

        // To prevent multiple receivers from being started, we check if the receiver is null
        if (mReceiver == null){

            // Activates receiver
            mReceiver = new MyConnectionChangeReceiver();
            registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    /**
     * Unregisters the internet is back broadcast receiver
     */
    public void deactivateConnectionBroadcast(){

        try{
            // Unregister receiver
            unregisterReceiver(mReceiver);
        }catch (Exception e){
            // Do nothing
        }
    }

    /**
     * Unregisters connection broadcast
     */
    @Override
    public void onResume() {
        super.onResume();

        // Register receiver
        activateConnectionBroadcast();
    }

    /**
     * Unregisters connection broadcast
     */
    @Override
    public void onPause() {
        super.onPause();

        // Unregister receiver
        deactivateConnectionBroadcast();
    }

    /**
     * Unregisters connection broadcast
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister receiver
        deactivateConnectionBroadcast();
    }
}