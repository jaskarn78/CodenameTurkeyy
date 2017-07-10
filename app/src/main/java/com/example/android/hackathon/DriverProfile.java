package com.example.android.hackathon;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class DriverProfile extends AppCompatActivity {
    private final String URL_PATH="http://jagpal-development.com/food_truck/trucks/0_TestTruck/";
    private AppBarLayout appBar;
    private MapView mapView;
    private double truckLat, truckLng;
    private String truckName, truckImage, menuImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_driver_profile);

        Bundle bundle = getIntent().getExtras();
        truckName = bundle.getString("truck_name");
        truckImage = bundle.getString("truck_image");
        menuImage = bundle.getString("menu_image");
        truckLat = bundle.getDouble("truck_lat");
        truckLng = bundle.getDouble("truck_lng");
        int truckStatus = Integer.parseInt(bundle.getString("truck_status"));

        setupViews();
        setupToolbar();

        setupMap(savedInstanceState);
        mapView.onResume();



    }

    private void setupViews(){
        TextView truckNameView = (TextView)findViewById(R.id.truck_name);
        truckNameView.setText(truckName);

        RoundedImageView truckImageView = (RoundedImageView)findViewById(R.id.truck_image);
        //Glide.with(this).load(R.drawable.burger_truck_marker).into(truckImageView);
        Glide.with(this).load((URL_PATH+truckImage+".jpg")).into(truckImageView);

        final ImageView truckMenuView = (ImageView)findViewById(R.id.menuImage);
        Glide.with(this).load((URL_PATH+menuImage+".jpg")).into(truckMenuView);

        // Create the relative layout for which the food truck information will be displayed
        RelativeLayout relLayout = (RelativeLayout)findViewById(R.id.image_layout);
        relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(truckMenuView.getVisibility()!=View.VISIBLE)
                    truckMenuView.setVisibility(View.VISIBLE);
                else
                    truckMenuView.setVisibility(View.GONE);
            }});

        //TextView truckStatusView = (TextView)findViewById(R.id.truck_statusView);
        //truckStatusView.setText(truckStatus);


    }



    private void setupToolbar(){
        AppBarLayout appBar = (AppBarLayout)findViewById(R.id.app_bar_layout);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getColor(R.color.black));
        collapsingToolbarLayout.setExpandedTitleColor(getColor(android.R.color.transparent));
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        collapsingToolbarLayout.setTitle("Your Profile");
    }

    /** Setup the map location of the food truck */
    public void setupMap(Bundle savedInstanceState) {
        // Display the location of the truck on a mini map
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                LatLng eventCoords = new LatLng(truckLat, truckLng);
                gMap.addMarker(new MarkerOptions().position(eventCoords).title("Location"));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(eventCoords, 10);
                gMap.animateCamera(update);
            }
        });
    }
}
