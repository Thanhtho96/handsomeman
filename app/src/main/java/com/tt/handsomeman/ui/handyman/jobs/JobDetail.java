package com.tt.handsomeman.ui.handyman.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityJobDetailBinding;
import com.tt.handsomeman.model.CustomerResponse;
import com.tt.handsomeman.model.HandymanJobDetail;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.model.PaymentMilestone;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.ui.BaseAppCompatActivity;
import com.tt.handsomeman.ui.handyman.jobs.bid_job_detail.BidJobDetail;
import com.tt.handsomeman.ui.messages.Conversation;
import com.tt.handsomeman.util.DimensionConverter;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.util.List;

import javax.inject.Inject;

public class JobDetail extends BaseAppCompatActivity<HandymanViewModel> {

    private static HandymanJobDetail handymanJobDetail;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ImageView imClientAvatar, imIsWish;
    private RatingBar rtReview;
    private TextView tvJobTitle, tvJobId, tvJobCreateTime, tvJobDetail,
            tvBudgetRange, tvJobDeadline, tvJobLocation,
            tvHired, tvPaymentMilestoneCount, tvClientName, tvReviewCount, tvShowClientProfile;
    private ImageButton ibSendMessage;
    private Button btnPlaceABid, btnViewTransaction;
    private TableLayout tlMileStone;
    private GoogleMap mMap;
    private boolean isAccept, isRead, isReadForFirstTime = false;
    private int notificationId, notificationPos;
    private ActivityJobDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        Intent intent = getIntent();
        Integer jobId = intent.getIntExtra("jobId", 0);
        notificationId = intent.getIntExtra("notificationId", 0);
        isRead = intent.getBooleanExtra("isRead", false);
        notificationPos = intent.getIntExtra("notificationPos", 0);

