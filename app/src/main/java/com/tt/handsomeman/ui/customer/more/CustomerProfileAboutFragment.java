package com.tt.handsomeman.ui.customer.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.FragmentCustomerProfileAboutBinding;
import com.tt.handsomeman.model.Customer;
import com.tt.handsomeman.ui.AvatarOptionDialogFragment;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.ui.MyProfile;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CustomerProfileAboutFragment extends BaseFragment<CustomerViewModel, FragmentCustomerProfileAboutBinding> {

    private static final Integer REQUEST_MY_PROFILE_RESULT_CODE = 77;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private TextView yourName, allProjects, successedProject;
    private boolean isMyProfileEdit = false;
    private ImageView imgAvatar;
    private String authorizationCode, avatarLink = "";
    private long updateDate = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);
        viewBinding = FragmentCustomerProfileAboutBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        yourName = viewBinding.aboutYourName;
        allProjects = viewBinding.aboutAllProjects;
        successedProject = viewBinding.aboutSuccessedProjects;
        imgAvatar = viewBinding.customerAvatar;

        fetchCustomerProfile();

        imgAvatar.setOnClickListener(v -> AvatarOptionDialogFragment.newInstance().show(getChildFragmentManager(), "avatar_option"));
    }

    private void fetchCustomerProfile() {
        authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel.fetchCustomerProfile();
        baseViewModel.getCustomerProfileResponseMutableLiveData().observe(getViewLifecycleOwner(), customerProfileResponse -> {
            Customer customer = customerProfileResponse.getCustomer();

            avatarLink = customerProfileResponse.getAvatar();
            updateDate = customerProfileResponse.getUpdateDate();

            GlideUrl glideUrl = new GlideUrl((customerProfileResponse.getAvatar()),
                    new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

            Glide.with(CustomerProfileAboutFragment.this)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .placeholder(R.drawable.custom_progressbar)
                    .error(R.drawable.logo)
                    .signature(new MediaStoreSignature("", customerProfileResponse.getUpdateDate(), 0))
                    .into(imgAvatar);

            yourName.setText(customer.getName());
            allProjects.setText(String.valueOf(customerProfileResponse.getAllProject()));
            successedProject.setText(String.valueOf(customerProfileResponse.getSuccessedProject()));
        });
    }

    public void updateAvatar(String fileDir) {
        MultipartBody.Part body;

        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
        String sendTime = formatter.format(now.getTime());
        RequestBody updateDate = RequestBody.create(sendTime, MultipartBody.FORM);

        File file = new File(fileDir);
        if (file.exists()) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
            body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            baseViewModel.updateAvatar(body, updateDate);
            baseViewModel.getStandardResponseMutableLiveData().observe(this, standardResponse -> {
                Toast.makeText(getContext(), standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    isMyProfileEdit = true;
                    MyProfile myProfile = (MyProfile) getActivity();
                    myProfile.setMyProfileEditResult(isMyProfileEdit);

                    fetchCustomerProfile();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {

        if (requestCode == REQUEST_MY_PROFILE_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("isMyProfileEdit", false)) {
                fetchCustomerProfile();
                isMyProfileEdit = true;
                MyProfile myProfile = (MyProfile) getActivity();
                myProfile.setMyProfileEditResult(isMyProfileEdit);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public long getUpdateDate() {
        return updateDate;
    }
}
