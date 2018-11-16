package com.example.roman.locationpath;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import database.DataBaseManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private LocationNode mLocation;
    private DataBaseManager db;
    private final String strDateFormat = "hh:mm:ss a";
    private static Date date;
    private static DateFormat dateFormat;
    private static final int DEFAULT_ZOOM = 16;
    private static String title;
    private static boolean flag = false;
    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dateFormat = new SimpleDateFormat(strDateFormat);
        date = new Date();
        db = new DataBaseManager(this);
        mLocation = (LocationNode) getIntent().getSerializableExtra("location");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "message: onMapReady called by "+Thread.currentThread().getName());
        mMap = googleMap;
        geocoder = new Geocoder(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        setActionListeners();
        getLocation();
        displayPins();
    }

    @Override
    public void onStop(){
        super.onStop();
        finish();
    }

    // HELPER METHOD, TO GET THE CURRENT/LAST LOCATION
    private void getLocation() {
        Log.d(TAG, "message: getLocation called by "+Thread.currentThread().getName());
        try {
            if (mLocation!=null && mLocation.permissionGranted()){
                LatLng myLocation = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myLocation).title(mLocation.getAddress())).setDraggable(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,DEFAULT_ZOOM));
            }
        }catch (SecurityException e){
            Log.d(TAG,"message: getDeviceLocation SecurityException: "+e.getMessage());
        }
    }

    private void displayPins(){
        Log.d(TAG, "message: displayPins called by "+Thread.currentThread().getName());
        List<String> locations = db.getLocations();
        for (String loc: locations){
            //Log.d(TAG, "message: location: "+loc);
            String[] location = loc.split(",");
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(location[2]),Double.parseDouble(location[3])))
                    .title(location[0]+" "+location[1]))
                    .setDraggable(true);
        }
    }

    private void setActionListeners(){
        Log.d(TAG, "message: setActionListeners called by "+Thread.currentThread().getName());
        // CHANGE CURRENT TITLE ON MARKER CLICK
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Log.d(TAG, "message: onMarkerClick marker title "+marker.getTitle());
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapsActivity.this);
                final View view = getLayoutInflater().inflate(R.layout.custom_change_name,null);
                final EditText text = (EditText) view.findViewById(R.id.newname);
                final TextView name = (TextView) view.findViewById(R.id.current_name);
                final ListView listView = (ListView) view.findViewById(R.id.nearlocations);
                final String[] str = marker.getTitle().split(" ");
                ArrayAdapter<String> adapter =  new ArrayAdapter<>(MapsActivity.this,
                        android.R.layout.simple_list_item_1,
                        db.getNearLocations(str[0]));
                listView.setAdapter(adapter);
                name.setText(marker.getTitle().toString());
                alertBuilder.setView(view)
                        .setTitle("LOCATIONS")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "message: setMarkerName.alertBuilder.onClick called title "+title);
                                if (!text.getText().toString().isEmpty()){
                                    title = text.getText().toString();
                                    marker.setTitle(title);
                                    // query change title
                                    // displayPins();
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                alertBuilder.setView(view);
                AlertDialog addAlbumDialog = alertBuilder.create();
                addAlbumDialog.show();
                return false;
            }
        });

        // ADD A NEW LOCATION ON MAP CLICK
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                Log.d(TAG, "message: setActionListeners.setOnMapClickListener.onMapClick called title "+title);
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String zipcode = address.get(1).getPostalCode();
                final String addrss = address.get(1).getAddressLine(0);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.custom_name,null);
                final EditText text = (EditText) view.findViewById(R.id.customname);
                alertBuilder.setView(view)
                        .setTitle("LOCATIONS")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "message: setMarkerName.alertBuilder.onClick called title "+title);
                                if (!text.getText().toString().isEmpty()){
                                    title = text.getText().toString();
                                    Log.d(TAG, "message: setAddress called by " + Thread.currentThread().getName());
                                    Log.d(TAG, "message: zip code " + zipcode);
                                    checkDistance(zipcode,latLng.latitude,latLng.longitude,title);
                                    displayPins();
                                    Log.d(TAG, "message: setMarkerName.alertBuilder.onClick called title "+title);
                                }else {
                                    checkDistance(zipcode,latLng.latitude,latLng.longitude,addrss);
                                    displayPins();
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                alertBuilder.setView(view);
                AlertDialog addAlbumDialog = alertBuilder.create();
                addAlbumDialog.show();
            }
        });
    }

    private void checkDistance(String zip, double lat, double lon, String title){
        Log.d(TAG, "message: checkDistance by zip "+zip);
        String key = "";
        String address = "";
        String formattedDate = dateFormat.format(date);
        List<String> locations = db.getLocationsByZip(zip);
        float[] distance = new float[2];
        for (String loc:locations){
            String[] coor = loc.split(",");
            Location.distanceBetween(lat,lon,Double.parseDouble(coor[1]),Double.parseDouble(coor[2]),distance);
            Log.d(TAG, "message: distance = "+distance[0]);
            if (distance[0] <= 30){
                key = coor[0];
                address = coor[3];
                flag = true;
                break;
            }
        }
        if (flag){
            Toast.makeText(this, title+" is within 30m from "+address, Toast.LENGTH_LONG).show();
            db.addNearLocation(key,""+lat,""+lon, title,formattedDate);
        }else {
            db.addLocation(""+lat,""+lon, title, zip,formattedDate);
            flag = false;
        }
    }
}
