package com.tt.handsomeman.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.tt.handsomeman.request.AcceptBidRequest;
import com.tt.handsomeman.response.DataBracketResponse;
import com.tt.handsomeman.response.MadeABidNotificationResponse;
import com.tt.handsomeman.response.NotificationResponse;
import com.tt.handsomeman.response.PaidPaymentNotificationResponse;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.service.NotificationService;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotificationViewModel extends BaseViewModel {
    private final NotificationService notificationService;
    private final MutableLiveData<List<NotificationResponse>> listNotificationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataBracketResponse<MadeABidNotificationResponse>> madeABidNotificationResponseMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<StandardResponse> standardResponseMarkReadMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<StandardResponse> standardResponseAcceptBidMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataBracketResponse<PaidPaymentNotificationResponse>> paidPaymentNotificationResponseMutableLiveData = new MutableLiveData<>();
    private final String locale = Constants.language.getValue();
    private final String authorization;

    @Inject
    NotificationViewModel(@NonNull Application application,
                          NotificationService notificationService,
                          SharedPreferencesUtils sharedPreferencesUtils) {
        super(application);
        this.notificationService = notificationService;
        authorization = sharedPreferencesUtils.get("token", String.class);
    }

    public MutableLiveData<List<NotificationResponse>> getListNotificationMutableLiveData() {
        return listNotificationMutableLiveData;
    }

    public MutableLiveData<DataBracketResponse<MadeABidNotificationResponse>> getMadeABidNotificationResponseMutableLiveData() {
        return madeABidNotificationResponseMutableLiveData;
    }

    public MutableLiveData<StandardResponse> getStandardResponseMarkReadMutableLiveData() {
        return standardResponseMarkReadMutableLiveData;
    }

    public MutableLiveData<StandardResponse> getStandardResponseAcceptBidMutableLiveData() {
        return standardResponseAcceptBidMutableLiveData;
    }

    public MutableLiveData<DataBracketResponse<PaidPaymentNotificationResponse>> getPaidPaymentNotificationResponseMutableLiveData() {
        return paidPaymentNotificationResponseMutableLiveData;
    }

    public void fetchNotificationOfAccount(String type) {
        compositeDisposable.add(notificationService.getAllNotification(this.authorization, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.body() != null) {
                                listNotificationMutableLiveData.setValue(response.body().getData());
                            }
                        }, throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void markNotificationAsRead(Integer notificationId) {
        compositeDisposable.add(notificationService.markNotificationRead(locale, this.authorization, notificationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> standardResponseMarkReadMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void fetchMadeBidNotification(Integer jobBidId) {
        compositeDisposable.add(notificationService.viewMadeBid(locale, this.authorization, jobBidId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> madeABidNotificationResponseMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void fetchPaidPaymentNotification(Integer customerTransferId) {
        compositeDisposable.add(notificationService.viewPaidPayment(locale, this.authorization, customerTransferId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> paidPaymentNotificationResponseMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void acceptBid(AcceptBidRequest acceptBidRequest) {
        compositeDisposable.add(notificationService.acceptBid(locale, this.authorization, acceptBidRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> standardResponseAcceptBidMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }
}
