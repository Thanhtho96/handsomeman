package com.tt.handsomeman.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityRegisterBinding;
import com.tt.handsomeman.ui.customer.CustomerMainScreen;
import com.tt.handsomeman.ui.handyman.SignUpAddPayout;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.RoleName;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import javax.inject.Inject;

public class Register extends BaseAppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Activity register;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        register = this;

        TextView tvRole = binding.textViewRole;

        Integer state = sharedPreferencesUtils.get("state", Integer.class);
        String type = sharedPreferencesUtils.get("type", String.class);

        switch (RoleName.valueOf(type)) {
            case ROLE_HANDYMAN:
                tvRole.setText(getString(R.string.handyman_role));
                if (state.equals(Constants.NOT_ACTIVE_ACCOUNT)) {
                    startActivity(new Intent(Register.this, SignUpAddPayout.class));
                    register = null;
                    finish();
                }
                break;
            case ROLE_CUSTOMER:
                tvRole.setText(getString(R.string.customer_role));
                if (state.equals(Constants.NOT_ACTIVE_ACCOUNT) || state.equals(Constants.STATE_REGISTER_ADDED_PAYOUT)) {
                    startActivity(new Intent(Register.this, CustomerMainScreen.class));
                    register = null;
                    finish();
                }
                break;
        }

        binding.registerBackButton.setOnClickListener(view -> {
            register = null;
            onBackPressed();
        });

        binding.loginFBLinear.setOnClickListener(view -> Toast.makeText(Register.this, "Login Facebook", Toast.LENGTH_LONG).show());

        binding.buttonRegisterLogin.setOnClickListener(view -> startActivity(new Intent(Register.this, Login.class)));

        binding.buttonRegisterSignUp.setOnClickListener(view -> startActivity(new Intent(Register.this, SignUp.class)));
    }
}
