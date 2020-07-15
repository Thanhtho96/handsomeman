package com.tt.handsomeman.ui.customer.my_projects.add_job;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.FragmentAddNewJobChildThirdBinding;
import com.tt.handsomeman.request.AddJobRequest;
import com.tt.handsomeman.util.DimensionConverter;
import com.tt.handsomeman.util.TimeCount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewJobChildThirdFragment extends Fragment {

    private TextView tvTitle, tvDetail, tvBudgetRange, tvDeadline, tvLocation, tvPaymentMilestoneCount;
    private GoogleMap mMap;
    private TableLayout tbPaymentMilestone;
    private SupportMapFragment mapFragment;
    private AddJobRequest addJobRequest;
    private SimpleDateFormat simpleDateFormat;
    private FragmentAddNewJobChildThirdBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNewJobChildThirdBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        bindView();
    }

    private void bindView() {
        tvTitle = binding.jobTitle;
        tvDetail = binding.jobDetail;
        tvBudgetRange = binding.budgetRange;
        tvDeadline = binding.deadline;
        tvLocation = binding.location;
        tvPaymentMilestoneCount = binding.paymentMileStoneCount;
        tbPaymentMilestone = binding.paymentMileStoneTableLayout;
        AddNewJob addNewJob = (AddNewJob) requireActivity();
        addJobRequest = addNewJob.addJobRequest;
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapReview);
    }

    @Override
    public void onResume() {
        super.onResume();
        tvTitle.setText(addJobRequest.getTitle());
        tvDetail.setText(addJobRequest.getDetail());
        tvBudgetRange.setText(setBudgetRange(addJobRequest.getBudgetMin(), addJobRequest.getBudgetMax()));
        try {
            Integer dayLeft = setDeadlineBinding(simpleDateFormat.parse(addJobRequest.getDeadline()));
            tvDeadline.setText(getResources().getQuantityString(R.plurals.numberOfDay, dayLeft, dayLeft));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvLocation.setText(addJobRequest.getLocation());
        moveMapToJobLocation(addJobRequest.getLocation());
        tvPaymentMilestoneCount.setText(String.valueOf(addJobRequest.getPercentages().size()));

        tbPaymentMilestone.removeAllViews();
        for (int i = 0; i < addJobRequest.getPercentages().size(); i++) {
            TableRow tr = new TableRow(getContext());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));

            TextView b = new TextView(getContext());
            b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            b.setTextColor(getResources().getColor(R.color.text_white_bg));
            b.setTextSize(DimensionConverter.spToPx(getResources().getDimension(R.dimen.design_3_3sp), requireContext()));
            b.setGravity(Gravity.START);
            switch ((i + 1) % 10) {
                case 1:
                    b.setText(getString(R.string.first_milestone, i + 1));
                    break;
                case 2:
                    b.setText(getString(R.string.second_milestone, i + 1));
                    break;
                case 3:
                    b.setText(getString(R.string.third_milestone, i + 1));
                    break;
                default:
                    b.setText(getString(R.string.default_milestone, i + 1));
                    break;
            }

            TextView b2 = new TextView(getContext());
            b2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            b2.setTextColor(getResources().getColor(R.color.text_white_bg));
            b2.setTextSize(DimensionConverter.spToPx(getResources().getDimension(R.dimen.design_3_3sp), requireContext()));
            b2.setGravity(Gravity.END);
            b2.setText(getString(R.string.percentage, addJobRequest.getPercentages().get(i)));
            tr.addView(b);
            tr.addView(b2);

            tbPaymentMilestone.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private void moveMapToJobLocation(String placeName) {
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.clear();

            LatLng jobLocation = new LatLng(addJobRequest.getLat(), addJobRequest.getLng());
            mMap.addMarker(new MarkerOptions().position(jobLocation).title(placeName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jobLocation, 15));
            mMap.getUiSettings().setScrollGesturesEnabled(false);
        });
    }

    private String setBudgetRange(Integer budgetMin,
                                  Integer budgetMax) {
        return HandymanApp.getInstance().getString(R.string.budget_range, budgetMin, budgetMax);
    }

    private Integer setDeadlineBinding(Date deadline) {
        Calendar now = Calendar.getInstance();
        return Math.max(TimeCount.getDaysBetween(now.getTime(), deadline), 0);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
