package com.lyft.lyft.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.lyft.lyft.Adapter.AvailableBookingAdapter;
import com.lyft.lyft.Api.API;
import com.lyft.lyft.Classes.Preferences;
import com.lyft.lyft.Driver.DriverDashboardActivity;
import com.lyft.lyft.MainActivity;
import com.lyft.lyft.Model.AvailableBooking;
import com.lyft.lyft.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserDashboardActivity extends AppCompatActivity implements LocationListener {

    private LinearLayout btnMenu;

    private boolean doubleBackToExitPressedOnce = false;
    private BottomSheetDialog exitDialog, menuDialog, vehicleDetailsDialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int REQUEST_CODE = 101;

    protected LocationManager locationManager;
    int LOCATION_REFRESH_TIME = 10000;
    int LOCATION_REFRESH_DISTANCE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

//        Dialog box
        exitDialog = new BottomSheetDialog(UserDashboardActivity.this, R.style.BottomSheetTheme);
        exitDialog.setContentView(R.layout.dialog_box_exit);

        menuDialog = new BottomSheetDialog(UserDashboardActivity.this, R.style.BottomSheetTheme);
        menuDialog.setContentView(R.layout.dialog_box_user_menu);

        vehicleDetailsDialog = new BottomSheetDialog(UserDashboardActivity.this, R.style.BottomSheetTheme);
        vehicleDetailsDialog.setContentView(R.layout.dialog_box_view_vehicle_details);

        btnMenu = (LinearLayout) this.findViewById(R.id.btnMenu);

//        Map initializing
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(UserDashboardActivity.this);

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

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout btnLogout, btnProfile, btnClose, btnAvailableBusBookings, btnMyBookings;
                menuDialog.show();

                btnProfile = (LinearLayout) menuDialog.findViewById(R.id.btnProfile);
                btnLogout = (LinearLayout) menuDialog.findViewById(R.id.btnLogout);
                btnClose = (LinearLayout) menuDialog.findViewById(R.id.btnClose);
                btnAvailableBusBookings = (LinearLayout) menuDialog.findViewById(R.id.btnAvailableBusBookings);
                btnMyBookings = (LinearLayout) menuDialog.findViewById(R.id.btnMyBookings);

                btnProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(UserDashboardActivity.this, UserProfileViewActivity.class);
                        startActivity(intent);

                    }
                });

                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editor.clear();
                        editor.apply();

                        menuDialog.dismiss();

                        Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

                btnAvailableBusBookings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(UserDashboardActivity.this, UserViewAvailableBusBookingListActivity.class);
                        startActivity(intent);

                    }
                });

                btnMyBookings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(UserDashboardActivity.this, UserViewMyBookingsListActivity.class);
                        startActivity(intent);

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

            if (ActivityCompat.checkSelfPermission(
                    UserDashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            UserDashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(UserDashboardActivity.this, new String[]
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

                                        String URL = API.TRACKING_API;
                                        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {

                                                    JSONArray jsonArray = new JSONArray(response);

                                                    for (int index = 0; index < jsonArray.length(); index++) {
                                                        JSONObject jsonObject = jsonArray.getJSONObject(index);

                                                        Integer gps_id = Integer.valueOf(jsonObject.getString("gps_id"));
                                                        Double latitude = Double.valueOf(jsonObject.getString("latitude"));
                                                        Double longitude = Double.valueOf(jsonObject.getString("longitude"));

                                                        LatLng latLng = new LatLng(latitude, longitude);
                                                        MarkerOptions optionsVehicle = new MarkerOptions().position(latLng)
                                                                .title(gps_id.toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.school_bus));

                                                        googleMap.addMarker(optionsVehicle);

                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Toast.makeText(UserDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        requestQueue.add(stringRequest);


                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        MarkerOptions options = new MarkerOptions().position(latLng)
                                                .title("I am there");


                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                        googleMap.addMarker(options);

                                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(@NonNull Marker marker) {

                                                viewVehicleDetails(marker.getTitle());

                                                return true;
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


    private void viewVehicleDetails(String gps_id) {
        TextView vehicle, start, end, startTime;
        Button btnSelect;

        vehicle = (TextView) vehicleDetailsDialog.findViewById(R.id.vehicle);
        start = (TextView) vehicleDetailsDialog.findViewById(R.id.start);
        end = (TextView) vehicleDetailsDialog.findViewById(R.id.end);
        startTime = (TextView) vehicleDetailsDialog.findViewById(R.id.startTime);
        btnSelect = (Button) vehicleDetailsDialog.findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity.this, UserVehicleViewOnMapActivity.class);
                intent.putExtra("gpsId", gps_id.toString());
                startActivity(intent);
            }
        });

        String URL = API.TRACKING_API + "/" + gps_id;

        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            if (response.length() > 0) {

                                vehicleDetailsDialog.show();

                                JSONObject jsonObject = response.getJSONObject(0);

                                String bus_reg_no = jsonObject.getString("bus_reg_no");
                                String started_date_time = jsonObject.getString("started_date_time");
                                String root_start = jsonObject.getString("root_start");
                                String root_end = jsonObject.getString("root_end");

                                DateFormat df=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                Date d;
                                try {
                                    d = df.parse(started_date_time);
                                    df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    started_date_time = df.format(d);

                                } catch (ParseException e) {}

                                vehicle.setText(bus_reg_no);
                                startTime.setText(started_date_time);
                                start.setText(root_start);
                                end.setText(root_end);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }


//    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

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

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(UserDashboardActivity.this, "Please enable gps and internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

}