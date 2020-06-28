package com.tt.handsomeman.ui.customer.find_handyman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.FindHandymanFilterAdapter;
import com.tt.handsomeman.databinding.ActivityHandymanNearYourLocationBinding;
import com.tt.handsomeman.request.NearbyHandymanRequest;
import com.tt.handsomeman.response.HandymanResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class HandymanNearYourLocation extends BaseAppCompatActivityWithViewModel<CustomerViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ActivityHandymanNearYourLocationBinding binding;
    private ProgressBar pgHandyman;
    private FindHandymanFilterAdapter findHandymanfilterAdapter;
    private List<HandymanResponse> handymanResponseList = new ArrayList<>();
    private String authorizationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHandymanNearYourLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);

        pgHandyman = binding.progressBarHandymanYourLocation;

        backPreviousActivity();
        createJobRecycleView();

        Constants.Latitude.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                fetchData(aDouble, Constants.Longitude.getValue());
            }
        });
    }

    private void fetchData(Double lat,
                           Double lng) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
        String dateRequest = formatter.format(now.getTime());
        double radius = 10d;

        baseViewModel.fetHandymanNearby(authorizationCode, new NearbyHandymanRequest(lat, lng, radius, dateRequest));

        baseViewModel.getNearbyHandymanResponseMutableLiveData().observe(this, data -> {
            pgHandyman.setVisibility(View.GONE);
            handymanResponseList.clear();
            handymanResponseList.addAll(data.getHandymanResponseList());
            findHandymanfilterAdapter.notifyDataSetChanged();
        });
    }

    private void createJobRecycleView() {
        RecyclerView rcvHandyman = binding.recycleViewHandymanYourLocation;
        findHandymanfilterAdapter = new FindHandymanFilterAdapter(this, handymanResponseList, authorizationCode);
        findHandymanfilterAdapter.setOnItemClickListener(new FindHandymanFilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(HandymanNearYourLocation.this, HandymanDetail.class);
                intent.putExtra("handymanId", handymanResponseList.get(position).getHandymanId());
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(this);
        rcvHandyman.setLayoutManager(layoutManagerJob);
        rcvHandyman.setItemAnimator(new DefaultItemAnimator());
        rcvHandyman.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvHandyman.setAdapter(findHandymanfilterAdapter);
    }

    private void backPreviousActivity() {
        binding.yourLocationBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
