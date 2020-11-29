package com.tt.handsomeman.ui.customer.my_projects;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityMyJobDetailBinding;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.model.PaymentMilestone;
import com.tt.handsomeman.response.CustomerJobDetail;
import com.tt.handsomeman.response.HandymanResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.ui.customer.find_handyman.HandymanDetail;
import com.tt.handsomeman.ui.messages.Conversation;
import com.tt.handsomeman.util.DimensionConverter;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.util.List;

import javax.inject.Inject;

public class MyJobDetail extends BaseAppCompatActivityWithViewModel<CustomerViewModel> {

    private static final Integer REVIEW_REQUEST = 777;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ImageView imgHandymanAvatar;
    private RatingBar rtReview;
    private TextView tvJobTitle, tvJobId, tvJobCreateTime, tvJobDetail,
            tvBudgetRange, tvJobDeadline, tvJobLocation,
            tvHired, tvPaymentMilestoneCount, tvHandymanName, tvReviewCount, tvShowHandymanProfile;
    private TableLayout tlMileStone;
    private LinearLayout layoutHandyman;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private boolean succeed = false, accepted = false;
    private int jobId;
    private ActivityMyJobDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyJobDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);

        jobId = getIntent().getIntExtra("jobId", 0);

        bindView();
        goBack();
        fetchData(jobId);
    }

    private void goBack() {
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }

    private void bindView() {
        rtReview = binding.ratingBar;
        tvJobTitle = binding.jobTitle;
        tvJobId = binding.jobId;
        tvJobCreateTime = binding.jobCreateTime;
        tvJobDetail = binding.jobDetail;
        tvBudgetRange = binding.budgetRange;
        tvJobDeadline = binding.deadline;
        tvJobLocation = binding.location;
        tvHired = binding.hired;
        tvPaymentMilestoneCount = binding.paymentMileStoneCount;
        tvHandymanName = binding.handymanName;
        tvReviewCount = binding.reviewCount;
        tvShowHandymanProfile = binding.showHandymanProfile;
        tlMileStone = binding.paymentMileStoneTableLayout;
        imgHandymanAvatar = binding.handymanAvatar;
        layoutHandyman = binding.handymanLayout;

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMyJobDetail);
    }

    private void fetchData(Integer jobId) {
        String authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel.fetchCustomerJobDetail(jobId);

        baseViewModel.getCustomerJobDetailMutableLiveData().observe(this, customerJobDetail -> {
            Job job = customerJobDetail.getJob();
            tvJobTitle.setText(job.getTitle());
            tvJobId.setText(String.valueOf(job.getId()));

            tvJobCreateTime.setText(getResources().getQuantityString(R.plurals.numberOfHour, job.setCreateTimeBinding(), job.setCreateTimeBinding()));
            tvJobDeadline.setText(getResources().getQuantityString(R.plurals.numberOfDay, job.setDeadlineBinding(), job.setDeadlineBinding()));
            tvJobDetail.setText(job.getDetail());
            tvBudgetRange.setText(job.setBudgetRange());
            tvJobLocation.setText(job.getLocation());

            List<PaymentMilestone> listPaymentMilestone = customerJobDetail.getListPaymentMilestone();
            tvPaymentMilestoneCount.setText(String.valueOf(listPaymentMilestone.size()));

            tlMileStone.removeAllViews();
            for (int i = 0; i < listPaymentMilestone.size(); i++) {
                TableRow tr = new TableRow(MyJobDetail.this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));

                TextView b = new TextView(MyJobDetail.this);
                b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                b.setTextColor(getResources().getColor(R.color.text_white_bg));
                b.setTextSize(DimensionConverter.spToPx(getResources().getDimension(R.dimen.design_3_3sp), MyJobDetail.this));
                b.setGravity(Gravity.START);
                switch ((i + 1) % 10) {
                    case 1:
                        b.setText(getString(R.string.first_milestone, i + 1));
                        break;
                    case 2:
                        b.setText(getString(R.string.second_milestone, i + 1));
                        break;
                    case 3:
                        b.setText(getString(R.string.third_milestone, i + 1));
                        break;
                    default:
                        b.setText(getString(R.string.default_milestone, i + 1));
                        break;
                }

                TextView b2 = new TextView(MyJobDetail.this);
                b2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                b2.setTextColor(getResources().getColor(R.color.text_white_bg));
                b2.setTextSize(DimensionConverter.spToPx(getResources().getDimension(R.dimen.design_3_3sp), getApplicationContext()));
                b2.setGravity(Gravity.END);
                b2.setText(getString(R.string.percentage, listPaymentMilestone.get(i).getPercentage()));
                tr.addView(b);
                tr.addView(b2);

                tlMileStone.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }

            Double lat = job.getLat();
            Double lng = job.getLng();

            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;

                LatLng jobLocation = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(jobLocation).title(job.getLocation()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jobLocation, 15));
                mMap.getUiSettings().setScrollGesturesEnabled(false);
            });

            if (customerJobDetail.isAccepted()) {
                tvHired.setText(getString(R.string.yes));

                layoutHandyman.setVisibility(View.VISIBLE);
                loadHandyman(customerJobDetail, authorizationCode);
            } else {
                tvHired.setText(getString(R.string.no));
            }

            accepted = customerJobDetail.isAccepted();
            succeed = customerJobDetail.isSucceed();
        });
    }

    private void loadHandyman(CustomerJobDetail customerJobDetail,
                              String authorizationCode) {
        HandymanResponse handymanResponse = customerJobDetail.getHandyman();
        tvHandymanName.setText(handymanResponse.getName());
        rtReview.setRating(handymanResponse.getAverageReviewPoint());
        tvReviewCount.setText(getResources().getQuantityString(R.plurals.numberOfReview, handymanResponse.getCountReviewers(), handymanResponse.getCountReviewers()));
        binding.imageButtonSendMessage.setOnClickListener(v -> {
            Intent intent = new Intent(MyJobDetail.this, Conversation.class);
            intent.putExtra("addressName", handymanResponse.getName());
            intent.putExtra("receiveId", handymanResponse.getHandymanId());
            startActivity(intent);
        });
        tvShowHandymanProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MyJobDetail.this, HandymanDetail.class);
            intent.putExtra("handymanId", handymanResponse.getHandymanId());
            intent.putExtra("succeed", succeed);
            intent.putExtra("accepted", accepted);
            if (succeed) {
                intent.putExtra("jobId", jobId);
            }
            startActivityForResult(intent, REVIEW_REQUEST);
        });

        GlideUrl glideUrl = new GlideUrl((customerJobDetail.getHandyman().getAvatar()),
                new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

        Glide.with(MyJobDetail.this)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .placeholder(R.drawable.custom_progressbar)
                .error(R.drawable.logo)
                .signature(new MediaStoreSignature("", customerJobDetail.getHandyman().getUpdateDate(), 0))
                .into(imgHandymanAvatar);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {

        if (data != null && requestCode == REVIEW_REQUEST && resultCode == RESULT_OK) {
            boolean reviewed = data.getBooleanExtra("reviewed", false);
            if (reviewed) {
                fetchData(jobId);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
