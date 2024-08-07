package com.example.kinga.ui.trips.tripIndex;



import java.io.Serializable;
import java.util.List;

/**
 * A basic class that holds store data
 *
 * Also this class implements Serializable so that it can be sent from one
 * activity to another
 */
public class TripIndexModel implements Serializable {

    private List<TripIndexInnerModel> mTripIndexInnerModel;
    private int mStatusCode;
    private String mNextPage;

    public void setTripIndexInnerModel(List<TripIndexInnerModel> mTripIndexInnerModel) {
        this.mTripIndexInnerModel = mTripIndexInnerModel;
    }

    public void setStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public void setNextPage(String mNextPage) {
        this.mNextPage = mNextPage;
    }

    public List<TripIndexInnerModel> getTripIndexInnerModel() {
        return mTripIndexInnerModel;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getNextPage() {
        return mNextPage;
    }

}
