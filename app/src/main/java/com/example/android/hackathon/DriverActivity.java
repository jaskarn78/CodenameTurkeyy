package com.example.android.hackathon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DriverActivity extends AppCompatActivity {
    private MaterialSpinner food_spinner;
    private String[] food_array;
    private MapView driver_map;
    private GoogleMap googleMap;
    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        MapsInitializer.initialize(this);
        gpsTracker = new GPSTracker(this);

        /******   Create Spinner    ******/
        food_spinner = (MaterialSpinner)findViewById(R.id.food_spinner);
        food_spinner.setTextColor(getColor(R.color.black));
        food_spinner.setBackgroundColor(getColor(R.color.cardview_light_background));

        String[] arr = getResources().getStringArray(R.array.food_spinner);

        List<String> spinList = new ArrayList<String>(Arrays.asList(arr));

        food_spinner.setItems(spinList);
        // Create spinner adapter
        // Attach adapter to spinner
        driver_map = (MapView)findViewById(R.id.mapView2);
        driver_map.onCreate(savedInstanceState);
        driver_map.onResume();

        setupMap();
    }

    private void setupMap(){
        driver_map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap=gMap;
                LatLng eventCoords = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(eventCoords).title("Current Loc"));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(eventCoords, 15);
                googleMap.animateCamera(update);
            }
        });
    }
}
