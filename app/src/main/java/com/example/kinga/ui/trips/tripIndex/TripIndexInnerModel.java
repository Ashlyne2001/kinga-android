package com.example.kinga.ui.trips.tripIndex;

import java.io.Serializable;

public class TripIndexInnerModel implements Serializable {
    private String mDriverName;
    private String mCustomerName;
    private String mFromLocationName;
    private String mDestinationLocationName;
    private String mFromLocationCoordinates;
    private String mDestinationLocationCoordinates;
    private String mStartTime;
    private String mEndTime;

    private String mCost;
    private boolean mIsCompleted;
    private long mRegNo;

    public void setDriverName(String driverName) {
        this.mDriverName = driverName;
    }

    public String getDriverName() {
        return mDriverName;
    }

    public String getCustomerName() {
        return mCustomerName;
    }

    public void setCustomerName(String mCustomerName) {
        this.mCustomerName = mCustomerName;
    }

    public String getFromLocationName() {
        return mFromLocationName;
    }

    public void setFromLocationName(String mFromLocationName) {
        this.mFromLocationName = mFromLocationName;
    }

    public String getDestinationLocationName() {
        return mDestinationLocationName;
    }

    public void setDestinationLocationName(String mDestinationLocationName) {
        this.mDestinationLocationName = mDestinationLocationName;
    }

    public String getFromLocationCoordinates() {
        return mFromLocationCoordinates;
    }

    public void setFromLocationCoordinates(String mFromLocationCoordinates) {
        this.mFromLocationCoordinates = mFromLocationCoordinates;
    }

    public String getDestinationLocationCoordinates() {
        return mDestinationLocationCoordinates;
    }

    public void setDestinationLocationCoordinates(String mDestinationLocationCoordinates) {
        this.mDestinationLocationCoordinates = mDestinationLocationCoordinates;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }

    public boolean isIsCompleted() {
        return mIsCompleted;
    }

    public void setIsCompleted(boolean mIsCompleted) {
        this.mIsCompleted = mIsCompleted;
    }

    public void setCost(String cost) {
        this.mCost = cost;
    }

    public String getCost() {
        return mCost;
    }

    public void setRegNo(long mRegNo) {
        this.mRegNo = mRegNo;
    }

    public long getRegNo() {
        return mRegNo;
    }


}
