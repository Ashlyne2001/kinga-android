package com.example.kinga.ui.trips.tripIndex;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.kinga.core.NetworkConnection.WebRequestHelpers.GeneralRepository;
import com.example.kinga.core.userUtils.SessionHandler;
import com.example.kinga.core.userUtils.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TripIndexRepository extends GeneralRepository {

    private static final String KEY_EMPTY = "";
    private TripIndexModel mTripIndex;
    private final Application mApplication;

    private final User mCurrentUser;
    private final String mRequestUrl;

    TripIndexRepository(Application application, String requestUrl) {

        mTripIndex = new TripIndexModel();
        mApplication = application;
        mRequestUrl = requestUrl;

        // Get user session
        SessionHandler session = new SessionHandler(mApplication.getApplicationContext());

        // Get current user
        mCurrentUser = session.getUserDetails();

        if (!KEY_EMPTY.equals(mRequestUrl)){
            createModel();
        }
    }

    private MutableLiveData<TripIndexModel> mTripIndexLiveData;

    /**
     * @return - Returns this repository's live data
     */
    public LiveData<TripIndexModel> getReportLiveData() {

        if (mTripIndexLiveData == null) {
            mTripIndexLiveData = new MutableLiveData<>();
            loadData();
        }

        return mTripIndexLiveData;
    }

    /**
     * Inserts new data into this repository's live data
     */
    public void loadData() {
        // Do an asynchronous operation to fetch data
        mTripIndexLiveData.postValue(mTripIndex);
    }

    /**
     * You must call this on a non-UI thread or your app will crash.
     *
     * Inserts new data into this repository's live data
     * @param newStoreIndex - new {@link TripIndexModel} object
     */
    private void insert(TripIndexModel newStoreIndex) {

        // Get current list
        List<TripIndexInnerModel> currentTripIndexInnerModelList =
                mTripIndex.getTripIndexInnerModel();

        // Update current list
        currentTripIndexInnerModelList.addAll(
                newStoreIndex.getTripIndexInnerModel());

        loadData();
    }

    /**
     * Reloads the repository's data by requesting new fresh data from the server
     */
    public void reloadData() {

        // We dispose the current object by initializing a new object
        mTripIndex = new TripIndexModel();

        loadData();
        createModel();
    }

    /**
     * During testing, this method when called, loads the live data with an empty model with
     * a 204 status code
     */
    public void test204Response(){

        mTripIndex = new TripIndexModel();

        mTripIndex.setStatusCode(204);

        loadData();
    }

    /**
     * During testing, this method is used in testing handleError
     */
    public void testHandleError(VolleyError error){

        mTripIndex = new TripIndexModel();

        int statusCode = handleError(error);

        setErrorCode(statusCode);

        loadData();
    }

    /**
     * Fetches data from the server
     */
    private void createModel() {getData(mApplication, mCurrentUser, mRequestUrl);}
    
    /**
     * Updates the repository with new data
     * @param tripIndex - New {@link TripIndexModel} object that will hold the response
     *                     data
     * @param response - A JSONObject holding response from the server
     */
    private void updateModel(TripIndexModel tripIndex, JSONObject response,
                             boolean paginate){

        try {

            // To avoid index model fragmentation, we update the global model with status code
            // and next page
            mTripIndex.setStatusCode(200);
            mTripIndex.setNextPage(response.optString("next"));

            List<TripIndexInnerModel> tripIndexInnerModelList = new ArrayList<>();

            JSONArray arr = response.getJSONArray("results");

            for(int i=0; i<arr.length(); ++i) {

                TripIndexInnerModel tempTripIndexModel = new TripIndexInnerModel();

                tempTripIndexModel.setFromLocationName(arr.getJSONObject(i).get("from_location_name").toString());
                tempTripIndexModel.setStartTime(arr.getJSONObject(i).get("start_time").toString());
                tempTripIndexModel.setCost(arr.getJSONObject(i).get("cost").toString());
                tempTripIndexModel.setRegNo(Long.parseLong(
                        arr.getJSONObject(i).get("reg_no").toString()));

                // Update list
                tripIndexInnerModelList.add(tempTripIndexModel);
            }

            // Update index
            tripIndex.setTripIndexInnerModel(tripIndexInnerModelList);

            // If there are no trip index item, we change status code to 204 (No content found)
            if (tripIndex.getTripIndexInnerModel().size() == 0){
                tripIndex.setStatusCode(204);
            }

            // Only insert during pagination
            if (paginate) {
                insert(tripIndex);
            }

        }catch(Exception e) {
            // Assign error status code into report model
            Log.d("mq-log", e.toString());
            tripIndex.setStatusCode(510);

        }
    }

    /**
     * Feeds the returned response data to the repository's model
     * @param response - Request response data
     */
    public void processResponse(JSONObject response){
        updateModel(mTripIndex, response, false);
    }

    /**
     * Sets the error status code ot the model
     * @param statusCode - Error status code from the resposne
     */
    public void setErrorCode(int statusCode){
        mTripIndex.setStatusCode(statusCode);
    }

    /**
     * Fetches pagination data
     */
    public void fetchPagination(String nextUrl){
        getPaginationData(mApplication, mCurrentUser, nextUrl);
    }

    /**
     * Feeds the returned pagination response data to the repository's model
     * @param response - Request response data
     */
    public void processPaginationResponse(JSONObject response){

        // When inserting new pagination data, we create a temporary TripIndexModel to temporary
        // hold our data
        TripIndexModel paginationIndex = new TripIndexModel();
        updateModel(paginationIndex, response, true);
    }

}
