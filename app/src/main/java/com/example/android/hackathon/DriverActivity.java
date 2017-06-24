package com.example.android.hackathon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

public class DriverActivity extends AppCompatActivity {
    private Spinner food_spinner;
    private String[] food_array;
    private MapView driver_map;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        MapsInitializer.initialize(this);

        /******   Create Spinner    ******/
        food_spinner = (Spinner)findViewById(R.id.food_spinner);
        // Create spinner adapter
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.food_spinner, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attach adapter to spinner
        food_spinner.setAdapter(spinner_adapter);
//        food_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                if (pos == 0) {
//                }else {
//                    // Your code to process the selection
//                }
//            }
//        });
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
                //LatLng eventCoords = new LatLng(eventLat, eventLng);
                //googleMap.addMarker(new MarkerOptions().position(eventCoords).title(eventName));
                //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(eventCoords, 10);
                //googleMap.animateCamera(update);
            }
        });
    }
}
