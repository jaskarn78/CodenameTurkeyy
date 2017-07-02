package com.example.android.hackathon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.android.hackathon.R.id.status;

// TODO Comment this .java file

/**
 *   Create the Activity for the Driver Profile Page, where users will view the food truck
 *  information.
 */
public class ProfileActivity extends AppCompatActivity {
    private GoogleMap map;
    private MapView mapView;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        // Create and display food truck information
        setupTruckInformation();

        // Create MapView for food truck position
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        setupMap();
        mapView.onResume();
    }

    /** Collect the selected food truck's information and display it to the view */
    private void setupTruckInformation() {
        //  Get position in array from Intent (for gathering the correct truck's information and
        // displaying it).
        int position = getIntent().getIntExtra("position", 0);

        // Create lists for truck images, names, menus, types...
        String truckImage = getIntent().getStringExtra("truckImage");
        String truckName = getIntent().getStringExtra("truckName");
        String menuImage = getIntent().getStringExtra("menuImage");
        String type = getIntent().getStringExtra("type");

        // Gather selected truck's latitude and longitude
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        // Set truck image view to correct truck photo
        ImageView truckimg = (ImageView)findViewById(R.id.display_truck_image);
        Glide.with(this).load(truckImage).into(truckimg);

        // Set truck name to correct food truck name
        TextView truckNameTxt = (TextView)findViewById(R.id.truck_owner);
        truckNameTxt.setText(truckName);

        // Set truck menu image to correct food truck menu
        final ImageView menuImageView = (ImageView)findViewById(R.id.menuImage);
        Glide.with(this).load(menuImage).into(menuImageView);

        // Get food truck's current status
        TextView statusTxt = (TextView)findViewById(status);
        statusTxt.setText("Status: Available");

        // Get food truck's food type
        TextView typeTxt = (TextView)findViewById(R.id.type);
        typeTxt.setText("Food Type: "+type);

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
}
