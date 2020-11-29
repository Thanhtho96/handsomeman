package com.tt.handsomeman.ui.handyman.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.JobFilterAdapter;
import com.tt.handsomeman.databinding.ActivityGroupByCategoryBinding;
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

public class GroupByCategory extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {

    private final List<Job> jobArrayList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private JobFilterAdapter jobAdapter;
    private ProgressBar pgJob;
    private TextView categoryName;
    private ImageButton btnFilter;
    private ActivityGroupByCategoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupByCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        pgJob = binding.progressBarJobCategory;
        categoryName = binding.textViewCategoryName;
        btnFilter = binding.imageButtonFilter;

        backPreviousActivity();

        navigateToFilter();

        createJobRecycleView();

        Integer categoryId = getIntent().getIntExtra("categoryId", 0);
        String categoryNameIntent = getIntent().getStringExtra("categoryName");
        categoryName.setText(categoryNameIntent);
        fetchData(categoryId);
    }

    private void navigateToFilter() {
        btnFilter.setOnClickListener(view -> {
            Intent intent = new Intent(GroupByCategory.this, JobFilter.class);
            intent.putExtra("categoryId", getIntent().getIntExtra("categoryId", 0));
            startActivity(intent);
        });
    }

    private void backPreviousActivity() {
        binding.categoryBackButton.setOnClickListener(view -> onBackPressed());
    }

    private void createJobRecycleView() {
        RecyclerView rcvJob = binding.recycleViewJobsByCategory;
        jobAdapter = new JobFilterAdapter(this, jobArrayList);
        jobAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(GroupByCategory.this, JobDetail.class);
            intent.putExtra("jobId", jobArrayList.get(position).getId());
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(this);
        rcvJob.setLayoutManager(layoutManagerJob);
        rcvJob.setItemAnimator(new DefaultItemAnimator());
        rcvJob.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvJob.setAdapter(jobAdapter);
    }


    private void fetchData(Integer categoryId) {

        baseViewModel.fetchJobsByCategory(categoryId, new NearbyJobRequest(Constants.Latitude.getValue(), Constants.Longitude.getValue(), 10));

        baseViewModel.getJobLiveData().observe(this, data -> {
            pgJob.setVisibility(View.GONE);
            jobArrayList.clear();
            jobArrayList.addAll(data);
            jobAdapter.notifyDataSetChanged();
        });
    }
}
