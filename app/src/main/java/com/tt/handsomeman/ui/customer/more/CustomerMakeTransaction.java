package com.tt.handsomeman.ui.customer.more;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.SpinnerBankAccount;
import com.tt.handsomeman.adapter.SpinnerJobTransaction;
import com.tt.handsomeman.databinding.ActivityCustomerMakeTransactionBinding;
import com.tt.handsomeman.request.MadeTheTransactionRequest;
import com.tt.handsomeman.response.JobTransactionResponse;
import com.tt.handsomeman.response.PayoutResponse;
import com.tt.handsomeman.response.ViewMadeTransactionResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.DecimalFormat;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class CustomerMakeTransaction extends BaseAppCompatActivityWithViewModel<CustomerViewModel> {

    private final List<JobTransactionResponse> jobTransactionResponses = new ArrayList<>();
    private final List<PayoutResponse> payoutResponseList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ImageButton btnCheck;
    private EditText edtBalanceTransfer;
    private TextView tvBonus, tvPaymentMilestoneOrder, paymentMilestonePercentage, handymanName;
    private Spinner spBankAccount, spProject;
    private ImageView imgHandymanAvatar;
    private SpinnerBankAccount spinnerBankAccount;
    private SpinnerJobTransaction spinnerJobTransaction;
    private ActivityCustomerMakeTransactionBinding binding;

    private int jobId;
    private int handymanId;
    private int payoutId;
    private double balance;
    private double minPay;
    private double bonus;
    private int paymentMilestoneOrder;
    private String authorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerMakeTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);

        authorization = sharedPreferencesUtils.get("token", String.class);

        bindView();
        generateBankAccountSpinner();
        generateJobTitleSpinner();
        fetchData();
        listenToProjectSpinner();
        listenToBankSpinner();
        listenToEditTextChange();
        makeTheTransfer();
    }

    private void makeTheTransfer() {
        btnCheck.setOnClickListener(v -> {
            btnCheck.setEnabled(false);
            Calendar now = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
            String sendTime = formatter.format(now.getTime());

            baseViewModel.makeTheTransaction(new MadeTheTransactionRequest(
                    jobId,
                    handymanId,
                    payoutId,
                    minPay,
                    paymentMilestoneOrder,
                    sendTime,
                    bonus));
            baseViewModel.getStandardResponseMutableLiveData().observe(this, standardResponse -> {
                Toast.makeText(CustomerMakeTransaction.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    onBackPressed();
                }
            });
        });
    }

    private void listenToEditTextChange() {
        edtBalanceTransfer.addTextChangedListener(new TextWatcher() {
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
                if (!edtBalanceTransfer.getText().toString().trim().equals("")) {
                    balance = Double.parseDouble(edtBalanceTransfer.getText().toString().trim());
                    if (balance > minPay) {
                        btnCheck.setEnabled(true);
                        bonus = balance - minPay;
                        tvBonus.setText(getString(R.string.bonus_value, getString(R.string.money_currency_string, DecimalFormat.format(bonus))));
                        tvBonus.setVisibility(View.VISIBLE);

                    } else if (balance < minPay) {
                        btnCheck.setEnabled(false);
                        edtBalanceTransfer.setError(getResources().getString(R.string.cant_send_smaller_than_min_pay, DecimalFormat.format(minPay)));
                    } else {
                        btnCheck.setEnabled(true);
                        bonus = 0;
                        tvBonus.setVisibility(View.GONE);
                    }
                } else {
                    btnCheck.setEnabled(false);
                    edtBalanceTransfer.setError(getResources().getString(R.string.please_input));
                }
            }
        });
    }

    private void listenToProjectSpinner() {
        spProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                JobTransactionResponse jobTransactionResponse = jobTransactionResponses.get(position);
                jobId = jobTransactionResponse.getJobId();
                handymanId = jobTransactionResponse.getHandymanId();
                balance = jobTransactionResponse.getMinPay();
                minPay = jobTransactionResponse.getMinPay();
                paymentMilestoneOrder = jobTransactionResponse.getPaymentMileStoneOrder();

                edtBalanceTransfer.setText(DecimalFormat.format(balance));
                switch (paymentMilestoneOrder) {
                    case 1:
                        tvPaymentMilestoneOrder.setText(getString(R.string.first_milestone, paymentMilestoneOrder));
                        break;
                    case 2:
                        tvPaymentMilestoneOrder.setText(getString(R.string.second_milestone, paymentMilestoneOrder));
                        break;
                    case 3:
                        tvPaymentMilestoneOrder.setText(getString(R.string.third_milestone, paymentMilestoneOrder));
                        break;
                    default:
                        tvPaymentMilestoneOrder.setText(getString(R.string.default_milestone, paymentMilestoneOrder));
                        break;
                }
                paymentMilestonePercentage.setText(getString(R.string.percentage, jobTransactionResponse.getPaymentMileStonePercentage()));
                handymanName.setText(jobTransactionResponse.getHandymanName());

                GlideUrl glideUrl1 = new GlideUrl((jobTransactionResponse.getHandymanAvatar()),
                        new LazyHeaders.Builder().addHeader("Authorization", authorization).build());

                Glide.with(CustomerMakeTransaction.this)
                        .load(glideUrl1)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .placeholder(R.drawable.custom_progressbar)
                        .error(R.drawable.logo)
                        .signature(new MediaStoreSignature("", jobTransactionResponse.getUpdateDate(), 0))
                        .into(imgHandymanAvatar);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void listenToBankSpinner() {
        spBankAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                PayoutResponse payoutResponse = payoutResponseList.get(position);
                payoutId = payoutResponse.getPayoutId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void fetchData() {
        baseViewModel.viewMakeTransaction();
        baseViewModel.getViewTransactionLiveData().observe(this, viewMadeTransactionResponseDataBracketResponse -> {
            if (viewMadeTransactionResponseDataBracketResponse.getStatus().equals(StatusConstant.OK)) {
                ViewMadeTransactionResponse transactionResponse = viewMadeTransactionResponseDataBracketResponse.getData();
                jobTransactionResponses.clear();
                jobTransactionResponses.addAll(transactionResponse.getJobTransactionResponseList());
                spinnerJobTransaction.notifyDataSetChanged();

                payoutResponseList.clear();
                payoutResponseList.addAll(transactionResponse.getPayoutResponseList());
                spinnerBankAccount.notifyDataSetChanged();

                btnCheck.setEnabled(true);
            } else {
                Toast.makeText(this, viewMadeTransactionResponseDataBracketResponse.getMessage(), Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    private void generateBankAccountSpinner() {
        spinnerBankAccount = new SpinnerBankAccount(this, payoutResponseList);
        spBankAccount.setAdapter(spinnerBankAccount);
    }

    private void generateJobTitleSpinner() {
        spinnerJobTransaction = new SpinnerJobTransaction(this, jobTransactionResponses);
        spProject.setAdapter(spinnerJobTransaction);
    }

    private void bindView() {
        btnCheck = binding.buttonCheck;
        edtBalanceTransfer = binding.editTextTransferAmount;
        tvBonus = binding.bonus;
        tvPaymentMilestoneOrder = binding.paymentMileStoneOrder;
        paymentMilestonePercentage = binding.paymentMileStonePercentage;
        handymanName = binding.handymanName;
        spBankAccount = binding.spinnerBankAccount;
        spProject = binding.spinnerProject;
        imgHandymanAvatar = binding.handymanAvatar;
        binding.backButton.setOnClickListener(v -> onBackPressed());
        btnCheck.setEnabled(false);
    }
}
