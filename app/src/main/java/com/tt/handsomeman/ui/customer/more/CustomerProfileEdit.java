package com.tt.handsomeman.ui.customer.more;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityCustomerProfileEditBinding;
import com.tt.handsomeman.model.Customer;
import com.tt.handsomeman.response.CustomerProfileResponse;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivity;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import javax.inject.Inject;

public class CustomerProfileEdit extends BaseAppCompatActivity<CustomerViewModel> {

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
        ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yourNameEditValue = yourNameEdit.getText().toString();
                String authorizationCode = sharedPreferencesUtils.get("token", String.class);

                baseViewModel.editCustomerProfile(authorizationCode, yourNameEditValue);
                baseViewModel.getStandardResponseMutableLiveData().observe(CustomerProfileEdit.this, new Observer<StandardResponse>() {
                    @Override
                    public void onChanged(StandardResponse standardResponse) {
                        Toast.makeText(CustomerProfileEdit.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                            isEdit = true;
                            Intent intent = new Intent();
                            intent.putExtra("isMyProfileEdit", isEdit);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void goBack() {
        binding.myProfileEditBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fetchCustomerProfile() {
        String authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel.fetchCustomerProfile(authorizationCode);
        baseViewModel.getCustomerProfileResponseMutableLiveData().observe(this, new Observer<CustomerProfileResponse>() {
            @Override
            public void onChanged(CustomerProfileResponse customerProfileResponse) {
                Customer customer = customerProfileResponse.getCustomer();

                yourNameEdit.setText(customer.getName());
            }
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
