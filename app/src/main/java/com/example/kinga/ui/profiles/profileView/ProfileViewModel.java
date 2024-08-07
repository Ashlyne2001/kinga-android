package com.example.kinga.ui.profiles.profileView;


import com.example.kinga.core.Urls.SharedUrls;

/*
 * A basic class that holds Profile data
 */
public class ProfileViewModel {

    private String mFullName;
    private String mEmail;
    private String mImageUrl;
    private String mJoinDate;
    private String mLastLoginDate;
    private long mPhone;

    private String mLocation;
    private String mCurrency;
    private String mAbout;
    private int mStatusCode;

    public void setFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setJoinDate(String mJoinDate) {
        this.mJoinDate = mJoinDate;
    }

    public void setLastLoginDate(String mLastLoginDate) {
        this.mLastLoginDate = mLastLoginDate;
    }

    public void setPhone(long mPhone) {
        this.mPhone = mPhone;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public void setCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public void setAbout(String mAbout) {
        this.mAbout = mAbout;
    }

    public void setStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getImageUrl() {
        return SharedUrls.getBaseUrl() + mImageUrl;
    }

    public String getJoinDate() {
        return mJoinDate;
    }

    public String getLastLoginDate() {
        return mLastLoginDate;
    }

    public long getPhone() {
        return mPhone;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getAbout() {
        return mAbout;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

}

