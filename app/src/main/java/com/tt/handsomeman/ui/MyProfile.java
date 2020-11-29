package com.tt.handsomeman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityMyProfileBinding;
import com.tt.handsomeman.ui.customer.more.CustomerProfileAboutFragment;
import com.tt.handsomeman.ui.customer.more.CustomerProfileEdit;
import com.tt.handsomeman.ui.customer.more.CustomerProfileReviewFragment;
import com.tt.handsomeman.ui.handyman.more.MyProfileAboutFragment;
import com.tt.handsomeman.ui.handyman.more.MyProfileEdit;
import com.tt.handsomeman.ui.handyman.more.MyProfileReviewsFragment;
import com.tt.handsomeman.util.RoleName;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import javax.inject.Inject;

public class MyProfile extends BaseAppCompatActivity {
    private static final Integer REQUEST_MY_PROFILE_RESULT_CODE = 77;

    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;

    private Fragment aboutFragment;
    private Fragment reviewsFragment;
    private Fragment active;
    private ImageButton ibEdit;
    private boolean isEdit = false;
    private ActivityMyProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        String type = sharedPreferencesUtils.get("type", String.class);

        RadioButton rdAbout = binding.radioButtonAbout;
        RadioButton rdReviews = binding.radioButtonReviews;
        ibEdit = binding.imageButtonMyProfileEdit;
        goBack();

        switch (RoleName.valueOf(type)) {
            case ROLE_CUSTOMER:
                aboutFragment = new CustomerProfileAboutFragment();
                reviewsFragment = new CustomerProfileReviewFragment();
                editCustomerProfile();
                active = aboutFragment;
                break;
            case ROLE_HANDYMAN:
                aboutFragment = new MyProfileAboutFragment();
                reviewsFragment = new MyProfileReviewsFragment();
                editHandymanProfile();
                active = aboutFragment;
                break;
        }

        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.myProfileFragmentParent, reviewsFragment).hide(reviewsFragment).commit();
        fm.beginTransaction().add(R.id.myProfileFragmentParent, aboutFragment).commit();

        rdAbout.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ibEdit.setVisibility(View.VISIBLE);
                fm.beginTransaction().hide(active).show(aboutFragment).commit();
                active = aboutFragment;
            }
        });

        rdReviews.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ibEdit.setVisibility(View.GONE);
                fm.beginTransaction().hide(active).show(reviewsFragment).commit();
                active = reviewsFragment;
            }
        });
    }

    private void goBack() {
        binding.myProfileBackButton.setOnClickListener(v -> onBackPressed());
    }

    private void editHandymanProfile() {
        ibEdit.setOnClickListener(v -> aboutFragment.startActivityForResult(new Intent(MyProfile.this, MyProfileEdit.class), REQUEST_MY_PROFILE_RESULT_CODE));
    }

    private void editCustomerProfile() {
        ibEdit.setOnClickListener(v -> aboutFragment.startActivityForResult(new Intent(MyProfile.this, CustomerProfileEdit.class), REQUEST_MY_PROFILE_RESULT_CODE));
    }

    public void setMyProfileEditResult(boolean isEdit) {
        this.isEdit = isEdit;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isMoreFragmentEdit", isEdit);
        setResult(RESULT_OK, intent);
        finish();
    }
}
