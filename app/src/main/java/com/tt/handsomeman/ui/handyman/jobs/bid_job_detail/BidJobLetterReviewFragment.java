package com.tt.handsomeman.ui.handyman.jobs.bid_job_detail;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.FileDisplayAdapter;
import com.tt.handsomeman.databinding.FragmentBidJobDetailLetterReviewBinding;
import com.tt.handsomeman.model.HandymanJobDetail;
import com.tt.handsomeman.model.PaymentMilestone;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.DimensionConverter;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import java.util.List;

import javax.inject.Inject;

public class BidJobLetterReviewFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private TextView tvLetter, tvMyBudget, tvPaymentMileStoneCount, tvJobTitle;
    private TableLayout tblPaymentMileStoneBidJobDetail;
    private FileDisplayAdapter displayAdapter;
    private String jobTitle;
    private JobBidRequest jobBidRequest;
    private int paymentMileStoneCount;
    private HandymanJobDetail handymanJobDetail;
    private FragmentBidJobDetailLetterReviewBinding binding;

    static BidJobLetterReviewFragment newInstance(HandymanJobDetail handymanJobDetail) {
        BidJobLetterReviewFragment bidJobLetterReviewFragment = new BidJobLetterReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("handymanJobDetail", handymanJobDetail);
        bidJobLetterReviewFragment.setArguments(args);
        return bidJobLetterReviewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handymanJobDetail = (HandymanJobDetail) getArguments().getSerializable("handymanJobDetail");
        jobTitle = handymanJobDetail.getJob().getTitle();
        paymentMileStoneCount = handymanJobDetail.getListPaymentMilestone().size();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBidJobDetailLetterReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView();

        tvLetter.setText(jobBidRequest.getDescription());
        tvJobTitle.setText(jobTitle);
        tvMyBudget.setText(getString(R.string.money_currency_string, jobBidRequest.getBid()));
        tvPaymentMileStoneCount.setText(String.valueOf(paymentMileStoneCount));

        List<PaymentMilestone> listPaymentMilestone = handymanJobDetail.getListPaymentMilestone();
        for (int i = 0; i < listPaymentMilestone.size(); i++) {
            TableRow tr = new TableRow(getContext());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));

            TextView b = new TextView(getContext());
            b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            b.setTextColor(getResources().getColor(R.color.text_white_bg));
            b.setTextSize(DimensionConverter.spToPx(getResources().getDimension(R.dimen.design_3_3sp), view.getContext()));
            b.setGravity(Gravity.START);

            switch ((i + 1) % 10) {
                case 1:
                    b.setText(i + 1 + "st milestone");
                    break;
                case 2:
                    b.setText(i + 1 + "nd milestone");
                    break;
                case 3:
                    b.setText(i + 1 + "rd milestone");
                    break;
                default:
                    b.setText(i + 1 + "th milestone");
                    break;
            }

            TextView b2 = new TextView(getContext());
            b2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            b2.setTextColor(getResources().getColor(R.color.text_white_bg));
            b2.setTextSize(DimensionConverter.spToPx(getResources().getDimension(R.dimen.design_3_3sp), view.getContext()));
            b2.setGravity(Gravity.END);
            b2.setText(getString(R.string.percentage, listPaymentMilestone.get(i).getPercentage()));

            tr.addView(b);
            tr.addView(b2);

            tblPaymentMileStoneBidJobDetail.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
        createRecyclerView();
    }

    private void bindView() {
        tvJobTitle = binding.jobTitleBidJobDetail;
        tvMyBudget = binding.myBudgetBidJobDetail;
        tvPaymentMileStoneCount = binding.paymentMileStoneCountBidJobDetail;
        tvLetter = binding.introduceYourSelfTextView;
        tblPaymentMileStoneBidJobDetail = binding.paymentMileStoneTableLayoutBidJobDetail;

        BidJobDetail bidJobDetail = (BidJobDetail) getActivity();
        jobBidRequest = bidJobDetail.jobBidRequest;
    }

    private void createRecyclerView() {
        if (jobBidRequest.getFileRequestList().size() > 0) {
            binding.listFileLayout.setVisibility(View.VISIBLE);
        } else {
            binding.listFileLayout.setVisibility(View.GONE);
        }
        RecyclerView rcvNotification = binding.recyclerFileName;
        displayAdapter = new FileDisplayAdapter(getContext(), jobBidRequest.getFileRequestList());
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(getContext());
        rcvNotification.setLayoutManager(layoutManagerJob);
        rcvNotification.setItemAnimator(new DefaultItemAnimator());
        rcvNotification.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider));
        rcvNotification.setAdapter(displayAdapter);
    }

    @Override
    public void onResume() {
        tvLetter.setText(jobBidRequest.getDescription());
        createRecyclerView();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
