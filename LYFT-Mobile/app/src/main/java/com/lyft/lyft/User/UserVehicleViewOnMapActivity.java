package com.lyft.lyft.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lyft.lyft.Api.API;
import com.lyft.lyft.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserVehicleViewOnMapActivity extends AppCompatActivity {

    private Button btnAlarmOff, btnAlarm;

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int REQUEST_CODE = 101;

    protected LocationManager locationManager;
    int LOCATION_REFRESH_TIME = 10000; // 10 seconds to update
    int LOCATION_REFRESH_DISTANCE = 0; // 100 meters to update

    private Uri notification;
    private Ringtone ringtone;

    private String gpsId = "", alarm = "on";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vehicle_view_on_map);

        Intent intent = getIntent();
        gpsId = intent.getStringExtra("gpsId");

        btnAlarmOff = (Button) this.findViewById(R.id.btnAlarmOff);
        btnAlarm = (Button) this.findViewById(R.id.btnAlarm);

        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alarm.equals("on")) {
                    alarm = "off";
                    ringtone.stop();
                    btnAlarmOff.setVisibility(View.GONE);
                    btnAlarm.setText("Alarm Turn On");

                } else {
                    alarm = "on";
                    btnAlarm.setText("Alarm Turn Off");
                }

            }
        });

        btnAlarmOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ringtone.stop();
                btnAlarmOff.setVisibility(View.GONE);

            }
        });

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);

//        Map initializing
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(UserVehicleViewOnMapActivity.this);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, locationListener);


    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            if (ActivityCompat.checkSelfPermission(
                    UserVehicleViewOnMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            UserVehicleViewOnMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(UserVehicleViewOnMapActivity.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                return;
            }

            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {

                        if (location != null) {
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {

                                    googleMap.clear();

                                    String URL = API.TRACKING_API + gpsId;

                                    RequestQueue requestQueue = Volley.newRequestQueue(UserVehicleViewOnMapActivity.this);
                                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                                            Request.Method.GET,
                                            URL,
                                            null,
                                            new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {

                                            try {

                                                if (response.length() > 0) {

                                                    JSONObject jsonObject = response.getJSONObject(0);

                                                    Double latitude = Double.valueOf(jsonObject.getString("latitude"));
                                                    Double longitude = Double.valueOf(jsonObject.getString("longitude"));

                                                    Location startPoint=new Location("locationA");
                                                    startPoint.setLatitude(latitude);
                                                    startPoint.setLongitude(longitude);

                                                    Location endPoint=new Location("locationA");
                                                    endPoint.setLatitude(location.getLatitude());
                                                    endPoint.setLongitude(location.getLongitude());

                                                    double distanceValue = startPoint.distanceTo(endPoint);
                                                    double distanceInKm = (distanceValue/1000);

                                                    if (distanceInKm < 1) {
                                                        if (alarm.equals("on")) {
                                                            ringtone.play();
                                                            btnAlarmOff.setVisibility(View.VISIBLE);
                                                            Toast.makeText(UserVehicleViewOnMapActivity.this, "Bus in within 1km", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    LatLng latLng = new LatLng(latitude, longitude);
                                                    MarkerOptions optionsVehicle = new MarkerOptions().position(latLng)
                                                            .title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.school_bus));

                                                    googleMap.addMarker(optionsVehicle);

                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                            Toast.makeText(UserVehicleViewOnMapActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    requestQueue.add(jsonArrayRequest);


                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    MarkerOptions options = new MarkerOptions().position(latLng)
                                            .title("I am there");

                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                    googleMap.addMarker(options);

                                }
                            });
                        }

                    }
                }
            });

        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ringtone.stop();
    }
}