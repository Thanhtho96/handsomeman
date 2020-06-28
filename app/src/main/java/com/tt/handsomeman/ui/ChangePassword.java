package com.tt.handsomeman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.databinding.ActivityChangePasswordBinding;
import com.tt.handsomeman.request.ChangePasswordRequest;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.ChangePasswordViewModel;
import com.tt.handsomeman.viewmodel.UserViewModel;

import javax.inject.Inject;

public class ChangePassword extends BaseAppCompatActivityWithViewModel<UserViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private EditText edtCurrentPassword, edtNewPassword, edtRePassword;
    private CheckBox cbCurrentPassword, cbNewPassword, cbRePassword;
    private ImageButton ibChangePassword;
    private ChangePasswordViewModel changePasswordViewModel;
    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        changePasswordViewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);

        edtCurrentPassword = binding.editTextCurrentPassword;
        edtNewPassword = binding.editTextNewPassword;
        edtRePassword = binding.editTextVerifyPassword;
        cbCurrentPassword = binding.checkboxVisibleCurrentPassword;
        cbNewPassword = binding.checkboxVisibleNewPassword;
        cbRePassword = binding.checkboxVisibleVerifyPassword;
        ibChangePassword = binding.imageButtonChangePassword;

        binding.imageButtonChangePasswordBack.setOnClickListener(v -> onBackPressed());

        editTextChangeListener(edtCurrentPassword, edtNewPassword, edtRePassword);
        observeEditTextChange(edtCurrentPassword, edtNewPassword, edtRePassword, ibChangePassword);
        cbViewPassword(cbCurrentPassword, cbNewPassword, cbRePassword, edtCurrentPassword, edtNewPassword, edtRePassword);
        ibChangePassword.setOnClickListener(v -> doChangePassword());
    }

    private void doChangePassword() {
        ibChangePassword.setEnabled(false);
        String authorization = sharedPreferencesUtils.get("token", String.class);

        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String rePassword = edtRePassword.getText().toString().trim();

        baseViewModel.changePassword(authorization, new ChangePasswordRequest(currentPassword, newPassword, rePassword));
        baseViewModel.getStandardResponseMutableLiveData().observe(this, standardResponse -> {
            Toast.makeText(this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
            if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                Intent intent = new Intent();
                intent.putExtra("isChangePassword", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void cbViewPassword(CheckBox cbCurrentPassword,
                                CheckBox cbNewPassword,
                                CheckBox cbRePassword,
                                EditText edtCurrentPassword,
                                EditText edtNewPassword,
                                EditText edtRePassword) {
        cbCurrentPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbCurrentPassword.isChecked()) {
                    edtCurrentPassword.setTransformationMethod(null);
                } else {
                    edtCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                edtCurrentPassword.setSelection(edtCurrentPassword.length());
            }
        });

        cbNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbNewPassword.isChecked()) {
                    edtNewPassword.setTransformationMethod(null);
                } else {
                    edtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                edtNewPassword.setSelection(edtNewPassword.length());
            }
        });

        cbRePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbRePassword.isChecked()) {
                    edtRePassword.setTransformationMethod(null);
                } else {
                    edtRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                edtRePassword.setSelection(edtRePassword.length());
            }
        });
    }

    private void editTextChangeListener(EditText edtCurrentPassword,
                                        EditText edtNewPassword,
                                        EditText edtRePassword) {
        TextWatcher textWatcher = new TextWatcher() {
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
                changePasswordViewModel.changePasswordStateChanged(edtCurrentPassword.getText().toString().trim(), edtNewPassword.getText().toString().trim(), edtRePassword.getText().toString().trim());
            }
        };
        edtCurrentPassword.addTextChangedListener(textWatcher);
        edtNewPassword.addTextChangedListener(textWatcher);
        edtRePassword.addTextChangedListener(textWatcher);
    }

    private void observeEditTextChange(EditText edtCurrentPassword,
                                       EditText edtNewPassword,
                                       EditText edtRePassword,
                                       ImageButton ibChangePassword) {
        changePasswordViewModel.getChangePasswordFormStateMutableLiveData().observe(this, changePasswordFormState -> {
            if (changePasswordFormState == null) {
                return;
            }
            ibChangePassword.setEnabled(changePasswordFormState.isDataValid());
            if (changePasswordFormState.getCurrentPasswordError() != null) {
                edtCurrentPassword.setError(getString(changePasswordFormState.getCurrentPasswordError()));
            }
            if (changePasswordFormState.getNewPasswordError() != null) {
                edtNewPassword.setError(getString(changePasswordFormState.getNewPasswordError()));
            }
            if (changePasswordFormState.getRePasswordError() != null) {
                edtRePassword.setError(getString(changePasswordFormState.getRePasswordError()));
            }
        });
    }

}
