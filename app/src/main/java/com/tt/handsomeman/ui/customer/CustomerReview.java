package com.tt.handsomeman.ui.customer;

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
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import javax.inject.Inject;

public class CustomerReview extends BaseAppCompatActivityWithViewModel<CustomerViewModel> {

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
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);

        int handymanId = getIntent().getIntExtra("handymanId", 0);
        int jobId = getIntent().getIntExtra("jobId", 0);

        bindView();
        fetchReview(handymanId);
        listenToRatingBar();
        makeReview(handymanId, jobId);
    }

    private void listenToRatingBar() {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> btnCheck.setEnabled(rating != 0f));
    }

    private void makeReview(int handymanId,
                            int jobId) {
        btnCheck.setOnClickListener(v -> {
            baseViewModel.reviewHandyman(new ReviewRequest(jobId,
                    handymanId,
                    (int) ratingBar.getRating(),
                    edtComment.getText().toString().trim()));
            baseViewModel.getStandardResponseMutableLiveData().observe(this, standardResponse -> {
                Toast.makeText(CustomerReview.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    Intent intent = new Intent();
                    intent.putExtra("reviewed", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        });
    }

    private void fetchReview(int handymanId) {
        baseViewModel.loadReviewWithHandyman(handymanId);
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
