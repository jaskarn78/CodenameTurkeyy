package com.example.android.hackathon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.esafirm.imagepicker.features.camera.CameraModule;
import com.esafirm.imagepicker.features.camera.DefaultCameraModule;
import com.esafirm.imagepicker.features.camera.ImmediateCameraModule;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DriverActivity extends AppCompatActivity {
    private static final int GET_FROM_GALLERY = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private MaterialSpinner food_spinner;
    private String[] food_array;
    private MapView driver_map;
    private GoogleMap googleMap;
    private GPSTracker gpsTracker;
    private ImageButton uploadBtn, cameraBtn;
    private ImageView menuImage;
    private static final int RC_CAMERA = 3000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        MapsInitializer.initialize(this);
        gpsTracker = new GPSTracker(this);

        menuImage = (ImageView) findViewById(R.id.upload_image_view);

        /******   Create Spinner    ******/
        food_spinner = (MaterialSpinner) findViewById(R.id.food_spinner);
        food_spinner.setTextColor(getColor(R.color.black));
        food_spinner.setBackgroundColor(getColor(R.color.cardview_light_background));

        String[] arr = getResources().getStringArray(R.array.food_spinner);

        List<String> spinList = new ArrayList<String>(Arrays.asList(arr));

        food_spinner.setItems(spinList);
        // Create spinner adapter
        // Attach adapter to spinner
        driver_map = (MapView) findViewById(R.id.mapView2);
        driver_map.onCreate(savedInstanceState);
        driver_map.onResume();

        setupMap();

        cameraBtn = (ImageButton) findViewById(R.id.camera_menu_button);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        uploadBtn = (ImageButton) findViewById(R.id.upload_menu_button);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DefaultCameraModule cameraModule = new DefaultCameraModule(); // or ImmediateCameraModule
                startActivityForResult(cameraModule.getCameraIntent(DriverActivity.this), RC_CAMERA);
            }
        });
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                menuImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (requestCode == RC_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            menuImage.setImageBitmap(imageBitmap);
        }
    }


}
