package com.harshs.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    public void zoomOnUsersLocation(Location location, String title){
        if(location != null ) {
            LatLng usersLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(usersLocation).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLocation, 12));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.clear();
                zoomOnUsersLocation(lastKnownLocation,"Your Location....");
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
       if( intent.getIntExtra("placeNumber", 0) == 0){
           //Zoom in onto user's location.
           locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
           locationListener = new LocationListener() {
               @Override
               public void onLocationChanged(Location location) {
                   mMap.clear();
                   zoomOnUsersLocation(location,"Your Location....");
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
           };

           if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

           }else{
               locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
               Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               zoomOnUsersLocation(lastKnownLocation,"Your Location....");

           }
       }else{
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(MainActivity.locationList.get(intent.getIntExtra("placeNumber",0)).latitude);
           newLocation.setLongitude(MainActivity.locationList.get(intent.getIntExtra("placeNumber",0)).longitude);

           zoomOnUsersLocation(newLocation,MainActivity.arrayList.get(intent.getIntExtra("placeNumber",0)));
       }
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address="";
        try{
            List<Address> listAddress = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(listAddress != null && listAddress.size() > 0){
             if(listAddress.get(0).getThoroughfare() != null){
                 if(listAddress.get(0).getSubThoroughfare() != null){
                     address += listAddress.get(0).getSubThoroughfare()+" ";
                 }
                 address += listAddress.get(0).getThoroughfare()+" ";
             }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(address.equals("")){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            address+= simpleDateFormat.format(new Date());
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        MainActivity.arrayList.add(address);
        MainActivity.locationList.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.harshs.memorableplaces",Context.MODE_PRIVATE);

        try{
            ArrayList<String> latitudes = new ArrayList<String>();
            ArrayList<String> longitudes = new ArrayList<String>();

            for (LatLng coordinates : MainActivity.locationList){

                latitudes.add(Double.toString(coordinates.latitude));
                longitudes.add(Double.toString(coordinates.longitude));
            }

            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.arrayList)).apply();
            sharedPreferences.edit().putString("lat",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("lon",ObjectSerializer.serialize(longitudes)).apply();

        }catch (Exception e){
            e.printStackTrace();
        }

        Toast.makeText(MapsActivity.this,"Your new Memorable Place is added to the list",Toast.LENGTH_SHORT).show();
    }
}
