package com.tt.handsomeman.ui.handyman.more;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.tt.handsomeman.adapter.SkillEditAdapter;
import com.tt.handsomeman.databinding.ActivityMyProfileEditBinding;
import com.tt.handsomeman.model.Handyman;
import com.tt.handsomeman.model.Skill;
import com.tt.handsomeman.request.HandymanEditRequest;
import com.tt.handsomeman.request.SkillEditRequest;
import com.tt.handsomeman.ui.BaseAppCompatActivityWithViewModel;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.util.YesOrNoDialog;
import com.tt.handsomeman.viewmodel.HandymanViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MyProfileEdit extends BaseAppCompatActivityWithViewModel<HandymanViewModel> {
    private static final Integer REQUEST_MY_PROFILE_EDIT_RESULT_CODE = 777;
    private final ArrayList<Skill> skillEditList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private EditText yourNameEdit, educationEdit, aboutEdit;
    private ImageButton ibEdit, ibAddSkillMyProfileEdit;
    private SkillEditAdapter skillEditAdapter;
    private RecyclerView rcvSkillEdit;
    private boolean isEdit = false;
    private boolean isLoadDone = false;
    private ActivityMyProfileEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(HandymanViewModel.class);

        yourNameEdit = binding.editTextYourNameMyProfileEdit;
        educationEdit = binding.editTextEducationMyProfileEdit;
        aboutEdit = binding.editTextAboutMyProfileEdit;
        rcvSkillEdit = binding.editMySkillRecyclerView;
        ibEdit = binding.imageButtonCheckEdit;
        ibAddSkillMyProfileEdit = binding.imageButtonAddSkillMyProfileEdit;

        goBack();

        ibAddSkillMyProfileEdit.setOnClickListener(v -> {
            if (isLoadDone) {
                Intent intent = new Intent(MyProfileEdit.this, AddNewSkill.class);
                intent.putExtra("listSkill", skillEditList);
                startActivityForResult(intent, REQUEST_MY_PROFILE_EDIT_RESULT_CODE);
            } else {
                Toast.makeText(MyProfileEdit.this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
            }
        });

        editTextYourNameListener(yourNameEdit);
        createRecyclerView();
        fetchHandymanProfile();
        doEdit();
    }

    private void goBack() {
        binding.myProfileEditBackButton.setOnClickListener(v -> onBackPressed());
    }

    private void editTextYourNameListener(EditText yourNameEdit) {
        yourNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String yourName = yourNameEdit.getText().toString().trim();
                if (TextUtils.isEmpty(yourName) || yourName.length() <= 4) {
                    yourNameEdit.setError(getString(R.string.not_valid_name));
                    ibEdit.setEnabled(false);
                } else {
                    ibEdit.setEnabled(true);
                }
            }
        });
    }


    private void createRecyclerView() {
        skillEditAdapter = new SkillEditAdapter(skillEditList, this);
        skillEditAdapter.setOnItemClickListener(position -> new YesOrNoDialog(MyProfileEdit.this, R.style.PauseDialog, HandymanApp.getInstance().getString(R.string.sure_to_delete_skill), R.drawable.dele, () -> {
            skillEditList.remove(position);
            skillEditAdapter.notifyItemRemoved(position);
        }).show());
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(this);
        rcvSkillEdit.setLayoutManager(layoutManagerJob);
        rcvSkillEdit.setItemAnimator(new DefaultItemAnimator());
        rcvSkillEdit.addItemDecoration(new CustomDividerItemDecoration(this, R.drawable.recycler_view_divider));
        rcvSkillEdit.setAdapter(skillEditAdapter);
    }

    private void doEdit() {
        ibEdit.setOnClickListener(v -> {
            String yourNameEditValue = yourNameEdit.getText().toString();
            String educationEditValue = educationEdit.getText().toString();
            String aboutEditValue = aboutEdit.getText().toString();

            List<SkillEditRequest> skillEditRequestList = new ArrayList<>();

            for (Skill skill : skillEditList) {
                SkillEditRequest skillEditRequest = new SkillEditRequest(skill.getCategory_id(), skill.getName());
                skillEditRequestList.add(skillEditRequest);
            }

            HandymanEditRequest handymanEditRequest = new HandymanEditRequest(yourNameEditValue, educationEditValue, aboutEditValue, skillEditRequestList);

            baseViewModel.editHandymanProfile(handymanEditRequest);
            baseViewModel.getStandardResponseMutableLiveData().observe(MyProfileEdit.this, standardResponse -> {
                Toast.makeText(MyProfileEdit.this, standardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                if (standardResponse.getStatus().equals(StatusConstant.OK)) {
                    isEdit = true;
                    Intent intent = new Intent();
                    intent.putExtra("isMyProfileEdit", isEdit);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        });
    }

    private void fetchHandymanProfile() {
        String authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel.fetchHandymanProfile();
        baseViewModel.getHandymanProfileResponseMutableLiveData().observe(this, handymanProfileResponse -> {
            Handyman handyman = handymanProfileResponse.getHandyman();

            yourNameEdit.setText(handyman.getName());
            educationEdit.setText(handyman.getEducation());
            aboutEdit.setText(handyman.getDetail());

            GlideUrl glideUrl = new GlideUrl((handymanProfileResponse.getAvatar()),
                    new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

            Glide.with(MyProfileEdit.this)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .placeholder(R.drawable.custom_progressbar)
                    .error(R.drawable.logo)
                    .signature(new MediaStoreSignature("", handymanProfileResponse.getUpdateDate(), 0))
                    .into(binding.accountAvatar);

            skillEditList.clear();
            skillEditList.addAll(handymanProfileResponse.getSkillList());
            skillEditAdapter.notifyDataSetChanged();

            isLoadDone = true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {

        if (requestCode == REQUEST_MY_PROFILE_EDIT_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Skill skill = (Skill) data.getSerializableExtra("skillAdded");
            if (skillEditList.contains(skill)) {
                Toast.makeText(this, getString(R.string.duplicate_skill), Toast.LENGTH_SHORT).show();
            } else {
                skillEditList.add(skill);
                skillEditAdapter.notifyItemInserted(skillEditList.size());
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
