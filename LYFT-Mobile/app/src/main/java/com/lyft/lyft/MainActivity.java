package com.lyft.lyft;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lyft.lyft.Classes.Preferences;
import com.lyft.lyft.Driver.DriverDashboardActivity;
import com.lyft.lyft.User.UserDashboardActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnEnter;

    private boolean doubleBackToExitPressedOnce = false;
    private BottomSheetDialog exitDialog, authDialog, userTypeDialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_main);

//        Dialog box
        authDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetTheme);
        authDialog.setContentView(R.layout.dialog_box_authentications);

        exitDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetTheme);
        exitDialog.setContentView(R.layout.dialog_box_exit);

        userTypeDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetTheme);
        userTypeDialog.setContentView(R.layout.dialog_box_user_type);

        btnEnter = (Button) this.findViewById(R.id.btnEnter);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String id = sharedPreferences.getString("id", "");
                String user_type = sharedPreferences.getString("user_type", "");

                if (!id.equals("") && !user_type.equals("")) {

                    if (user_type.equals("user")) {

                        Preferences.LOGGED_USER_ID = id;

                        Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (user_type.equals("vehicles")) {

                        Preferences.LOGGED_USER_ID = id;

                        Intent intent = new Intent(MainActivity.this, DriverDashboardActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Preferences.LOGGED_USER_ID = "";

                        editor.clear();
                        editor.apply();

                        showLoginDialog();

                    }

                } else {

                    Preferences.LOGGED_USER_ID = "";

                    showLoginDialog();

                }

            }
        });

    }


    private void showLoginDialog() {

        Button btnLogin, btnReg;
        authDialog.show();

        btnLogin = (Button) authDialog.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authDialog.dismiss();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
//                        finish();

            }
        });

        btnReg = (Button) authDialog.findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authDialog.dismiss();

                Button btnDriver, btnTraveller;
                userTypeDialog.show();

                btnDriver = (Button) userTypeDialog.findViewById(R.id.btnDriver);
                btnDriver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userTypeDialog.dismiss();

                        Intent intent = new Intent(MainActivity.this, DriverRegistrationActivity.class);
                        startActivity(intent);
//                                finish();

                    }
                });

                btnTraveller = (Button) userTypeDialog.findViewById(R.id.btnTraveller);
                btnTraveller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userTypeDialog.dismiss();

                        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                        startActivity(intent);
//                                finish();

                    }
                });

            }
        });

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

}