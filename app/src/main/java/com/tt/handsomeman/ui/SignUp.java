package com.tt.handsomeman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.databinding.ActivitySignUpBinding;
import com.tt.handsomeman.request.UserRegistration;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.service.UserService;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusCodeConstant;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.SignUpViewModel;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends BaseAppCompatActivity {

    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    @Inject
    UserService userService;
    private SignUpViewModel signUpViewModel;
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        final CheckBox cbPassword = binding.checkboxVisibleSignUpPassword;
        final CheckBox cbRePassword = binding.checkboxVisibleSignUpRePassword;
        final EditText edtPassword = binding.editTextSignUpPassword;
        final EditText edtRePassword = binding.editTextSignUpRePassword;
        final EditText edtName = binding.editTextSignUpYourName;
        final EditText edtMail = binding.editTextSignUpYourMail;
        final Button btnSignUp = binding.buttonSignUp;
        final ProgressBar pgSignUp = binding.progressBarSignUp;

        HandymanApp.getComponent().inject(this);

        binding.signUpBackButton.setOnClickListener(view -> onBackPressed());

        observeSignUpState(edtPassword, edtRePassword, edtName, edtMail, btnSignUp);

        edtChangedListener(edtPassword, edtRePassword, edtName, edtMail);

        cbViewPassword(cbPassword, cbRePassword, edtPassword, edtRePassword);

        doSignUp(edtPassword, edtRePassword, edtName, edtMail, btnSignUp, pgSignUp);
    }

    private void doSignUp(EditText edtPassword,
                          EditText edtRePassword,
                          EditText edtName,
                          EditText edtMail,
                          Button btnSignUp,
                          ProgressBar pgSignUp) {
        String type = sharedPreferencesUtils.get("type", String.class);

        btnSignUp.setOnClickListener(view -> {
            pgSignUp.setVisibility(View.VISIBLE);
            btnSignUp.setEnabled(false);
            String name = edtName.getText().toString().trim();
            String mail = edtMail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String rePassword = edtRePassword.getText().toString().trim();

            userService.doSignUp(type, new UserRegistration(name, mail, password, rePassword)).enqueue(new Callback<StandardResponse>() {
                @Override
                public void onResponse(@NonNull Call<StandardResponse> call,
                                       @NonNull Response<StandardResponse> response) {
                    if (response.body().getStatus().equals(StatusConstant.OK) && response.body().getStatusCode().equals(StatusCodeConstant.CREATED)) {
                        pgSignUp.setVisibility(View.INVISIBLE);
                        Toast.makeText(SignUp.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignUp.this, Login.class));
                        finish();
                    } else {
                        pgSignUp.setVisibility(View.INVISIBLE);
                        btnSignUp.setEnabled(true);
                        Toast.makeText(SignUp.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StandardResponse> call,
                                      @NonNull Throwable t) {
                    pgSignUp.setVisibility(View.INVISIBLE);
                    btnSignUp.setEnabled(true);
                    Toast.makeText(SignUp.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void cbViewPassword(CheckBox cbPassword,
                                CheckBox cbRePassword,
                                EditText edtPassword,
                                EditText edtRePassword) {
        cbPassword.setOnClickListener(view -> {
            if (cbPassword.isChecked()) {
                edtPassword.setTransformationMethod(null);
            } else {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            edtPassword.setSelection(edtPassword.length());
        });

        cbRePassword.setOnClickListener(view -> {
            if (cbRePassword.isChecked()) {
                edtRePassword.setTransformationMethod(null);
            } else {
                edtRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            edtRePassword.setSelection(edtRePassword.length());
        });
    }

    private void edtChangedListener(EditText edtPassword,
                                    EditText edtRePassword,
                                    EditText edtName,
                                    EditText edtMail) {
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                signUpViewModel.signUpDateChanged(edtName.getText().toString().trim(), edtMail.getText().toString().trim(),
                        edtPassword.getText().toString().trim(), edtRePassword.getText().toString().trim());
            }
        };

        edtName.addTextChangedListener(afterTextChangedListener);
        edtMail.addTextChangedListener(afterTextChangedListener);
        edtPassword.addTextChangedListener(afterTextChangedListener);
        edtRePassword.addTextChangedListener(afterTextChangedListener);
    }

    private void observeSignUpState(EditText edtPassword,
                                    EditText edtRePassword,
                                    EditText edtName,
                                    EditText edtMail,
                                    Button btnSignUp) {
        signUpViewModel.getSignUpFormState().observe(this, signUpFormState -> {
            if (signUpFormState == null) {
                return;
            }
            btnSignUp.setEnabled(signUpFormState.isDataValid());
            if (signUpFormState.getNameError() != null) {
                edtName.setError(getString(signUpFormState.getNameError()));
            }
            if (signUpFormState.getMailError() != null) {
                edtMail.setError(getString(signUpFormState.getMailError()));
            }
            if (signUpFormState.getPasswordError() != null) {
                edtPassword.setError(getString(signUpFormState.getPasswordError()));
            }
            if (signUpFormState.getRePasswordError() != null) {
                edtRePassword.setError(getString(signUpFormState.getRePasswordError()));
            }
        });
    }
}
