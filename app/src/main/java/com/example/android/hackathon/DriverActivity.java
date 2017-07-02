package com.example.android.hackathon;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *  Activity for the Driver. Provides the option to set truck name, food type, active time,
 * truck photo, and truck menu.
 */
// TODO Redo everything...
public class DriverActivity extends AppCompatActivity {
    private static final int GET_TRUCK_FROM_GALLERY = 1;
    private static final int REQUEST_TRUCK_IMAGE_CAPTURE = 2;
    private static final int GET_MENU_FROM_GALLERY = 3;
    private static final int REQUEST_MENU_IMAGE_CAPTURE = 4;

    private MapView driver_map;
    private GoogleMap googleMap;
    private GPSTracker gpsTracker;
    private ImageView menuImage;
    private String mCurrentPhotoPath;


    /**
     *   Creates Driver User's activity. Provides GPS positioning, truck name setting, active toggle,
     *  active time, picture taking/gallery uploading for truck and menu photos.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_driver);

        // Initialize the Driver's position
        MapsInitializer.initialize(this);
        gpsTracker = new GPSTracker(this);

        menuImage = (ImageView) findViewById(R.id.upload_image_view);

        // Create Spinners
        setupSpinners();

        // Get Driver's current position and set up Map
        driver_map = (MapView) findViewById(R.id.mapView2);
        driver_map.onCreate(savedInstanceState);
        driver_map.onResume();
        setupMap();

        // Create Camera buttons
        setupCameraButtons();
    }

    /** Creates the dropdown selection for Food Type and Truck Active Time*/
    private void setupSpinners() {
        MaterialSpinner food_spinner, startSpinner, endSpinner;

        // Create the spinned
        food_spinner = (MaterialSpinner) findViewById(R.id.food_spinner);

        // Set Food Spinner colors
        food_spinner.setTextColor(getColor(R.color.black));
        food_spinner.setBackgroundColor(getColor(R.color.cardview_light_background));

        // Get String-Array of all food type options and set Food Type spinner values
        String[] arr = getResources().getStringArray(R.array.food_spinner);
        List<String> spinList = new ArrayList<String>(Arrays.asList(arr));
        food_spinner.setItems(spinList);

        // Create Active Time Spinners
        startSpinner = (MaterialSpinner)findViewById(R.id.startTime);
        endSpinner = (MaterialSpinner)findViewById(R.id.endTime);

        // Set Active Time Spinner Colors
        startSpinner.setBackgroundColor(getColor(R.color.cardview_light_background));
        endSpinner.setBackgroundColor(getColor(R.color.cardview_light_background));


        // Get String-Array of all time options and set Active Time spinner values
        String[] times = getResources().getStringArray(R.array.time_spinner);
        List<String> timeList = new ArrayList<String>(Arrays.asList(times));
        startSpinner.setItems(timeList);
        endSpinner.setItems(timeList);
    }

    /** Grabs GPS coordinates of Driver user and posts the marker to the map */
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

    // TODO Create an 'X' on the captured image. Giving the driver the option to not save it
    /** Create camera buttons and add onClickListeners */
    private void setupCameraButtons() {
        ImageButton uploadTruckPhotoBtn, captureTruckPhotoBtn,
                    uploadMenuPhotoBtn, captureMenuPhotoBtn;

        /* If the user has a camera feature, enable the CAPTURE buttons */
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // Create button to CAPTURE TRUCK image from camera
            captureTruckPhotoBtn = (ImageButton) findViewById(R.id.camera_truck_button);
            captureTruckPhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an Intent for Image Capturing, start activity for capturing
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile(REQUEST_TRUCK_IMAGE_CAPTURE);
                        } catch (IOException ex) {
                            // Error occurred while creating the File

                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(DriverActivity.this,
                                    "com.example.android.hackathon.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_TRUCK_IMAGE_CAPTURE);
                        }
                    }
                }
            });

            /* Create button to CAPTURE MENU photo from camera */
            captureMenuPhotoBtn = (ImageButton) findViewById(R.id.camera_menu_button);
            captureMenuPhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an Intent for Image Capturing, start activity for capturing
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile(REQUEST_MENU_IMAGE_CAPTURE);
                            } catch (IOException ex) {
                                // Error occurred while creating the File

                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(DriverActivity.this,
                                        "com.example.android.hackathon.fileprovider",
                                        photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_MENU_IMAGE_CAPTURE);
                            }
                        }
                    }
                }
            });

        }

        /* Create button to UPLOAD TRUCK photo from phone gallery */
        uploadTruckPhotoBtn = (ImageButton) findViewById(R.id.upload_truck_button);
        uploadTruckPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* Create button to UPLOAD MENU photo from gallery */
        uploadMenuPhotoBtn = (ImageButton) findViewById(R.id.upload_menu_button);
        uploadMenuPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    /**
     *  Called due to Driver user selecting the Capture Photo button or Upload From Gallery button.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Checks requestCode to see if user would like to UPLOAD TRUCK photo from gallery
        if(requestCode == GET_TRUCK_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            // TODO Save image to a database

            // Create Uri for the selected image in gallery
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;

            try {
                // set image to
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
        // Checks requestCode to see if user is requesting to CAPTURE TRUCK image with camera
        else if (requestCode == REQUEST_TRUCK_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                setImageUpright(mCurrentPhotoPath);
            } catch (IOException ex) {
                Log.e("IMG_SAVE", "Error trying to save image");
            }

            galleryAddPic();
        }
        // Checks requestCode to see if user would like to UPLOAD MENU image from gallery
        else if(requestCode == GET_MENU_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            // TODO Save image to a database

            // Create Uri for the selected image in gallery
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;

            try {
                // Create bitmap from selected image
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
        // Checks requestCode to see if user is requesting to CAPTURE MENU image with camera
        else if(requestCode == REQUEST_MENU_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                setImageUpright(mCurrentPhotoPath);
                // set menuImage to upright bitmap
                menuImage.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            } catch (IOException ex) {
                Log.e("IMG_SAVE", "Error trying to save image");
            }

            galleryAddPic();
        }

    }

    /** Sets the image stored at 'imagepath' to the upright position */
    private static void setImageUpright(String imagepath) throws IOException{
        FileOutputStream fos = null;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            ExifInterface ei = new ExifInterface(imagepath);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateBitmap(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateBitmap(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateBitmap(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }

            // Resave image as upright
            File f = new File(imagepath);
            fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (IOException e) {
            e.getMessage();
        } finally {
            fos.close();
        }
    }

    /** Rotates the parameter bitmap by "angle" degrees and returns the new bitmap */
    private static Bitmap rotateBitmap(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        return rotatedBitmap;
    }

    /** Creates an image file for the picture that was taken */
    private File createImageFile(int captureId) throws IOException {
        String prefix;

        // Set file prefix
        if (captureId == REQUEST_MENU_IMAGE_CAPTURE)
            prefix = "MENU_";
        else
            prefix = "TRUCK_";

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = prefix + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

   /** Adds currently taken picture to the photo gallery */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
        Uri contentUri = Uri.fromFile(f);
        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                f.getName(),
                f.getName());
    }
}
