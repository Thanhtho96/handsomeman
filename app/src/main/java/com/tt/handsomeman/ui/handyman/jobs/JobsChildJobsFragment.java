package com.tt.handsomeman.ui.handyman.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.CategoryAdapter;
import com.tt.handsomeman.adapter.JobAdapter;
import com.tt.handsomeman.databinding.FragmentJobsChildJobsBinding;
import com.tt.handsomeman.model.Category;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.request.NearbyJobRequest;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class JobsChildJobsFragment extends BaseFragment<HandymanViewModel, FragmentJobsChildJobsBinding> {

    private final List<Job> jobArrayList = new ArrayList<>();
    private final List<Category> categoryArrayList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ProgressBar pgJob, pgCategory;
    private JobAdapter jobAdapter;
    private CategoryAdapter categoryAdapter;
    private LinearLayout showMoreYourLocation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);
        viewBinding = FragmentJobsChildJobsBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pgJob = viewBinding.progressBarJobs;
        pgCategory = viewBinding.progressBarCategory;
        showMoreYourLocation = viewBinding.showMoreYourLocation;

        createJobRecycleView();

        createCategoryRecycleView();

        showMoreByYourLocation();

        Constants.Longitude.observe(getViewLifecycleOwner(), aDouble -> fetchData(Constants.Latitude.getValue(), aDouble));
    }

    private void showMoreByYourLocation() {
        showMoreYourLocation.setOnClickListener(view -> startActivity(new Intent(getContext(), YourLocation.class)));
    }

    private void createJobRecycleView() {
        RecyclerView rcvJob = viewBinding.recycleViewJobs;
        jobAdapter = new JobAdapter(getContext(), jobArrayList);
        jobAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), JobDetail.class);
            intent.putExtra("jobId", jobArrayList.get(position).getId());
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(getContext());
        rcvJob.setLayoutManager(layoutManagerJob);
        rcvJob.setItemAnimator(new DefaultItemAnimator());
        rcvJob.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider));
        rcvJob.setAdapter(jobAdapter);
    }

    private void createCategoryRecycleView() {
        RecyclerView rcvCategory = viewBinding.recycleViewCategories;
        categoryAdapter = new CategoryAdapter(getContext(), categoryArrayList);
        categoryAdapter.setOnItemClickListener(position -> {
            String categoryName = categoryArrayList.get(position).getName();

            Intent intent = new Intent(getContext(), GroupByCategory.class);
            intent.putExtra("categoryName", categoryName);
            intent.putExtra("categoryId", categoryArrayList.get(position).getCategory_id());
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManagerCategory = new LinearLayoutManager(getContext());
        rcvCategory.setLayoutManager(layoutManagerCategory);
        rcvCategory.setItemAnimator(new DefaultItemAnimator());
        rcvCategory.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider));
        rcvCategory.setAdapter(categoryAdapter);
    }

    private void fetchData(Double lat,
                           Double lng) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
        String dateRequest = formatter.format(now.getTime());
        double radius = 10d;

        baseViewModel.fetchDataStartScreen(new NearbyJobRequest(lat, lng, radius, dateRequest));

        baseViewModel.getStartScreenData().observe(getViewLifecycleOwner(), data -> {
            pgJob.setVisibility(View.GONE);
            jobArrayList.clear();
            jobArrayList.addAll(data.getJobList());
            jobAdapter.notifyDataSetChanged();

            pgCategory.setVisibility(View.GONE);
            categoryArrayList.clear();
            categoryArrayList.addAll(data.getCategoryList());
            categoryAdapter.notifyDataSetChanged();

            sharedPreferencesUtils.put("updateDate", data.getUpdateDate());
        });
    }

    @Override
    public void onStop() {
        Constants.Longitude.removeObservers(this);
        super.onStop();
    }
}
