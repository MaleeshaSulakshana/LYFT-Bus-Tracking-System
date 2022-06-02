package com.lyft.lyft;

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
import com.lyft.lyft.Classes.UTILS;
import com.lyft.lyft.Driver.DriverDashboardActivity;
import com.lyft.lyft.User.UserDashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegistrationActivity extends AppCompatActivity {

    private Button btnReg;
    private EditText name, email, number, psw, cpsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnReg = (Button) this.findViewById(R.id.btnReg);

        name = (EditText) this.findViewById(R.id.name);
        email = (EditText) this.findViewById(R.id.email);
        number = (EditText) this.findViewById(R.id.number);
        psw = (EditText) this.findViewById(R.id.psw);
        cpsw = (EditText) this.findViewById(R.id.cpsw);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameValue = name.getText().toString();
                String emailValue = email.getText().toString();
                String numberValue = number.getText().toString();
                String pswValue = psw.getText().toString();
                String cpswValue = cpsw.getText().toString();

                if (nameValue.equals("") || emailValue.equals("") || numberValue.equals("")
                        || pswValue.equals("") || cpswValue.equals("")) {
                    Toast.makeText(RegistrationActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                } else if (!UTILS.isValidEmail(emailValue)) {
                    Toast.makeText(RegistrationActivity.this, "Email pattern invalid!", Toast.LENGTH_SHORT).show();

                } else if (numberValue.length() != 10) {
                    Toast.makeText(RegistrationActivity.this, "Invalid mobile number!",Toast.LENGTH_SHORT).show();

                } else if (!pswValue.equals(cpswValue)) {
                    Toast.makeText(RegistrationActivity.this, "Password and confirm password not matched!",Toast.LENGTH_SHORT).show();

                } else {

                    try {

                        Toast.makeText(RegistrationActivity.this, "Waiting for create account!",Toast.LENGTH_SHORT).show();

                        String URL = API.USERS_API;

                        RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivity.this);
                        JSONObject jsonBody = new JSONObject();

                        jsonBody.put("name", nameValue);
                        jsonBody.put("email", emailValue);
                        jsonBody.put("mobile", numberValue);
                        jsonBody.put("psw", pswValue);

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String status = jsonObject.getString("status");
                                    String msg = jsonObject.getString("msg");

                                    if (status.equals("success")) {

                                        name.setText("");
                                        email.setText("");
                                        number.setText("");
                                        psw.setText("");
                                        cpsw.setText("");


                                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                    Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(RegistrationActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
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
        });

    }
}