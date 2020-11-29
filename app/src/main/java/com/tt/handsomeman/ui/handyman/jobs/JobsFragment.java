package com.tt.handsomeman.ui.handyman.jobs;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.FragmentJobsBinding;

public class JobsFragment extends Fragment {

    private final Fragment jobsChildJobsFragment = new JobsChildJobsFragment();
    private final Fragment jobsChildWishListFragment = new JobsChildWishListFragment();
    private Fragment active = jobsChildJobsFragment;
    private EditText edtSearchByWord;
    private FragmentJobsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentJobsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RadioButton rdJobs = binding.radioButtonJobs;
        RadioButton rdWishList = binding.radioButtonWishList;
        edtSearchByWord = binding.editTextSearchByWordJobFragment;

        final FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().add(R.id.jobsFragmentParent, jobsChildWishListFragment).hide(jobsChildWishListFragment).commit();
        fm.beginTransaction().add(R.id.jobsFragmentParent, jobsChildJobsFragment).commit();

        int choice = getActivity().getIntent().getIntExtra("radioButtonChoice", 0);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setEditTextHintTextAndIcon();

        if (choice == 1) {
            rdWishList.setChecked(true);
            fm.beginTransaction().hide(active).show(jobsChildWishListFragment).commit();
            active = jobsChildWishListFragment;
        }

        rdJobs.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fm.beginTransaction().hide(active).show(jobsChildJobsFragment).commit();
                active = jobsChildJobsFragment;
            }
        });

        rdWishList.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fm.beginTransaction().hide(active).show(jobsChildWishListFragment).commit();
                active = jobsChildWishListFragment;
            }
        });
    }

    private void setEditTextHintTextAndIcon() {
        ImageSpan imageHint = new ImageSpan(getContext(), R.drawable.ic_search_19dp);
        SpannableString spannableString = new SpannableString("    " + getResources().getString(R.string.search_by_word));
        spannableString.setSpan(imageHint, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        edtSearchByWord.setHint(spannableString);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}