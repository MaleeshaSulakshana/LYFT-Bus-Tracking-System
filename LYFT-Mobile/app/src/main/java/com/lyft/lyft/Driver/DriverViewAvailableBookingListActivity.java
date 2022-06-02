package com.lyft.lyft.Driver;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lyft.lyft.Adapter.AvailableBookingAdapter;
import com.lyft.lyft.Api.API;
import com.lyft.lyft.Classes.Preferences;
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

public class DriverViewAvailableBookingListActivity extends AppCompatActivity {

    private ListView litView;
    private ArrayList<AvailableBooking> detailsArrayList = new ArrayList<>();

    private String driverId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_available_booking_list);

        litView = this.findViewById(R.id.listView);

        showListData();

//        litView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
//
//                String selected = String.valueOf(detailsArrayList.get(i).getId());
//
//                Intent intent = new Intent(DriverViewAvailableBookingListActivity.this, DriverViewAvailableBookingListActivity.class);
//                intent.putExtra("id", selected);
//                startActivity(intent);
//
//            }
//        });

    }


    private void showListData()
    {
        detailsArrayList.clear();
        litView.setAdapter(null);

        AvailableBookingAdapter adapter = new AvailableBookingAdapter(this, R.layout.row_available_booking_item, detailsArrayList);
        litView.setAdapter(adapter);

        String todayDate = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS", Locale.getDefault()).format(new Date());

        try {
            String URL = API.BOOKING_AVAILABLE_API + "vehicle";

            RequestQueue requestQueue = Volley.newRequestQueue(DriverViewAvailableBookingListActivity.this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", Preferences.LOGGED_USER_ID);
            jsonBody.put("date", todayDate);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONArray jsonArray = new JSONArray(response);

                        for (int index = 0; index < jsonArray.length(); index++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(index);

                            Integer id = Integer.valueOf(jsonObject.getString("id"));
                            Integer vehicle = Integer.valueOf(jsonObject.getString("vehicle_id"));
                            String dateTime = jsonObject.getString("available_date_time");
                            String sheets = jsonObject.getString("sheet_count");
                            String start = jsonObject.getString("start_location");
                            String end = jsonObject.getString("end_location");
                            String busName = jsonObject.getString("bus_name");
                            String nusRegNo = jsonObject.getString("bus_reg_no");
                            String rootNo = jsonObject.getString("root_no");

                            DateFormat df=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            Date d;
                            try {
                                d = df.parse(dateTime);
                                df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                dateTime = df.format(d);

                            } catch (ParseException e) {}

                            detailsArrayList.add(new AvailableBooking(id, vehicle,dateTime,sheets,start,end,busName,nusRegNo,rootNo));

                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(DriverViewAvailableBookingListActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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

}