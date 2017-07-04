package com.example.android.hackathon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.hackathon.Utilities.GPSTracker;
import com.example.android.hackathon.Utilities.RoundedImageView;
import com.example.android.hackathon.Utilities.UploadFileAsync;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hlab.fabrevealmenu.view.FABRevealMenu;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 *  Activity for the Driver. Provides the option to set truck name, food type, active time,
 * truck photo, and truck menu.
 */
// TODO Redo everything...
public class DriverActivity extends Activity {
    private static final int GET_TRUCK_FROM_GALLERY = 1;
    private static final int REQUEST_TRUCK_IMAGE_CAPTURE = 2;
    private static final int GET_MENU_FROM_GALLERY = 3;
    private static final int REQUEST_MENU_IMAGE_CAPTURE = 4;

    private MapView driver_map;
    private GoogleMap googleMap;
    private GPSTracker gpsTracker;
    private RoundedImageView menuImage, truckImage;
    private String mCurrentPhotoPath;
    private TextView stepOneTV, stepTwoTV, stepThreeTV, stepFourTV;

    private FloatingActionButton fabName, fabLocation, fabType, fabImage;

    private String truckName;
    private double driverLat, driverLng;
    private View fabMenuView;
    private FABRevealMenu fabMenu1, fabMenu2, fabMenu3, fabMenu4;
    private TextView currentLocationTV;
    private ViewPager typePager;
    private Button truckFromDevice, truckFromCamera, menuFromDevice, menuFromCamera;
    int[] truckDrawables = {
            R.drawable.burger_truck,
            R.drawable.pizza_truck,
            R.drawable.spec_truck,
            R.drawable.taco_truck,
            R.drawable.twinkie_truck
    };
    String[] truckTextViews = {
            "American", "Pizza", "Speciality", "Mexican", "Desserts"
    };



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
        setContentView(R.layout.truck_info_reveal);
        GPSTracker gpsTracker = new GPSTracker(this);
        driverLat = gpsTracker.getLatitude();
        driverLng = gpsTracker.getLongitude();
        // Initialize the Driver's position
        MapsInitializer.initialize(this);
        fabMenu1 = (FABRevealMenu)findViewById(R.id.reveal);
        fabMenu2 = (FABRevealMenu)findViewById(R.id.reveal2);
        fabMenu3 = (FABRevealMenu)findViewById(R.id.reveal3);
        fabMenu4 = (FABRevealMenu)findViewById(R.id.reveal4);

        setupFabs();
        setupStepTextViews();
        setupFabName();
        setupLocationFAB();

        // Get Driver's current position and set up Map
        driver_map = (MapView)fabMenuView.findViewById(R.id.mapView2);
        driver_map.onCreate(savedInstanceState);
        driver_map.onResume();

        setupMap();
        setupTypeFAB();
        setupCameraFab();


