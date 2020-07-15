package com.tt.handsomeman.ui.customer.my_projects.add_job;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class AddNewJobChildSecondFragment extends Fragment {

    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private Spinner spDeadline;
    private EditText edtLocation;
    private ImageButton ibCheckSecond;
    private ViewPager2 viewPager;
    private GoogleMap mMap;
    private PlaceAdapter placeAdapter;
    private RecyclerView rcvPlace;
    private List<PlaceResponse> placeResponseList = new ArrayList<>();
    private String[] deadlineOption;
    private Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat sdf;
    private AddJobRequest addJobRequest;
    private Double lat, lng;
    private String countryCode;
    private PlacesClient placesClient;
    private AutocompleteSessionToken token;
    private RectangularBounds bounds;
    private SupportMapFragment mapFragment;
    private Double jobLatEdit, jobLngEdit;
    private String chosenPlaceName = null;
    private FragmentAddNewJobChildSecondBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
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
        countryCode = sharedPreferencesUtils.get("countryCode", String.class);

        placesClient = Constants.placesClientMutableLiveData.getValue();
        token = Constants.autocompleteSessionTokenMutableLiveData.getValue();
        // Create a RectangularBounds object.
        bounds = RectangularBounds.newInstance(
                new LatLng(lat - 0.01, lng - 0.01),
                new LatLng(lat + 0.01, lng + 0.01));

        bindView();
        generateSpinnerDeadline(spDeadline);
        createRecyclerView();
        initJobLocation(currentAddress);
        editTextEmitValueListener();
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
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .filter(charSequence -> charSequence.length() >= 3 && !charSequence.equals(chosenPlaceName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::autoCompletePlaceBuilder);
    }

    private void autoCompletePlaceBuilder(String charSequence) {
        Log.d("RxTextView ", "is working");
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setOrigin(new LatLng(lat, lng))
                .setCountry(countryCode)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(charSequence)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            placeResponseList.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                placeResponseList.add(new PlaceResponse(prediction.getPlaceId(), prediction.getPrimaryText(null).toString(), prediction.getSecondaryText(null).toString()));
            }
            placeAdapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int errorCode = apiException.getStatusCode();
                Log.e("Autocomplete builder", "Place not found: " + errorCode);
                if (errorCode == 9010) {
                    Toast.makeText(getContext(), getResources().getString(R.string.error_9010_auto_complete), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.places_not_found_auto_complete), Toast.LENGTH_SHORT).show();
                }
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
            chosenPlaceName = placeResponse.getPrimaryPlaceName();

            placeResponseList.clear();
            placeAdapter.notifyDataSetChanged();
            searchPlaceBaseOnPlaceId(placeResponse.getPlaceId(), placeResponse.getPrimaryPlaceName());
            edtLocation.setText(placeResponse.getPrimaryPlaceName());
        });
        RecyclerView.LayoutManager layoutManagerPayout = new LinearLayoutManager(getContext());
        rcvPlace.setLayoutManager(layoutManagerPayout);
        rcvPlace.setItemAnimator(new DefaultItemAnimator());
        rcvPlace.addItemDecoration(new CustomDividerItemDecoration(getResources().getDrawable(R.drawable.recycler_view_divider_white)));
        rcvPlace.setAdapter(placeAdapter);
    }

    private void searchPlaceBaseOnPlaceId(String placeId,
                                          String placeName) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            if (place.getLatLng() != null) {
                jobLatEdit = place.getLatLng().latitude;
                jobLngEdit = place.getLatLng().longitude;
                Log.i("Search place", "Place found: " + place.getName() + ", " + jobLatEdit + "|" + jobLngEdit);
            }

            moveMapToJobLocation(placeName);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("Search place", "Place not found: " + apiException.getStatusCode());
                if (statusCode == 9010) {
                    Toast.makeText(getContext(), getResources().getString(R.string.error9010_search_place), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.places_not_found_search_place), Toast.LENGTH_SHORT).show();
                }
                ibCheckSecond.setEnabled(false);
            }
        });
    }

    private void moveMapToJobLocation(String placeName) {
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.clear();

            LatLng jobLocation = new LatLng(jobLatEdit, jobLngEdit);
            mMap.addMarker(new MarkerOptions().position(jobLocation).title(placeName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jobLocation, 15));
            mMap.getUiSettings().setScrollGesturesEnabled(false);

            addJobRequest.setLat(jobLatEdit);
            addJobRequest.setLng(jobLngEdit);
            addJobRequest.setLocation(placeName);
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
