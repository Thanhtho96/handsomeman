package com.tt.handsomeman.ui.customer.find_handyman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.HandymanReviewAdapter;
import com.tt.handsomeman.adapter.SkillAdapter;
import com.tt.handsomeman.databinding.ActivityHandymanDetailBinding;
import com.tt.handsomeman.model.Skill;
import com.tt.handsomeman.request.HandymanDetailRequest;
import com.tt.handsomeman.response.HandymanReviewResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.ui.customer.CustomerReview;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.DecimalFormat;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class HandymanDetail extends BaseAppCompatActivityWithViewModel<CustomerViewModel> {

    private static final Integer REVIEW_REQUEST = 777;
    private static boolean reviewed;
    private final List<Skill> skillList = new ArrayList<>();
    private final List<HandymanReviewResponse> handymanReviewResponses = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ConstraintLayout container;
    private TextView tvDistance, accountName, education, about, allProjects, successedProject, countReviews;
    private RatingBar rtCountPoint;
    private Button btnInviteToProject, btnReview;
    private SkillAdapter skillAdapter;
    private RecyclerView rcvSkill, rcvReview;
    private HandymanReviewAdapter handymanReviewAdapter;
    private int handymanId;
    private ActivityHandymanDetailBinding binding;
    private String authorizationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHandymanDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);

        bindView();

        Intent intent = getIntent();
        boolean succeed = intent.getBooleanExtra("succeed", false);
        boolean accepted = intent.getBooleanExtra("accepted", false);
        handymanId = intent.getIntExtra("handymanId", 0);
        int jobId = intent.getIntExtra("jobId", 0);

        if (succeed) {
            container.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnReview.setVisibility(View.VISIBLE);
        } else if (!accepted) {
            container.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnInviteToProject.setVisibility(View.VISIBLE);
        }

        btnReview.setOnClickListener(v -> {
            Intent startIntent = new Intent(HandymanDetail.this, CustomerReview.class);
            startIntent.putExtra("handymanId", handymanId);
            startIntent.putExtra("jobId", jobId);
            startActivityForResult(startIntent, REVIEW_REQUEST);
        });

        goBack();
        createSkillRecyclerView();
        createHandymanReviewRecycleView();
        fetchHandymanDetail(handymanId);
    }

    @Override
    public void onBackPressed() {

        if (reviewed) {
            Intent intent = new Intent();
            intent.putExtra("reviewed", true);
            setResult(RESULT_OK, intent);
            finish();
        }

        super.onBackPressed();
    }

    private void bindView() {
        container = binding.container;
        tvDistance = binding.textViewHandymanDistance;
        accountName = binding.accountName;
        education = binding.education;
        about = binding.about;
        allProjects = binding.allProjects;
        successedProject = binding.successedProjects;
        btnInviteToProject = binding.buttonInviteToProject;
        btnReview = binding.buttonReview;
        rcvSkill = binding.mySkillRecyclerView;
        rcvReview = binding.reviewHandymanDetailRecycleView;
        countReviews = binding.reviewCountHandymanDetail;
        rtCountPoint = binding.ratingBarHandymanDetail;
    }

    private void fetchHandymanDetail(int handymanId) {
        Double lat = Constants.Latitude.getValue();
        Double lng = Constants.Longitude.getValue();


        baseViewModel.fetchHandymanDetail(authorizationCode, new HandymanDetailRequest(lat, lng, handymanId));
        baseViewModel.getHandymanDetailResponseMutableLiveData().observe(this, handymanDetailResponse -> {
            int distance = Integer.parseInt(DecimalFormat.formatGetDistance(handymanDetailResponse.getDistance() * 1000));
            tvDistance.setText(HandymanApp.getInstance().getResources().getQuantityString(R.plurals.handymanDistance, distance, distance));

            accountName.setText(handymanDetailResponse.getHandymanName());
            education.setText(handymanDetailResponse.getEducation());
            about.setText(handymanDetailResponse.getDetail());
            allProjects.setText(String.valueOf(handymanDetailResponse.getAllProject()));
            successedProject.setText(String.valueOf(handymanDetailResponse.getSuccessedProject()));
            countReviews.setText(getResources().getQuantityString(R.plurals.numberOfReview, handymanDetailResponse.getCountReviewers(), handymanDetailResponse.getCountReviewers()));
            rtCountPoint.setRating(handymanDetailResponse.getAverageReviewPoint());

            GlideUrl glideUrl = new GlideUrl((handymanDetailResponse.getAvatar()),
                    new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

            Glide.with(HandymanDetail.this)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .placeholder(R.drawable.custom_progressbar)
                    .error(R.drawable.logo)
                    .signature(new MediaStoreSignature("", handymanDetailResponse.getUpdateDate(), 0))
                    .into(binding.accountAvatar);

            handymanReviewResponses.clear();
            handymanReviewResponses.addAll(handymanDetailResponse.getHandymanReviewResponseList());
            handymanReviewAdapter.notifyDataSetChanged();

            skillList.clear();
            skillList.addAll(handymanDetailResponse.getSkillList());
            skillAdapter.notifyDataSetChanged();
        });
    }

    private void goBack() {
        binding.handymanDetailBackButton.setOnClickListener(v -> onBackPressed());
    }

    private void createHandymanReviewRecycleView() {
        handymanReviewAdapter = new HandymanReviewAdapter(handymanReviewResponses, this, authorizationCode);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvReview.setLayoutManager(layoutManager);
        rcvReview.setItemAnimator(new DefaultItemAnimator());
        rcvReview.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvReview.setAdapter(handymanReviewAdapter);
    }

    private void createSkillRecyclerView() {
        skillAdapter = new SkillAdapter(skillList, this);
        RecyclerView.LayoutManager layoutManagerPayout = new LinearLayoutManager(this);
        rcvSkill.setLayoutManager(layoutManagerPayout);
        rcvSkill.setItemAnimator(new DefaultItemAnimator());
        rcvSkill.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvSkill.setAdapter(skillAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {

        if (data != null && requestCode == REVIEW_REQUEST && resultCode == RESULT_OK) {
            reviewed = data.getBooleanExtra("reviewed", false);
            if (reviewed) {
                fetchHandymanDetail(handymanId);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
