package com.lyft.lyft.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lyft.lyft.Api.API;
import com.lyft.lyft.Classes.Preferences;
import com.lyft.lyft.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class DriverPswChangeActivity extends AppCompatActivity {

    private EditText psw, cpsw;
    private Button btnChange, btnBackToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_psw_change);

        btnChange = (Button) this.findViewById(R.id.btnChange);
        btnBackToProfile = (Button) this.findViewById(R.id.btnBackToProfile);

        psw = (EditText) this.findViewById(R.id.psw);
        cpsw = (EditText) this.findViewById(R.id.cpsw);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccountPsw();
            }
        });

        btnBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DriverPswChangeActivity.this, DriverProfileViewActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }


//    For update account psw
    private void updateAccountPsw() {

        String pswValue = psw.getText().toString();
        String cpswValue = cpsw.getText().toString();

        if (pswValue.equals("") || cpswValue.equals("")) {
            Toast.makeText(DriverPswChangeActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

        } else if (!pswValue.equals(cpswValue)) {
            Toast.makeText(DriverPswChangeActivity.this, "Password and confirm password not matched!",Toast.LENGTH_SHORT).show();

        } else {


            Toast.makeText(DriverPswChangeActivity.this, "Waiting for update account password",Toast.LENGTH_SHORT).show();

            try {

                String URL = API.VEHICLES_API+ "psw/" + Preferences.LOGGED_USER_ID;

                RequestQueue requestQueue = Volley.newRequestQueue(DriverPswChangeActivity.this);
                JSONObject jsonBody = new JSONObject();

                jsonBody.put("psw", pswValue);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String status = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            if (status.equals("success")) {

                                psw.setText("");
                                cpsw.setText("");

                            }

                            Toast.makeText(DriverPswChangeActivity.this, msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(DriverPswChangeActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
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