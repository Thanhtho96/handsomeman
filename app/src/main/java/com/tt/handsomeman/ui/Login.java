package com.tt.handsomeman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityLoginBinding;
import com.tt.handsomeman.request.UserLogin;
import com.tt.handsomeman.response.DataBracketResponse;
import com.tt.handsomeman.response.TokenState;
import com.tt.handsomeman.service.UserService;
import com.tt.handsomeman.ui.customer.CustomerMainScreen;
import com.tt.handsomeman.ui.handyman.HandyManMainScreen;
import com.tt.handsomeman.ui.handyman.SignUpAddPayout;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.RoleName;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusCodeConstant;
import com.tt.handsomeman.util.StatusConstant;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends BaseAppCompatActivity {
    @Inject
    UserService userService;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private CheckBox cbVisiblePassword;
    private EditText edtMail, edtPassword;
    private Button btLogin, btForgot;
    private ProgressBar pgLogin;
    private boolean mailValidate = false;
    private boolean passwordValidate = false;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cbVisiblePassword = binding.checkboxLoginVisiblePassword;
        edtMail = binding.editTextLoginYourMail;
        edtPassword = binding.editTextLoginPassword;
        btLogin = binding.buttonLogin;
        btForgot = binding.buttonForgotPassword;
        pgLogin = binding.progressBarLogin;

        HandymanApp.getComponent().inject(this);

        binding.loginBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        edtChangedListener();

        doLogin();

        doForgotPassword();

        viewPassword();
    }

    private void viewPassword() {
        cbVisiblePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbVisiblePassword.isChecked()) {
                    edtPassword.setTransformationMethod(null);
                } else {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                edtPassword.setSelection(edtPassword.length());
            }
        });
    }

    private void doForgotPassword() {
        btForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });
    }

    private void doLogin() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                pgLogin.setVisibility(View.VISIBLE);
                btLogin.setEnabled(false);
                String type = sharedPreferencesUtils.get("type", String.class);
                String mail = edtMail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                userService.doLogin(new UserLogin(mail, password), type).enqueue(new Callback<DataBracketResponse<TokenState>>() {
                    @Override
                    public void onResponse(Call<DataBracketResponse<TokenState>> call,
                                           Response<DataBracketResponse<TokenState>> response) {
                        if (response.body().getStatus().equals(StatusConstant.OK) && response.body().getStatusCode().equals(StatusCodeConstant.OK)) {
                            String token = response.body().getData().getToken();
                            Integer state = response.body().getData().getState();
                            String userId = response.body().getData().getUserId();

                            pgLogin.setVisibility(View.INVISIBLE);

                            sharedPreferencesUtils.put("token", token);
                            sharedPreferencesUtils.put("state", state);
                            sharedPreferencesUtils.put("userId", userId);

                            switch (RoleName.valueOf(type)) {
                                case ROLE_HANDYMAN:
                                    if (state.equals(Constants.NOT_ACTIVE_ACCOUNT)) {
                                        startActivity(new Intent(Login.this, SignUpAddPayout.class));
                                        if (Register.register != null) {
                                            Register.register.finish();
                                            Register.register = null;
                                        }
                                        if (Start.start != null) {
                                            Start.start.finish();
                                            Start.start = null;
                                        }
                                        finish();
                                    } else if (state.equals(Constants.STATE_REGISTER_ADDED_PAYOUT)) {
                                        startActivity(new Intent(Login.this, HandyManMainScreen.class));
                                        if (Register.register != null) {
                                            Register.register.finish();
                                            Register.register = null;
                                        }
                                        if (Start.start != null) {
                                            Start.start.finish();
                                            Start.start = null;
                                        }
                                        finish();
                                    }
                                    break;
                                case ROLE_CUSTOMER:
                                    if (state.equals(Constants.NOT_ACTIVE_ACCOUNT) || state.equals(Constants.STATE_REGISTER_ADDED_PAYOUT)) {
                                        startActivity(new Intent(Login.this, CustomerMainScreen.class));
                                        if (Register.register != null) {
                                            Register.register.finish();
                                            Register.register = null;
                                        }
                                        if (Start.start != null) {
                                            Start.start.finish();
                                            Start.start = null;
                                        }
                                        finish();
                                    }
                                    break;
                            }
                        } else {
                            pgLogin.setVisibility(View.INVISIBLE);
                            btLogin.setEnabled(true);
                            Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DataBracketResponse<TokenState>> call,
                                          Throwable t) {
                        pgLogin.setVisibility(View.INVISIBLE);
                        btLogin.setEnabled(true);
                        Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void edtChangedListener() {
        edtMail.addTextChangedListener(new TextWatcher() {
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
                String mail = edtMail.getText().toString().trim();
                if (!(Patterns.EMAIL_ADDRESS.matcher(mail).matches()) || TextUtils.isEmpty(mail)) {
                    mailValidate = false;
                    btLogin.setEnabled(false);
                    edtMail.setError(getResources().getString(R.string.not_valid_mail));
                } else {
                    mailValidate = true;
                }

                if (mailValidate && passwordValidate) {
                    btLogin.setEnabled(true);
                }
            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
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
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password) || password.length() < 6) {
                    passwordValidate = false;
                    btLogin.setEnabled(false);
                    edtPassword.setError(getResources().getString(R.string.not_valid_password));
                } else {
                    passwordValidate = true;
                }

                if (mailValidate && passwordValidate) {
                    btLogin.setEnabled(true);
                }
            }
        });
    }
}
