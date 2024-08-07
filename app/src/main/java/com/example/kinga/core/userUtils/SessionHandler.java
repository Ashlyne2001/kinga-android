package com.example.kinga.core.userUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.kinga.core.NetworkConnection.ConnectionHelpers.ConnectionMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

public class SessionHandler {
    private static final String KEY_EMPTY = "";
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_REG_NO = "reg_no";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_DESTINATION_ID = "destination_id";
    private static final String KEY_CURRENT_STORE_NAME = "store_name";
    private static final String KEY_CURRENT_STORE_REG_NO = "store_reg_no";
    private static final String KEY_PERMISSIONS = "userPermissions";
    private static final String KEY_USER_GENERAL_SETTINGS = "userGeneralSettings";
    private static final String KEY_LOYALTY = "userLoyaltyDetails";

    private final Context mContext;
    private final SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences = null;

    public SessionHandler(Context context) {
        mContext = context;

        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public void loginUser(
            String username,
            String token,
            int userType,
            long regNo) {

        mEditor.putString(KEY_USERNAME, username);
        mEditor.putString(KEY_TOKEN, token);
        mEditor.putInt(KEY_USER_TYPE, userType);
        mEditor.putLong(KEY_REG_NO, regNo);

        // Set user session for the next 7 days
        mEditor.putLong(KEY_EXPIRES, new Date().getTime() + 7 * 24 * 60 * 60 * 1000);
        mEditor.commit();
    }

    public void setUserGeneralSettings(boolean enableShifts, boolean enableOpenTickets, boolean enableLowStockNotifications, boolean enableNegativeStockAlerts) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("enableShifts", enableShifts);
            obj.put("enableOpenTickets", enableOpenTickets);
            obj.put("enableLowStockNotifications", enableLowStockNotifications);
            obj.put("enableNegativeStockAlerts", enableNegativeStockAlerts);
            mEditor.putString(KEY_USER_GENERAL_SETTINGS, obj.toString());
        } catch (Exception e) {
            mEditor.putString(KEY_USER_GENERAL_SETTINGS, KEY_EMPTY);
        }
        mEditor.commit();
    }


    public User getUserDetails() {
        JSONArray permissions;
        try {
            permissions = new JSONArray(mPreferences.getString(KEY_PERMISSIONS, KEY_EMPTY));
        } catch (JSONException e) {
            permissions = new JSONArray();
        }

        return new User(
                mPreferences.getString(KEY_USERNAME, KEY_EMPTY),
                mPreferences.getString(KEY_TOKEN, KEY_EMPTY),
                mPreferences.getInt(KEY_USER_TYPE, 0),
                mPreferences.getLong(KEY_REG_NO, 0),
                isLoggedIn(),
                new Date(mPreferences.getLong(KEY_EXPIRES, 0)),
                mPreferences.getString(KEY_DESTINATION_ID, KEY_EMPTY),
                mPreferences.getString(KEY_CURRENT_STORE_NAME, KEY_EMPTY),
                String.valueOf(mPreferences.getLong(KEY_CURRENT_STORE_REG_NO, 0)),
                permissions,
                mPreferences.getString(KEY_LOYALTY, KEY_EMPTY)
        );
    }

    public boolean isLoggedIn() {
        if (!isLoggedInWithToken()) {
            return false;
        }
        long currentStoreRegNo = mPreferences.getLong(KEY_REG_NO, 0);

        return currentStoreRegNo != 0L;
    }

    private boolean isLoggedInWithToken() {
        Date currentDate = new Date();
        long millis = mPreferences.getLong(KEY_EXPIRES, 0);



        if (millis == 0L) {
            return false;
        }

        // Start network monitoring callback process if one is not running
        ConnectionMonitor.isConnected(mContext);

        Date expiryDate = new Date(millis);

        // Check if the session is expired by comparing the current date and session expiry date
        return currentDate.before(expiryDate);
    }

    public void logoutUser() {
        mEditor.clear();
        mEditor.commit();
    }
}
