package com.lyft.lyft.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.lyft.lyft.Driver.DriverProfileViewActivity;
import com.lyft.lyft.MainActivity;
import com.lyft.lyft.R;
import com.lyft.lyft.RegistrationActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UserProfileViewActivity extends AppCompatActivity {

    private Button btnPswChange, btnEdit;
    private EditText name, email, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);

        btnPswChange = (Button) this.findViewById(R.id.btnPswChange);
        btnEdit = (Button) this.findViewById(R.id.btnEdit);

        name = (EditText) this.findViewById(R.id.name);
        email = (EditText) this.findViewById(R.id.email);
        number = (EditText) this.findViewById(R.id.number);

        showDetails();

        btnPswChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UserProfileViewActivity.this, UserPswChangeActivity.class);
                startActivity(intent);
//                finish();

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateAccountDetails();

            }
        });

    }


//    For get account details
    private void showDetails()
    {

        String URL = API.USERS_API + "/" + Preferences.LOGGED_USER_ID;

        RequestQueue requestQueue = Volley.newRequestQueue(UserProfileViewActivity.this);
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

                                String idValue = jsonObject.getString("id");
                                String nameValue = jsonObject.getString("name");
                                String emailValue = jsonObject.getString("email");
                                String mobileValue = jsonObject.getString("mobile");


                                name.setText(nameValue);
                                email.setText(emailValue);
                                number.setText(mobileValue);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserProfileViewActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }


    //    For update account details
    private void updateAccountDetails() {

        String nameValue = name.getText().toString();
        String numberValue = number.getText().toString();

        if (nameValue.equals("") || numberValue.equals("")) {
            Toast.makeText(UserProfileViewActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

        } else if (numberValue.length() != 10) {
            Toast.makeText(UserProfileViewActivity.this, "Invalid mobile number!",Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(UserProfileViewActivity.this, "Waiting for update account!",Toast.LENGTH_SHORT).show();

            try {

                String URL = API.USERS_API + "/" + Preferences.LOGGED_USER_ID;

                RequestQueue requestQueue = Volley.newRequestQueue(UserProfileViewActivity.this);
                JSONObject jsonBody = new JSONObject();

                jsonBody.put("name", nameValue);
                jsonBody.put("mobile", numberValue);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            Toast.makeText(UserProfileViewActivity.this, msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(UserProfileViewActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
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