package com.example.android.searchvehicle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    public static String PostalCode;
    public static String Locality;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "postal";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.car_homepic)      // Set logo drawable
                        .build(),
                RC_SIGN_IN);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Toast.makeText(this, "Allow Permissions", Toast.LENGTH_SHORT).show();
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            // Logic to handle location object
                            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                            List<Address> addresses;
                            Log.e(TAG, "onLocationChanged: "+location.toString());
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(),
                                        location.getLongitude(), 1);
                                if (addresses.size() > 0) {
                                    Locality = (addresses.get(0).getLocality());
                                    PostalCode = addresses.get(0).getPostalCode();
                                    Log.e(TAG, "onLocationChanged: "+PostalCode);
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Grant Permission to Know PetrolPrice", Toast.LENGTH_SHORT).show();
        }else{
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode==RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                startActivity(new Intent(this,MainActivity.class));
            }
        }
    }



    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location loc) {
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            Log.e(TAG, "onLocationChanged: "+loc.toString());
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    PostalCode = addresses.get(0).getPostalCode();
                    Log.e(TAG, "onLocationChanged: "+PostalCode);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }



        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

}
