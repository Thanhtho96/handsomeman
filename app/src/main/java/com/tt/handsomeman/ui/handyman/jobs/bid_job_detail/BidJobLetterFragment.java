package com.tt.handsomeman.ui.handyman.jobs.bid_job_detail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.FileAddAdapter;
import com.tt.handsomeman.databinding.FragmentBidJobDetailLetterWritingBinding;
import com.tt.handsomeman.model.FileRequest;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.CustomViewPager;
import com.tt.handsomeman.util.FileUtils;
import com.tt.handsomeman.util.MD5;

import java.io.File;

public class BidJobLetterFragment extends Fragment {
    private static final int PERMISSION_REQUEST_READ_STORAGE = 0;
    private static final int REQUEST_FILE = 67;
    private EditText edtIntroduce;
    private ImageButton ibCheckButtonLetter;
    private ImageButton ibAttachFile;
    private FileAddAdapter fileAddAdapter;
    private JobBidRequest jobBidRequest;
    private CustomViewPager mPager;
    private FragmentBidJobDetailLetterWritingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBidJobDetailLetterWritingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView();
        createRecyclerView();

        ibAttachFile.setOnClickListener(v -> startFileChooser());

        edtIntroduce.addTextChangedListener(new TextWatcher() {
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
                String edtValue = edtIntroduce.getText().toString().trim();
                if (TextUtils.isEmpty(edtValue) || edtValue.length() < 10) {
                    ibCheckButtonLetter.setEnabled(false);
                    edtIntroduce.setError(getResources().getString(R.string.introduce_error));
                } else {
                    ibCheckButtonLetter.setEnabled(true);
                }
            }
        });

        ibCheckButtonLetter.setOnClickListener(v -> {
            jobBidRequest.setDescription(edtIntroduce.getText().toString().trim());

            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        });
    }

    private void bindView() {
        edtIntroduce = binding.introduceYourSelfEditText;
        ibAttachFile = binding.attachFileButton;

        BidJobDetail bidJobDetail = (BidJobDetail) getActivity();
        jobBidRequest = bidJobDetail.jobBidRequest;
        ibCheckButtonLetter = bidJobDetail.viewBinding.imageButtonCheckLetterBidJobDetail;
        mPager = bidJobDetail.viewBinding.bidJobDetailPager;
        ibCheckButtonLetter.setEnabled(false);
    }

    private void createRecyclerView() {
        RecyclerView rcvNotification = binding.recyclerFileName;
        fileAddAdapter = new FileAddAdapter(getContext(), jobBidRequest.getFileRequestList());
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(getContext());
        rcvNotification.setLayoutManager(layoutManagerJob);
        rcvNotification.setItemAnimator(new DefaultItemAnimator());
        rcvNotification.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider));
        rcvNotification.setAdapter(fileAddAdapter);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {
        if (requestCode == REQUEST_FILE && data != null) {
            binding.listFileLayout.setVisibility(View.VISIBLE);
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (int i = 0; i < count; i++) {
                    Uri fileURI = data.getClipData().getItemAt(i).getUri();
                    String filePath = FileUtils.onSelectFromGalleryResult(requireActivity(), fileURI, Constants.FILE_TYPE.PDF);
                    if (filePath != null) {
                        File file = new File(filePath);
                        FileRequest fileRequest = new FileRequest(file.getName(), filePath, MD5.calculateMD5(file));
                        if (!jobBidRequest.getFileRequestList().contains(fileRequest)) {
                            jobBidRequest.getFileRequestList().add(fileRequest);
                            fileAddAdapter.notifyItemInserted(jobBidRequest.getFileRequestList().size() - 1);
                        } else {
                            Toast.makeText(getContext(), getString(R.string.duplicate_file), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                String filePath = FileUtils.onSelectFromGalleryResult(requireActivity(), data.getData(), Constants.FILE_TYPE.PDF);
                if (filePath != null) {
                    File file = new File(filePath);
                    FileRequest fileRequest = new FileRequest(file.getName(), filePath, MD5.calculateMD5(file));
                    if (!jobBidRequest.getFileRequestList().contains(fileRequest)) {
                        jobBidRequest.getFileRequestList().add(fileRequest);
                        fileAddAdapter.notifyItemInserted(jobBidRequest.getFileRequestList().size() - 1);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.duplicate_file), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            Toast.makeText(getContext(), getString(R.string.select_pdf), Toast.LENGTH_SHORT).show();
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_pdf)), REQUEST_FILE);
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

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
