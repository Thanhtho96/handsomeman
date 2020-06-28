package com.tt.handsomeman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityViewPayoutBinding;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.util.YesOrNoDialog;
import com.tt.handsomeman.viewmodel.UserViewModel;

import javax.inject.Inject;

public class ViewPayout extends BaseAppCompatActivityWithViewModel<UserViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ActivityViewPayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);

        binding.viewPayoutBackButton.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.textViewLastPayoutNumber.setText(getIntent().getStringExtra("lastPayoutNumber"));

        binding.imageButtonDeletePayout.setOnClickListener(view -> {
            new YesOrNoDialog(this, R.style.PauseDialog, HandymanApp.getInstance().getString(R.string.sure_to_delete_payout, getIntent().getStringExtra("lastPayoutNumber")), R.drawable.dele, new YesOrNoDialog.OnItemClickListener() {
                @Override
                public void onItemClickYes() {
                    delete(getIntent().getIntExtra("payoutId", 0));
                }
            }).show();

        });
    }

    private void delete(int payoutId) {
        String authorizationCode = sharedPreferencesUtils.get("token", String.class);

        baseViewModel.removePayout(authorizationCode, payoutId);
        baseViewModel.getStandardResponseMutableLiveData().observe(this, standardResponse -> {
            Toast.makeText(this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
            if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                Intent intent = new Intent();
                intent.putExtra("isMoreFragmentEdit", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
