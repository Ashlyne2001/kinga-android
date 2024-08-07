package com.example.kinga.core.Validators;

import android.content.Context;
import android.widget.EditText;

import com.example.kinga.R;
import com.example.kinga.ui.accounts.SignUpActivity;

public class FieldValidator {

    private static final String KEY_EMPTY = "";

    // Validate first name
    public static boolean validateFirstNameField(Context context, EditText editText){

        return FieldValidator.validateStringFieldLength(context,
                "First name",
                editText,
                ApiModelsMaxLength.sUserFirstNameMaxLength,
                true
        );
    }

    // Validate last name
    public static boolean validateLastNameField(Context context, EditText editText){

        return FieldValidator.validateStringFieldLength(
                context,
                "Last name",
                editText,
                ApiModelsMaxLength.sUserLastNameMaxLength,
                true
        );
    }

    // Validate email
    public static boolean validateEmailField(Context context, EditText editText, boolean isRequired){

        String value = editText.getText().toString().toLowerCase().trim();
        String name = "Email";
        int maxLength = ApiModelsMaxLength.sUserEmailMaxLength;

        if (!isRequired && KEY_EMPTY.equals(value)){
            return true;
        }

        if(KEY_EMPTY.equals(value)){
            editText.setError(context.getResources().getString(R.string.field_cannot_be_empty, "Email"));
            editText.requestFocus();
            return false;
        }

        if(value.length() > maxLength){
            editText.setError(context.getResources().getString(R.string.field_string_too_long,
                    name,
                    maxLength));
            editText.requestFocus();
            return false;
        }


        if (!EmailValidator.isValidEmail(value)){
            editText.setError(context.getResources().getString(R.string.field_email_invalid));
            editText.requestFocus();
            return false;
        }

        return true;
    }

    // Validate phone
    public static boolean validatePhoneField(Context context, EditText editText, boolean isRequired){

        String value = editText.getText().toString().trim();

        if (!isRequired && KEY_EMPTY.equals(value)){
            return true;
        }

        // Phone validation
        if(KEY_EMPTY.equals(value)){
            editText.setError(context.getResources().getString(R.string.field_cannot_be_empty, "Phone"));
            editText.requestFocus();
            return false;
        }

        if(value.length() > ApiModelsMaxLength.sUserPhoneMaxLength){
            editText.setError(context.getResources().getString(R.string.field_phone_too_long));
            editText.requestFocus();
            return false;
        }

        if(value.length() < ApiModelsMaxLength.sUserPhoneMaxLength){
            editText.setError(context.getResources().getString(R.string.field_phone_too_short));
            editText.requestFocus();
            return false;
        }

        return true;

    }

    // Validate password1
    public static boolean validatePassword1Field(Context context, EditText editText){

        String value = editText.getText().toString().trim();
        // Passwords validation
        if(KEY_EMPTY.equals(value)){
            editText.setError(context.getResources().getString(R.string.field_password_empty));
            editText.requestFocus();
            return false;
        }

        if(value.length() < 8){
            editText.setError(context.getResources().getString(R.string.field_password_too_short));
            editText.requestFocus();
            return false;
        }

        if(value.length() > 30){
            editText.setError(context.getResources().getString(R.string.field_password_too_long));
            editText.requestFocus();
            return false;
        }

        return true;
    }

    // Validate password2
    public static boolean validatePassword2Field(Context context, EditText editText, String password1){

        String value = editText.getText().toString().trim();

        if(!value.equals(password1)){
            editText.setError(context.getResources().getString(R.string.field_passwords_mismatch));
            editText.requestFocus();
            return false;
        }

        return true;
    }


    // Validate location
    public static boolean validateLocationField(Context context, EditText editText){

        return FieldValidator.validateStringFieldLength(
                context,
                "Location",
                editText,
                ApiModelsMaxLength.sUserLocationMaxLength,
                true
        );
    }

    
    // Validate general string field
    public static boolean validateStringFieldLength(
            Context context, 
            String name, 
            EditText editText, 
            int maxLength,
            boolean isRequired){

        return validateLength(context, name, editText, maxLength, true, isRequired);
    }

    public static boolean validateLength(
            Context context,
            String name, 
            EditText editText,
            int maxLength, 
            boolean isString,
            boolean isRequired
    ){

        String text;
        if (isString){
            text = context.getResources().getString(R.string.field_string_too_long,
                                                                name,
                                                                maxLength);
        } else{
            text = context.getResources().getString(R.string.field_digit_too_long,
                    name,
                    maxLength);
        }

        String value = editText.getText().toString().trim();

        // Check if we should allow empty
        if (!isRequired && KEY_EMPTY.equals(value)){
            return true;
        }
        
        
        // Check if field is empty
        if(KEY_EMPTY.equals(value)){
            editText.setError(context.getResources().getString(R.string.field_cannot_be_empty, name));
            editText.requestFocus();
            return false;
        }

        // Check field length
        if(value.length() > maxLength){
            editText.setError(text);
            editText.requestFocus();
            return false;
        }

        // If its digit, check if it's not 0
        if(!isString){

            // We use Float so tht we can be able to validate both integers and floats
            if(Float.parseFloat(value) == 0){

                text = context.getResources().getString(R.string.field_digit_cannot_be_zero,
                        name,
                        0);

                editText.setError(text);

                editText.requestFocus();
                return false;
            }
        }

        return true;
    }

    public static boolean validateVehicleModelNameField(Context context, EditText editText) {
        return true;
    }

    public static boolean validateVehicleColorField(Context context, EditText editText) {
        return true;
    }

    public static boolean validateVehicleRegistrationNumberField(Context context, EditText editText) {
        return true;
    }

    public static boolean validateVehicleYearOfManufactureField(Context context, EditText editText) {
        return true;
    }
}
