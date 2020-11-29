package com.tt.handsomeman.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseViewModel extends AndroidViewModel {
    final CompositeDisposable compositeDisposable;

    BaseViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
    }

    public void clearSubscriptions(String name) {
        compositeDisposable.clear();
        super.onCleared();
        Log.d("Clear sub", name + ": Disposed");
    }
}
