package com.tt.handsomeman.ui.customer.my_projects.add_job;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityAddNewJobBinding;
import com.tt.handsomeman.request.AddJobRequest;
import com.tt.handsomeman.ui.BaseFragmentActivity;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import me.relex.circleindicator.CircleIndicator3;

public class AddNewJob extends BaseFragmentActivity<CustomerViewModel, ActivityAddNewJobBinding> {

    private static final int NUM_PAGES = 3;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    ViewPager2 viewPager;
    AddJobRequest addJobRequest;
    private LinearLayout container;
    private Button btnSubmit;
    private FragmentStateAdapter pagerAdapter;
    private OnPageChangeCallback pageChangeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityAddNewJobBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        addJobRequest = new AddJobRequest();
        btnSubmit = viewBinding.buttonSubmit;
        container = viewBinding.container;

        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);

        generateViewPager();
        goBackward();
        setViewPagerUILogic();
        addNewJob();
    }

    private void addNewJob() {
        btnSubmit.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
            String token = sharedPreferencesUtils.get("token", String.class);
            addJobRequest.setCreateTime(simpleDateFormat.format(now.getTime()));

            baseViewModel.addNewJob(token, addJobRequest);
            baseViewModel.getStandardResponseMutableLiveData().observe(this, standardResponse -> {
                Toast.makeText(AddNewJob.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        });
    }

    private void setViewPagerUILogic() {
        viewBinding.imageButtonCheckFirst.setEnabled(false);
        viewBinding.imageButtonCheckSecond.setEnabled(false);

        viewPager.registerOnPageChangeCallback(pageChangeCallback = new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        container.setBackgroundColor(getResources().getColor(R.color.white));
                        viewBinding.imageButtonCheckFirst.setVisibility(View.VISIBLE);
                        viewBinding.imageButtonCheckSecond.setVisibility(View.INVISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        break;
                    case 1:
                        container.setBackgroundColor(getResources().getColor(R.color.white));
                        viewBinding.imageButtonCheckFirst.setVisibility(View.INVISIBLE);
                        viewBinding.imageButtonCheckSecond.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        break;
                    case 2:
                        container.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        viewBinding.imageButtonCheckFirst.setVisibility(View.INVISIBLE);
                        viewBinding.imageButtonCheckSecond.setVisibility(View.INVISIBLE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private void generateViewPager() {
        viewPager = viewBinding.viewPager;
        viewPager.setUserInputEnabled(false);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        CircleIndicator3 indicator = viewBinding.circleIndicator;
        indicator.setViewPager(viewPager);
    }

    private void goBackward() {
        viewBinding.backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onDestroy() {
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new AddNewJobChildFirstFragment();
                case 1:
                    return new AddNewJobChildSecondFragment();
                default:
                    return new AddNewJobChildThirdFragment();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
