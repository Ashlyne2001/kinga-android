package com.example.kinga.core.Validators;

import java.util.regex.Pattern;

/**
 * An Email format validator for {@link android.widget.EditText}.
 */
public class EmailValidator {
    /**
     * * Email validation pattern.
     * */
    public static final Pattern Email_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
    private boolean mIsValid = false;    public boolean isValid() {
    return mIsValid;
    }
    /**
     * * Validates if the given input is a valid email address.
     * *
     * * @param email        The email to validate.
     * * @return {@code true} if the input is a valid email. {@code false} otherwise.
     * */
    public static boolean isValidEmail(CharSequence email) {

        return email != null && Email_PATTERN.matcher(email).matches();
    }





}
