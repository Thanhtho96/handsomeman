package com.tt.handsomeman.ui.handyman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.PaymentMilestoneAdapter;
import com.tt.handsomeman.databinding.ActivityViewJobTransactionBinding;
import com.tt.handsomeman.response.PaymentPaid;
import com.tt.handsomeman.response.TransactionDetailResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.DecimalFormat;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ViewJobTransaction extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private List<PaymentPaid> paymentPaidList = new ArrayList<>();
    private PaymentMilestoneAdapter paymentMilestoneAdapter;
    private TextView jobTitle, totalBalance, willReceive, haveReceive, fee, bonus;
    private LinearLayout layoutBonus;
    private RecyclerView rcvPaymentMilestone;
    private ActivityViewJobTransactionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewJobTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        Intent intent = getIntent();
        int jobId = intent.getIntExtra("jobId", 0);

        bindView();
        createRecycleView();
        fetchData(jobId);
    }

    private void fetchData(int jobId) {
        baseViewModel.fetchJobTransactionDetail(sharedPreferencesUtils.get("token", String.class), jobId);
        baseViewModel.getJobTransactionLiveData().observe(this, transactionDetailResponseDataBracketResponse -> {
            TransactionDetailResponse transactionDetailResponse = transactionDetailResponseDataBracketResponse.getData();
            jobTitle.setText(transactionDetailResponse.getJobTitle());
            totalBalance.setText(getString(R.string.money_currency_string, DecimalFormat.format(transactionDetailResponse.getTotalBalance())));
            willReceive.setText(getString(R.string.money_currency_string, DecimalFormat.format(transactionDetailResponse.getWillReceive())));
            fee.setText(getString(R.string.money_currency_string, DecimalFormat.format(transactionDetailResponse.getFee())));
            haveReceive.setText(getString(R.string.money_currency_string, DecimalFormat.format(transactionDetailResponse.getHaveReceive())));

            if (transactionDetailResponse.getBonus() > 0) {
                layoutBonus.setVisibility(View.VISIBLE);
                bonus.setText(getString(R.string.money_currency_string, DecimalFormat.format(transactionDetailResponse.getBonus())));
            }

            paymentPaidList.clear();
            paymentPaidList.addAll(transactionDetailResponse.getPaymentPaidList());
            paymentMilestoneAdapter.notifyDataSetChanged();
        });
    }

    private void createRecycleView() {
        paymentMilestoneAdapter = new PaymentMilestoneAdapter(this, paymentPaidList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvPaymentMilestone.setLayoutManager(layoutManager);
        rcvPaymentMilestone.setItemAnimator(new DefaultItemAnimator());
        rcvPaymentMilestone.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvPaymentMilestone.setAdapter(paymentMilestoneAdapter);
    }


    private void bindView() {
        jobTitle = binding.jobTitle;
        totalBalance = binding.totalBalance;
        willReceive = binding.willReceive;
        fee = binding.fee;
        haveReceive = binding.balanceReceive;
        bonus = binding.bonus;
        layoutBonus = binding.layoutBonus;
        rcvPaymentMilestone = binding.recyclerViewPaymentMilestone;
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }
}
