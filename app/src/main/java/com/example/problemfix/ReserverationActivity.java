package com.example.problemfix;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.problemfix.Helper.Api;
import com.example.problemfix.Helper.Dialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReserverationActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener, LocationSource.OnLocationChangedListener, GoogleMap.OnMyLocationClickListener
        , GoogleMap.OnMyLocationChangeListener, AdapterView.OnItemSelectedListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 32;
    private static final int PERMISSIONS_REQUEST_ACCESS_Camera = 33;
    ImageView viewImage;


    Dialog dialog;
    SharedPreferences sharedpref;
    Double lat = 0.0;
    Double lng = 0.0;
    EditText NoteText;
    int menu_id = 1;
    Spinner menuSpinner;
    ArrayList<String> menus_titles;
    ArrayList<Integer> menus_ids;
    Location mLastKnownLocation;
    int DEFAULT_ZOOM = 15;
    Button submitData;
    private boolean mLocationPermissionGranted;
    private Bitmap uploadedFileBitmap;
    private LinearLayout showUploadingMessageLinearLayout;
    private GoogleMap mMap;
    private ImageButton openCameraButton, openStorageButton;
    private int REQUEST_VIDEO_CAPTURE = 2;
    private int REQUEST_IMAGE_CAPTURE = 1;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserveration);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        sharedpref = getSharedPreferences("Storage", MODE_PRIVATE);

        dialog = new Dialog(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getCameraPermission();
        this.iniViews();
        dialog.show();
        this.loadDataFromBackEnd();

    }


    private void loadDataFromBackEnd() {


        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.baseUrl + "menu",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            menus_titles = new ArrayList<>();
                            menus_ids = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject(String.valueOf(jsonArray.get(i)));

                                menus_ids.add(jsonObject.getInt("id"));
                                menus_titles.add(jsonObject.getString("title"));
                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (ReserverationActivity.this, android.R.layout.simple_spinner_item, menus_titles);

// Specify the layout to use when the list of choices appears
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            menuSpinner.setAdapter(spinnerArrayAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Menus List", error.toString());
            }
        });
        dialog.hide();

        queue.add(stringRequest);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        this.getLocationPermission();

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMyLocationChangeListener(this);

    }

    private void iniViews() {
        NoteText = findViewById(R.id.noteText);
        submitData = findViewById(R.id.submitData);
        openCameraButton = findViewById(R.id.openCameraButton);
        viewImage = findViewById(R.id.viewImage);
        openStorageButton = findViewById(R.id.openStorageButton);
        showUploadingMessageLinearLayout = findViewById(R.id.showUploadingMessageArea);
        menuSpinner = findViewById(R.id.menu_list);


        openStorageButton.setOnClickListener(this);
        openCameraButton.setOnClickListener(this);
        submitData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.openCameraButton) {
            this.takeImage();
        } else if (v.getId() == R.id.openStorageButton) {
            this.takeVideo();
        } else if (v.getId() == R.id.submitData) {
            sendDatToBackEnd();
        }


    }


    public void takeImage() {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        context.startActivity(intent);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    public void takeVideo() {

//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
////        context.startActivity(intent);
//        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Uri filePath = data.getData();


            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            viewImage.setImageBitmap(imageBitmap);
            uploadedFileBitmap = imageBitmap;
            showUploadingMessageLinearLayout.setVisibility(View.VISIBLE);
            Log.i("uploaded file bitmap", imageBitmap.toString());
        }
//        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
////            viewImage.setImageBitmap(getVideoFrame(FileDescriptor.in));
//            uploadedFileBitmap = imageBitmap;
//
//            showUploadingMessageLinearLayout.setVisibility(View.VISIBLE);
//            Log.i("uploaded file bitmap", imageBitmap.toString());
//        }
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getCameraPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_ACCESS_Camera);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    mMap.setMyLocationEnabled(true);
                }
            }
            case PERMISSIONS_REQUEST_ACCESS_Camera: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
            }
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "onMyLocationButtonClick", Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        this.updateMapLocation(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.updateMapLocation(location);
    }


    @Override
    public void onMyLocationChange(Location location) {
        this.updateMapLocation(location);
    }


    private void updateMapLocation(Location location) {
        mLastKnownLocation = location;

        lat = location.getLatitude();
        lng = location.getLongitude();
        mMap.clear();
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate animation = CameraUpdateFactory.newLatLngZoom(
                myLocation, 19);
        mMap.animateCamera(animation);

        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Locations"));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        menu_id = menus_ids.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @SuppressLint("LongLogTag")
    public void sendDatToBackEnd() {


        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.baseUrl + "order",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.


                        Intent intent = new Intent(ReserverationActivity.this, ReportActivity.class);
                        startActivity(intent);
                        Log.i("server response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Menus List", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map jsonPostUtility = new HashMap();
                jsonPostUtility.put("menu_id", String.valueOf(menu_id));
                jsonPostUtility.put("user_id", String.valueOf(sharedpref.getInt("id", 1)));
                jsonPostUtility.put("lat", String.valueOf(lat));
                jsonPostUtility.put("long", String.valueOf(lng));
                jsonPostUtility.put("note", NoteText.getText().toString());

//                if (uploadedFileBitmap != null) {
                jsonPostUtility.put("attachment", getFileI4Base65());
//                }


                return jsonPostUtility;
            }
        };

        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

        queue.add(stringRequest);


    }


    public String getFileI4Base65() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        uploadedFileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.i("base64 image", encodedImage);
        return encodedImage;
    }
}




