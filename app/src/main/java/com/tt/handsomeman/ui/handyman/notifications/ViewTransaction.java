package com.tt.handsomeman.ui.handyman.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityViewTransactionBinding;
import com.tt.handsomeman.response.DataBracketResponse;
import com.tt.handsomeman.response.PaidPaymentNotificationResponse;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.DecimalFormat;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.NotificationViewModel;

import javax.inject.Inject;

public class ViewTransaction extends BaseAppCompatActivityWithViewModel<NotificationViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private TextView jobTile, whoPaid, balanceTransfer, balanceReceive, fee, bonus;
    private LinearLayout layoutBonus;
    private boolean isRead, isReadForFirstTime = false;
    private int notificationPos, notificationId;
    private ActivityViewTransactionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(NotificationViewModel.class);

        bindView();

        Intent intent = getIntent();
        notificationId = intent.getIntExtra("notificationId", 0);
        isRead = intent.getBooleanExtra("isRead", false);
        notificationPos = intent.getIntExtra("notificationPos", 0);

        int customerTransferId = intent.getIntExtra("customerTransferId", 0);
        String authorization = sharedPreferencesUtils.get("token", String.class);
        fetchData(authorization, customerTransferId);
    }

    private void fetchData(String authorization,
                           int customerTransferId) {
        baseViewModel.fetchPaidPaymentNotification(authorization, customerTransferId);
        baseViewModel.getPaidPaymentNotificationResponseMutableLiveData().observe(this, new Observer<DataBracketResponse<PaidPaymentNotificationResponse>>() {
            @Override
            public void onChanged(DataBracketResponse<PaidPaymentNotificationResponse> paidPaymentNotificationResponseDataBracketResponse) {
                PaidPaymentNotificationResponse paidPaymentNotificationResponse = paidPaymentNotificationResponseDataBracketResponse.getData();

                int paymentMilestoneOrder = paidPaymentNotificationResponse.getPaymentMilestoneOrder();
                String paymentOrder;
                switch (paymentMilestoneOrder) {
                    case 1:
                        paymentOrder = getString(R.string.first_milestone, paymentMilestoneOrder);
                        break;
                    case 2:
                        paymentOrder = getString(R.string.second_milestone, paymentMilestoneOrder);
                        break;
                    case 3:
                        paymentOrder = getString(R.string.third_milestone, paymentMilestoneOrder);
                        break;
                    default:
                        paymentOrder = getString(R.string.default_milestone, paymentMilestoneOrder);
                        break;
                }

                jobTile.setText(paidPaymentNotificationResponse.getJobTitle());
                whoPaid.setText(getString(R.string.paid_payment_notification, paidPaymentNotificationResponse.getCustomerName(), paymentOrder));
                balanceTransfer.setText(getString(R.string.money_currency_string, DecimalFormat.format(paidPaymentNotificationResponse.getBalanceTransfer())));
                balanceReceive.setText(getString(R.string.money_currency_string, DecimalFormat.format(paidPaymentNotificationResponse.getBalanceReceive())));
                fee.setText(getString(R.string.money_currency_string, DecimalFormat.format(paidPaymentNotificationResponse.getFee())));

                if (paidPaymentNotificationResponse.getBonus() > 0) {
                    layoutBonus.setVisibility(View.VISIBLE);
                    bonus.setText(getString(R.string.money_currency_string, DecimalFormat.format(paidPaymentNotificationResponse.getBonus())));
                }

                if (!isRead) {
                    markAsRead(notificationId, authorization);
                }
            }
        });
    }

    private void markAsRead(Integer notificationId,
                            String token) {
        if (!isRead) {
            baseViewModel.markNotificationAsRead(token, notificationId);
            baseViewModel.getStandardResponseMarkReadMutableLiveData().observe(this, new Observer<StandardResponse>() {
                @Override
                public void onChanged(StandardResponse standardResponse) {
                    if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                        isRead = true;
                        ViewTransaction.this.isReadForFirstTime = true;
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (notificationId != 0) {
            Intent intent = new Intent();
            intent.putExtra("isRead", isRead);
            intent.putExtra("isReadForFirstTime", this.isReadForFirstTime);
            intent.putExtra("notificationPos", notificationPos);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void bindView() {
        jobTile = binding.jobTitle;
        whoPaid = binding.whoPaid;
        balanceTransfer = binding.transferBalance;
        balanceReceive = binding.balanceReceive;
        fee = binding.fee;
        bonus = binding.bonus;
        layoutBonus = binding.layoutBonus;
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }
}
