package com.tt.handsomeman.ui.handyman.my_projects;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.JobFilterAdapter;
import com.tt.handsomeman.databinding.FragmentMyProjectsChildInProgressBinding;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.ui.handyman.jobs.JobDetail;
import com.tt.handsomeman.util.CustomDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MyProjectsChildInProgressFragment extends Fragment {

    private final List<Job> inProgressList = new ArrayList<>();
    private JobFilterAdapter jobAdapter;
    private FragmentMyProjectsChildInProgressBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyProjectsChildInProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createJobRecycleView();

        ((MyProjectsFragment) getParentFragment()).inProgressList.observe(getViewLifecycleOwner(), jobs -> {
            inProgressList.clear();
            inProgressList.addAll(jobs);
            jobAdapter.notifyDataSetChanged();
        });
    }

    private void createJobRecycleView() {
        RecyclerView rcvJob = binding.recycleViewJobsInProgress;
        jobAdapter = new JobFilterAdapter(getContext(), inProgressList);
        jobAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), JobDetail.class);
            intent.putExtra("jobId", inProgressList.get(position).getId());
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(getContext());
        rcvJob.setLayoutManager(layoutManagerJob);
        rcvJob.setItemAnimator(new DefaultItemAnimator());
        rcvJob.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvJob.setAdapter(jobAdapter);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
