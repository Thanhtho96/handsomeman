package com.tt.handsomeman.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.tt.handsomeman.request.ChangePasswordRequest;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.service.UserService;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserViewModel extends BaseViewModel {

    private final UserService userService;
    private final MutableLiveData<StandardResponse> standardResponseMutableLiveData = new MutableLiveData<>();
    private final String locale = Constants.language.getValue();
    private final String authorization;

    @Inject
    UserViewModel(@NonNull Application application,
                  UserService userService,
                  SharedPreferencesUtils sharedPreferencesUtils) {
        super(application);
        this.userService = userService;
        authorization = sharedPreferencesUtils.get("token", String.class);
    }

    public MutableLiveData<StandardResponse> getStandardResponseMutableLiveData() {
        return standardResponseMutableLiveData;
    }

    public void removePayout(Integer payoutId) {
        compositeDisposable.add(userService.removePayout(locale, this.authorization, payoutId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> standardResponseMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        compositeDisposable.add(userService.changePassword(locale, this.authorization, changePasswordRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> standardResponseMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void forgotPassword(String email) {
        compositeDisposable.add(userService.forgetPassword(locale, email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> standardResponseMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }
}
