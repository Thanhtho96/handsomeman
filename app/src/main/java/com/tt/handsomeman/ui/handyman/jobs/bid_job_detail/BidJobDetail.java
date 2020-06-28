package com.tt.handsomeman.ui.handyman.jobs.bid_job_detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityBidJobDetailBinding;
import com.tt.handsomeman.model.FileRequest;
import com.tt.handsomeman.model.HandymanJobDetail;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.ui.BaseFragmentActivity;
import com.tt.handsomeman.ui.handyman.HandyManMainScreen;
import com.tt.handsomeman.util.CustomViewPager;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusCodeConstant;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BidJobDetail extends BaseFragmentActivity<HandymanViewModel, ActivityBidJobDetailBinding> {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    JobBidRequest jobBidRequest;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private CustomViewPager mPager;
    private HandymanJobDetail handymanJobDetail;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;
    private ConstraintLayout container;
    private TextView tvViewPagerName;
    private Button btnSubmit;
    private ImageButton ibCheckButtonBudget, ibCheckButtonLetter;
    private FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityBidJobDetailBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        jobBidRequest = new JobBidRequest();
        handymanJobDetail = (HandymanJobDetail) getIntent().getSerializableExtra("handymanJobDetail");

        bindView();
        generateViewPager();
        viewPagerUILogic();
        goBackward();

        bidJob();
    }

    private void bidJob() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlphaAnimation inAnimation;

                progressBarHolder.bringToFront();
                inAnimation = new AlphaAnimation(0f, 1f);
                inAnimation.setDuration(300);
                progressBarHolder.setAnimation(inAnimation);
                progressBarHolder.setVisibility(View.VISIBLE);

                btnSubmit.setEnabled(false);

                Calendar now = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
                String sendTime = formatter.format(now.getTime());

                String authorizationCode = sharedPreferencesUtils.get("token", String.class);

                RequestBody bid = RequestBody.create(jobBidRequest.getBid(), MultipartBody.FORM);
                RequestBody description = RequestBody.create(jobBidRequest.getDescription(), MultipartBody.FORM);
                RequestBody jobId = RequestBody.create(String.valueOf(handymanJobDetail.getJob().getId()), MultipartBody.FORM);
                RequestBody serviceFee = RequestBody.create(jobBidRequest.getServiceFee(), MultipartBody.FORM);
                RequestBody bidTime = RequestBody.create(sendTime, MultipartBody.FORM);

                List<MultipartBody.Part> body = new ArrayList<>();
                List<RequestBody> md5List = new ArrayList<>();

                for (FileRequest fileRequest : jobBidRequest.getFileRequestList()) {
                    File file = new File(fileRequest.getFileDir());
                    if (file.exists()) {
                        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
                        RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
                        body.add(MultipartBody.Part.createFormData("files", file.getName(), requestFile));
                        md5List.add(RequestBody.create(fileRequest.getMd5(), MultipartBody.FORM));
                    }
                }

                baseViewModel.addJobBid(authorizationCode, bid, description, body, jobId, serviceFee, bidTime, md5List);
                baseViewModel.getStandardResponseMutableLiveData().observe(BidJobDetail.this, new Observer<StandardResponse>() {
                    @Override
                    public void onChanged(StandardResponse standardResponse) {
                        Toast.makeText(BidJobDetail.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (standardResponse.getStatusCode().equals(StatusCodeConstant.CREATED)) {
                            Intent intent = new Intent(BidJobDetail.this, HandyManMainScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("radioButtonChoice", 1);
                            startActivity(intent);
                        }
                        AlphaAnimation outAnimation;

                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        progressBarHolder.setAnimation(outAnimation);
                        progressBarHolder.setVisibility(View.GONE);

                        btnSubmit.setEnabled(true);
                    }
                });
            }
        });
    }

    private void goBackward() {
        viewBinding.bidJobDetailBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void bindView() {
        container = viewBinding.container;
        tvViewPagerName = viewBinding.bidJobDetailViewPagerName;
        ibCheckButtonBudget = viewBinding.imageButtonCheckBudgetBidJobDetail;
        ibCheckButtonLetter = viewBinding.imageButtonCheckLetterBidJobDetail;
        btnSubmit = viewBinding.submitBidJobDetail;
        mPager = viewBinding.bidJobDetailPager;
        progressBarHolder = viewBinding.progressBarHolder;
    }

    private void viewPagerUILogic() {
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        container.setBackgroundColor(getResources().getColor(R.color.white));
                        ibCheckButtonBudget.setVisibility(View.VISIBLE);
                        ibCheckButtonLetter.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.GONE);
                        tvViewPagerName.setText(getResources().getText(R.string.budget));
                        break;
                    case 1:
                        container.setBackgroundColor(getResources().getColor(R.color.white));
                        ibCheckButtonBudget.setVisibility(View.GONE);
                        ibCheckButtonLetter.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        tvViewPagerName.setText(getResources().getText(R.string.letter));
                        break;
                    case 2:
                        container.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        ibCheckButtonBudget.setVisibility(View.GONE);
                        ibCheckButtonLetter.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void generateViewPager() {
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(pagerAdapter);

        CircleIndicator indicator = viewBinding.bidJobDetailIndicator;
        indicator.setViewPager(mPager);

        mPager.disableScroll(true);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            if (btnSubmit.isEnabled()) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return BidJobBudgetFragment.newInstance(handymanJobDetail);
                case 1: // Fragment # 0 - This will show FirstFragment
                    return new BidJobLetterFragment();
                case 2:
                    return BidJobLetterReviewFragment.newInstance(handymanJobDetail);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
