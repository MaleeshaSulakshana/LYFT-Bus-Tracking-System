package com.lyft.lyft.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.lyft.lyft.Api.API;
import com.lyft.lyft.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserViewBookingDetailsActivity extends AppCompatActivity {

    private TextView date, start, end, pickup, busNo, booked;
    private String itemId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_booking_details);

        Intent intent = getIntent();
        itemId = intent.getStringExtra("id");

        date = (TextView) this.findViewById(R.id.date);
        start = (TextView) this.findViewById(R.id.start);
        end = (TextView) this.findViewById(R.id.end);
        pickup = (TextView) this.findViewById(R.id.pickup);
        busNo = (TextView) this.findViewById(R.id.busNo);
        booked = (TextView) this.findViewById(R.id.booked);

        showDetails();

    }


//    For get booking details
    private void showDetails()
    {

        String URL = API.BOOKINGS_API + itemId;

        RequestQueue requestQueue = Volley.newRequestQueue(UserViewBookingDetailsActivity.this);
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

                                String booked_date_time = jsonObject.getString("booked_date_time");
                                String bus_reg_no = jsonObject.getString("bus_reg_no");
                                String available_date_time = jsonObject.getString("available_date_time");
                                String picked_location = jsonObject.getString("picked_location");
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

                                date.setText("Start To Travel : " + available_date_time);
                                start.setText("Start : " + start_location);
                                end.setText("End : " + end_location);
                                pickup.setText("Price : " + picked_location);
                                busNo.setText("Bus Name : " + bus_reg_no);
                                booked.setText("Bus Number : " + booked_date_time);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserViewBookingDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

}