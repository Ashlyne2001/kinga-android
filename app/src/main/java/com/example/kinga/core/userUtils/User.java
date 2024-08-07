package com.example.kinga.core.userUtils;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.Date;

public class User {
    private String username;
    private String token;
    private int userType;
    private long regNo;
    private boolean isAuthenticated;
    private Date sessionExpiryDate;
    private String destinationId;
    private String currentStoreName;
    private String currentStoreRegNo;
    private JSONArray permissions;
    private String loyaltyValue;

    public User(String username, String token, int userType, long regNo, boolean isAuthenticated, Date sessionExpiryDate, String destinationId, String currentStoreName, String currentStoreRegNo, JSONArray permissions, String loyaltyValue) {
        this.username = username;
        this.token = token;
        this.userType = userType;
        this.regNo = regNo;
        this.isAuthenticated = isAuthenticated;
        this.sessionExpiryDate = sessionExpiryDate;
        this.destinationId = destinationId;
        this.currentStoreName = currentStoreName;
        this.currentStoreRegNo = currentStoreRegNo;
        this.permissions = permissions;
        this.loyaltyValue = loyaltyValue;
    }

    public String getUsername() {
        return username;
    }

    public boolean hasManageItemsPerm() {
        return hasPerm("can_manage_items");
    }

    private boolean hasPerm(String perm) {
        try {
            for (int i = 0; i < permissions.length(); i++) {
                if (permissions.get(i) == perm) return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    public String getToken() {
        return token;
    }

    public int getUserType() {
        return userType;
    }
}

