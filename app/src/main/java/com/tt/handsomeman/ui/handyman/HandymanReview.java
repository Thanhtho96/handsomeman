package com.tt.handsomeman.ui.handyman;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.databinding.ActivityReviewBinding;
import com.tt.handsomeman.request.ReviewRequest;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import javax.inject.Inject;

public class HandymanReview extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ImageButton btnCheck;
    private RatingBar ratingBar;
    private EditText edtComment;
    private ActivityReviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        int customerId = getIntent().getIntExtra("customerId", 0);
        int jobId = getIntent().getIntExtra("jobId", 0);

        bindView();
        fetchReview(customerId);
        listenToRatingBar();
        makeReview(customerId, jobId);
    }

    private void listenToRatingBar() {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> btnCheck.setEnabled(rating != 0f));
    }

    private void makeReview(int customerId,
                            int jobId) {
        btnCheck.setOnClickListener(v -> {
            baseViewModel.reviewCustomer(new ReviewRequest(jobId,
                    customerId,
                    (int) ratingBar.getRating(),
                    edtComment.getText().toString().trim()));
            baseViewModel.getStandardResponseMutableLiveData().observe(this, standardResponse -> {
                Toast.makeText(HandymanReview.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    Intent intent = new Intent();
                    intent.putExtra("reviewed", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        });
    }

    private void fetchReview(int customerId) {
        baseViewModel.loadReviewWithCustomer(customerId);
        baseViewModel.getReviewResponseLiveData().observe(this, reviewResponseDataBracketResponse -> {
            ratingBar.setRating((float) reviewResponseDataBracketResponse.getData().getVote());
            edtComment.setText(reviewResponseDataBracketResponse.getData().getComment());
        });
    }

    private void bindView() {
        btnCheck = binding.check;
        ratingBar = binding.ratingBar;
        edtComment = binding.editTextComment;
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }

}
