package com.tt.handsomeman.ui.customer.my_projects.add_job;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.LanguageCode;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.search.Place;
import com.here.sdk.search.SearchEngine;
import com.here.sdk.search.SearchOptions;
import com.here.sdk.search.Suggestion;
import com.here.sdk.search.TextQuery;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.PlaceAdapter;
import com.tt.handsomeman.adapter.SpinnerString;
import com.tt.handsomeman.databinding.FragmentAddNewJobChildSecondBinding;
import com.tt.handsomeman.model.PlaceResponse;
import com.tt.handsomeman.request.AddJobRequest;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class AddNewJobChildSecondFragment extends Fragment {

    private final List<PlaceResponse> placeResponseList = new ArrayList<>();
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private Spinner spDeadline;
    private EditText edtLocation;
    private ImageButton ibCheckSecond;
    private ViewPager2 viewPager;
    private GoogleMap mMap;
    private PlaceAdapter placeAdapter;
    private RecyclerView rcvPlace;
    private String[] deadlineOption;
    private Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat sdf;
    private AddJobRequest addJobRequest;
    private Double lat, lng;
    private SearchEngine searchEngine;
    private SearchOptions searchOptions;
    private SupportMapFragment mapFragment;
    private String chosenPlaceName = null;
    private FragmentAddNewJobChildSecondBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        try {
            searchEngine = new SearchEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of SearchEngine failed: " + e.error.name());
        }

        binding = FragmentAddNewJobChildSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        lat = Constants.Latitude.getValue();
        lng = Constants.Longitude.getValue();

        String currentAddress = sharedPreferencesUtils.get("address", String.class);
        String countryCode = sharedPreferencesUtils.get("countryCode", String.class);

        int maxItems = 5;
        if (countryCode.equalsIgnoreCase("vn"))
            searchOptions = new SearchOptions(LanguageCode.VI_VN, maxItems);
        else
            searchOptions = new SearchOptions(LanguageCode.EN_US, maxItems);

        bindView();
        generateSpinnerDeadline(spDeadline);
        createRecyclerView();

        if (searchEngine != null) {
            initJobLocation(currentAddress);
            editTextEmitValueListener();
        }

        sendData();
    }

    private void initJobLocation(String currentAddress) {
        edtLocation.setText(currentAddress);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;

            LatLng jobLocation = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(jobLocation).title(currentAddress));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jobLocation, 15));
            mMap.getUiSettings().setScrollGesturesEnabled(false);

            addJobRequest.setLat(lat);
            addJobRequest.setLng(lng);
            addJobRequest.setLocation(currentAddress);
            ibCheckSecond.setEnabled(true);
        });
    }

    private void sendData() {
        ibCheckSecond.setOnClickListener(v -> {
                    getDeadlineOption();

                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
        );
    }

    private void editTextEmitValueListener() {
        RxTextView.textChanges(edtLocation)
                .skip(1)
                .debounce(277, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .filter(charSequence -> charSequence.length() >= 3 && !charSequence.equals(chosenPlaceName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::autoCompletePlaceBuilder);
    }

    private void autoCompletePlaceBuilder(String charSequence) {
        Double lat, lng;

        if (Constants.Latitude.getValue() == null)
            lat = 90d;
        else
            lat = Constants.Latitude.getValue();

        if (Constants.Longitude.getValue() == null)
            lng = 180d;
        else
            lng = Constants.Longitude.getValue();

        GeoCoordinates centerGeoCoordinates = new GeoCoordinates(lat, lng);

        searchEngine.suggest(
                new TextQuery(charSequence, centerGeoCoordinates),
                searchOptions,
                (searchError, list) -> {
                    if (searchError != null) {
                        Log.d("HereMapInfo", "AutoSuggest Error: " + searchError.name());
                        placeResponseList.clear();
                        placeAdapter.notifyDataSetChanged();
                        ibCheckSecond.setEnabled(false);
                        return;
                    }
                    if (list != null) {
                        Log.d("HereMapInfo", "AutoSuggest results: " + list.size());
                        placeResponseList.clear();

                        for (Suggestion autoSuggestResult : list) {
                            Place place = autoSuggestResult.getPlace();
                            if (place != null && place.getGeoCoordinates() != null) {
                                placeResponseList.add(new PlaceResponse(
                                        autoSuggestResult.getTitle(),
                                        place.getAddress().addressText,
                                        place.getGeoCoordinates().latitude,
                                        place.getGeoCoordinates().longitude)
                                );
                            }
                        }
                        placeAdapter.notifyDataSetChanged();
                    } else {
                        placeResponseList.clear();
                        placeAdapter.notifyDataSetChanged();
                        ibCheckSecond.setEnabled(false);
                    }
                });
    }

    private void createRecyclerView() {
        placeAdapter = new PlaceAdapter(getContext(), placeResponseList);
        placeAdapter.setOnItemClickListener(position -> {
            PlaceResponse placeResponse = placeResponseList.get(position);
            chosenPlaceName = placeResponse.getSecondaryPlaceName();

            placeResponseList.clear();
            placeAdapter.notifyDataSetChanged();
            edtLocation.setText(placeResponse.getSecondaryPlaceName());
            moveMapToJobLocation(placeResponse);
        });
        RecyclerView.LayoutManager layoutManagerPayout = new LinearLayoutManager(getContext());
        rcvPlace.setLayoutManager(layoutManagerPayout);
        rcvPlace.setItemAnimator(new DefaultItemAnimator());
        rcvPlace.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider_white));
        rcvPlace.setAdapter(placeAdapter);
    }

    private void moveMapToJobLocation(PlaceResponse placeResponse) {
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.clear();

            LatLng jobLocation = new LatLng(placeResponse.getLatitude(), placeResponse.getLongitude());
            mMap.addMarker(new MarkerOptions().position(jobLocation).title(placeResponse.getSecondaryPlaceName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jobLocation, 15));
            mMap.getUiSettings().setScrollGesturesEnabled(false);

            addJobRequest.setLat(placeResponse.getLatitude());
            addJobRequest.setLng(placeResponse.getLongitude());
            addJobRequest.setLocation(placeResponse.getSecondaryPlaceName());
            ibCheckSecond.setEnabled(true);
        });
    }

    private void getDeadlineOption() {
        switch (spDeadline.getSelectedItemPosition()) {
            case 0:
                // Tomorrow
                myCalendar.add(Calendar.DATE, 1);
                break;
            case 1:
                // 2 days from now
                myCalendar.add(Calendar.DATE, 2);
                break;
            case 2:
                // 3 days from now
                myCalendar.add(Calendar.DATE, 3);
                break;
            case 3:
                // 4 days from now
                myCalendar.add(Calendar.DATE, 4);
                break;
            case 4:
                // 5 days from now
                myCalendar.add(Calendar.DATE, 5);
                break;
            case 5:
                // 6 days from now
                myCalendar.add(Calendar.DATE, 6);
                break;
            case 6:
                // 1 week from now
                myCalendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case 7:
                // 2 week from now
                myCalendar.add(Calendar.WEEK_OF_YEAR, 2);
                break;
            case 8:
                // 3 week from now
                myCalendar.add(Calendar.WEEK_OF_YEAR, 3);
                break;
            case 9:
                // 1 month from now
                myCalendar.add(Calendar.MONTH, 1);
                break;
        }
        addJobRequest.setDeadline(sdf.format(myCalendar.getTime()));
        // Reset myCalendar to current date (now)
        myCalendar = Calendar.getInstance();
    }

    private void bindView() {
        spDeadline = binding.spinnerDeadline;
        edtLocation = binding.editTextLocation;
        rcvPlace = binding.placeRecyclerView;

        deadlineOption = getResources().getStringArray(R.array.dead_line);

        AddNewJob addNewJob = (AddNewJob) requireActivity();
        addJobRequest = addNewJob.addJobRequest;
        viewPager = addNewJob.viewBinding.viewPager;
        ibCheckSecond = addNewJob.viewBinding.imageButtonCheckSecond;

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.addJobMap);
    }

    private void generateSpinnerDeadline(Spinner spPaymentMileStone) {
        SpinnerString spinnerAdapter = new SpinnerString(getContext(), deadlineOption);
        spPaymentMileStone.setAdapter(spinnerAdapter);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
