package com.tt.handsomeman.ui.handyman.more;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.TransferHistoryAdapter;
import com.tt.handsomeman.databinding.ActivityTransferHistoryBinding;
import com.tt.handsomeman.response.TransferHistoryResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TransferHistory extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;

    private RecyclerView rcvTransferHistory;
    private TransferHistoryAdapter transferHistoryAdapter;
    private List<TransferHistoryResponse> transferHistoryList = new ArrayList<>();
    private ActivityTransferHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        rcvTransferHistory = binding.recyclerViewTransferHistory;
        binding.viewTransferHistoryBackButton.setOnClickListener(view -> {
            onBackPressed();
        });

        createRecyclerView();
        fetchTransferHistory();
    }

    private void fetchTransferHistory() {
        baseViewModel.viewTransferHistory(sharedPreferencesUtils.get("token", String.class));
        baseViewModel.getListTransferHistoryMutableLiveData().observe(this, listTransferHistory -> {
            transferHistoryList.clear();
            transferHistoryList.addAll(listTransferHistory.getTransferHistoryResponseList());
            transferHistoryAdapter.notifyDataSetChanged();
        });
    }

    private void createRecyclerView() {
        transferHistoryAdapter = new TransferHistoryAdapter(transferHistoryList, this);
        RecyclerView.LayoutManager layoutManagerPayout = new LinearLayoutManager(this);
        rcvTransferHistory.setLayoutManager(layoutManagerPayout);
        rcvTransferHistory.setItemAnimator(new DefaultItemAnimator());
        rcvTransferHistory.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvTransferHistory.setAdapter(transferHistoryAdapter);
    }
}
