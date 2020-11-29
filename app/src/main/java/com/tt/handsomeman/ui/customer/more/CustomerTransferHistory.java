package com.tt.handsomeman.ui.customer.more;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.CustomerTransferHistoryAdapter;
import com.tt.handsomeman.databinding.ActivityCustomerTransferHistoryBinding;
import com.tt.handsomeman.response.CustomerTransferHistoryResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CustomerTransferHistory extends BaseAppCompatActivityWithViewModel<CustomerViewModel> {

    private final List<CustomerTransferHistoryResponse> historyResponseList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private CustomerTransferHistoryAdapter customerTransferHistoryAdapter;
    private ActivityCustomerTransferHistoryBinding binding;
    private RecyclerView rcvTransferHistory;
    private String authorizationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerTransferHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        authorizationCode = sharedPreferencesUtils.get("token", String.class);

        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);

        bindView();
        createRecyclerView();
        fetchData();
    }

    private void bindView() {
        binding.backButton.setOnClickListener(v -> onBackPressed());
        rcvTransferHistory = binding.recyclerViewTransferHistory;
    }

    private void createRecyclerView() {
        customerTransferHistoryAdapter = new CustomerTransferHistoryAdapter(historyResponseList, this, authorizationCode);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvTransferHistory.setLayoutManager(layoutManager);
        rcvTransferHistory.setItemAnimator(new DefaultItemAnimator());
        rcvTransferHistory.addItemDecoration(new CustomDividerItemDecoration(this, R.drawable.recycler_view_divider));
        rcvTransferHistory.setAdapter(customerTransferHistoryAdapter);
    }

    private void fetchData() {
        baseViewModel.fetchTransferHistory();
        baseViewModel.getListTransferHistoryLiveData().observe(this, listCustomerTransferDataBracketResponse -> {
            historyResponseList.clear();
            historyResponseList.addAll(listCustomerTransferDataBracketResponse.getData().getCustomerTransferHistoryList());
            customerTransferHistoryAdapter.notifyDataSetChanged();
        });
    }
}
