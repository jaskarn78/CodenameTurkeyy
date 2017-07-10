package com.example.android.hackathon;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.example.android.hackathon.Utilities.Imageutils;
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
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 *  Activity for the Driver. Provides the option to set truck name, food type, active time,
 * truck photo, and truck menu.
 */
// TODO Redo everything...
public class DriverActivity extends Activity implements Imageutils.ImageAttachmentListener {
    private static final int REQUEST_TRUCK_IMAGE = 1;
    private static final int REQUEST_MENU_IMAGE = 2;
    private MapView driver_map;
    private GoogleMap googleMap;
    private TextView stepOneTV, stepTwoTV, stepThreeTV, stepFourTV;

    private FloatingActionButton fabName, fabLocation, fabType, fabImage;
    private FABRevealMenu fabMenu1, fabMenu2, fabMenu3, fabMenu4;
    private Truck truckObj;
    private Button submitButton;
    private View fabMenuView;
    private TextView currentLocationTV;
    private ViewPager typePager;
    private ImageView truck, menu;
    private Imageutils imageutils;
    private double driverLat, driverLng;

    int[] truckDrawables = {
            R.drawable.burger_truck_marker,
            R.drawable.pizza_truck_marker,
            R.drawable.spec_truck_marker,
            R.drawable.taco_truck_marker,
            R.drawable.twinkie_truck_marker
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

        truckObj = new Truck();
        imageutils = new Imageutils(this);
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
        setupSubmitButton();

    }

    private void setupSubmitButton(){
        submitButton = (Button)findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verify that required fields are not null and push to db
                if(truckObj.getName()!=null && truckObj.getType()!=null && truckObj.getLat()!=0.0
                        && truckObj.getLong()!=0.0 && truckObj.getTruckImage()!=null && truckObj.getMenuImage()!=null) {
                    String insertTruck = "INSERT INTO food_truck(truck_status, truck_name, truck_type, " +
                            "truck_lat, truck_lng, truck_rating, truck_image, truck_menu) VALUES(" +
                            "'" + truckObj.getStatus() + "', '" + truckObj.getName() + "', '" + truckObj.getType() + "', " +
                            truckObj.getLat() + ", " + truckObj.getLong() + ", 1, '" +truckObj.getTruckImage().replace(" ", "")+
                             "', '" +truckObj.getMenuImage().replace(" ", "")+ "');";
                    Toast.makeText(getApplicationContext(), insertTruck, Toast.LENGTH_SHORT).show();
                    new QueryJSONArray().execute(insertTruck);
                    startDriverProfileActivity();
                    Toast.makeText(getApplicationContext(), "Truck pushed to db", Toast.LENGTH_SHORT).show();

                }
                //if required field is null, display message
                else Toast.makeText(getApplicationContext(), "Please eomplete all steps", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startDriverProfileActivity(){
        Intent intent = new Intent(DriverActivity.this, DriverProfile.class);
        intent.putExtra("truck_name", truckObj.getName());
        intent.putExtra("truck_image", truckObj.getTruckImage().replace(" ", ""));
        intent.putExtra("menu_image", truckObj.getMenuImage().replace(" ", ""));
        intent.putExtra("truck_lat", truckObj.getLat());
        intent.putExtra("truck_lng", truckObj.getLat());
        intent.putExtra("truck_status", truckObj.getStatus());
        startActivity(intent);
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

            //close the view, assign truck name to editText field and set FAB to green
            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nameEditText.getText().toString().length()>1) {
                        fabMenu1.closeMenu();
                        stepOneTV.setText(nameEditText.getText().toString());
                        truckObj.setName(nameEditText.getText().toString());
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


    //sets up location floating action button,
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
                    truckObj.setLat(driverLat);
                    truckObj.setLong(driverLng);
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

    //sets up the food type floating action button
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
                    truckObj.setType(truckTextViews[typePager.getCurrentItem()]);
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

    //Sets up the image floating action button
    private void setupCameraFab() {
        if (fabImage != null && fabMenu4 != null) {
            View customView = View.inflate(this, R.layout.custom_image_layout, null);
            fabMenuView = customView;
            fabMenu4.setCustomView(customView);
            fabMenu4.bindAncherView(fabImage);

            truck = (ImageView) fabMenuView.findViewById(R.id.truck);
            menu = (ImageView) fabMenuView.findViewById(R.id.menu);
            truck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageutils.imagepicker(REQUEST_TRUCK_IMAGE);
                }
            });

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageutils.imagepicker(REQUEST_MENU_IMAGE);
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

    /**Uses driver lat and lng to retrieve corresponding address*/
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
        imageutils.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        String prefix=""; String fileName="";
        String path =  Environment.getExternalStorageDirectory() + File.separator + prefix + File.separator;
        imageutils.createImage(file,filename,path,false);
        if(from==REQUEST_TRUCK_IMAGE) {
            truck.setImageBitmap(file);
            truckObj.setTruckImage("TRUCK_"+truckObj.getName());
            fileName = truckObj.getTruckImage();
        }
        else {
            menu.setImageBitmap(file);
            truckObj.setMenuImage("MENU_"+truckObj.getName());
            fileName = truckObj.getMenuImage();
        }
        new UploadFileAsync().execute(imageutils.getPath(uri), "0_TestTruck",fileName.replace(" ", ""));

        truckObj.setStatus("1");
    }

    /**Used to set up scrollable list of truck types*/
    private class TruckTypePagerAdapter extends PagerAdapter {

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
