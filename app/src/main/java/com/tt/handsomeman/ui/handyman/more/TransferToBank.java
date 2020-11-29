package com.tt.handsomeman.ui.handyman.more;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.SpinnerBankAccount;
import com.tt.handsomeman.databinding.ActivityTransferToBankBinding;
import com.tt.handsomeman.request.HandymanTransferRequest;
import com.tt.handsomeman.response.PayoutResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.DecimalFormat;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TransferToBank extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {

    private final List<PayoutResponse> payoutResponseList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private TextView tvWalletBalance;
    private ImageButton ibTransfer;
    private EditText edtTransferAmount;
    private Spinner spBankAccount;
    private SpinnerBankAccount spinnerBankAccountAdapter;
    private PayoutResponse payoutResponse;
    private double balance;
    private double myBalanceToTransfer;
    private ActivityTransferToBankBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferToBankBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        tvWalletBalance = binding.walletBalance;
        ibTransfer = binding.imageButtonTransfer;
        edtTransferAmount = binding.editTextTransferAmount;
        spBankAccount = binding.spinnerBankAccount;
        binding.imageButtonTransferBack.setOnClickListener(view -> onBackPressed());

        fetchHandymanInfo();
        generateBankAccountSpinner();
        editTextManipulation();
        ibTransfer.setOnClickListener(view -> transferToBank());
    }

    private void transferToBank() {
        if (balance > 0) {
            ibTransfer.setEnabled(false);

            Calendar now = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
            String dateTransfer = formatter.format(now.getTime());

            payoutResponse = payoutResponseList.get(spBankAccount.getSelectedItemPosition());
            int payoutId = payoutResponse.getPayoutId();

            HandymanTransferRequest transferRequest = new HandymanTransferRequest(payoutId, myBalanceToTransfer, dateTransfer);
            baseViewModel.transferToBankAccount(transferRequest);
            baseViewModel.getStandardResponseMutableLiveData().observe(TransferToBank.this, standardResponse -> {
                Toast.makeText(TransferToBank.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    Intent intent = new Intent();
                    intent.putExtra("isMoreFragmentEdit", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.balance_is_empty), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchHandymanInfo() {
        baseViewModel.getListPayoutOfHandyman();
        baseViewModel.getListPayoutResponseMutableLiveData().observe(this, listPayoutResponse -> {
            balance = listPayoutResponse.getBalance();
            tvWalletBalance.setText(getString(R.string.money_currency_string, DecimalFormat.format(balance)));
            edtTransferAmount.setText(DecimalFormat.format(listPayoutResponse.getBalance()));
            payoutResponseList.clear();
            payoutResponseList.addAll(listPayoutResponse.getPayoutResponseList());
            spinnerBankAccountAdapter.notifyDataSetChanged();
        });
    }

    private void generateBankAccountSpinner() {
        spinnerBankAccountAdapter = new SpinnerBankAccount(this, payoutResponseList);
        spBankAccount.setAdapter(spinnerBankAccountAdapter);
    }

    private void editTextManipulation() {
        edtTransferAmount.addTextChangedListener(new TextWatcher() {
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
                if (!edtTransferAmount.getText().toString().trim().equals("")) {
                    myBalanceToTransfer = Double.parseDouble(edtTransferAmount.getText().toString().trim());
                    if (myBalanceToTransfer > balance) {
                        ibTransfer.setEnabled(false);
                        edtTransferAmount.setError(getResources().getString(R.string.over_balance));
                    } else if (myBalanceToTransfer <= 0) {
                        ibTransfer.setEnabled(false);
                        edtTransferAmount.setError(getResources().getString(R.string.cant_withdraw_zero));
                    } else {
                        ibTransfer.setEnabled(true);
                    }
                } else {
                    ibTransfer.setEnabled(false);
                    edtTransferAmount.setError(getResources().getString(R.string.please_input));
                }
            }
        });
    }
}
