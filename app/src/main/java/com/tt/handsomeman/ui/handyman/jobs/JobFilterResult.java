package com.tt.handsomeman.ui.handyman.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.JobFilterAdapter;
import com.tt.handsomeman.databinding.ActivityFilterResultBinding;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.request.JobFilterRequest;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class JobFilterResult extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {

    private final List<Job> jobArrayList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private JobFilterAdapter jobAdapter;
    private ProgressBar pgJob;
    private ActivityFilterResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilterResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);

        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        pgJob = binding.progressBarFilterResult;

        backPreviousActivity();

        createJobRecycleView();

        Integer radius = getIntent().getIntExtra("radius", 0);
        Integer priceMin = getIntent().getIntExtra("priceMin", 0);
        Integer priceMax = getIntent().getIntExtra("priceMax", 0);
        String dateCreated = getIntent().getStringExtra("dateCreated");
        Integer categoryId = getIntent().getIntExtra("categoryId", 0);
        Constants.Latitude.observe(this, aDouble -> fetchData(aDouble, Constants.Longitude.getValue(), radius, priceMin, priceMax, dateCreated, categoryId));
    }

    private void backPreviousActivity() {
        binding.filterResultBackButton.setOnClickListener(view -> onBackPressed());
    }

    private void createJobRecycleView() {
        RecyclerView rcvJob = binding.recycleViewFilterResult;
        jobAdapter = new JobFilterAdapter(this, jobArrayList);
        jobAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(JobFilterResult.this, JobDetail.class);
            intent.putExtra("jobId", jobArrayList.get(position).getId());
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(this);
        rcvJob.setLayoutManager(layoutManagerJob);
        rcvJob.setItemAnimator(new DefaultItemAnimator());
        rcvJob.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvJob.setAdapter(jobAdapter);
    }


    private void fetchData(Double lat,
                           Double lng,
                           Integer radius,
                           Integer priceMin,
                           Integer priceMax,
                           String createTime,
                           Integer categoryId) {

        baseViewModel.fetchJobsByFilter(new JobFilterRequest(lat, lng, radius, priceMin, priceMax, createTime, categoryId));

        baseViewModel.getJobLiveData().observe(this, data -> {
            pgJob.setVisibility(View.GONE);
            jobArrayList.clear();
            jobArrayList.addAll(data);
            jobAdapter.notifyDataSetChanged();
        });
    }
}
