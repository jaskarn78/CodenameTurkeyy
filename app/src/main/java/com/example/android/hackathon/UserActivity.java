package com.example.android.hackathon;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.hackathon.Utilities.GPSTracker;
import com.example.android.hackathon.Utilities.QueryTruckList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 *   Activity for the General User Functionality. This activity will display a map of the user's
 *  current general location and will display all nearby food trucks. Users will be able to select
 *  food trucks and expand on their information.
 */
public class UserActivity extends AppCompatActivity implements OnMapReadyCallback {
    // TODO Create an actual database for hosting this information

    private ArrayList<Truck> truckList;
    private MarkerOptions markerOptions = new MarkerOptions();

    private ImageButton truckButton;
    private TextView truckName;
    private ImageView truckImage;
    private SlidingUpPanelLayout slidingPanel;
    private LinearLayout dragView;
    private int clickedPosition=0;
    private ListView listView;



    /**
     *  Creates the UserActivity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Create truck button
        setupTruckButton();

        // Create drag and list views
        dragView = (LinearLayout)findViewById(R.id.dragInfo);
        listView = (ListView)findViewById(R.id.list);

        // Setup food truck list sliding panel
        setupSlidingPanels();

        // Setup ArrayList of food truck information
        setupTruckArrayList();

        // Setup MapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void setupTruckButton() {
        truckButton = (ImageButton)findViewById(R.id.truckfollow);
        truckButton.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        truckImage = (ImageView)findViewById(R.id.truck_image);
        truckName = (TextView)findViewById(R.id.truckname);
    }



    /** Creates the truck array list from the hardcoded string */
    // TODO check the way this works (cleaner and
    private void setupTruckArrayList() {
        try {
            ArrayList<JSONObject> pulledList = new QueryTruckList().execute().get();
            truckList = new ArrayList<Truck>();
            for (int i=0; i<pulledList.size(); i++) {
                JSONObject json_data = pulledList.get(i);
                Truck temp = new Truck();
                temp.setName(json_data.getString("truck_name"));
                temp.setType(json_data.getString("truck_type"));
                temp.setMenu(json_data.getString("truck_menu"));
                temp.setStatus(json_data.getInt("truck_status"));
                temp.setTruckImage(json_data.getString("truck_image"));
                temp.setLat(json_data.getDouble("truck_lat"));
                temp.setLong(json_data.getDouble("truck_lng"));
                temp.setIcon(temp.getType());
                truckList.add(i, temp);
            }


            // Create adapter for the truck list
            TruckAdapter adapter = new TruckAdapter(this, truckList);
            listView.setAdapter(adapter);

            // Create on click listener for the list of trucks
            truckButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileActivity();
                }
            });

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // TODO Add method description comment (Does this do anything? Is this the Truck list?)
    private void setupSlidingPanels() {
        // Set SlidingPanel to the food truck list panel and add OnClickListener
        slidingPanel = (SlidingUpPanelLayout)findViewById(R.id.food_truck_sliding_layout);
        slidingPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO method description (what do?)
        slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }
            /**
             *  Called when the panel has changed state.
             * @param panel
             * @param previousState
             * @param newState
             */
            @Override
            public void onPanelStateChanged(View panel,
                                            SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                if(newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED) ||
                        newState.equals(SlidingUpPanelLayout.PanelState.DRAGGING)){
                    dragView.setVisibility(View.GONE);
                }else{
                    dragView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     *  Initializes the google map, setting food truck icons where there are active food trucks.
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // TODO add inline comments to this method
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //users current location, can call currentLocation.getLatitude() and
        //currentLocation.getLongitude();
        GPSTracker currentLocation = new GPSTracker(getApplicationContext());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(38.579431, -121.479059)).zoom(18)
                .tilt(67.5f).bearing(314).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        for(int i=0; i<truckList.size(); i++){
            markerOptions.position(new LatLng(truckList.get(i).getLat(), truckList.get(i).getLong()));
            markerOptions.title(truckList.get(i).getName());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(truckList.get(i).getIcon()));
            googleMap.addMarker(markerOptions);
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = Integer.parseInt(marker.getId().replace("m", ""));
                clickedPosition=position;
                truckName.setText(marker.getTitle());
                Glide.with(UserActivity.this).load(truckList.get(position).getTruckImage()).into(truckImage);
                truckButton.setVisibility(View.VISIBLE);

                return false;
            }
        });

    }

    /**
     * Adapter for the truck list.
     */
    class TruckAdapter extends ArrayAdapter<Truck> {
        public TruckAdapter(Context context, ArrayList<Truck> trucks) {
            super(context, 0, trucks);
        }

        /**
         *  Creates the view for truck list.
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Truck truck = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.trucklist, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.truck_type_list);
            TextView tvHome = (TextView) convertView.findViewById(R.id.truck_name_list);
            ImageView tvImage = (ImageView) convertView.findViewById(R.id.truck_image_list);
            // Populate the data into the template view using the data object
            tvName.setText(truck.getName());
            tvHome.setText(truck.getType());
            Glide.with(getApplicationContext()).load(truck.getTruckImage()).into(tvImage);

            //  Create an OnClickListener for the selected truck and push food truck information
            // to intent, which will be used to display the food truck page.
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileActivity();
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }
    }

    private void profileActivity() {
        Intent intent = new Intent(UserActivity.this, ProfileActivity.class);
        intent.putExtra("truckName", truckList.get(clickedPosition).getName());
        intent.putExtra("truckImage", truckList.get(clickedPosition).getTruckImage());
        intent.putExtra("menuImage", truckList.get(clickedPosition).getMenu());
        intent.putExtra("type", truckList.get(clickedPosition).getType());
        intent.putExtra("position", clickedPosition);
        intent.putExtra("lat", truckList.get(clickedPosition).getLat());
        intent.putExtra("lng", truckList.get(clickedPosition).getLong());
        startActivity(intent);
    }

}
