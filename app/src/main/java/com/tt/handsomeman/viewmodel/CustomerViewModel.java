package com.tt.handsomeman.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.tt.handsomeman.model.Customer;
import com.tt.handsomeman.request.AddJobRequest;
import com.tt.handsomeman.request.HandymanDetailRequest;
import com.tt.handsomeman.request.MadeTheTransactionRequest;
import com.tt.handsomeman.request.NearbyHandymanRequest;
import com.tt.handsomeman.request.ReviewRequest;
import com.tt.handsomeman.response.CustomerJobDetail;
import com.tt.handsomeman.response.CustomerProfileResponse;
import com.tt.handsomeman.response.CustomerReviewProfile;
import com.tt.handsomeman.response.DataBracketResponse;
import com.tt.handsomeman.response.HandymanDetailResponse;
import com.tt.handsomeman.response.ListCategory;
import com.tt.handsomeman.response.ListCustomerTransfer;
import com.tt.handsomeman.response.MyProjectList;
import com.tt.handsomeman.response.NearbyHandymanResponse;
import com.tt.handsomeman.response.ReviewResponse;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.response.StartScreenCustomer;
import com.tt.handsomeman.response.ViewMadeTransactionResponse;
import com.tt.handsomeman.service.CustomerService;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.LiveEvent;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CustomerViewModel extends BaseViewModel {

    private final CustomerService customerService;
    private final MutableLiveData<StartScreenCustomer> startScreenCustomerMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<NearbyHandymanResponse> nearbyHandymanResponseMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<CustomerReviewProfile> customerReviewProfileMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<HandymanDetailResponse> handymanDetailResponseMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Customer> customerMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ListCategory> listCategoryMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<MyProjectList> myProjectListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<CustomerProfileResponse> customerProfileResponseMutableLiveData = new MutableLiveData<>();
    private final LiveEvent<StandardResponse> standardResponseMutableLiveData = new LiveEvent<>();
    private final MutableLiveData<CustomerJobDetail> customerJobDetailMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataBracketResponse<ViewMadeTransactionResponse>> viewTransactionLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataBracketResponse<ListCustomerTransfer>> listTransferHistoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataBracketResponse<ReviewResponse>> reviewResponseLiveData = new MutableLiveData<>();
    private final String locale = Constants.language.getValue();
    private final String authorization;

    @Inject
    CustomerViewModel(@NonNull Application application,
                      CustomerService customerService,
                      SharedPreferencesUtils sharedPreferencesUtils) {
        super(application);
        this.customerService = customerService;
        this.authorization = sharedPreferencesUtils.get("token", String.class);
    }

    public MutableLiveData<StartScreenCustomer> getStartScreenCustomerMutableLiveData() {
        return startScreenCustomerMutableLiveData;
    }

    public MutableLiveData<NearbyHandymanResponse> getNearbyHandymanResponseMutableLiveData() {
        return nearbyHandymanResponseMutableLiveData;
    }

    public MutableLiveData<ListCategory> getListCategoryMutableLiveData() {
        return listCategoryMutableLiveData;
    }

    public MutableLiveData<CustomerReviewProfile> getCustomerReviewProfileMutableLiveData() {
        return customerReviewProfileMutableLiveData;
    }

    public MutableLiveData<HandymanDetailResponse> getHandymanDetailResponseMutableLiveData() {
        return handymanDetailResponseMutableLiveData;
    }

    public MutableLiveData<MyProjectList> getMyProjectListMutableLiveData() {
        return myProjectListMutableLiveData;
    }

    public MutableLiveData<Customer> getCustomerMutableLiveData() {
        return customerMutableLiveData;
    }

    public MutableLiveData<CustomerProfileResponse> getCustomerProfileResponseMutableLiveData() {
        return customerProfileResponseMutableLiveData;
    }

    public MutableLiveData<DataBracketResponse<ListCustomerTransfer>> getListTransferHistoryLiveData() {
        return listTransferHistoryLiveData;
    }

    public LiveEvent<StandardResponse> getStandardResponseMutableLiveData() {
        return standardResponseMutableLiveData;
    }

    public MutableLiveData<CustomerJobDetail> getCustomerJobDetailMutableLiveData() {
        return customerJobDetailMutableLiveData;
    }

    public MutableLiveData<DataBracketResponse<ViewMadeTransactionResponse>> getViewTransactionLiveData() {
        return viewTransactionLiveData;
    }

    public MutableLiveData<DataBracketResponse<ReviewResponse>> getReviewResponseLiveData() {
        return reviewResponseLiveData;
    }

    public void fetchDataStartScreen(NearbyHandymanRequest nearbyHandymanRequest) {
        compositeDisposable
                .add(customerService.getStartScreen(locale, this.authorization, nearbyHandymanRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((startScreenResponseResponse) -> {
                                    if (startScreenResponseResponse.body() != null) {
                                        startScreenCustomerMutableLiveData.setValue(startScreenResponseResponse.body().getData());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetHandymanByCategory(Integer categoryId,
                                      NearbyHandymanRequest nearbyHandymanRequest) {
        compositeDisposable
                .add(customerService.getHandymanByCateGory(locale, this.authorization, categoryId, nearbyHandymanRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((nearbyHandymanResponseResponse) -> {
                                    if (nearbyHandymanResponseResponse.body() != null) {
                                        nearbyHandymanResponseMutableLiveData.setValue(nearbyHandymanResponseResponse.body().getData());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetHandymanNearby(NearbyHandymanRequest nearbyHandymanRequest) {
        compositeDisposable
                .add(customerService.getHandymanNearby(locale, this.authorization, nearbyHandymanRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((nearbyHandymanResponseResponse) -> {
                                    if (nearbyHandymanResponseResponse.body() != null) {
                                        nearbyHandymanResponseMutableLiveData.setValue(nearbyHandymanResponseResponse.body().getData());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchCustomerReview() {
        compositeDisposable
                .add(customerService.getCustomerReview(locale, this.authorization)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((nearbyHandymanResponseResponse) -> {
                                    if (nearbyHandymanResponseResponse.body() != null) {
                                        customerReviewProfileMutableLiveData.setValue(nearbyHandymanResponseResponse.body().getData());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchHandymanDetail(String token,
                                    HandymanDetailRequest handymanDetailRequest) {
        compositeDisposable
                .add(customerService.getHandymanDetail(locale, token, handymanDetailRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((nearbyHandymanResponseResponse) -> {
                                    if (nearbyHandymanResponseResponse.body() != null) {
                                        handymanDetailResponseMutableLiveData.setValue(nearbyHandymanResponseResponse.body().getData());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchCustomerInfo() {
        compositeDisposable
                .add(customerService.getCustomerInfo(locale, this.authorization)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((nearbyHandymanResponseResponse) -> {
                                    if (nearbyHandymanResponseResponse.body() != null) {
                                        customerProfileResponseMutableLiveData.setValue(nearbyHandymanResponseResponse.body().getData());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchCustomerProfile() {
        compositeDisposable
                .add(customerService.getCustomerProfile(locale, this.authorization)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((nearbyHandymanResponseResponse) -> {
                                    if (nearbyHandymanResponseResponse.body() != null) {
                                        customerProfileResponseMutableLiveData.setValue(nearbyHandymanResponseResponse.body().getData());
                                    }
                                },
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void editCustomerProfile(String customerEditName) {
        compositeDisposable
                .add(customerService.editCustomerProfile(locale, this.authorization, customerEditName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((nearbyHandymanResponseResponse) -> standardResponseMutableLiveData.setValue(nearbyHandymanResponseResponse.body()),
                                throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchCustomerJobDetail(Integer jobId) {
        compositeDisposable.add(customerService.getCustomerJobDetail(locale, this.authorization, jobId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((jobResponse) -> {
                            if (jobResponse.body() != null) {
                                customerJobDetailMutableLiveData.setValue(jobResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchJobsOfCustomer() {
        compositeDisposable.add(customerService.getJobsOfCustomer(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((myProjectListResponse) -> {
                            if (myProjectListResponse.body() != null) {
                                myProjectListMutableLiveData.setValue(myProjectListResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void addNewJob(AddJobRequest addJobRequest) {
        compositeDisposable.add(customerService.addNewJob(locale, this.authorization, addJobRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(standardResponseResponse -> standardResponseMutableLiveData.setValue(standardResponseResponse.body()),
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_LONG).show()));
    }

    public void fetchListCategory() {
        compositeDisposable.add(customerService.getListCategory(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((dataBracketResponseResponse) -> {
                            if (dataBracketResponseResponse.body() != null) {
                                listCategoryMutableLiveData.setValue(dataBracketResponseResponse.body().getData());
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void viewMakeTransaction() {
        compositeDisposable.add(customerService.viewMakeTransaction(locale, this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBracketResponseResponse -> viewTransactionLiveData.setValue(dataBracketResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void makeTheTransaction(MadeTheTransactionRequest madeTheTransactionRequest) {
        compositeDisposable.add(customerService.makeTheTransaction(locale, this.authorization, madeTheTransactionRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(standardResponseResponse -> standardResponseMutableLiveData.setValue(standardResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void fetchTransferHistory() {
        compositeDisposable.add(customerService.viewTransferHistory(this.authorization)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBracketResponseResponse -> listTransferHistoryLiveData.setValue(dataBracketResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void loadReviewWithHandyman(int handymanId) {
        compositeDisposable.add(customerService.loadReviewWithHandyman(this.authorization, handymanId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBracketResponseResponse -> reviewResponseLiveData.setValue(dataBracketResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void reviewHandyman(ReviewRequest reviewRequest) {
        compositeDisposable.add(customerService.reviewHandyman(locale, this.authorization, reviewRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(standardResponseResponse -> standardResponseMutableLiveData.setValue(standardResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    public void updateAvatar(MultipartBody.Part body,
                             RequestBody updateDate) {
        compositeDisposable.add(customerService.updateAvatar(locale, authorization, body, updateDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(standardResponseResponse -> standardResponseMutableLiveData.setValue(standardResponseResponse.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }
}
