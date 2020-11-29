package com.tt.handsomeman.ui.handyman.jobs.bid_job_detail;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.FragmentBidJobDetailBudgetBinding;
import com.tt.handsomeman.model.HandymanJobDetail;
import com.tt.handsomeman.util.CustomViewPager;
import com.tt.handsomeman.util.DecimalFormat;

public class BidJobBudgetFragment extends Fragment {

    private int myBid, highestBid, lowestBid, myBidValue;
    private double budgetReceive, serviceFee;
    private ImageButton ibCheckButtonBudget;
    private EditText edtMyBid;
    private CustomViewPager mPager;
    private JobBidRequest jobBidRequest;
    private TextView tvBudgetReceive, tvServiceFee, tvHighestBid, tvLowestBid;
    private HandymanJobDetail handymanJobDetail;
    private FragmentBidJobDetailBudgetBinding binding;

    static BidJobBudgetFragment newInstance(HandymanJobDetail handymanJobDetail) {
        BidJobBudgetFragment bidJobBudgetFragment = new BidJobBudgetFragment();
        Bundle args = new Bundle();
        args.putSerializable("handymanJobDetail", handymanJobDetail);
        bidJobBudgetFragment.setArguments(args);
        return bidJobBudgetFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handymanJobDetail = (HandymanJobDetail) getArguments().getSerializable("handymanJobDetail");
        myBid = handymanJobDetail.getJob().getBudgetMin();
        budgetReceive = handymanJobDetail.getJob().getBudgetMin() * 0.9;
        serviceFee = handymanJobDetail.getJob().getBudgetMin() * 0.1;
        highestBid = handymanJobDetail.getJob().getBudgetMax();
        lowestBid = handymanJobDetail.getJob().getBudgetMin();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBidJobDetailBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView();

        edtMyBid.setText(String.valueOf(myBid));
        tvBudgetReceive.setText(getString(R.string.money_currency_string, DecimalFormat.format(this.budgetReceive)));
        tvServiceFee.setText(DecimalFormat.format(this.serviceFee));
        tvHighestBid.setText(String.valueOf(highestBid));
        tvLowestBid.setText(String.valueOf(lowestBid));
        edtMyBid.addTextChangedListener(new TextWatcher() {
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
                if (!edtMyBid.getText().toString().trim().equals("")) {
                    myBidValue = Integer.parseInt(edtMyBid.getText().toString().trim());
                    if (myBidValue > highestBid) {
                        ibCheckButtonBudget.setEnabled(false);
                        edtMyBid.setError(getResources().getString(R.string.lower_price_waring));
                    } else if (myBidValue < lowestBid) {
                        ibCheckButtonBudget.setEnabled(false);
                        edtMyBid.setError(getResources().getString(R.string.higher_price_warning));
                    } else {
                        tvBudgetReceive.setText(getString(R.string.money_currency_string, DecimalFormat.format(myBidValue * 0.9)));
                        tvServiceFee.setText(DecimalFormat.format(myBidValue * 0.1));
                        ibCheckButtonBudget.setEnabled(true);
                    }
                } else {
                    ibCheckButtonBudget.setEnabled(false);
                    edtMyBid.setError(getResources().getString(R.string.please_input));
                }
            }
        });

        ibCheckButtonBudget.setOnClickListener(v -> {
            jobBidRequest.setBid(edtMyBid.getText().toString());
            jobBidRequest.setServiceFee(tvServiceFee.getText().toString());

            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        });

    }

    private void bindView() {
        edtMyBid = binding.myBidBidJobDetail;
        tvBudgetReceive = binding.budgetReceiveBidJobDetail;
        tvServiceFee = binding.serviceFeeBidJobDetail;
        tvHighestBid = binding.highestBidJobDetail;
        tvLowestBid = binding.lowestBidJobDetail;

        BidJobDetail bidJobDetail = (BidJobDetail) getActivity();
        jobBidRequest = bidJobDetail.jobBidRequest;
        ibCheckButtonBudget = bidJobDetail.viewBinding.imageButtonCheckBudgetBidJobDetail;
        mPager = bidJobDetail.viewBinding.bidJobDetailPager;
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
