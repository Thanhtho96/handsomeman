package com.tt.handsomeman.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tt.handsomeman.databinding.FragmentAvatarOptionBinding;
import com.tt.handsomeman.ui.customer.more.CustomerProfileAboutFragment;
import com.tt.handsomeman.ui.handyman.more.MyProfileAboutFragment;

public class AvatarOptionDialogFragment extends BottomSheetDialogFragment {

    private FragmentAvatarOptionBinding binding;

    public static AvatarOptionDialogFragment newInstance() {
        return new AvatarOptionDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAvatarOptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.changeAvatar.setOnClickListener(v -> ChangeAvatarOptionFragment.newInstance().show(getChildFragmentManager(), "change_avatar"));

        binding.cancel.setOnClickListener(v -> dismiss());

        binding.viewAvatar.setOnClickListener(v -> {
            try {
                MyProfileAboutFragment aboutFragment = (MyProfileAboutFragment) getParentFragment();
                Intent intent = new Intent(getContext(), PhotoViewer.class);
                intent.putExtra("authorizationCode", aboutFragment.getAuthorizationCode());
                intent.putExtra("avatarLink", aboutFragment.getAvatarLink());
                intent.putExtra("updateDate", aboutFragment.getUpdateDate());
                startActivity(intent);
            } catch (ClassCastException e) {
                CustomerProfileAboutFragment customerAboutFragment = (CustomerProfileAboutFragment) getParentFragment();
                Intent intent = new Intent(getContext(), PhotoViewer.class);
                intent.putExtra("authorizationCode", customerAboutFragment.getAuthorizationCode());
                intent.putExtra("avatarLink", customerAboutFragment.getAvatarLink());
                intent.putExtra("updateDate", customerAboutFragment.getUpdateDate());
                startActivity(intent);
            }

        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