        bindView();
        goBack();
        fetchData(jobId);
        showClientProfile();
    }

    private void bindView() {
        rtReview = binding.ratingBarJobDetail;
        tvJobTitle = binding.jobTitleJobDetail;
        tvJobId = binding.jobIdJobDetail;
        tvJobCreateTime = binding.jobCreateTimeJobDetail;
        tvJobDetail = binding.jobDetailJobDetail;
        tvBudgetRange = binding.budgetRangeJobDetail;
        tvJobDeadline = binding.deadlineJobDetail;
        tvJobLocation = binding.locationJobDetail;
        tvHired = binding.hiredJobDetail;
        tvPaymentMilestoneCount = binding.paymentMileStoneCountJobDetail;
        tvClientName = binding.clientNameJobDetail;
        tvReviewCount = binding.reviewCountJobDetail;
        tvShowClientProfile = binding.showClientProfileJobDetail;
        tlMileStone = binding.paymentMileStoneTableLayoutJobDetail;
        imClientAvatar = binding.clientAvatarJobDetail;
        imIsWish = binding.isWishListImage;
        btnPlaceABid = binding.buttonPlaceBidJobDetail;
        btnViewTransaction = binding.viewTransaction;
        ibSendMessage = binding.imageButtonSendMessage;
    }

    private void goBack() {
        binding.jobDetailBackButton.setOnClickListener(v -> {
            handymanJobDetail = null;
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        if (notificationId != 0) {
            Intent intent = new Intent();
            intent.putExtra("isRead", isRead);
            intent.putExtra("isReadForFirstTime", this.isReadForFirstTime);
            intent.putExtra("notificationPos", notificationPos);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void bidJob() {
        Intent intent = new Intent(JobDetail.this, BidJobDetail.class);
        intent.putExtra("handymanJobDetail", handymanJobDetail);
        startActivity(intent);
    }

    private void showClientProfile() {
        tvShowClientProfile.setOnClickListener(v -> {
            Intent intent = new Intent(JobDetail.this, CustomerProfileJobDetail.class);
            intent.putExtra("customerId", handymanJobDetail.getCustomer().getAccountId());
            intent.putExtra("isAccept", this.isAccept);
            startActivity(intent);
        });
    }

    private void fetchData(Integer jobId) {
        String authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel.fetchHandymanJobDetail(authorizationCode, jobId);

        baseViewModel.getJobDetailLiveData().observe(this, jobDetail -> {
            JobDetail.handymanJobDetail = jobDetail;

            Job job = jobDetail.getJob();
            tvJobTitle.setText(job.getTitle());
            tvJobId.setText(String.valueOf(job.getId()));

            tvJobCreateTime.setText(getResources().getQuantityString(R.plurals.numberOfHour, job.setCreateTimeBinding(), job.setCreateTimeBinding()));
            tvJobDeadline.setText(getResources().getQuantityString(R.plurals.numberOfDay, job.setDeadlineBinding(), job.setDeadlineBinding()));
            tvJobDetail.setText(job.getDetail());
            tvBudgetRange.setText(job.setBudgetRange());
            tvJobLocation.setText(job.getLocation());

            List<PaymentMilestone> listPaymentMilestone = jobDetail.getListPaymentMilestone();
            tvPaymentMilestoneCount.setText(String.valueOf(listPaymentMilestone.size()));
            for (int i = 0; i < listPaymentMilestone.size(); i++) {
                TableRow tr = new TableRow(JobDetail.this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));

                TextView b = new TextView(JobDetail.this);
                b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                b.setTextColor(getResources().getColor(R.color.text_white_bg));
                b.setTextSize(DimensionConverter.spToPx(getResources().getDimension(R.dimen.design_3_3sp), JobDetail.this));
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

                TextView b2 = new TextView(JobDetail.this);
                b2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
                b2.setTextColor(getResources().getColor(R.color.text_white_bg));
                b2.setTextSize(DimensionConverter.spToPx(getResources().getDimension(R.dimen.design_3_3sp), getApplicationContext()));
                b2.setGravity(Gravity.END);
                b2.setText(getString(R.string.percentage, listPaymentMilestone.get(i).getPercentage()));
                tr.addView(b);
                tr.addView(b2);

                tlMileStone.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }

            CustomerResponse customerResponse = jobDetail.getCustomer();
            tvClientName.setText(customerResponse.getCustomerName());
            Integer countReview = customerResponse.getCountReviewers();
            tvReviewCount.setText(getResources().getQuantityString(R.plurals.numberOfReview, countReview, countReview));
            rtReview.setRating(customerResponse.getAverageReviewPoint());

            Double lat = job.getLat();
            Double lng = job.getLng();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    LatLng jobLocation = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(jobLocation).title(job.getLocation()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jobLocation, 15));
                    mMap.getUiSettings().setScrollGesturesEnabled(false);
                }
            });

            if (jobDetail.isBid()) {
                imIsWish.setImageResource(R.drawable.ic_hearted);
            }

            if (jobDetail.isAccepted()) {
                tvHired.setText(getString(R.string.yes));
                this.isAccept = true;
                btnViewTransaction.setVisibility(View.VISIBLE);
                btnViewTransaction.setOnClickListener(v -> {
                    // TODO: View transaction
                });
                ibSendMessage.setVisibility(View.VISIBLE);
                ibSendMessage.setOnClickListener(v -> {
                    sendMessage(jobDetail.getCustomer().getCustomerName(), jobDetail.getJob().getCustomerId());
                });
            } else if (jobDetail.isBid()){
                this.isAccept = false;
                tvHired.setText(getString(R.string.no));
            } else{
                this.isAccept = false;
                btnPlaceABid.setVisibility(View.VISIBLE);
                btnPlaceABid.setOnClickListener(v -> bidJob());
                tvHired.setText(getString(R.string.no));
            }

            if (!isRead) {
                markAsRead(notificationId, authorizationCode);
            }
        });
    }

    private void sendMessage(String receiverName, Integer customerId) {
        Intent intent = new Intent(JobDetail.this, Conversation.class);
        intent.putExtra("addressName", receiverName);
        intent.putExtra("receiveId", customerId);
        startActivity(intent);
    }

    private void markAsRead(Integer notificationId, String token) {
        if (!isRead) {
            baseViewModel.markNotificationAsRead(token, notificationId);
            baseViewModel.getStandardResponseMarkReadMutableLiveData().observe(this, new Observer<StandardResponse>() {
                @Override
                public void onChanged(StandardResponse standardResponse) {
                    if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                        isRead = true;
                        JobDetail.this.isReadForFirstTime = true;
                    }
                }
            });
        }
    }
}