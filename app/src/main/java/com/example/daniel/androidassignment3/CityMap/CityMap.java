package com.example.daniel.androidassignment3.CityMap;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.test.espresso.core.deps.guava.base.Charsets;
import android.support.test.espresso.core.deps.guava.io.Files;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.daniel.androidassignment3.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class CityMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    File directory;
    final String cities = "Växjö,Malmö,Hässleholm,Göteborg,Kalmar";
    List<MarkerOptions> markerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_map);
        markerList = new ArrayList<>();
        directory = this.getExternalFilesDir(null);
        createFile();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;
        String[] cities;
        final Toast distanceMessage = Toast.makeText(CityMap.this, "", Toast.LENGTH_SHORT);
        final Toast selectedToast = Toast.makeText(CityMap.this, "", Toast.LENGTH_SHORT);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition currPos = mMap.getCameraPosition();
                MarkerOptions closest = getClosestCity(markerList, currPos.target);
                float distance = distanceTo(currPos.target, closest.getPosition()) /1000;

                DecimalFormat form = new DecimalFormat("#.#");
                String toastString = closest.getTitle() + ": " + form.format(distance) + "km";
                distanceMessage.setText(toastString);
                distanceMessage.show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedToast.setText(marker.getTitle());
                selectedToast.show();
                return true;
            }
        });

        cities = readCities();
        for (String c : cities) {
            Geocoder gc = new Geocoder(this);
            List<Address> addresses;
            try {
                addresses = gc.getFromLocationName(c, 1);
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        MarkerOptions mo = new MarkerOptions()
                                .position(new LatLng(a.getLatitude(), a.getLongitude()))
                                .title(c);
                        mMap.addMarker(mo);
                        markerList.add(mo);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions mo : markerList) {
            builder.include(mo.getPosition());
        }
        LatLngBounds bounds = builder.build();
        //LatLngBounds cameraBounds = getCameraPosition(markerList);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 500, 500, 5);
        mMap.moveCamera(cu);

    }

    private String[] readCities() {
        File file = new File(directory, "cities");
        try {
            String cityList = Files.toString(file, Charsets.UTF_8);
            return cityList.split(",");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createFile() {
        File file = new File(directory, "cities");
        try {
            Files.write(cities, file, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LatLngBounds getCameraPosition(List<MarkerOptions> list) {
        LatLngBounds.Builder cb = new LatLngBounds.Builder();
        for (MarkerOptions mo : list) {
            cb.include(mo.getPosition());
        }
        return cb.build();
    }

    private MarkerOptions getClosestCity(List<MarkerOptions> list, LatLng center) {
        MarkerOptions closest = null;
        float cls = Float.MAX_VALUE;
        for (MarkerOptions mo : list) {
            float dist = distanceTo(mo.getPosition(), center);
            if (dist < cls) {
                closest = mo;
                cls = dist;
            }
        }
        return closest;
    }

    private float distanceTo(LatLng locOne, LatLng locTwo) {
        float[] res = new float[1];
        Location.distanceBetween(locOne.latitude, locOne.longitude, locTwo.latitude, locTwo.longitude, res);
        return res[0];
    }

}
