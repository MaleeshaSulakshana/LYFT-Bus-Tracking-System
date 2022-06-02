package com.lyft.lyft.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.lyft.lyft.Api.API;
import com.lyft.lyft.Classes.Preferences;
import com.lyft.lyft.R;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserViewAvailableBusBookingDetailsActivity extends AppCompatActivity {

    private TextView date, start, end, price, busName, busNo, root;
    private EditText pickup;
    private Button btnBook;
    private String itemId = "", priceValue = "";


    public static final String CONFIG_CLIENT_ID = "ASTaz35VV2liCVUN2pBtw5HFy98u9WDTg2p9lXc-hJ0T1tT0NvMfS3g9icqZSEMUfUwDtj4xx70wRB55";
    private static final String TAG = "Bus-Tracker";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_available_bus_booking_details);

        Intent intent = getIntent();
        itemId = intent.getStringExtra("id");

        date = (TextView) this.findViewById(R.id.date);
        start = (TextView) this.findViewById(R.id.start);
        end = (TextView) this.findViewById(R.id.end);
        price = (TextView) this.findViewById(R.id.price);
        busName = (TextView) this.findViewById(R.id.busName);
        busNo = (TextView) this.findViewById(R.id.busNo);
        root = (TextView) this.findViewById(R.id.root);

        pickup = (EditText) this.findViewById(R.id.pickup);

        btnBook = (Button) this.findViewById(R.id.btnBook);

        showDetails();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPayment();

            }
        });

    }


//    For get available booking details
    private void showDetails()
    {

        String URL = API.BOOKING_AVAILABLE_API + itemId;

        RequestQueue requestQueue = Volley.newRequestQueue(UserViewAvailableBusBookingDetailsActivity.this);
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

                                String id = jsonObject.getString("id");
                                String vehicle_id = jsonObject.getString("vehicle_id");
                                String available_date_time = jsonObject.getString("available_date_time");
                                String sheet_count = jsonObject.getString("sheet_count");
                                String start_location = jsonObject.getString("start_location");
                                String end_location = jsonObject.getString("end_location");
                                String bus_name = jsonObject.getString("bus_name");
                                String bus_reg_no = jsonObject.getString("bus_reg_no");
                                String root_no = jsonObject.getString("root_no");
                                String ticketPrice  = jsonObject.getString("price");

                                priceValue = ticketPrice;

                                DateFormat df=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                try {
                                    Date dd = df.parse(available_date_time);
                                    df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    available_date_time = df.format(dd);

                                } catch (ParseException e) {}

                                date.setText("Start To Travel : " + available_date_time);
                                start.setText("Start : " + start_location);
                                end.setText("End : " + end_location);
                                price.setText("Price : " + ticketPrice);
                                busName.setText("Bus Name : " + bus_name);
                                busNo.setText("Bus Number : " + bus_reg_no);
                                root.setText("Root Number : " + root_no);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserViewAvailableBusBookingDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }


    private void getPayment() {

        if (priceValue.equals("") || pickup.getText().toString().equals("")){
            Toast.makeText(UserViewAvailableBusBookingDetailsActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();
        } else {

            PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(String.valueOf(priceValue)),
                    "USD", "Licence Tracker | Pay fine",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(UserViewAvailableBusBookingDetailsActivity.this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);

        }

    }

    protected void displayResultText(String result) {
        Toast.makeText(
                getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        addBooking();


                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                displayResultText("Payment canceled!");

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Future Payment code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");

            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");

            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) { }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    private void addBooking() {

        try {
            String URL = API.BOOKINGS_API;

            String todayDate = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS", Locale.getDefault()).format(new Date());

            RequestQueue requestQueue = Volley.newRequestQueue(UserViewAvailableBusBookingDetailsActivity.this);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", Preferences.LOGGED_USER_ID);
            jsonBody.put("available_booking_id", itemId);
            jsonBody.put("booked_date_time", todayDate);
            jsonBody.put("picked_location", pickup.getText().toString());

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

                        Toast.makeText(UserViewAvailableBusBookingDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UserViewAvailableBusBookingDetailsActivity.this, UserViewAvailableBusBookingListActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(UserViewAvailableBusBookingDetailsActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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