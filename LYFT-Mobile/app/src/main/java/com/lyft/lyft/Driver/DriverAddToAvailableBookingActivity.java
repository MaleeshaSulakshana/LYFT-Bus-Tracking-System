package com.lyft.lyft.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lyft.lyft.Api.API;
import com.lyft.lyft.Classes.Preferences;
import com.lyft.lyft.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DriverAddToAvailableBookingActivity extends AppCompatActivity {

    private EditText dateTime, sheets, startLocation, endLocation;
    private Button btnAdd, backToDashboard;

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_add_to_available_booking);

        btnAdd = (Button) this.findViewById(R.id.btnAdd);
        backToDashboard = (Button) this.findViewById(R.id.backToDashboard);

        dateTime = (EditText) this.findViewById(R.id.dateTime);
        sheets = (EditText) this.findViewById(R.id.sheets);
        startLocation = (EditText) this.findViewById(R.id.startLocation);
        endLocation = (EditText) this.findViewById(R.id.endLocation);


        //        Show calender
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR, dayOfMonth);
                calendar.set(Calendar.MINUTE, dayOfMonth);
                updateDate();
            }

        };

//        Set some option to text layouts
        dateTime.setEnabled(true);
        dateTime.setTextIsSelectable(true);
        dateTime.setFocusable(false);
        dateTime.setFocusableInTouchMode(false);

//        Onclick for show date picker
        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DriverAddToAvailableBookingActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        backToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DriverAddToAvailableBookingActivity.this,DriverDashboardActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToBooking();

            }
        });

    }


    //    Method for show date on text box
    private void updateDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateTime.setText(sdf.format(calendar.getTime()));
    }


//    For add to booking
    private void addToBooking() {

        String dateTimeValue = dateTime.getText().toString();
        String sheetsValue = sheets.getText().toString();
        String startLocationValue = startLocation.getText().toString();
        String endLocationValue = endLocation.getText().toString();

        if (dateTimeValue.equals("") || sheetsValue.equals("") || startLocationValue.equals("") || endLocationValue.equals("")) {
            Toast.makeText(DriverAddToAvailableBookingActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(DriverAddToAvailableBookingActivity.this, "Waiting for add to booking!",Toast.LENGTH_SHORT).show();

            try {

                String URL = API.BOOKING_AVAILABLE_API;

                RequestQueue requestQueue = Volley.newRequestQueue(DriverAddToAvailableBookingActivity.this);
                JSONObject jsonBody = new JSONObject();

                jsonBody.put("vehicle_id", Preferences.LOGGED_USER_ID);
                jsonBody.put("available_date_time", dateTimeValue);
                jsonBody.put("sheet_count", sheetsValue);
                jsonBody.put("start_location", startLocationValue);
                jsonBody.put("end_location", endLocationValue);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            if (status.equals("success")) {

                                dateTime.setText("");
                                sheets.setText("");
                                startLocation.setText("");
                                endLocation.setText("");

                            }

                            Toast.makeText(DriverAddToAvailableBookingActivity.this, msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(DriverAddToAvailableBookingActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
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

}