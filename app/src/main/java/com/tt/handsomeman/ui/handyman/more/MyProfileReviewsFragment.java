package com.tt.handsomeman.ui.handyman.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.HandymanReviewAdapter;
import com.tt.handsomeman.databinding.FragmentMyProfileReviewsBinding;
import com.tt.handsomeman.response.HandymanReviewResponse;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MyProfileReviewsFragment extends BaseFragment<HandymanViewModel, FragmentMyProfileReviewsBinding> {

    private final List<HandymanReviewResponse> handymanReviewResponseList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private TextView countReviewers;
    private RatingBar rtCountPoint;
    private HandymanReviewAdapter handymanReviewAdapter;
    private String authorizationCode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);
        viewBinding = FragmentMyProfileReviewsBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        countReviewers = viewBinding.reviewCountHandymanProfile;
        rtCountPoint = viewBinding.ratingBarHandymanProfile;

        createReviewRecyclerView();
        fetchData();
    }

    private void createReviewRecyclerView() {
        RecyclerView rcvReview = viewBinding.reviewHandymanProfile;
        handymanReviewAdapter = new HandymanReviewAdapter(handymanReviewResponseList, getContext(), authorizationCode);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcvReview.setLayoutManager(layoutManager);
        rcvReview.setItemAnimator(new DefaultItemAnimator());
        rcvReview.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvReview.setAdapter(handymanReviewAdapter);
    }

    private void fetchData() {
        baseViewModel.fetchHandymanReview();
        baseViewModel.getHandymanReviewProfileLiveData().observe(getViewLifecycleOwner(), handymanReviewProfile -> {
            countReviewers.setText(getResources().getQuantityString(R.plurals.numberOfReview, handymanReviewProfile.getCountReviewers(), handymanReviewProfile.getCountReviewers()));
            rtCountPoint.setRating(handymanReviewProfile.getAverageReviewPoint());

            handymanReviewResponseList.clear();
            handymanReviewResponseList.addAll(handymanReviewProfile.getHandymanReviewResponseList());
            handymanReviewAdapter.notifyDataSetChanged();
        });
    }
}
