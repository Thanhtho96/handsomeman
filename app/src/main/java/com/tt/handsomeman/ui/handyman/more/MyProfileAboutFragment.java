package com.tt.handsomeman.ui.handyman.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.tt.handsomeman.adapter.SkillAdapter;
import com.tt.handsomeman.databinding.FragmentMyProfileAboutBinding;
import com.tt.handsomeman.model.Handyman;
import com.tt.handsomeman.model.Skill;
import com.tt.handsomeman.ui.AvatarOptionDialogFragment;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.ui.MyProfile;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MyProfileAboutFragment extends BaseFragment<HandymanViewModel, FragmentMyProfileAboutBinding> {
    private static final Integer REQUEST_MY_PROFILE_RESULT_CODE = 77;
    private final List<Skill> skillList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ImageView imAvatar;
    private TextView yourName, education, about, allProjects, successedProject;
    private SkillAdapter skillAdapter;
    private RecyclerView rcvSkill;
    private boolean isMyProfileEdit = false;
    private String authorizationCode, avatarLink = "";
    private long updateDate = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);
        viewBinding = FragmentMyProfileAboutBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authorizationCode = sharedPreferencesUtils.get("token", String.class);
        bindView();
        createSkillRecyclerView();
        fetchHandymanProfile(authorizationCode);

        imAvatar.setOnClickListener(v -> AvatarOptionDialogFragment.newInstance().show(getChildFragmentManager(), "avatar_option"));
    }

    private void bindView() {
        yourName = viewBinding.aboutYourName;
        education = viewBinding.aboutEducation;
        about = viewBinding.about;
        allProjects = viewBinding.aboutAllProjects;
        successedProject = viewBinding.aboutSuccessedProjects;
        rcvSkill = viewBinding.aboutMySkillRecyclerView;
        imAvatar = viewBinding.handymanAvatar;
    }

    private void createSkillRecyclerView() {
        skillAdapter = new SkillAdapter(skillList, getContext());
        RecyclerView.LayoutManager layoutManagerPayout = new LinearLayoutManager(getContext());
        rcvSkill.setLayoutManager(layoutManagerPayout);
        rcvSkill.setItemAnimator(new DefaultItemAnimator());
        rcvSkill.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider)));
        rcvSkill.setAdapter(skillAdapter);
    }

    private void fetchHandymanProfile(String authorizationCode) {
        baseViewModel.fetchHandymanProfile();
        baseViewModel.getHandymanProfileResponseMutableLiveData().observe(getViewLifecycleOwner(), handymanProfileResponse -> {
            Handyman handyman = handymanProfileResponse.getHandyman();

            yourName.setText(handyman.getName());
            education.setText(handyman.getEducation());
            about.setText(handyman.getDetail());
            allProjects.setText(String.valueOf(handymanProfileResponse.getAllProject()));
            successedProject.setText(String.valueOf(handymanProfileResponse.getSuccessedProject()));

            avatarLink = handymanProfileResponse.getAvatar();
            updateDate = handymanProfileResponse.getUpdateDate();

            GlideUrl glideUrl = new GlideUrl((handymanProfileResponse.getAvatar()),
                    new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

            Glide.with(MyProfileAboutFragment.this)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .placeholder(R.drawable.custom_progressbar)
                    .error(R.drawable.logo)
                    .signature(new MediaStoreSignature("", handymanProfileResponse.getUpdateDate(), 0))
                    .into(imAvatar);

            skillList.clear();
            skillList.addAll(handymanProfileResponse.getSkillList());
            skillAdapter.notifyDataSetChanged();
        });
    }

    public void updateAvatar(String fileDir) {
        MultipartBody.Part body;

        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
        String sendTime = formatter.format(now.getTime());
        RequestBody updateDate = RequestBody.create(sendTime, MultipartBody.FORM);

        File file = new File(fileDir);
        if (file.exists()) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            RequestBody requestFile = RequestBody.create(file, MediaType.parse(mimeType));
            body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            baseViewModel.updateAvatar(body, updateDate);
            baseViewModel.getStandardResponseMutableLiveData().observe(this, standardResponse -> {
                Toast.makeText(getContext(), standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    isMyProfileEdit = true;
                    MyProfile myProfile = (MyProfile) getActivity();
                    myProfile.setMyProfileEditResult(isMyProfileEdit);

                    fetchHandymanProfile(authorizationCode);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {

        if (requestCode == REQUEST_MY_PROFILE_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("isMyProfileEdit", false)) {
                fetchHandymanProfile(authorizationCode);
                isMyProfileEdit = true;
                MyProfile myProfile = (MyProfile) getActivity();
                myProfile.setMyProfileEditResult(isMyProfileEdit);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public long getUpdateDate() {
        return updateDate;
    }
}
