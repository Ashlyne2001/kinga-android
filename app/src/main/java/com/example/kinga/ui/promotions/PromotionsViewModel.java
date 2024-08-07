package com.example.kinga.ui.promotions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PromotionsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PromotionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This view will have promotions information");
    }

    public LiveData<String> getText() {
        return mText;
    }
}