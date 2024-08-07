package com.example.kinga.ui.accounts;

import android.content.Context;
import android.util.Log;
import com.example.kinga.R;
import com.example.kinga.core.Dialogs.displayAlertToast;
import com.example.kinga.core.userUtils.SessionHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
public class LoginUtils {

    public static boolean loginUser(Context context, SessionHandler session, JSONObject response) {
        String responseName = response.optString("name");
        String responseToken = response.optString("token");
        String responseUserType = response.optString("user_type");
        String responseRegNo = response.optString("reg_no");
        int userType;
        long userRegNo;

        try {

            userType = Integer.parseInt(responseUserType);
            userRegNo = Long.parseLong(responseRegNo);

        } catch (Exception e) {
            // If we get an error, show an error message and then stop any further
            // code execution
            displayAlertToast.showLongDeterminedError(
                    context,
                    context.getResources().getString(R.string.error_message_when_parameters_are_wrong)
            );
            e.getStackTrace();
            return false;
        }
        if (responseToken.length() > 0) {
            // Login user
            session.loginUser(
                    responseName,
                    responseToken,
                    userType,
                    userRegNo
            );
            // Log that user has been logged in
            Log.d("mq-log", "User has been logged in");
            return true;
        }
        return false;
    }
}
