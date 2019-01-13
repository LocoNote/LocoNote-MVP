package games.android.com.loconotemvp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import games.android.com.loconotemvp.Constants;

import static android.os.Build.VERSION_CODES.M;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity",
            FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION,
            COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION,
            NO_VALUE_BUNDLE_STRING = "STRING NOT FOUND IN BUNDLE";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234,
            NO_VALUE_BUNDLE_INT = -1;
    private static final float DEFAULT_ZOOM = 17f;
    private static final double NO_VALUE_BUNDLE_NUMBER = -1f;
    private static final boolean NO_VALUE_BUNDLE_BOOLEAN = false;

    private Boolean locationPermissionsGranted = false;
    private float currZoomValue;
    private static LatLng currPosLatLng;
    private CameraPosition cameraPosition;
    private TextView zoomText;
    private GoogleMap mMap;
    private games.android.com.loconotemvp.LatLng currLoc;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference noteBase;

    private GeofencingClient mGeofencingClient;
    private Constants consta;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        consta = new Constants();
        Button createNote = (Button) findViewById(R.id.createButton);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();
        currZoomValue = DEFAULT_ZOOM;
        getDeviceLocation();
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        noteBase = FirebaseDatabase.getInstance().getReference("Notes");

        noteBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot noteSnapShot: dataSnapshot.getChildren()){

                    Message msg = noteSnapShot.getValue(Message.class);
                    LatLng msgLoc = new LatLng(msg.getLatLng().getLatitude(),msg.getLatLng().getLongitude());
                    LatLng curLoc = new LatLng(getCurrPosLatLng().latitude,getCurrPosLatLng().longitude);
                    if(isNearby(msgLoc,curLoc)){
                        AddMarker(msg);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(currPosLatLng != null) {
            cameraPosition = CameraPosition.builder().target(currPosLatLng).zoom(DEFAULT_ZOOM).tilt(45).bearing(0f).build();
        }


        if (savedInstanceState != null) {
            Toast.makeText(this, "saved instance state", Toast.LENGTH_SHORT).show();
            zoomText.setText(savedInstanceState.getString("Zoom"));
            float zoom = savedInstanceState.getFloat("zoom");
            //Toast.makeText(this, new Float(zoom).toString(), Toast.LENGTH_SHORT).show();
        }
        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double lat = getCurrPosLatLng().latitude;
                double lng = getCurrPosLatLng().longitude;
                Intent intent = new Intent(MapsActivity.this, CreateNoteActivity.class);
                Bundle extras = new Bundle();
                extras.putDouble("lat",lat);
                extras.putDouble("lng",lng);
                intent.putExtras(extras);

                startActivity(intent);
                finish();
            }
        });
    }


    public LatLng getCurrPosLatLng() {
        return currPosLatLng;
    }

    public void AddMarker(Message message){
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(message.getLatLng().getLatitude(),message.getLatLng().getLongitude()))
                .title(message.getMesssage()));

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // sets map to listen for marker clicks
            // Google Map options
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
        }
    }
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(locationPermissionsGranted) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            // current location marker
                            Location currentLocation = (Location)task.getResult();
                            currPosLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            cameraPosition = CameraPosition.builder().target(currPosLatLng).zoom(21).tilt(45).bearing(0f).build();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 21);


                            // add job marker
                            /*
                            if (getBundleStringInfo("add marker").equals("add marker")) {
                                LatLng latLng = new LatLng(addJobHandler.getLocation().getLatitude(), addJobHandler.getLocation().getLongitude());
                                addMarker(latLng, addJobTitle, addJobHandler.getTag());
                                moveCamera(latLng, DEFAULT_ZOOM - 0f);
                            }
                            */

                        } else {
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: security exception " + e.getMessage());
        }
    }
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private boolean isNearby(LatLng marker, LatLng currLoc){
        double x1 = marker.latitude;
        double y1 = marker.longitude;
        double x2 = currLoc.latitude;
        double y2 = currLoc.longitude;
        double theta = y1 - y2;
        double dist = Math.sin(Math.toRadians(x1)) * Math.sin(Math.toRadians(x2)) + Math.cos(Math.toRadians(x1)) * Math.cos(Math.toRadians(x2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.3444;
        return (dist<10);
    }

}
