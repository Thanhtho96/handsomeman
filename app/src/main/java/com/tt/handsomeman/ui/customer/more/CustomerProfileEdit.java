package com.tt.handsomeman.ui.customer.more;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityCustomerProfileEditBinding;
import com.tt.handsomeman.model.Customer;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import javax.inject.Inject;

public class CustomerProfileEdit extends BaseAppCompatActivityWithViewModel<CustomerViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private EditText yourNameEdit;
    private ImageButton ibEdit;
    private boolean isEdit = false;
    private ActivityCustomerProfileEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);

        yourNameEdit = binding.editTextYourNameMyProfileEdit;
        ibEdit = binding.imageButtonCheckEdit;

        goBack();
        editTextYourNameListener(yourNameEdit);
        fetchCustomerProfile();
        doEdit();
    }

    private void doEdit() {
        ibEdit.setOnClickListener(v -> {
            String yourNameEditValue = yourNameEdit.getText().toString();

            baseViewModel.editCustomerProfile(yourNameEditValue);
            baseViewModel.getStandardResponseMutableLiveData().observe(CustomerProfileEdit.this, standardResponse -> {
                Toast.makeText(CustomerProfileEdit.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    isEdit = true;
                    Intent intent = new Intent();
                    intent.putExtra("isMyProfileEdit", isEdit);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        });
    }

    private void goBack() {
        binding.myProfileEditBackButton.setOnClickListener(v -> onBackPressed());
    }

    private void fetchCustomerProfile() {
        String authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel.fetchCustomerProfile();
        baseViewModel.getCustomerProfileResponseMutableLiveData().observe(this, customerProfileResponse -> {
            Customer customer = customerProfileResponse.getCustomer();

            GlideUrl glideUrl = new GlideUrl((customerProfileResponse.getAvatar()),
                    new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

            Glide.with(CustomerProfileEdit.this)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .placeholder(R.drawable.custom_progressbar)
                    .error(R.drawable.logo)
                    .signature(new MediaStoreSignature("", customerProfileResponse.getUpdateDate(), 0))
                    .into(binding.accountAvatar);

            yourNameEdit.setText(customer.getName());
        });
    }

    private void editTextYourNameListener(EditText yourNameEdit) {
        yourNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String yourName = yourNameEdit.getText().toString().trim();
                if (TextUtils.isEmpty(yourName) || yourName.length() <= 4) {
                    yourNameEdit.setError(getString(R.string.not_valid_name));
                    ibEdit.setEnabled(false);
                } else {
                    ibEdit.setEnabled(true);
                }
            }
        });
    }
}
