package com.tt.handsomeman.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tt.handsomeman.databinding.OnBoardingFragmentSlidePageBinding;

public class OnBoardingSlidePageFragment extends Fragment {

    private int image;
    private int description;
    private OnBoardingFragmentSlidePageBinding binding;

    static OnBoardingSlidePageFragment newInstance(int image,
                                                   int description) {
        OnBoardingSlidePageFragment screenSlidePageFragment = new OnBoardingSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt("image", image);
        args.putInt("description", description);
        screenSlidePageFragment.setArguments(args);
        return screenSlidePageFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = getArguments().getInt("image", 0);
        description = getArguments().getInt("description");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = OnBoardingFragmentSlidePageBinding.inflate(inflater, container, false);

        ImageView imgOnBoarding = binding.imageViewOnBoarding;
        TextView txtOnBoarding = binding.textViewOnBoarding;
        imgOnBoarding.setImageResource(image);
        txtOnBoarding.setText(description);

        return binding.getRoot();
    }
}
