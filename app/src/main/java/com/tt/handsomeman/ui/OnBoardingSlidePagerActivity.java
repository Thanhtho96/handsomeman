package com.tt.handsomeman.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityOnBoardingSlidePagerBinding;
import com.tt.handsomeman.ui.customer.CustomerMainScreen;
import com.tt.handsomeman.ui.handyman.HandyManMainScreen;
import com.tt.handsomeman.ui.handyman.SignUpAddPayout;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomViewPager;
import com.tt.handsomeman.util.RoleName;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import java.util.Locale;

import javax.inject.Inject;

import me.relex.circleindicator.CircleIndicator;

public class OnBoardingSlidePagerActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private CustomViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    private ConstraintLayout skip;
    private ActivityOnBoardingSlidePagerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingSlidePagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HandymanApp.getComponent().inject(this);

        sharedPreferencesUtils.put("language", Locale.getDefault().getLanguage());
        Integer state = sharedPreferencesUtils.get("state", Integer.class);
        String type = sharedPreferencesUtils.get("type", String.class);
        Constants.language.setValue(sharedPreferencesUtils.get("language", String.class));

        skip = binding.skipOnBoarding;

        skip.setOnClickListener(view -> {
            if (!type.equals("")) {
                switch (RoleName.valueOf(type)) {
                    case ROLE_HANDYMAN:
                        if (state.equals(Constants.NOT_ACTIVE_ACCOUNT)) {
                            startActivity(new Intent(OnBoardingSlidePagerActivity.this, SignUpAddPayout.class));
                        } else {
                            startActivity(new Intent(OnBoardingSlidePagerActivity.this, Start.class));
                        }
                        finish();
                        break;
                    case ROLE_CUSTOMER:
                        if (state.equals(Constants.NOT_ACTIVE_ACCOUNT) || state.equals(Constants.STATE_REGISTER_ADDED_PAYOUT)) {
                            startActivity(new Intent(OnBoardingSlidePagerActivity.this, CustomerMainScreen.class));
                        } else {
                            startActivity(new Intent(OnBoardingSlidePagerActivity.this, Start.class));
                        }
                        finish();
                        break;
                }
            } else {
                startActivity(new Intent(OnBoardingSlidePagerActivity.this, Start.class));
                finish();
            }
        });

        if (!type.equals("")) {
            switch (RoleName.valueOf(type)) {
                case ROLE_HANDYMAN:
                    if (state.equals(Constants.NOT_ACTIVE_ACCOUNT)) {
                        startActivity(new Intent(OnBoardingSlidePagerActivity.this, SignUpAddPayout.class));
                        finish();
                    } else if (state.equals(Constants.STATE_REGISTER_ADDED_PAYOUT)) {
                        startActivity(new Intent(OnBoardingSlidePagerActivity.this, HandyManMainScreen.class));
                        finish();
                    }
                    break;
                case ROLE_CUSTOMER:
                    if (state.equals(Constants.NOT_ACTIVE_ACCOUNT) || state.equals(Constants.STATE_REGISTER_ADDED_PAYOUT)) {
                        startActivity(new Intent(OnBoardingSlidePagerActivity.this, CustomerMainScreen.class));
                        finish();
                    }
                    break;
            }
        }

        Places.initialize(this, "AIzaSyBzEI6AAHYuzXQOj9fdD2fEpH2gTcaam48");
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        Constants.placesClientMutableLiveData.setValue(placesClient);
        Constants.autocompleteSessionTokenMutableLiveData.setValue(token);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = binding.boardingPager;

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(pagerAdapter);

        CircleIndicator indicator = binding.boardingIndicator;
        indicator.setViewPager(mPager);

        mPager.disableScroll(false);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 OnBoardingSlidePageFragment objects, in
     * sequence.
     */
    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return OnBoardingSlidePageFragment.newInstance(R.drawable.on_boarding_1, R.string.boarding_des_1);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return OnBoardingSlidePageFragment.newInstance(R.drawable.on_boarding_2, R.string.boarding_des_2);
                case 2: // Fragment # 1 - This will show SecondFragment
                    return OnBoardingSlidePageFragment.newInstance(R.drawable.on_boarding_3, R.string.boarding_des_3);
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