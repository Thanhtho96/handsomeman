package com.tt.handsomeman.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.tt.handsomeman.BuildConfig;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.FragmentChangeAvatarOptionBinding;
import com.tt.handsomeman.ui.customer.more.CustomerProfileAboutFragment;
import com.tt.handsomeman.ui.handyman.more.MyProfileAboutFragment;
import com.tt.handsomeman.util.RealPathUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class ChangeAvatarOptionFragment extends BottomSheetDialogFragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PIC_CROP = 2;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 3;
    private static final int REQUEST_FILE = 67;
    private String currentPhotoPath;
    private Uri photoURI;
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

        binding.takePhoto.setOnClickListener(v -> dispatchTakePictureIntent());

        binding.chooseImage.setOnClickListener(v -> startFileChooser());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
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
            cropImage();
        }
        if (requestCode == PIC_CROP && resultCode == RESULT_OK) {
            setImageView();
        }
        if (requestCode == REQUEST_FILE && data != null && resultCode == RESULT_OK) {
            String src = RealPathUtil.getRealPath(getContext(), data.getData());
            File source = new File(src);
            File destination = new File(currentPhotoPath);

            try {
                copy(source, destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cropImage();
        }
    }

    private void cropImage() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(photoURI, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 777);
            cropIntent.putExtra("outputY", 777);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException e) {
            // display an error message
            String errorMessage = "Please install some Photo app with crop function, ie: Google Photos";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void copy(File source,
                      File destination) throws IOException {

        try (FileChannel in = new FileInputStream(source).getChannel(); FileChannel out = new FileOutputStream(destination).getChannel()) {
            in.transferTo(0, in.size(), out);
        } catch (Exception e) {
            // post to log
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
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

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile);
                Toast.makeText(getContext(), getString(R.string.select_image), Toast.LENGTH_SHORT).show();

                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQUEST_FILE);
            }
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
