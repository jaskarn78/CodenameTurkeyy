package com.example.android.hackathon;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DriverActivity extends AppCompatActivity {
    private static final int GET_FROM_GALLERY = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;


    private MaterialSpinner food_spinner;
    private String[] food_array;
    private MapView driver_map;
    private GoogleMap googleMap;
    private GPSTracker gpsTracker;
    private ImageButton uploadBtn, cameraBtn;
    private ImageView menuImage;
    int TAKE_PICTURE = 0;
    private Uri outputFileUri;
    public static int count = 0;
    String mCurrentPhotoPath;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        MapsInitializer.initialize(this);
        gpsTracker = new GPSTracker(this);

        menuImage = (ImageView) findViewById(R.id.upload_image_view);

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

        cameraBtn = (ImageButton)findViewById(R.id.camera_menu_button);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /***** Upload Images ******/

            }
        });

        uploadBtn = (ImageButton)findViewById(R.id.upload_menu_button);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /***** Upload Images ******/
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
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
        }else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            menuImage.setImageBitmap(photo);
        }
    }



}
