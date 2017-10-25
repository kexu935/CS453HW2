package com.example.kex.cs453hw2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Menu;

public class MainActivity extends AppCompatActivity implements LocationListener {

    EditText latitude;
    EditText longitude;
    EditText radius;

    LocationManager locationManager;
    String provider;
    private Double currentLatitude;
    private Double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button targetPlace = (Button) findViewById(R.id.targetPlace);
        final Button currentPlace = (Button) findViewById(R.id.currentPlace);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        radius = (EditText) findViewById(R.id.radius);

        targetPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitudeString = latitude.getText().toString();
                String longitudeString = longitude.getText().toString();
                String radiusString = radius.getText().toString();
                if (latitudeString.isEmpty() || longitudeString.isEmpty() || radiusString.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter valid inputs!", Toast.LENGTH_LONG).show();
                } else {
                    Double la = Double.parseDouble(latitudeString);
                    Double lo = Double.parseDouble(longitudeString);
                    Double ra = Double.parseDouble(radiusString);
                    if ((la < -90) || (la > 90) || (lo < -180) || (lo > 180) || (ra < 1) || (ra > 50000)) {
                        Toast.makeText(MainActivity.this, "Please enter valid inputs!", Toast.LENGTH_LONG).show();
                    } else {
                        Intent myIntent = new Intent(MainActivity.this, GetPOI.class);
                        Bundle b = new Bundle();
                        b.putDouble("latitude", la);
                        b.putDouble("longitude", lo);
                        b.putDouble("radius", ra);
                        myIntent.putExtras(b);
                        startActivity(myIntent);
                    }
                }
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (provider != null && !provider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 20000, 1, this);
            if(location!=null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }

        currentPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLatitude == null || currentLongitude == null) {
                    Toast.makeText(MainActivity.this, "Cannot get current location!", Toast.LENGTH_LONG).show();
                } else {
                    Intent myIntent = new Intent(MainActivity.this, GetPOI.class);
                    Bundle b = new Bundle();
                    b.putDouble("latitude", currentLatitude);
                    b.putDouble("longitude", currentLongitude);
                    b.putDouble("radius", 5000);
                    myIntent.putExtras(b);
                    startActivity(myIntent);
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
