package com.example.gotsaintwho;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TrackActivity extends AppCompatActivity
        implements
        OnMapReadyCallback, SensorEventListener {

    private static final String TAG = "TrackActivity";
    private static final long POLLING_FREQ_2SECONDS = 2;
    private static final long FASTEST_UPDATE_FREQ_2_SECONDS = 2;


    // Flag indicating whether a requested permission has been denied after returning in
    private boolean permissionDenied = false;
    private long startTime;
    private long pauseTime;

    private SupportMapFragment mapFragment;
    private TextView myLocationText;
    private TextView distance;
    private TextView step;
    private GoogleMap map;
    private Toolbar toolbar_track;
    private Button start;
    private Button stop;
    private FusedLocationProviderClient client;
    private Polyline mutablePolyline;
    private Location mLastLocation;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private float lastRotateDegree;

    private long lastUpdatetime;
    private boolean requestingLocationUpdates = true;
    private boolean isPause = true;
    private LocationCallback locationCallback = null;
    private LocationRequest locationRequest = null;
    private float roteteDegree;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
    private float mTotalDistance;
    private long mTotalTimes;
    private int mTotalStep;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;
    private long totalMilliSeconds = 0;


    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;
    private Marker mCurrentMarKer;
    private List<LatLng> points = new ArrayList<>();
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);


        client = LocationServices.getFusedLocationProviderClient(this);
        toolbar_track = findViewById(R.id.toolbar_track);

        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPause) {
                    startTime = System.currentTimeMillis();
                    start.setText("PAUSE");
                    isPause = false;
                    onResume();
                } else {
                    isPause = true;
                    onPause();
                    mTotalTimes = System.currentTimeMillis() - startTime;
                    start.setText("START");
                }

            }
        });


        myLocationText = findViewById(R.id.myLocation_Text);
        distance = findViewById(R.id.distance);
        step = findViewById(R.id.step);

        // initing sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // init accelerometer sensor and magnetic field sensor
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];

        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];


        if (sensorManager != null) {
            // init step sensor
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        //    map.setMyLocationEnabled(true);
        //    map.getUiSettings().setMyLocationButtonEnabled(true);
            getCurrentLocation();
        }
    }

    //get current location
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    // when success
                    if (location != null) {

                        //Sync map
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                map = googleMap;
                                // Initialize lat lng
                                Log.d("getCurrentLocation", points.size() + "");
                                mLastLocation = location;

                                placeMarkerOnMap(location);

                            }
                        });
                    } else {
                        myLocationText.setText("Get location failed.");
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(TrackActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    // location call back
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                placeMarkerOnMap(location);
                Log.e("mLocationCallback", points.size() + "");


            }
        }
    };



    // place maker on the map
    public void placeMarkerOnMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mCurrentMarKer != null) {
            mCurrentMarKer.remove();
            mCurrentMarKer = null;
        }

        if (mCurrentMarKer == null) {

            // displaying location details
            String locationDetail = getStreet(TrackActivity.this, latLng.latitude, latLng.longitude);//location.getLatitude(), location.getLongitude())
            myLocationText.setText("Location: " + locationDetail);

            // when start button is clicked, drawing the blue line for the track record.
            if (!isPause) {
                if (points.size() == 0 || points.size() > 0 && (latLng.latitude != points.get(points.size() - 1).latitude || latLng.longitude != points.get(points.size() - 1).longitude)) {
                    // calculate the total distance.
                    if(points.size() > 0)
                        mTotalDistance += (float) getDistance(points.get(points.size() -1),latLng);

                    points.add(latLng);
                    map.clear();

                    // draw a poly line
                    mutablePolyline = map.addPolyline(new PolylineOptions()
                            .color(Color.BLUE)
                            .width(30)
                            .geodesic(true)
                            .addAll(points));

                }
            }
            mCurrentMarKer = map.addMarker(new MarkerOptions().position(latLng).title("I'm here").icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)).anchor(0.5f, 0.5f));

            mCurrentMarKer.setPosition(latLng);

            updateCameraBearing(map, -lastRotateDegree, location);

        }
    }


    // get location details
    public String getStreet(Context context, double latitude, double longitude) {
        Address address = getAddress(context, latitude, longitude);
        return address == null ? "unknown" : address.getAddressLine(0);
    }

    public Address getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0)
                return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // when permission granted
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        }

        if (sensorManager != null) {
            sensorManager.registerListener(this,
                    sensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this,
                    sensorMagneticField,
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this,
                    stepSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (requestingLocationUpdates) {
            locationRequest = createLocationRequest();
            locationCallback = mLocationCallback;
            startLocationUpdates();
        }
    }

    // location update request
    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // interval 5s
        locationRequest.setFastestInterval(5000); //the fastest request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    // update camera
    private void updateCameraBearing(GoogleMap googleMap, float bearing, Location position) {
        if (googleMap == null) return;
        LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());

        CameraPosition camPos = CameraPosition
                .builder(
                        googleMap.getCameraPosition() // current Camera
                )
                .target(latLng)
                .zoom(17)
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), null);
    }


    // updating location
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);

    }


    /**
     * unregister the sensors of accelerometer and magneticfield
     */
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,
                sensorAccelerometer);
        sensorManager.unregisterListener(this,
                sensorMagneticField);
    }


    /**
     * getting step, rotate degree and timer.
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                // dispalying the total step
                step.setText("Total Step: " + sensorEvent.values[0]);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                valuesAccelerometer = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                valuesMagneticField = sensorEvent.values.clone();
                break;
        }

        boolean success = SensorManager.getRotationMatrix(
                matrixR,
                matrixI,
                valuesAccelerometer,
                valuesMagneticField);

        // retate degree from accelerometer and magneticField sensors
        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues);

            float rotateDegree = -(float) Math.toDegrees(matrixValues[0]);
            if (Math.abs(rotateDegree - lastRotateDegree) > 1) {
                RotateAnimation animation = new RotateAnimation
                        (lastRotateDegree, rotateDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.
                                RELATIVE_TO_SELF, 0.5f);
                animation.setFillAfter(true);
            }

            // setting timer
            if (!isPause) {
                totalMilliSeconds =
                        System.currentTimeMillis() + mTotalTimes - startTime;
            }

            long totalSeconds = totalMilliSeconds / 1000;

            long currentSecond = totalSeconds % 60;

            long totalMinutes = totalSeconds / 60;
            long currentMinute = totalMinutes % 60;

            long totalHour = totalMinutes / 60;
            long currentHour = totalHour % 24;

            // Setting distance and timer on the screen
            distance.setText("Distance: " + mTotalDistance + "\nTimer: " + currentHour + ":" + currentMinute + ":" + currentSecond);

            // setting retate degree for the camera
            lastRotateDegree = rotateDegree;

        }

    }

    // get distance between two GPS locations
    public double getDistance(LatLng start,LatLng end){
        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;

        //Radius of the Earth
        double R = 6371;

        // distance between two locations (KM)
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;

        // distance between two location (m)
        return d*1000;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}