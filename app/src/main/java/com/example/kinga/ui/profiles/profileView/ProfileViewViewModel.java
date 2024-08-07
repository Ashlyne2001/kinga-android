package com.example.kinga.ui.profiles.profileView;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.volley.VolleyError;


/**
 * View Model to keep a reference to the profile repository and
 * an up-to-date profileModel.
 */
public class ProfileViewViewModel extends AndroidViewModel {

    private ProfileViewRepository mRepository;
    private LiveData<ProfileViewModel> profile;

    ProfileViewViewModel(Application application, String requestUrl) {
        super(application);
        mRepository = new ProfileViewRepository(application, requestUrl);
        profile = mRepository.getProfileLiveData();
    }

    LiveData<ProfileViewModel> getProfileLiveData() {
        return profile;
    }

    /**
     * Reloads repository data
     */
    void reloadRepository() {
        mRepository.reloadData();
    }

    /**
     * Notifies the repository that we are testing repository's handle error
     */
    void testHandleError(VolleyError error){
        mRepository.testHandleError(error);
    }

}