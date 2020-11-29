package com.tt.handsomeman.ui.customer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ActivityCustomerMainScreenBinding;
import com.tt.handsomeman.ui.BaseAppCompatActivity;
import com.tt.handsomeman.ui.NotificationsFragment;
import com.tt.handsomeman.ui.customer.find_handyman.FindHandymanFragment;
import com.tt.handsomeman.ui.customer.more.CustomerMoreFragment;
import com.tt.handsomeman.ui.customer.my_projects.MyProjectsFragment;
import com.tt.handsomeman.ui.messages.MessagesFragment;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class CustomerMainScreen extends BaseAppCompatActivity {

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private static final long MIN_TIME_TO_REQUEST_LOCATION = 30000; //30s
    private static final float MIN_DISTANCE_TO_REQUEST_LOCATION = 5; //5 meters
    final Fragment fragment1 = new FindHandymanFragment();
    final Fragment fragment2 = new MessagesFragment();
    final Fragment fragment3 = new MyProjectsFragment();
    final Fragment fragment4 = new NotificationsFragment();
    final Fragment fragment5 = new CustomerMoreFragment();
    final FragmentManager fm = getSupportFragmentManager();
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ActivityCustomerMainScreenBinding binding;
    private LocationManager locationManager;
    private Fragment active = fragment1;
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_jobs:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;

            case R.id.navigation_messages:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;

            case R.id.navigation_my_projects:
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;

            case R.id.navigation_notifications:
                fm.beginTransaction().hide(active).show(fragment4).commit();
                active = fragment4;
                return true;

            case R.id.navigation_more:
                fm.beginTransaction().hide(active).show(fragment5).commit();
                active = fragment5;
                return true;
        }
        return false;
    };
    private Geocoder geoCoder;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            if (lat != null && lng != null) {
                Constants.Latitude.setValue(location.getLatitude());
                Constants.Longitude.setValue(location.getLongitude());
                Toast.makeText(CustomerMainScreen.this, "Location changed", Toast.LENGTH_SHORT).show();

                try {
                    List<Address> address = geoCoder.getFromLocation(lat, lng, 1);

                    StringBuilder builder = new StringBuilder();

                    String featureName = address.get(0).getFeatureName();
                    String thoroughfare = address.get(0).getThoroughfare();
                    String subAdminArea = address.get(0).getSubAdminArea();
                    String adminArea = address.get(0).getAdminArea();

                    if (featureName != null) {
                        builder.append(featureName);
                        builder.append(", ");
                    }
                    if (thoroughfare != null) {
                        builder.append(thoroughfare);
                        builder.append(", ");
                    }
                    if (subAdminArea != null) {
                        builder.append(subAdminArea);
                        builder.append(", ");
                    }
                    if (adminArea != null) {
                        builder.append(adminArea);
                        builder.append(", ");
                    }

                    sharedPreferencesUtils.put("address", builder.substring(0, builder.length() - 2));
                    sharedPreferencesUtils.put("countryCode", address.get(0).getCountryCode());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Address error", e.getMessage());
                }
            }
        }

        @Override
        public void onStatusChanged(String s,
                                    int i,
                                    Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HandymanApp.getComponent().inject(this);

        geoCoder = new Geocoder(CustomerMainScreen.this, Locale.getDefault());

        BottomNavigationView navView = binding.navView;

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupWithNavController(navView, navController);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationService();
        getLastKnownLocation();

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManipulate();
    }

    private void getLastKnownLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Constants.Latitude.setValue(location.getLatitude());
                        Constants.Longitude.setValue(location.getLongitude());
                    }
                });
    }

    private void FragmentManipulate() {
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment5, "5").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start location service.
                startLocationService();
            } else {
                // Permission request was denied.
                Snackbar.make(binding.container, "Permission is denied",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void startLocationService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_TO_REQUEST_LOCATION, MIN_DISTANCE_TO_REQUEST_LOCATION, locationListener);

            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_TO_REQUEST_LOCATION, MIN_DISTANCE_TO_REQUEST_LOCATION, locationListener);
            }
        } else {
            // Permission is missing and must be requested.
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(binding.container, "Permission is needed",
                    Snackbar.LENGTH_INDEFINITE).setAction("GRANT IT", view -> {
                // Request the permission
                ActivityCompat.requestPermissions(CustomerMainScreen.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);
            }).show();

        } else {
            Snackbar.make(binding.container, "Location is unavailable", Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String userId = sharedPreferencesUtils.get("userId", String.class);
        FirebaseMessaging.getInstance().subscribeToTopic("notification-" + userId);
    }

    @Override
    protected void onDestroy() {
        Log.d("Location", "Destroy");
        locationManager.removeUpdates(locationListener);
        Constants.Latitude.removeObservers(this);
        Constants.Longitude.removeObservers(this);
        super.onDestroy();
    }
}
