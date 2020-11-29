package com.tt.handsomeman.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.FragmentChangeAvatarOptionBinding;
import com.tt.handsomeman.ui.customer.more.CustomerProfileAboutFragment;
import com.tt.handsomeman.ui.handyman.more.MyProfileAboutFragment;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.FileUtils;

import static android.app.Activity.RESULT_OK;

public class ChangeAvatarOptionFragment extends BottomSheetDialogFragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 3;
    private static final int REQUEST_FILE = 67;
    private String currentPhotoPath;
    private FragmentChangeAvatarOptionBinding binding;

    public static ChangeAvatarOptionFragment newInstance() {
        return new ChangeAvatarOptionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChangeAvatarOptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.back.setOnClickListener(v -> dismiss());

        binding.takePhoto.setOnClickListener(v -> currentPhotoPath = FileUtils.dispatchTakePictureIntent(getActivity(), this, REQUEST_IMAGE_CAPTURE));

        binding.chooseImage.setOnClickListener(v -> startFileChooser());
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setImageView();
        }
        if (requestCode == REQUEST_FILE && data != null && resultCode == RESULT_OK) {
            currentPhotoPath = FileUtils.onSelectFromGalleryResult(requireActivity(), data.getData(), Constants.FILE_TYPE.IMAGE);
            setImageView();
        }
    }

    private void setImageView() {
        try {
            MyProfileAboutFragment aboutFragment = (MyProfileAboutFragment) getParentFragment().getParentFragment();
            aboutFragment.updateAvatar(currentPhotoPath);
        } catch (ClassCastException e) {
            CustomerProfileAboutFragment customerAboutFragment = (CustomerProfileAboutFragment) getParentFragment().getParentFragment();
            customerAboutFragment.updateAvatar(currentPhotoPath);
        }
        dismiss();
        AvatarOptionDialogFragment avatarOptionDialogFragment = (AvatarOptionDialogFragment) getParentFragment();
        avatarOptionDialogFragment.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                startFileChooser();
            } else {
                // Permission request was denied.
                Snackbar.make(binding.container, getString(R.string.permission_denied),
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void startFileChooser() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            Toast.makeText(getContext(), getString(R.string.select_image), Toast.LENGTH_SHORT).show();
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQUEST_FILE);
        } else {
            // Permission is missing and must be requested.
            requestReadStoragePermission();
        }
    }

    private void requestReadStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(binding.container, getString(R.string.need_permission),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.grant_permisstion), view -> {
                // Request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_STORAGE);
            }).show();

        } else {
            Snackbar.make(binding.container, getString(R.string.read_permisstion_unvailable), Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        }
    }
}
