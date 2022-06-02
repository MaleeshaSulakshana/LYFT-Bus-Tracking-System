package com.lyft.lyft.Driver;

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
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import com.lyft.lyft.Classes.Preferences;
import com.lyft.lyft.MainActivity;
import com.lyft.lyft.R;
import com.lyft.lyft.User.UserDashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.xml.transform.Result;

public class DriverDashboardActivity extends AppCompatActivity implements LocationListener {

    private LinearLayout btnMenu;

    private boolean doubleBackToExitPressedOnce = false;
    private BottomSheetDialog exitDialog, menuDialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int REQUEST_CODE = 101;
    private Location currentLocation;

    protected LocationManager locationManager;
    int LOCATION_REFRESH_TIME = 10000; // 10 seconds to update
    int LOCATION_REFRESH_DISTANCE = 0; // 100 meters to update

    private Button btnStart;
    private String trackingKey = "", isRegistered = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

//        Dialog box
        exitDialog = new BottomSheetDialog(DriverDashboardActivity.this, R.style.BottomSheetTheme);
        exitDialog.setContentView(R.layout.dialog_box_exit);

        menuDialog = new BottomSheetDialog(DriverDashboardActivity.this, R.style.BottomSheetTheme);
        menuDialog.setContentView(R.layout.dialog_box_vehicle_menu);

        btnMenu = (LinearLayout) this.findViewById(R.id.btnMenu);
        btnStart = (Button) this.findViewById(R.id.btnStart);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

//        Map initializing
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DriverDashboardActivity.this);

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

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnStart.getText().toString().toUpperCase().equals("START")) {
                    if (currentLocation != null){
                        registerTracking();
                    } else {
                        Toast.makeText(DriverDashboardActivity.this, "Waiting for get your location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    stopTracking();
                }

            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout btnBookingsList, btnLogout, btnAdToBooking, btnProfile, btnClose;
                menuDialog.show();

                btnBookingsList = (LinearLayout) menuDialog.findViewById(R.id.btnBookingsList);
                btnAdToBooking = (LinearLayout) menuDialog.findViewById(R.id.btnAdToBooking);
                btnProfile = (LinearLayout) menuDialog.findViewById(R.id.btnProfile);
                btnLogout = (LinearLayout) menuDialog.findViewById(R.id.btnLogout);
                btnClose = (LinearLayout) menuDialog.findViewById(R.id.btnClose);

                btnBookingsList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isRegistered.equals("no")) {
                            Intent intent = new Intent(DriverDashboardActivity.this, DriverViewAvailableBookingListActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(DriverDashboardActivity.this, "Please end tracking before exit", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btnAdToBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRegistered.equals("no")) {
                            Intent intent = new Intent(DriverDashboardActivity.this, DriverAddToAvailableBookingActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(DriverDashboardActivity.this, "Please end tracking before exit", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btnProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRegistered.equals("no")) {
                            Intent intent = new Intent(DriverDashboardActivity.this, DriverProfileViewActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(DriverDashboardActivity.this, "Please end tracking before exit", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isRegistered.equals("no")) {

                            editor.clear();
                            editor.apply();

                            menuDialog.dismiss();

                            Intent intent = new Intent(DriverDashboardActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(DriverDashboardActivity.this, "Please end tracking before exit", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuDialog.dismiss();
                    }
                });

            }
        });

    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            currentLocation = location;

            if (ActivityCompat.checkSelfPermission(
                    DriverDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            DriverDashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(DriverDashboardActivity.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

                return;
            }

            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
//                            currentLocation = location;

                        if (location != null) {
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {

                                    googleMap.clear();

                                    if (isRegistered.equals("yes")) {
                                        updateTracking(String.valueOf(location.getLatitude()),
                                                String.valueOf(location.getLongitude()));
                                    }

                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    MarkerOptions options = new MarkerOptions().position(latLng)
                                            .title("I am there").icon(BitmapDescriptorFactory.fromResource(R.drawable.school_bus));

                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                                    googleMap.addMarker(options);

                                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(@NonNull Marker marker) {

//                                                if (marker.getTitle().equals("MyHome"))
                                            Toast.makeText(DriverDashboardActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();

                                            return false;
                                        }
                                    });

                                }
                            });
                        }

                    }
                }
            });

        }
    };

    private void registerTracking() {

        int min = 1000000;
        int max = 9999999;
        String random_number = String.valueOf(new Random().nextInt(max) + min);

        String todayDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        trackingKey = random_number.toString();
        Toast.makeText(DriverDashboardActivity.this, "Waiting for register tracking", Toast.LENGTH_SHORT).show();

        try {

            String URL = API.TRACKING_API;

            RequestQueue requestQueue = Volley.newRequestQueue(DriverDashboardActivity.this);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("gps_id", random_number);
            jsonBody.put("latitude", String.valueOf(currentLocation.getLatitude()));
            jsonBody.put("longitude", String.valueOf(currentLocation.getLongitude()));
            jsonBody.put("vehicle", Preferences.LOGGED_USER_ID);
            jsonBody.put("status", "started");
            jsonBody.put("started_date_time", todayDateTime);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

                        if (status.equals("success")) {

                            isRegistered = "yes";
                            btnStart.setText("STOP");

                        }

                        Toast.makeText(DriverDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(DriverDashboardActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void stopTracking() {

        Toast.makeText(DriverDashboardActivity.this, "Waiting for stop tracking", Toast.LENGTH_SHORT).show();

        try {

            String URL = API.TRACKING_API + "end/" + trackingKey;

            RequestQueue requestQueue = Volley.newRequestQueue(DriverDashboardActivity.this);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("status", "stopped");

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

                        if (status.equals("success")) {

                            isRegistered = "no";
                            trackingKey = "";
                            currentLocation.reset();
                            btnStart.setText("START");

                        }

                        Toast.makeText(DriverDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(DriverDashboardActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateTracking(String latitude, String longitude) {


        try {

            String URL = API.TRACKING_API + trackingKey;

            RequestQueue requestQueue = Volley.newRequestQueue(DriverDashboardActivity.this);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("latitude", latitude);
            jsonBody.put("longitude", longitude);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

//                        Toast.makeText(DriverDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(DriverDashboardActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        if (isRegistered.equals("no")) {
            Button btnExitYes, btnExitNo;
            exitDialog.show();

            btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
            btnExitYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                    System.exit(0);
                }
            });

            btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
            btnExitNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(DriverDashboardActivity.this, "Please end tracking before exit", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(DriverDashboardActivity.this, "Please enable gps and internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }


}