package com.example.gotsaintwho;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//This GoogleMapActivity is produced according to
//background research on how to use google maps APIs
//explained in the following video with a link down below
//Video link: https://www.youtube.com/watch?v=MWowf5SkiOE

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static Location currentLocation;
    private EditText inputSearch;
    private ImageView iconGPS;

    private Boolean locationGranted = false;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermission();
    }

    //获取使用地理位置的许可
    private void getPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationGranted = true;
            setContentView(R.layout.activity_google_map);
            inputSearch = (EditText) findViewById(R.id.input_search);
            iconGPS = (ImageView) findViewById(R.id.ic_gps);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(GoogleMapActivity.this);

        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    locationGranted = false;
                    finish();
                    Toast.makeText(this, "Denied Permission", Toast.LENGTH_LONG).show();
                }else{
                    locationGranted = true;
                    getPermission();
                }
            }
        }
    }

    //准备地图
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        this.googleMap = googleMap;

        if (locationGranted) {

            getCurrentLocation();

            try {
                this.googleMap.setMyLocationEnabled(true);
                this.googleMap.getUiSettings().setZoomControlsEnabled(true);
                this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                this.googleMap.getUiSettings().setMapToolbarEnabled(true);
            }catch(SecurityException e){
                Toast.makeText(this, "Denied Permission", Toast.LENGTH_LONG).show();
            }
            listening();
        }
    }

    //搜索栏输入地址
    private void listening(){
        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                googleMap.clear();
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    getPlaceLocation();
                }
                return false;
            }
        });

        iconGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

    }

    //搜索所填地址并定位
    private void getPlaceLocation(){
        String searchString = "australia" + inputSearch.getText().toString();
        Geocoder geocoder = new Geocoder(GoogleMapActivity.this);
        List<Address> addressList = new ArrayList<>();
        try{
            addressList = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
        }

        if(addressList.size() > 0){
            Address address = addressList.get(0);

            Toast.makeText(this, "Latitude:" + address.getLatitude() + "\nLongitude:" + address.getLongitude(), Toast.LENGTH_LONG).show();

            if(!address.getAddressLine(0).equals("My Location")){
                MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                        .title(address.getAddressLine(0));
                googleMap.addMarker(options);
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            builder.include(new LatLng(address.getLatitude(), address.getLongitude()));

            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.40);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            googleMap.animateCamera(cameraUpdate);

            hideSoftKeyboard(this);

        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(GoogleMapActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("No such location found on the map");
            alertDialog.show();
        }
        hideSoftKeyboard(this);
    }

    //获取当前位置
    private void getCurrentLocation(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(locationGranted){

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() & (Location) task.getResult()!= null){
                            currentLocation = (Location) task.getResult();
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),15f));
                        }else{
                            Toast.makeText(GoogleMapActivity.this, "No access to current location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
        }
    }

    //隐藏软键盘
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}