        // Create Camera buttons
        //setupCameraButtons();
    }

    private void setupFabs(){
        //Floating action buttons used for each step of the food truck registration
        fabName = (FloatingActionButton)findViewById(R.id.truck_name_fab);
        fabLocation = (FloatingActionButton)findViewById(R.id.truck_fab_location);
        fabType = (FloatingActionButton)findViewById(R.id.truck_fab_type);
        fabImage = (FloatingActionButton)findViewById(R.id.truck_fab_image);
    }

    private void setupStepTextViews(){
        //Textviews to display driver input
        stepOneTV = (TextView)findViewById(R.id.step_one_name);
        stepTwoTV = (TextView)findViewById(R.id.step_two_location);
        stepThreeTV = (TextView)findViewById(R.id.step_three_type);
        stepFourTV = (TextView)findViewById(R.id.step_four_image);

    }

    //Assigning floating action button to reveal frame
    private void setupFabName(){
        if(fabName!=null && fabMenu1!=null) {
            View customView = View.inflate(this, R.layout.custom_name_layout, null);
            fabMenuView = customView;
            fabMenu1.setCustomView(customView);
            fabMenu1.bindAncherView(fabName);
            TextView okTextView = (TextView)fabMenuView.findViewById(R.id.okName_tv);
            TextView cancelTextView = (TextView)fabMenuView.findViewById(R.id.cancelName_tv);
            final EditText nameEditText = (EditText)fabMenuView.findViewById(R.id.enter_name);
            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nameEditText.getText().toString().length()>1) {
                        fabMenu1.closeMenu();
                        truckName = nameEditText.getText().toString();
                        stepOneTV.setText(truckName);
                        fabName.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.step_complete)));
                        fabName.setImageResource(R.drawable.ic_check_white_24dp);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Enter valid name", Toast.LENGTH_SHORT).show();
                } });

            cancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabMenu1.closeMenu(); } });

        }
    }


    private void setupLocationFAB(){
        if(fabLocation!=null && fabMenu2!=null) {
            View customView = View.inflate(this, R.layout.custom_location_layout, null);
            fabMenuView = customView;
            fabMenu2.setCustomView(customView);
            fabMenu2.bindAncherView(fabLocation);
            TextView okTextView = (TextView)findViewById(R.id.ok_tv);
            TextView cancelTextView = (TextView)findViewById(R.id.cancel_tv);
            currentLocationTV = (TextView) findViewById(R.id.current_location);
            currentLocationTV.setText(getAddressFromLatLng());
            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stepTwoTV.setText(currentLocationTV.getText());
                    fabLocation.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.step_complete)));
                    fabLocation.setImageResource(R.drawable.ic_check_white_24dp);
                    fabMenu2.closeMenu();
                } });

            cancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabMenu2.closeMenu(); } });
        }

    }

    private void setupTypeFAB(){
        if(fabType!=null && fabMenu3!=null){
            View customView = View.inflate(this, R.layout.custom_type_layout, null);
            fabMenuView = customView;
            fabMenu3.setCustomView(customView);
            fabMenu3.bindAncherView(fabType);

            //set up viewpager that to display various food truck types
            typePager = (ViewPager)fabMenuView.findViewById(R.id.type_viewPager);
            typePager.setAdapter(new TruckTypePagerAdapter(this));

            TextView okTextView = (TextView)findViewById(R.id.ok_type_tv);
            TextView cancelTextView = (TextView)findViewById(R.id.cancel_type_tv);
            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stepThreeTV.setText(truckTextViews[typePager.getCurrentItem()]);
                    fabType.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.step_complete)));
                    fabType.setImageResource(R.drawable.ic_check_white_24dp);
                    fabMenu3.closeMenu();
                } });

            cancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabMenu3.closeMenu(); } });
        }
    }

    private void setupCameraFab() {
        if (fabImage != null && fabMenu4 != null) {
            View customView = View.inflate(this, R.layout.custom_image_layout, null);
            fabMenuView = customView;
            fabMenu4.setCustomView(customView);
            fabMenu4.bindAncherView(fabImage);
            setupCameraButtons();

            truckFromDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Truck image from storage", Toast.LENGTH_SHORT).show();
                }
            });

            truckFromCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                    "com.example.android.hackathon.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_TRUCK_IMAGE_CAPTURE);
                        }
                    }
                }
            });

            menuFromDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Menu image from storage", Toast.LENGTH_SHORT).show();

                }
            });

            menuFromCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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


            TextView okTextView = (TextView) fabMenuView.findViewById(R.id.ok_tv);
            TextView cancelTextView = (TextView) fabMenuView.findViewById(R.id.cancel_tv);
            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabMenu4.closeMenu();
                    stepFourTV.setText("Complete");
                    fabImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.step_complete)));
                    fabImage.setImageResource(R.drawable.ic_check_white_24dp);
                }
            });

            cancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fabMenu4.closeMenu();
                }
            });
        }
    }
    private void setupCameraButtons() {
        //initialize buttons used for retrieving truck and menu images
        truckFromDevice = (Button) fabMenuView.findViewById(R.id.truck_device);
        truckFromCamera = (Button) fabMenuView.findViewById(R.id.truck_camera);
        menuFromDevice = (Button) fabMenuView.findViewById(R.id.menu_device);
        menuFromCamera = (Button) fabMenuView.findViewById(R.id.menu_camera);

        menuImage = (RoundedImageView) fabMenuView.findViewById(R.id.menu_imageView);
        truckImage = (RoundedImageView) fabMenuView.findViewById(R.id.truck_imageView);
    }



    private String getAddressFromLatLng(){
        Geocoder geocoder;
        List<Address> addresses;
        String address=""; String city=""; String state=""; String postalCode="";
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(driverLat, driverLng, 1);
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



    /** Grabs GPS coordinates of Driver user and posts the marker to the map */
    private void setupMap(){
        driver_map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap=gMap;
                LatLng eventCoords = new LatLng(driverLat, driverLng);
                googleMap.addMarker(new MarkerOptions().position(eventCoords).title("Current Loc"));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(eventCoords, 15);
                googleMap.animateCamera(update);
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
                menuImage.setVisibility(View.VISIBLE);
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
                // set menuImage to upright bitmap
                menuImage.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                menuImage.setVisibility(View.VISIBLE);
                // TODO Make this not test code...
                new UploadFileAsync().execute(mCurrentPhotoPath, "0_TestTruck", "Truck_1").get();

            } catch (IOException ex) {
                Log.e("IMG_SAVE", "Error trying to save image");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
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
                menuImage.setVisibility(View.VISIBLE);
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
                menuImage.setVisibility(View.VISIBLE);
                // TODO Make this not test code...
                new UploadFileAsync().execute(mCurrentPhotoPath, "0_TestTruck", "Menu_1").get();

            } catch (IOException ex) {
                Log.e("IMG_SAVE", "Error trying to save image");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
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



    class TruckTypePagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public TruckTypePagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return truckDrawables.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.typeImage);
            imageView.setImageResource(truckDrawables[position]);

            TextView typeTextView = (TextView)itemView.findViewById(R.id.typeTextView);
            typeTextView.setText(truckTextViews[position]);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
