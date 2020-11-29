package com.tt.handsomeman.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tt.handsomeman.model.HandymanJobDetail;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.request.HandymanEditRequest;
import com.tt.handsomeman.request.HandymanTransferRequest;
import com.tt.handsomeman.request.JobFilterRequest;
import com.tt.handsomeman.request.NearbyJobRequest;
import com.tt.handsomeman.request.ReviewRequest;
import com.tt.handsomeman.response.DataBracketResponse;
import com.tt.handsomeman.response.HandymanProfileResponse;
import com.tt.handsomeman.response.HandymanReviewProfile;
import com.tt.handsomeman.response.JobDetailProfile;
import com.tt.handsomeman.response.ListCategory;
import com.tt.handsomeman.response.ListPayoutResponse;
import com.tt.handsomeman.response.ListTransferHistory;
import com.tt.handsomeman.response.MyProjectList;
import com.tt.handsomeman.response.ReviewResponse;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.response.StartScreenHandyman;
import com.tt.handsomeman.response.TransactionDetailResponse;
import com.tt.handsomeman.service.HandymanService;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class HandymanViewModel extends BaseViewModel {

    private final HandymanService handymanService;
    private final MutableLiveData<HandymanReviewProfile> handymanReviewProfileMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<HandymanProfileResponse> handymanProfileResponseMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ListCategory> listCategoryMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ListTransferHistory> listTransferHistoryMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ListPayoutResponse> listPayoutResponseMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<StandardResponse> standardResponseMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<MyProjectList> myProjectListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<StartScreenHandyman> screenDataMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Job>> jobMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<HandymanJobDetail> jobDetailMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<JobDetailProfile> jobDetailProfileMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<StandardResponse> standardResponseMarkReadMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataBracketResponse<TransactionDetailResponse>> jobTransactionLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataBracketResponse<ReviewResponse>> reviewResponseLiveData = new MutableLiveData<>();
    private final String locale = Constants.language.getValue();
    private final String authorization;

    @Inject
    HandymanViewModel(@NonNull Application application,
                      HandymanService handymanService,
                      SharedPreferencesUtils sharedPreferencesUtils) {
        super(application);
        this.handymanService = handymanService;
        this.authorization = sharedPreferencesUtils.get("token", String.class);
    }

    public MutableLiveData<HandymanReviewProfile> getHandymanReviewProfileLiveData() {
        return handymanReviewProfileMutableLiveData;
    }

    public MutableLiveData<HandymanProfileResponse> getHandymanProfileResponseMutableLiveData() {
        return handymanProfileResponseMutableLiveData;
    }

    public MutableLiveData<ListCategory> getListCategoryMutableLiveData() {
        return listCategoryMutableLiveData;
    }

    public MutableLiveData<ListPayoutResponse> getListPayoutResponseMutableLiveData() {
        return listPayoutResponseMutableLiveData;
    }

    public MutableLiveData<ListTransferHistory> getListTransferHistoryMutableLiveData() {
        return listTransferHistoryMutableLiveData;
    }

    public MutableLiveData<MyProjectList> getMyProjectListMutableLiveData() {
        return myProjectListMutableLiveData;
    }

    public MutableLiveData<StandardResponse> getStandardResponseMutableLiveData() {
        return standardResponseMutableLiveData;
    }

    public LiveData<StartScreenHandyman> getStartScreenData() {
        return screenDataMutableLiveData;
    }

    public LiveData<List<Job>> getJobLiveData() {
        return jobMutableLiveData;
    }

    public LiveData<HandymanJobDetail> getJobDetailLiveData() {
        return jobDetailMutableLiveData;
    }

    public LiveData<JobDetailProfile> getJobDetailProfileLiveData() {
        return jobDetailProfileMutableLiveData;
    }

    public MutableLiveData<StandardResponse> getStandardResponseMarkReadMutableLiveData() {
        return standardResponseMarkReadMutableLiveData;
    }

    public MutableLiveData<DataBracketResponse<TransactionDetailResponse>> getJobTransactionLiveData() {
        return jobTransactionLiveData;
    }

    public MutableLiveData<DataBracketResponse<ReviewResponse>> getReviewResponseLiveData() {
        return reviewResponseLiveData;
    }

    public void fetchHandymanInfo() {
        compositeDisposable.add(handymanService.getHandymanInfo(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((dataBracketResponseResponse) -> {
                            if (dataBracketResponseResponse.body() != null) {
                                handymanProfileResponseMutableLiveData.setValue(dataBracketResponseResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void fetchHandymanReview() {
        compositeDisposable.add(handymanService.getHandymanReview(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((response) -> {
                    if (response.body() != null) {
                        handymanReviewProfileMutableLiveData.setValue(response.body().getData());
                    }
                }, throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void fetchHandymanProfile() {
        compositeDisposable.add(handymanService.getHandymanProfile(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((dataBracketResponseResponse) -> {
                            if (dataBracketResponseResponse.body() != null) {
                                handymanProfileResponseMutableLiveData.setValue(dataBracketResponseResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void editHandymanProfile(HandymanEditRequest handymanEditRequest) {
        compositeDisposable.add(handymanService.editHandymanProfile(locale, this.authorization, handymanEditRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBracketResponseResponse -> standardResponseMutableLiveData.setValue(dataBracketResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void fetchListCategory() {
        compositeDisposable.add(handymanService.getListCategory(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((dataBracketResponseResponse) -> {
                            if (dataBracketResponseResponse.body() != null) {
                                listCategoryMutableLiveData.setValue(dataBracketResponseResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void viewTransferHistory() {
        compositeDisposable.add(handymanService.viewTransferHistory(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((dataBracketResponseResponse) -> {
                            if (dataBracketResponseResponse.body() != null) {
                                listTransferHistoryMutableLiveData.setValue(dataBracketResponseResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void getListPayoutOfHandyman() {
        compositeDisposable.add(handymanService.getListPayoutOfHandyman(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((dataBracketResponseResponse) -> {
                            if (dataBracketResponseResponse.body() != null) {
                                listPayoutResponseMutableLiveData.setValue(dataBracketResponseResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void transferToBankAccount(HandymanTransferRequest handymanTransferRequest) {
        compositeDisposable.add(handymanService.transferToBank(locale, this.authorization, handymanTransferRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBracketResponseResponse -> standardResponseMutableLiveData.setValue(dataBracketResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void fetchJobsOfHandyman() {
        compositeDisposable.add(handymanService.getJobsOfHandyman(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((myProjectListResponse) -> {
                            if (myProjectListResponse.body() != null) {
                                myProjectListMutableLiveData.setValue(myProjectListResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchDataStartScreen(NearbyJobRequest nearbyJobRequest) {
        compositeDisposable
                .add(handymanService.getStartScreen(locale, this.authorization, nearbyJobRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((startScreenResponseResponse) -> {
                                    if (startScreenResponseResponse.body() != null) {
                                        screenDataMutableLiveData.setValue(startScreenResponseResponse.body().getData());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchJobsWishList() {
        compositeDisposable
                .add(handymanService.getJobWishList(locale, this.authorization)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((jobWishList) -> {
                                    if (jobWishList.body() != null) {
                                        jobMutableLiveData.setValue(jobWishList.body().getData().getJobs());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }


    public void fetchYourLocationData(NearbyJobRequest nearbyJobRequest) {
        compositeDisposable
                .add(handymanService.getJobNearBy(locale, this.authorization, nearbyJobRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((jobResponse) -> {
                                    if (jobResponse.body() != null) {
                                        jobMutableLiveData.setValue(jobResponse.body().getData().getJobs());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchJobsByCategory(Integer categoryId,
                                    NearbyJobRequest nearbyJobRequest) {
        compositeDisposable
                .add(handymanService.getJobByCategory(locale, this.authorization, categoryId, nearbyJobRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((jobResponse) -> {
                                    if (jobResponse.body() != null) {
                                        jobMutableLiveData.setValue(jobResponse.body().getData().getJobs());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchJobsByFilter(JobFilterRequest jobFilterRequest) {
        compositeDisposable
                .add(handymanService.getJobByFilter(locale, this.authorization, jobFilterRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((jobResponse) -> {
                                    if (jobResponse.body() != null) {
                                        jobMutableLiveData.setValue(jobResponse.body().getData().getJobs());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchHandymanJobDetail(Integer jobId) {
        compositeDisposable.add(handymanService.getHandymanJobDetail(locale, this.authorization, jobId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((jobResponse) -> {
                            if (jobResponse.body() != null) {
                                jobDetailMutableLiveData.setValue(jobResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchJobDetailProfile(Integer customerId) {
        compositeDisposable.add(handymanService.getJobDetailProfile(locale, this.authorization, customerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((profileResponse) -> {
                            if (profileResponse.body() != null) {
                                jobDetailProfileMutableLiveData.setValue(profileResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }


    public void addJobBid(RequestBody bid,
                          RequestBody description,
                          List<MultipartBody.Part> files,
                          RequestBody jobId,
                          RequestBody serviceFee,
                          RequestBody bidTime,
                          List<RequestBody> md5List) {
        compositeDisposable.add(handymanService.addJobBid(locale, this.authorization, bid, description, files, jobId, serviceFee, bidTime, md5List)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((response) -> standardResponseMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void fetchJobTransactionDetail(Integer jobId) {
        compositeDisposable.add(handymanService.viewPaymentTransaction(locale, this.authorization, jobId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBracketResponseResponse -> jobTransactionLiveData.setValue(dataBracketResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void markNotificationAsRead(Integer notificationId) {
        compositeDisposable.add(handymanService.markNotificationRead(this.authorization, notificationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> standardResponseMarkReadMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void loadReviewWithCustomer(int customerId) {
        compositeDisposable.add(handymanService.loadReviewWithCustomer(this.authorization, customerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBracketResponseResponse -> reviewResponseLiveData.setValue(dataBracketResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void reviewCustomer(ReviewRequest reviewRequest) {
        compositeDisposable.add(handymanService.reviewCustomer(locale, this.authorization, reviewRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(standardResponseResponse -> standardResponseMutableLiveData.setValue(standardResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void updateAvatar(MultipartBody.Part body,
                             RequestBody updateDate) {
        compositeDisposable.add(handymanService.updateAvatar(locale, this.authorization, body, updateDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(standardResponseResponse -> standardResponseMutableLiveData.setValue(standardResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }
}
