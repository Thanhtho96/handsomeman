package com.tt.handsomeman.ui.handyman.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.JobFilterAdapter;
import com.tt.handsomeman.databinding.ActivityYourLocationBinding;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.request.NearbyJobRequest;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class YourLocation extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {
    private final List<Job> jobArrayList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private JobFilterAdapter jobAdapter;
    private ProgressBar pgJob;
    private ImageButton btnFilter;
    private ActivityYourLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYourLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        btnFilter = binding.imageButtonFilter;
        pgJob = binding.progressBarJobYourLocation;

        backPreviousActivity();

        navigateToFilter();

        createJobRecycleView();

        Constants.Longitude.observe(this, aDouble -> fetchData(Constants.Latitude.getValue(), aDouble));
    }

    private void navigateToFilter() {
        btnFilter.setOnClickListener(view -> startActivity(new Intent(YourLocation.this, JobFilter.class)));
    }

    private void backPreviousActivity() {
        binding.yourLocationBackButton.setOnClickListener(view -> onBackPressed());
    }

    private void createJobRecycleView() {
        RecyclerView rcvJob = binding.recycleViewJobsYourLocation;
        jobAdapter = new JobFilterAdapter(this, jobArrayList);
        jobAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(YourLocation.this, JobDetail.class);
            intent.putExtra("jobId", jobArrayList.get(position).getId());
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(this);
        rcvJob.setLayoutManager(layoutManagerJob);
        rcvJob.setItemAnimator(new DefaultItemAnimator());
        rcvJob.addItemDecoration(new CustomDividerItemDecoration(this, R.drawable.recycler_view_divider));
        rcvJob.setAdapter(jobAdapter);
    }


    private void fetchData(Double lat,
                           Double lng) {

        double radius = 10d;

        baseViewModel.fetchYourLocationData(new NearbyJobRequest(lat, lng, radius));

        baseViewModel.getJobLiveData().observe(this, data -> {
            pgJob.setVisibility(View.GONE);
            jobArrayList.clear();
            jobArrayList.addAll(data);
            jobAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onStop() {
        Constants.Latitude.removeObservers(this);
        super.onStop();
    }
}
