package com.tt.handsomeman.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityForgotPasswordBinding;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.UserViewModel;

import javax.inject.Inject;

public class ForgotPassword extends BaseAppCompatActivityWithViewModel<UserViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);

        final EditText edtForgotPassword = binding.editTextForgotPasswordYourMail;
        final Button btnForgotPassword = binding.buttonSendForgotPassword;

        binding.forgotPasswordBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnForgotPassword.setOnClickListener(v -> {
            baseViewModel.forgotPassword(edtForgotPassword.getText().toString().trim());
            baseViewModel.getStandardResponseMutableLiveData().observe(this, new Observer<StandardResponse>() {
                @Override
                public void onChanged(StandardResponse standardResponse) {
                    Toast.makeText(ForgotPassword.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                        onBackPressed();
                    }
                }
            });
        });

        edtForgotPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i,
                                          int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence,
                                      int i,
                                      int i1,
                                      int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(edtForgotPassword.getText().toString()) || !(Patterns.EMAIL_ADDRESS.matcher(edtForgotPassword.getText().toString()).matches())) {
                    btnForgotPassword.setEnabled(false);
                    edtForgotPassword.setError(getResources().getString(R.string.not_valid_mail));
                } else {
                    btnForgotPassword.setEnabled(true);
                }
            }
        });
    }
}
