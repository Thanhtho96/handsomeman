package com.tt.handsomeman.ui.customer.find_handyman;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.CategoryAdapter;
import com.tt.handsomeman.adapter.FindHandymanAdapter;
import com.tt.handsomeman.databinding.FragmentFindHandymanChildHandymanBinding;
import com.tt.handsomeman.model.Category;
import com.tt.handsomeman.request.NearbyHandymanRequest;
import com.tt.handsomeman.response.HandymanResponse;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class FindHandymanChildHandymanFragment extends BaseFragment<CustomerViewModel, FragmentFindHandymanChildHandymanBinding> {

    private final List<HandymanResponse> handymanResponseList = new ArrayList<>();
    private final List<Category> categoryArrayList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ProgressBar pgFindHandyman, pgCategory;
    private LinearLayout showMoreYourLocation;
    private FindHandymanAdapter findHandymanAdapter;
    private CategoryAdapter categoryAdapter;
    private String authorizationCode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);
        viewBinding = FragmentFindHandymanChildHandymanBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pgFindHandyman = viewBinding.progressBarFindHandyman;
        pgCategory = viewBinding.progressBarCategory;
        showMoreYourLocation = viewBinding.showMoreYourLocation;

        createJobRecycleView();
        createCategoryRecycleView();
        showMoreByYourLocation();

        Constants.Longitude.observe(getViewLifecycleOwner(), aDouble -> fetchData(Constants.Latitude.getValue(), aDouble));
    }

    private void showMoreByYourLocation() {
        showMoreYourLocation.setOnClickListener(view -> startActivity(new Intent(getContext(), HandymanNearYourLocation.class)));
    }

    private void createJobRecycleView() {
        RecyclerView rcvFindHandyman = viewBinding.recycleViewFindHandyman;
        findHandymanAdapter = new FindHandymanAdapter(getContext(), handymanResponseList, authorizationCode);
        findHandymanAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getContext(), HandymanDetail.class);
            intent.putExtra("handymanId", handymanResponseList.get(position).getHandymanId());
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManagerJob = new LinearLayoutManager(getContext());
        rcvFindHandyman.setLayoutManager(layoutManagerJob);
        rcvFindHandyman.setItemAnimator(new DefaultItemAnimator());
        rcvFindHandyman.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider));
        rcvFindHandyman.setAdapter(findHandymanAdapter);
    }

    private void createCategoryRecycleView() {
        RecyclerView rcvCategory = viewBinding.recycleViewCategories;
        categoryAdapter = new CategoryAdapter(getContext(), categoryArrayList);
        categoryAdapter.setOnItemClickListener(position -> {
            String categoryName = categoryArrayList.get(position).getName();

            Intent intent = new Intent(getContext(), FindHandymanCategory.class);
            intent.putExtra("categoryName", categoryName);
            intent.putExtra("categoryId", categoryArrayList.get(position).getCategory_id());
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManagerCategory = new LinearLayoutManager(getContext());
        rcvCategory.setLayoutManager(layoutManagerCategory);
        rcvCategory.setItemAnimator(new DefaultItemAnimator());
        rcvCategory.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider));
        rcvCategory.setAdapter(categoryAdapter);
    }

    private void fetchData(Double lat,
                           Double lng) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZ", Locale.getDefault());
        String dateRequest = formatter.format(now.getTime());
        double radius = 10d;

        baseViewModel.fetchDataStartScreen(new NearbyHandymanRequest(lat, lng, radius, dateRequest));

        baseViewModel.getStartScreenCustomerMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            pgFindHandyman.setVisibility(View.GONE);
            handymanResponseList.clear();
            handymanResponseList.addAll(data.getHandymanResponsesList());
            findHandymanAdapter.notifyDataSetChanged();

            pgCategory.setVisibility(View.GONE);
            categoryArrayList.clear();
            categoryArrayList.addAll(data.getCategoryList());
            categoryAdapter.notifyDataSetChanged();

            sharedPreferencesUtils.put("updateDate", data.getUpdateDate());
        });
    }

    @Override
    public void onStop() {
        Constants.Longitude.removeObservers(this);
        super.onStop();
    }
}
