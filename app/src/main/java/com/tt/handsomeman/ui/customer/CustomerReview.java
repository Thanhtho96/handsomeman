package com.tt.handsomeman.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.databinding.ActivityReviewBinding;
import com.tt.handsomeman.request.ReviewRequest;
import com.tt.handsomeman.response.DataBracketResponse;
import com.tt.handsomeman.response.ReviewResponse;
import com.tt.handsomeman.response.StandardResponse;
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

        String token = sharedPreferencesUtils.get("token", String.class);
        int handymanId = getIntent().getIntExtra("handymanId", 0);
        int jobId = getIntent().getIntExtra("jobId", 0);

        bindView();
        fetchReview(token, handymanId);
        listenToRatingBar();
        makeReview(token, handymanId, jobId);
    }

    private void listenToRatingBar() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar,
                                        float rating,
                                        boolean fromUser) {
                btnCheck.setEnabled(rating != 0f);
            }
        });
    }

    private void makeReview(String token,
                            int handymanId,
                            int jobId) {
        btnCheck.setOnClickListener(v -> {
            baseViewModel.reviewHandyman(token, new ReviewRequest(jobId,
                    handymanId,
                    (int) ratingBar.getRating(),
                    edtComment.getText().toString().trim()));
            baseViewModel.getStandardResponseMutableLiveData().observe(this, new Observer<StandardResponse>() {
                @Override
                public void onChanged(StandardResponse standardResponse) {
                    Toast.makeText(CustomerReview.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                        Intent intent = new Intent();
                        intent.putExtra("reviewed", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        });
    }

    private void fetchReview(String token,
                             int handymanId) {
        baseViewModel.loadReviewWithHandyman(token, handymanId);
        baseViewModel.getReviewResponseLiveData().observe(this, new Observer<DataBracketResponse<ReviewResponse>>() {
            @Override
            public void onChanged(DataBracketResponse<ReviewResponse> reviewResponseDataBracketResponse) {
                ratingBar.setRating((float) reviewResponseDataBracketResponse.getData().getVote());
                edtComment.setText(reviewResponseDataBracketResponse.getData().getComment());
            }
        });
    }

    private void bindView() {
        btnCheck = binding.check;
        ratingBar = binding.ratingBar;
        edtComment = binding.editTextComment;
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
