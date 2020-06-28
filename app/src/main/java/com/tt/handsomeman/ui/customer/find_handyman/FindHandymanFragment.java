package com.tt.handsomeman.ui.customer.find_handyman;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.FragmentFindHandymanBinding;

public class FindHandymanFragment extends Fragment {

    private Fragment childHandymanFragment = new FindHandymanChildHandymanFragment();
    private Fragment childWishlistFragment = new FindHandymanChildWishlistFragment();
    private Fragment active = childHandymanFragment;
    private EditText edtSearchByWord;
    private FragmentFindHandymanBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFindHandymanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RadioButton rdHandyman = binding.radioButtonHandyman;
        RadioButton rdWishList = binding.radioButtonWishList;
        edtSearchByWord = binding.editTextSearchByWordFindHandymanFragment;

        final FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().add(R.id.findHandymanFragmentParent, childWishlistFragment).hide(childWishlistFragment).commit();
        fm.beginTransaction().add(R.id.findHandymanFragmentParent, childHandymanFragment).commit();

        int choice = getActivity().getIntent().getIntExtra("radioButtonChoice", 0);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setEditTextHintTextAndIcon();

        if (choice == 1) {
            rdWishList.setChecked(true);
            fm.beginTransaction().hide(active).show(childWishlistFragment).commit();
            active = childWishlistFragment;
        }

        rdHandyman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    fm.beginTransaction().hide(active).show(childHandymanFragment).commit();
                    active = childHandymanFragment;
                }
            }
        });

        rdWishList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    fm.beginTransaction().hide(active).show(childWishlistFragment).commit();
                    active = childWishlistFragment;
                }
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
