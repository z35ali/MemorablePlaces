package com.zafar.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static com.zafar.memorableplaces.MainActivity.sharedPreferences;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    BitmapDescriptor bitmapDescriptor;


    // Zoom camera on certain location on Google Map
    public void zoomOnLocation(Location location, String title) {


        if (location != null) {


            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if (title.equals("Your Location")) {
                mMap.addMarker(new MarkerOptions().position(userLocation).title(title).icon(bitmapDescriptor));
            }else{
                mMap.addMarker(new MarkerOptions().position(userLocation).title(title));

            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                zoomOnLocation(lastKnownLocation, "Your Location");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        if (intent.getIntExtra("placeID", 0) == 0) {
            // Zoom in on user location
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    zoomOnLocation(location, "Your Location");
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                zoomOnLocation(lastKnownLocation, "Your Location");
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            double latitude = intent.getDoubleExtra("lat",0.0);
            double longitude = intent.getDoubleExtra("long",0.0);
            String address = intent.getStringExtra("address");




           placeLocation.setLatitude(latitude);
           placeLocation.setLongitude(longitude);




            zoomOnLocation(placeLocation, address);
        }
    }



    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";
        String country = "";

        try {

            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);


            if (listAddresses != null && listAddresses.size() > 0) {
                if (listAddresses.get(0).getThoroughfare() != null) {
                    if (listAddresses.get(0).getSubThoroughfare() != null) {
                        address += listAddresses.get(0).getSubThoroughfare() + " ";
                    }
                    address += listAddresses.get(0).getThoroughfare();
                    country += listAddresses.get(0).getCountryName();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // If no address found then address is replaced with date
        if (address.equals("") || address.equals("Unnamed Road")) {
            Toast.makeText(this,"No Address Exists At This Location!",Toast.LENGTH_SHORT).show();
            address += "NO ADDRESS FOUND";

        }

            mMap.addMarker(new MarkerOptions().position(latLng).title(address));
            final Location temp = new Location(LocationManager.GPS_PROVIDER);
            temp.setLatitude(latLng.latitude);
            temp.setLongitude(latLng.longitude);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    zoomOnLocation(temp,"location");                }
            }, 3000);



            MainActivity.places.add("Address: "+address+ " \nLat: "+latLng.latitude+" \nLong: "+latLng.longitude + "\nCountry: "+country);


            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.zafar.memorableplaces", Context.MODE_PRIVATE);
            HashSet<String> set = new HashSet<>(MainActivity.places);
            sharedPreferences.edit().putStringSet("placesList", set).apply();



            MainActivity.arrayAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Place Added! Zooming In 3 Seconds!", Toast.LENGTH_SHORT).show();


    }

}
