package com.lyft.lyft;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lyft.lyft.Api.API;
import com.lyft.lyft.Classes.UTILS;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class DriverRegistrationActivity extends AppCompatActivity {

    private Button btnReg;
    private ImageView itemImage;
    private EditText name, email, number, busName, busRegNumber, rootNo, rootStart, rootEnd, ownerName, ownerNumber, driverName, conductorName, psw, cpsw;

    private Uri imgUri;
    private String thumbnailUrl = "";
    private String isSelected = "";

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        btnReg = (Button) this.findViewById(R.id.btnReg);

        name = (EditText) this.findViewById(R.id.name);
        email = (EditText) this.findViewById(R.id.email);
        number = (EditText) this.findViewById(R.id.number);
        busName = (EditText) this.findViewById(R.id.busName);
        busRegNumber = (EditText) this.findViewById(R.id.busRegNumber);
        rootNo = (EditText) this.findViewById(R.id.rootNo);
        rootStart = (EditText) this.findViewById(R.id.rootStart);
        rootEnd = (EditText) this.findViewById(R.id.rootEnd);
        ownerName = (EditText) this.findViewById(R.id.ownerName);
        ownerNumber = (EditText) this.findViewById(R.id.ownerNumber);
        driverName = (EditText) this.findViewById(R.id.driverName);
        conductorName = (EditText) this.findViewById(R.id.conductorName);
        psw = (EditText) this.findViewById(R.id.psw);
        cpsw = (EditText) this.findViewById(R.id.cpsw);

        itemImage = (ImageView) this.findViewById(R.id.itemImage);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPopUp();
                choosePicture();
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadRegDetails();

            }
        });


    }

//    For select item image
    private void choosePicture() {
        Intent selectPicture = new Intent();
        selectPicture.setType("image/*");
        selectPicture.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(selectPicture, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri = data.getData();
            itemImage.setImageURI(imgUri);
            isSelected = "yes";
//            uploadPicture();
        }

    }


    private void uploadRegDetails() {

        String nameValue = name.getText().toString();
        String emailValue = email.getText().toString();
        String numberValue = number.getText().toString();
        String busNameValue = busName.getText().toString();
        String busRegNumberValue = busRegNumber.getText().toString();
        String rootNoValue = rootNo.getText().toString();
        String rootStartValue = rootStart.getText().toString();
        String rootEndValue = rootEnd.getText().toString();
        String ownerNameValue = ownerName.getText().toString();
        String ownerNumberValue = ownerNumber.getText().toString();
        String driverNameValue = driverName.getText().toString();
        String conductorNameValue = conductorName.getText().toString();
        String pswValue = psw.getText().toString();
        String cpswValue = cpsw.getText().toString();

        if (nameValue.equals("") || emailValue.equals("") || numberValue.equals("")
                || busNameValue.equals("") || busRegNumberValue.equals("") || rootNoValue.equals("")
                || rootStartValue.equals("") || rootEndValue.equals("") || ownerNameValue.equals("")
                || ownerNumberValue.equals("") || driverNameValue.equals("") || conductorNameValue.equals("")
                || pswValue.equals("") || cpswValue.equals("") || isSelected.equals("")) {
            Toast.makeText(DriverRegistrationActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

        } else if (!UTILS.isValidEmail(emailValue)) {
            Toast.makeText(DriverRegistrationActivity.this, "Email pattern invalid!", Toast.LENGTH_SHORT).show();

        } else if (numberValue.length() != 10) {
            Toast.makeText(DriverRegistrationActivity.this, "Invalid mobile number!",Toast.LENGTH_SHORT).show();

        } else if (ownerNumberValue.length() != 10) {
            Toast.makeText(DriverRegistrationActivity.this, "Invalid owner mobile number!",Toast.LENGTH_SHORT).show();

        } else if (!pswValue.equals(cpswValue)) {
            Toast.makeText(DriverRegistrationActivity.this, "Password and confirm password not matched!",Toast.LENGTH_SHORT).show();

        } else {


            Toast.makeText(DriverRegistrationActivity.this, "Waiting for create account!",Toast.LENGTH_SHORT).show();



            final ProgressDialog progressDialog = new ProgressDialog(DriverRegistrationActivity.this);
            progressDialog.setTitle("Uploading ...");
            progressDialog.show();

            String random_number = UTILS.generateRandomNumber();

            StorageReference riversRef = storageReference.child("vehicles_Pictures/" + random_number);
            riversRef.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    try {

                                        String URL = API.VEHICLES_API;

                                        RequestQueue requestQueue = Volley.newRequestQueue(DriverRegistrationActivity.this);
                                        JSONObject jsonBody = new JSONObject();

                                        jsonBody.put("name", nameValue);
                                        jsonBody.put("email", emailValue);
                                        jsonBody.put("mobile", numberValue);
                                        jsonBody.put("bus_name", busNameValue);
                                        jsonBody.put("bus_reg_no", busRegNumberValue);
                                        jsonBody.put("root_no", rootNoValue);
                                        jsonBody.put("root_start", rootStartValue);
                                        jsonBody.put("root_end", rootEndValue);
                                        jsonBody.put("owner_name", ownerNameValue);
                                        jsonBody.put("owner_number", ownerNumberValue);
                                        jsonBody.put("driver_name", driverNameValue);
                                        jsonBody.put("conductor_name", conductorNameValue);
                                        jsonBody.put("pws", pswValue);
                                        jsonBody.put("image", uri.toString());

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
                                                        busName.setText("");
                                                        busRegNumber.setText("");
                                                        rootNo.setText("");
                                                        rootStart.setText("");
                                                        rootEnd.setText("");
                                                        ownerName.setText("");
                                                        ownerNumber.setText("");
                                                        driverName.setText("");
                                                        conductorName.setText("");
                                                        psw.setText("");
                                                        cpsw.setText("");

                                                        isSelected = "";

                                                        Intent intent = new Intent(DriverRegistrationActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }

                                                    Toast.makeText(DriverRegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Toast.makeText(DriverRegistrationActivity.this, "Some error occur " + error.toString(), Toast.LENGTH_SHORT).show();
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
                            });
                            progressDialog.dismiss();
                            Toast.makeText(DriverRegistrationActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(DriverRegistrationActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading... " + (int) progressPercentage + "%");
                }
            });


        }

    }

}