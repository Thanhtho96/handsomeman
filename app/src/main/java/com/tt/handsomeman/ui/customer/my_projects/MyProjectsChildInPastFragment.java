package com.tt.handsomeman.ui.customer.my_projects;

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
import com.tt.handsomeman.databinding.FragmentMyProjectsChildInPastBinding;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.util.CustomDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MyProjectsChildInPastFragment extends Fragment {

    private final List<Job> inPastList = new ArrayList<>();
    private JobFilterAdapter jobAdapter;
    private FragmentMyProjectsChildInPastBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyProjectsChildInPastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createJobRecycleView();

        ((MyProjectsFragment) getParentFragment()).inPastList.observe(getViewLifecycleOwner(), jobs -> {
            inPastList.clear();
            inPastList.addAll(jobs);
            jobAdapter.notifyDataSetChanged();
        });
    }

    private void createJobRecycleView() {
        RecyclerView rcvJob = binding.recycleViewJobsInPast;
        jobAdapter = new JobFilterAdapter(getContext(), inPastList);
        jobAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), MyJobDetail.class);
            intent.putExtra("jobId", inPastList.get(position).getId());
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
