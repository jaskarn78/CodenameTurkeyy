package com.example.android.hackathon;

import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.hackathon.Utilities.RoundedImageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 *   Create the Activity for the Driver Profile Page, where users will view the food truck
 *  information.
 */
public class ProfileActivity extends AppCompatActivity {
    private GoogleMap map;
    private MapView mapView;
    private double lat, lng;
    private String truckName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        // Create and display food truck information
        setupTruckInformation();
        setupToolbar();
        // Create MapView for food truck position
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        setupMap();
        mapView.onResume();
    }

    private void setupToolbar(){
        AppBarLayout appBar = (AppBarLayout)findViewById(R.id.app_bar_layout);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getColor(R.color.black));
        collapsingToolbarLayout.setExpandedTitleColor(getColor(android.R.color.transparent));
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(truckName);
    }


    /** Collect the selected food truck's information and display it to the view */
    private void setupTruckInformation() {
        //  Get position in array from Intent (for gathering the correct truck's information and
        // displaying it).

        // Create lists for truck images, names, menus, types...
        String truckImage = getIntent().getStringExtra("truckImage");
        truckName = getIntent().getStringExtra("truckName");
        String menuImage = getIntent().getStringExtra("menuImage");
        String truckStatus = getIntent().getStringExtra("truckStatus");
        String type = getIntent().getStringExtra("type");


        // Gather selected truck's latitude and longitude
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        // Set truck image view to correct truck photo
        RoundedImageView truckimg = (RoundedImageView) findViewById(R.id.display_truck_image);
        Glide.with(this).load(truckImage).into(truckimg);

        // Set truck name to correct food truck name
        TextView truckNameTxt = (TextView)findViewById(R.id.truck_name_profile);
        truckNameTxt.setText(truckName);

        // Set truck menu image to correct food truck menu
        final ImageView menuImageView = (ImageView)findViewById(R.id.menuImage);
        Glide.with(this).load(menuImage).into(menuImageView);

        // Get food truck's current status
        TextView statusTxt = (TextView)findViewById(R.id.truck_status);
        Toast.makeText(getApplicationContext(), statusTxt.getText(), Toast.LENGTH_SHORT).show();
        if(truckStatus.equals("1")) {
            statusTxt.setText("Status: Available");
            statusTxt.setTextColor(getColor(R.color.md_light_green_A400));
        }
        else {
            statusTxt.setText("Status: Unavailable");
            statusTxt.setTextColor(getColor(R.color.md_red_A400));
        }

        statusTxt.setText("Status: Available");

        // Get food truck's food type
        TextView typeTxt = (TextView)findViewById(R.id.type);
        typeTxt.setText(type);

        TextView locationTxt = (TextView)findViewById(R.id.truck_location);
        locationTxt.setText(getAddressFromLatLng());

        // Create the relative layout for which the food truck information will be displayed
        RelativeLayout relLayout = (RelativeLayout)findViewById(R.id.image_layout);
        relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuImageView.getVisibility()!=View.VISIBLE)
                    menuImageView.setVisibility(View.VISIBLE);
                else
                    menuImageView.setVisibility(View.GONE);
            }});
    }


    /** Setup the map location of the food truck */
    public void setupMap() {
        // Display the location of the truck on a mini map
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                map = gMap;
                LatLng eventCoords = new LatLng(lat, lng);
                gMap.addMarker(new MarkerOptions().position(eventCoords).title("Location"));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(eventCoords, 10);
                gMap.animateCamera(update);
            }
        });
    }

    /**Uses driver lat and lng to retrieve corresponding address*/
    private String getAddressFromLatLng(){
        Geocoder geocoder;
        List<Address> addresses;
        String address=""; String city=""; String state=""; String postalCode="";
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if(!addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                postalCode = addresses.get(0).getPostalCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!state.isEmpty())
            state = state.substring(0, 2);
        return address+" "+city+", "+state+" "+postalCode;
    }
}
