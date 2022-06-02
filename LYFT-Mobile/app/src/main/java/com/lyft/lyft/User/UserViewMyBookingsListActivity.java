package com.lyft.lyft.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lyft.lyft.Adapter.BookingAdapter;
import com.lyft.lyft.Api.API;
import com.lyft.lyft.Classes.Preferences;
import com.lyft.lyft.Model.Booking;
import com.lyft.lyft.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserViewMyBookingsListActivity extends AppCompatActivity {

    private ListView litView;
    private ArrayList<Booking> detailsArrayList = new ArrayList<>();

    private String driverId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_my_bookings_list);

        litView = this.findViewById(R.id.listView);

        showListData();

        litView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                String selected = String.valueOf(detailsArrayList.get(i).getId());

                Intent intent = new Intent(UserViewMyBookingsListActivity.this, UserViewBookingDetailsActivity.class);
                intent.putExtra("id", selected);
                startActivity(intent);

            }
        });

    }

    private void showListData()
    {
        detailsArrayList.clear();
        litView.setAdapter(null);

        BookingAdapter adapter = new BookingAdapter(this, R.layout.row_available_booking_item, detailsArrayList);
        litView.setAdapter(adapter);

        String todayDate = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS", Locale.getDefault()).format(new Date());

        String URL = API.BOOKINGS_API + "user/" + Preferences.LOGGED_USER_ID;

        RequestQueue requestQueue = Volley.newRequestQueue(UserViewMyBookingsListActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);

                        Integer id = Integer.valueOf(jsonObject.getString("id"));
                        Integer user_id = Integer.valueOf(jsonObject.getString("user_id"));
                        Integer available_booking_id = Integer.valueOf(jsonObject.getString("available_booking_id"));
                        String booked_date_time = jsonObject.getString("booked_date_time");
                        String picked_location = jsonObject.getString("picked_location");
                        String bus_reg_no = jsonObject.getString("bus_reg_no");
                        String available_date_time = jsonObject.getString("available_date_time");
                        String start_location = jsonObject.getString("start_location");
                        String end_location = jsonObject.getString("end_location");

                        DateFormat df=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        DateFormat df2=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        try {
                            Date d = df.parse(booked_date_time);
                            df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            booked_date_time = df.format(d);

                            Date dd = df2.parse(available_date_time);
                            df2=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            available_date_time = df2.format(dd);

                        } catch (ParseException e) {}

                        detailsArrayList.add(new Booking(id, user_id, available_booking_id, booked_date_time, picked_location,
                                bus_reg_no, available_date_time, start_location, end_location));

                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(UserViewMyBookingsListActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);

    }

}