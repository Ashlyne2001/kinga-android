package com.example.kinga.ui.trips.tripIndex;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.volley.VolleyError;

public class TripIndexViewModel extends AndroidViewModel {

    private final TripIndexRepository mRepository;
    private final LiveData<TripIndexModel> mStoreIndex;

    TripIndexViewModel(Application application, String requestUrl) {
        super(application);
        mRepository = new TripIndexRepository(application, requestUrl);
        mStoreIndex = mRepository.getReportLiveData();
    }

    LiveData<TripIndexModel> getTripIndexLiveData() {
        return mStoreIndex;
    }

    public void fetchPagination(String nextUrl){
        mRepository.fetchPagination(nextUrl);
    }

    /**
     * Reloads repository data
     */
    public void reloadRepository() {
        mRepository.reloadData();
    }

    /**
     * Notifies the repository that we are testing 204 response
     */
    void test204Response(){
        mRepository.test204Response();
    }

    /**
     * Notifies the repository that we are testing repository's handle error
     */
    void testHandleError(VolleyError error){
        mRepository.testHandleError(error);
    }
}
