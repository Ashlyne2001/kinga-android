package com.example.kinga.ui.profiles.profileView;


import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.VolleyError;
import com.example.kinga.core.NetworkConnection.WebRequestHelpers.GeneralRepository;
import com.example.kinga.core.userUtils.SessionHandler;
import com.example.kinga.core.userUtils.User;

import org.json.JSONObject;

public class ProfileViewRepository extends GeneralRepository {

    private static final String KEY_EMPTY = "";
    private ProfileViewModel mProfileModel;
    private Application mApplication;

    private User mCurrentUser;
    private String mRequestUrl;

    ProfileViewRepository(Application application, String requestUrl) {

        mProfileModel = new ProfileViewModel();
        mApplication = application;

        // Get user session
        SessionHandler session = new SessionHandler(mApplication.getApplicationContext());

        // Get current user
        mCurrentUser = session.getUserDetails();

        mRequestUrl = requestUrl;

        if (!KEY_EMPTY.equals(mRequestUrl)){
            createModel();
        }
    }

    private MutableLiveData<ProfileViewModel> profileLiveData;

    /**
     * @return - Returns this repository's live data
     */
    LiveData<ProfileViewModel> getProfileLiveData() {

        if (profileLiveData == null) {
            profileLiveData = new MutableLiveData<>();
            loadData();
        }

        return profileLiveData;
    }

    /**
     * Inserts new data into this repository's live data
     */
    public void loadData() {
        // Do an asynchronous operation to fetch data
        profileLiveData.postValue(mProfileModel);
    }

    /**
     * Reloads the repository's data by requesting new fresh data from the server
     */
    public void reloadData() {

        // We dispose the current object by initializing a new object
        mProfileModel = new ProfileViewModel();

        loadData();
        createModel();
    }

    /**
     * Fetches data from the server
     */
    private void createModel() {
        getData(mApplication, mCurrentUser, mRequestUrl);
    }

    /**
     * During testing, this method is used in testing handleError
     */
    public void testHandleError(VolleyError error){

        mProfileModel = new ProfileViewModel();

        int statusCode = handleError(error);

        setErrorCode(statusCode);

        loadData();

    }

    /**
     * Updates the repository with new data
     * @param profileModel - New {@link ProfileViewModel} object that will hold the
     * response data
     * @param response - A JSONObject holding response from the server
     */
    private void updateModel(ProfileViewModel profileModel, JSONObject response){

        try {

            // Assign returned values and status code into mProfileModel model
            profileModel.setFullName(response.optString("full_name"));
            profileModel.setEmail(response.optString("email"));
            profileModel.setImageUrl(response.optString("image_url"));
            profileModel.setJoinDate(response.optString("join_date"));
            profileModel.setLastLoginDate(response.optString("last_login_date"));
            profileModel.setPhone(response.optLong("phone"));

            profileModel.setLocation(response.optString("current_location_name"));

            profileModel.setStatusCode(200);

        }catch(Exception e) {
            // Assign error status code into mProfileModel model
            profileModel.setStatusCode(510);

        }
    }

    /**
     * Feeds the returned response data to the repository's model
     * @param response - Request response data
     */
    public void processResponse(JSONObject response){
        updateModel(mProfileModel, response);
    }

    @Override
    public void processPaginationResponse(JSONObject response) {

    }

    /**
     * Sets the error status code ot the model
     * @param statusCode - Error status code from the resposne
     */
    public void setErrorCode(int statusCode){
        mProfileModel.setStatusCode(statusCode);
    }
}
