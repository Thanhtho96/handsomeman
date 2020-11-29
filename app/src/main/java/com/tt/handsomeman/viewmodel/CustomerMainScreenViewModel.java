package com.tt.handsomeman.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CustomerMainScreenViewModel extends ViewModel {
    private final MutableLiveData<Integer> successfulJob = new MutableLiveData<>();

    public MutableLiveData<Integer> getSuccessfulJob() {
        return successfulJob;
    }
}
