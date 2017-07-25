package com.example.yps.assignment_5;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat,lng;
    String TAG = "mtag";
    String nickname;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_activity_map);
        nickname = getIntent().getExtras().getString("nick");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void onMapSetLatLongClick(View view){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);
        intent.putExtra("nick",nickname);

        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                lat = point.latitude;
                lng = point.longitude;

                LatLng sydney = new LatLng(lat, lng);
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Your Hometown"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                Log.e(TAG,lat+"//"+lng);
            }
        });
    }
}
