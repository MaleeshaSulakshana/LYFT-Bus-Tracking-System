package com.lyft.lyft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.lyft.lyft.Driver.DriverDashboardActivity;
import com.lyft.lyft.User.UserDashboardActivity;
import com.lyft.lyft.User.UserProfileViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText email, psw;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) this.findViewById(R.id.btnLogin);

        email = (EditText) this.findViewById(R.id.email);
        psw = (EditText) this.findViewById(R.id.psw);

        //        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailValue = email.getText().toString();
                String pswValue = psw.getText().toString();

                if (emailValue.equals("") || pswValue.equals("")) {
                    Toast.makeText(LoginActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                } else {

                    try {

                        Toast.makeText(LoginActivity.this, "Waiting for login!",Toast.LENGTH_SHORT).show();

                        String URL = API.LOGIN_API;

                        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                        JSONObject jsonBody = new JSONObject();

                        jsonBody.put("email", emailValue);
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

                                        email.setText("");
                                        psw.setText("");

                                        String user_type = jsonObject.getString("user_type");
                                        String logged_user_id = jsonObject.getString("id");

                                        Preferences.LOGGED_USER_ID = logged_user_id;

                                        if (user_type.equals("user")) {

                                            editor.putString("id", logged_user_id );
                                            editor.putString("user_type", user_type );
                                            editor.commit();

                                            Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else if (user_type.equals("vehicles")) {

                                            editor.putString("id", logged_user_id );
                                            editor.putString("user_type", user_type );
                                            editor.commit();

                                            Intent intent = new Intent(LoginActivity.this, DriverDashboardActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Toast.makeText(LoginActivity.this, "Some error occur!.", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(LoginActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
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