package com.tt.handsomeman.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.NotificationAdapter;
import com.tt.handsomeman.databinding.FragmentNotificationsBinding;
import com.tt.handsomeman.response.NotificationResponse;
import com.tt.handsomeman.ui.customer.notification.ViewMadeBid;
import com.tt.handsomeman.ui.handyman.jobs.JobDetail;
import com.tt.handsomeman.ui.handyman.notifications.ViewTransaction;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.NotificationType;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NotificationsFragment extends BaseFragment<NotificationViewModel, FragmentNotificationsBinding> {

    private static final int NOTIFICATION_REQUEST = 47;
    private final List<NotificationResponse> responseList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private NotificationAdapter notificationAdapter;
    private String authorization;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        authorization = sharedPreferencesUtils.get("token", String.class);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(NotificationViewModel.class);
        viewBinding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createJobRecycleView();
        fetchData();
    }

    private void fetchData() {
        String type = sharedPreferencesUtils.get("type", String.class);
        baseViewModel.fetchNotificationOfAccount(type);
        baseViewModel.getListNotificationMutableLiveData().observe(getViewLifecycleOwner(), notifications -> {
            responseList.clear();
            responseList.addAll(notifications);
            notificationAdapter.notifyDataSetChanged();
        });
    }

    private void createJobRecycleView() {
        RecyclerView rcvNotification = viewBinding.recyclerViewNotification;
        notificationAdapter = new NotificationAdapter(getContext(), responseList, authorization);
        notificationAdapter.setOnItemClickListener(position -> {
            NotificationResponse notificationResponse = responseList.get(position);
            switch (NotificationType.valueOf(notificationResponse.getNotificationType())) {
                case MADE_A_BID:
                    Intent intent = new Intent(getContext(), ViewMadeBid.class);
                    intent.putExtra("handymanId", notificationResponse.getSenderId());
                    intent.putExtra("notificationId", notificationResponse.getId());
                    intent.putExtra("jobTitle", notificationResponse.getNotificationDescription());
                    intent.putExtra("jobBidId", notificationResponse.getContentId());
                    intent.putExtra("personBid", notificationResponse.getSenderName());
                    intent.putExtra("isRead", notificationResponse.getRead());
                    intent.putExtra("notificationPos", position);
                    startActivityForResult(intent, NOTIFICATION_REQUEST);
                    break;
                case ACCEPT_BID:
                    Intent intent1 = new Intent(getContext(), JobDetail.class);
                    intent1.putExtra("notificationId", notificationResponse.getId());
                    intent1.putExtra("jobId", notificationResponse.getContentId());
                    intent1.putExtra("isRead", notificationResponse.getRead());
                    intent1.putExtra("notificationPos", position);
                    startActivityForResult(intent1, NOTIFICATION_REQUEST);
                    break;
                case PAID_PAYMENT:
                    Intent intent2 = new Intent(getContext(), ViewTransaction.class);
                    intent2.putExtra("notificationId", notificationResponse.getId());
                    intent2.putExtra("customerTransferId", notificationResponse.getContentId());
                    intent2.putExtra("isRead", notificationResponse.getRead());
                    intent2.putExtra("notificationPos", position);
                    startActivityForResult(intent2, NOTIFICATION_REQUEST);
                    break;
            }
        });
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(getContext());
        rcvNotification.setLayoutManager(layoutManagerJob);
        rcvNotification.setItemAnimator(new DefaultItemAnimator());
        rcvNotification.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvNotification.setAdapter(notificationAdapter);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {

        if (requestCode == NOTIFICATION_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            boolean isRead = data.getBooleanExtra("isRead", false);
            boolean isReadForFirstTime = data.getBooleanExtra("isReadForFirstTime", false);

            if (isRead && isReadForFirstTime) {
                int notificationPos = data.getIntExtra("notificationPos", 0);
                responseList.get(notificationPos).setRead(true);
                notificationAdapter.notifyItemChanged(notificationPos);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}