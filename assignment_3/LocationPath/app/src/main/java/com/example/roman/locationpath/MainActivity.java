package com.example.roman.locationpath;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import database.DataBaseManager;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Geocoder geocoder;
    private static TextView mLatitude_por, mLongitude_por, mLocation_por;
    private static ListView mLocationlist;
    private Button mCheckin, mMapview;
    private static DataBaseManager db;
    private final String strDateFormat = "hh:mm:ss a";
    private static Date date;
    private static DateFormat dateFormat;
    private LocationNode mLocationNode;
    private static ArrayAdapter<String> adapter;
    private static String zipCode;
    private static LocationNode START_POINT;
    private static Thread pthread;
    private static boolean mLocationPermissionGranted = false;
    private static boolean flag = false;
    private static boolean flag2 = false;
    private static boolean isThereLocation = false;
    private AlertDialog.Builder nearAlertBuilder;
    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_CODE = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBaseManager(this);
        mLatitude_por = findViewById(R.id.latitude_por);
        mLongitude_por = findViewById(R.id.longitude_por);
        mLocation_por = findViewById(R.id.location_por);
        mCheckin = findViewById(R.id.checkin);
        mMapview = findViewById(R.id.mapview);
        mLocationlist = findViewById(R.id.locationlist);
        nearAlertBuilder = new AlertDialog.Builder(MainActivity.this);
        mLocationNode = new LocationNode();
        dateFormat = new SimpleDateFormat(strDateFormat);
        date = new Date();
        geocoder = new Geocoder(this);
        if (isThereService()) {
            pthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // CALLS METHOD ON A SEPARATE THREAD! NO NEED TO WAIT FOR IT.
                    getLocationPermission();
                }
            });
            pthread.start();
        }
        //defaultCheckIn();
        setLocationManager();
        setAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "message: onResume called by " + Thread.currentThread().getName());
        // ONCE THE OTHER THREAD GETS PERMISSION SETMAP METHOD WILL BE INVOKED.
        if (mLocationPermissionGranted) {
            Log.d(TAG, "message: mLocationPermissionGranted " + Thread.currentThread().getName());
            getLocation();
        }
    }

    // REQUEST USER PERMISSION FOR LOCATION ACCESS BY ENABLING FINE AND COARSE LOCATION ACCESS
    private void getLocationPermission() {
        Log.d(TAG, "message: getLocationPermission called by " + Thread.currentThread().getName());
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d(TAG, "message: mLocationPermissionGranted " + Thread.currentThread().getName());
        } else {
            // PERMISSION IS NOT YET GRANTED. MAKE A REQUEST (calls onRequestPermissionsResult(...) method)
            ActivityCompat.requestPermissions(this, permissions, LOCATION_CODE);
        }
    }

    // IF PERMISSION IS NOT YET GRANTED THIS METHOD WILL BE INVOKED AND PROMPTS USER TO GRANT PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "message: onRequestPermissionsResult called by " + Thread.currentThread().getName());
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.d(TAG, "message: mLocationPermissionGranted " + Thread.currentThread().getName());
                }
            }
        }
    }

    // HELPER METHOD, TO GET THE CURRENT/LAST LOCATION
    private void getLocation() {
        Log.d(TAG, "message: getLocation called by " + Thread.currentThread().getName());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient
                        .getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(final Location location) {
                                if (location != null) {
                                    setAddress(location);
                                    isThereLocation = true;
                                    setButtonListener();
                                    Log.d(TAG, "message: there is location ");
                                    // RUN AUTO CHECK IN ON ANOTHER THREAD AND CHECK IN AFTER 5 MINUTES
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            automaticCheckIn(location);
                                        }
                                    }).start();
                                    setAdapter();
                                } else {
                                    Log.d(TAG, "message: NO KNOWN LOCATION :( " + Thread.currentThread().getName());
                                    setLocationManager();
                                }
                            }
                        });

            }
        } catch (SecurityException e) {
            Log.d(TAG, "message: getDeviceLocation SecurityException: " + e.getMessage());
        }
    }

    private void setLocationManager(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    setAddress(location);
                    isThereLocation = true;
                    Log.d(TAG, "message: onLocationChanged.NETWORK_PROVIDER isThereLocation " + isThereLocation);
                    setButtonListener();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    setAddress(location);
                    isThereLocation = true;
                    Log.d(TAG, "message: onLocationChanged.GPS_PROVIDER isThereLocation " + isThereLocation);
                    setButtonListener();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }

    private void setAddress(Location location) {
        Log.d(TAG, "message: setAddress called by " + Thread.currentThread().getName());
        List<Address> address = null;
        try {
            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        zipCode = address.get(1).getPostalCode();
        mLatitude_por.setText("" + location.getLatitude());
        mLongitude_por.setText("" + location.getLongitude());
        mLocation_por.setText(address.get(1).getAddressLine(0));
        START_POINT = new LocationNode(address.get(1).getAddressLine(0),
                location.getLatitude(),
                location.getLongitude(),
                zipCode);
        //nearLocation(zipCode,location.getLatitude(),location.getLongitude(),address.get(1).getAddressLine(0));
    }

    private void setButtonListener() {
        Log.d(TAG, "message: setButtonListener called by " + Thread.currentThread().getName());
        Log.d(TAG, "message: isThereLocation " + isThereLocation);
        // IF THERE IS A LOCATION DISPLAYING ALLOW USER TO CHECK IN AND SAVE TO DATABASE
        if (isThereLocation) {
            mCheckin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.custom_name, null);
                    final EditText mCustomname = (EditText) view.findViewById(R.id.customname);
                    final String formattedDate = dateFormat.format(date);
                    alertBuilder.setView(view)
                            .setTitle("LOCATIONS")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!mCustomname.getText().toString().isEmpty()) {
                                        checkDistance(zipCode,
                                                Double.parseDouble(mLatitude_por.getText().toString()),
                                                Double.parseDouble(mLongitude_por.getText().toString()),
                                                mCustomname.getText().toString());
                                        mLocationNode.setAddress(mCustomname.getText().toString());
                                        setAdapter();
                                    } else {
                                        checkDistance(zipCode,
                                                Double.parseDouble(mLatitude_por.getText().toString()),
                                                Double.parseDouble(mLongitude_por.getText().toString()),
                                                "no name");
                                        mLocationNode.setAddress(mLocation_por.getText().toString());
                                        setAdapter();
                                    }
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    alertBuilder.setView(view);
                    AlertDialog addAlbumDialog = alertBuilder.create();
                    addAlbumDialog.show();
                }
            });
        }

        mMapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                if (!mLatitude_por.getText().toString().isEmpty() && !mLongitude_por.getText().toString().isEmpty()) {
                    mLocationNode.setLatitude(Double.parseDouble(mLatitude_por.getText().toString()));
                    mLocationNode.setLongitude(Double.parseDouble(mLongitude_por.getText().toString()));
                    mLocationNode.setZip(zipCode);
                    intent.putExtra("location", mLocationNode);
                }
                intent.putExtra("location", START_POINT);
                startActivity(intent);
            }
        });
    }

    private void setAdapter() {
        adapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                db.getLocations());
        mLocationlist.setAdapter(adapter);
        mLocationlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                final View v = getLayoutInflater().inflate(R.layout.custom_change_name,null);
                final EditText text = (EditText) v.findViewById(R.id.newname);
                final TextView name = (TextView) v.findViewById(R.id.current_name);
                final ListView listView = (ListView) v.findViewById(R.id.nearlocations);
                final String[] str = parent.getItemAtPosition(position).toString().split(",");
                System.out.print("ITEM: "+parent.getItemAtPosition(position).toString());
                ArrayAdapter<String> adapter =  new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        db.getNearLocations(str[0]));
                listView.setAdapter(adapter);
                name.setText(str[1]);
                alert.setView(v)
                        .setTitle("LOCATIONS")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!text.getText().toString().isEmpty()){
                                    // query change title
                                    // displayPins();
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                alert.setView(v);
                AlertDialog window = alert.create();
                window.show();
            }
        });
    }


    // TO CHECK IF THERE IS ANY SERVICE CONNECTION TO THE GOOGLE API
    private boolean isThereService() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                    MainActivity.this,
                    available,
                    ERROR_DIALOG_REQUEST
            );
            dialog.show();
        } else {
            Toast.makeText(this, "message: No service connection", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void automaticCheckIn(Location location) {
        Log.d(TAG, "message: automaticCheckIn called by "+Thread.currentThread().getName());
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String formattedDate = dateFormat.format(date);
        db.addLocation(location.getLongitude()+"",
                location.getLongitude()+"",
                mLocation_por.getText().toString(),
                formattedDate,zipCode);
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
            }else if(distance[0]==0){
                flag = false;
                return;
            }
        }
        if (flag){
            db.addNearLocation(key,""+lat,""+lon, title,formattedDate);
            Toast.makeText(this, title+" is within 30m from "+address, Toast.LENGTH_LONG).show();
        }else {
            db.addLocation(""+lat,""+lon, title, zip,formattedDate);
            Toast.makeText(this, title+" added to db", Toast.LENGTH_LONG).show();
            flag = false;
        }
    }

    private void defaultCheckIn() { //74.4592
        String formattedDate = dateFormat.format(date);
        db.addLocation(40.5233 + "", -74.4588 + "", "Busch STC", formattedDate,"08854");
        db.addNearLocation(""+1,40.52331 + "", -74.45881 + "", "Pandera", formattedDate);
        db.addNearLocation(""+1,40.52332 + "", -74.45882 + "", "Moes", "12:00");
        db.addNearLocation(""+1,40.52333 + "", -74.45883 + "", "Chinese food", formattedDate);

        db.addLocation(40.5218 + "", -74.4608 + "", "ECE Building", formattedDate,"08854");
        db.addNearLocation(""+2,40.52181 + "", -74.46081 + "", "B100", formattedDate);
        db.addNearLocation(""+2,40.52182 + "", -74.46082 + "", "VR lab", formattedDate);

        db.addLocation(40.5213 + "", -74.4611 + "", "Core Building", formattedDate,"08854");
        db.addNearLocation(""+3,40.52131 + "", -74.456111 + "", "Near1", formattedDate);
        db.addNearLocation(""+3,40.52132 + "", -74.456112 + "", "Near2", formattedDate);

        db.addLocation(40.5227 + "", -74.4627 + "", "SERC Building", formattedDate,"08232");
        db.addNearLocation(""+4,40.52271 + "", -74.456271 + "", "Near1", formattedDate);
        db.addNearLocation(""+4,40.52272 + "", -74.456272 + "", "Near2", formattedDate);

        db.addLocation(40.5197 + "", -74.4610 + "", "Gym", formattedDate,"08232");
        db.addNearLocation(""+5,40.51971 + "", -74.46101 + "...", "Near1", formattedDate);
        db.addNearLocation(""+5,40.51972 + "", -74.46102 + "...", "Near2", formattedDate);
    }

    private void nearLocation(String zip, double lat, double lon, final String title){
        Log.d(TAG, "message: checkDistance by zip "+zip);
        List<String> locations = db.getLocationsByZip(zip);
        String address = "";
        float[] distance = new float[2];
        for (String loc:locations){
            String[] coor = loc.split(",");
            Location.distanceBetween(lat,lon,Double.parseDouble(coor[1]),Double.parseDouble(coor[2]),distance);
            Log.d(TAG, "message: distance = "+distance[0]);
            if (distance[0] <= 30){
                flag2 = true;
                address = coor[3];
                break;
            }
        }

        View view = getLayoutInflater().inflate(R.layout.near_location_message, null);
        TextView mCloseLocation = (TextView) view.findViewById(R.id.closeLocation);
        mCloseLocation.setText(title+" near "+address);
        nearAlertBuilder.setView(view)
                .setTitle("Near location")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
        nearAlertBuilder.setView(view);
        AlertDialog addAlbumDialog = nearAlertBuilder.create();
        if(flag2){
            addAlbumDialog.show();
            flag2 = false;
        }else {
            addAlbumDialog.dismiss();
        }
    }
}
