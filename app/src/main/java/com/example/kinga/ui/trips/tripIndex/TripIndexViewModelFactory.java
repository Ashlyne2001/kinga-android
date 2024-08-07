package com.example.kinga.ui.trips.tripIndex;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class TripIndexViewModelFactory implements ViewModelProvider.Factory {

    private final Application mApplication;
    private final String mRequestUrl;

    public TripIndexViewModelFactory(Application application, String requestUrl) {
        mApplication = application;
        mRequestUrl = requestUrl;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TripIndexViewModel(mApplication, mRequestUrl);
    }
}
