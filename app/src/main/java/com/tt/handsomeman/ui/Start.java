package com.tt.handsomeman.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityStartBinding;
import com.tt.handsomeman.util.RoleName;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import javax.inject.Inject;

public class Start extends BaseAppCompatActivity implements View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    public static Activity start;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ActivityStartBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        start = this;
        HandymanApp.getComponent().inject(this);

        binding.continueHandyman.setOnClickListener(this);
        binding.continueCustomer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continue_handyman:
                sharedPreferencesUtils.put("type", RoleName.ROLE_HANDYMAN.name());
                startActivity(new Intent(Start.this, Register.class));
                break;

            case R.id.continue_customer:
                sharedPreferencesUtils.put("type", RoleName.ROLE_CUSTOMER.name());
                startActivity(new Intent(Start.this, Register.class));
                break;
        }
    }
}
