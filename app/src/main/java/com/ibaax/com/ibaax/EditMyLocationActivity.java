package com.ibaax.com.ibaax;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import Entity.GeoLocationModel;
import Event.IHttpResponse;
import ServiceInvoke.GetAllLocationInfoFromLatLon;
import ServiceInvoke.HttpRequest;
import UI.WorkaroundMapFragment;
import sensor.GPSTracker;

public class EditMyLocationActivity extends AppCompatActivity {

    TextView lblMyCountry;
    TextView lblMyState;
    TextView lblMyCity;
    TextView lblMyArea;
    TextView lblMyNeighbourHood;
    EditText txtMyZipcode;
    EditText txtMyAddress;
    Button btnSavemylocation;
    String Latitude, Longitude;
    ScrollView ScrlStep3;

    GoogleMap gMap;
    double CURRENT_latitude = 23.7000, CURRENT_longitude = 90.3667;
    PlaceAutocompleteFragment autocompleteFragment;
    Context context;
    GeoLocationModel LocationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Set Address");
        context = this;
        LocationAddress = new GeoLocationModel();
        findViews();
    }

    private void findViews() {
        lblMyCountry = (TextView) findViewById(R.id.lbl_myCountry);
        lblMyState = (TextView) findViewById(R.id.lbl_myState);
        lblMyCity = (TextView) findViewById(R.id.lbl_myCity);
        lblMyArea = (TextView) findViewById(R.id.lbl_myArea);
        lblMyNeighbourHood = (TextView) findViewById(R.id.lbl_myNeighbourHood);
        txtMyZipcode = (EditText) findViewById(R.id.txt_my_zipcode);
        txtMyAddress = (EditText) findViewById(R.id.txt_my_address);
        btnSavemylocation = (Button) findViewById(R.id.btn_savemylocation);

        initMap();
    }

    private void initMap() {
        ScrlStep3 = (ScrollView) findViewById(R.id.myscrl);
        GPSTracker gps = new GPSTracker(this);

        if (gps.canGetLocation() == true) {


            if (gps.getLatitude() > 0 && gps.getLongitude() > 0) {
                CURRENT_latitude = gps.getLatitude();
                CURRENT_longitude = gps.getLongitude();
                //IS_LOCATION_FOUND_FROM_GPS = true;
            }
            Log.v("esty", "Longitude traker :: " + CURRENT_latitude + " " + CURRENT_longitude);

        } else {

        }

        WorkaroundMapFragment mapFragment = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mymap));
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                ScrlStep3.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    gMap = googleMap;
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(CURRENT_latitude, CURRENT_longitude)).zoom(12).build();
                    gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    if (CURRENT_latitude > 0 && CURRENT_longitude > 0) {
                        Latitude = String.valueOf(CURRENT_latitude);
                        Longitude = String.valueOf(CURRENT_longitude);
                        getSetMapLocations(Latitude, Longitude);
                    }

                    gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition c) {
                            try {
                                final double latitude = c.target.latitude;
                                final double longitude = c.target.longitude;
                                Latitude = String.valueOf(latitude);
                                Longitude = String.valueOf(longitude);
                                getSetMapLocations(Latitude, Longitude);
                            } catch (Exception e) {

                            }

                        }
                    });
                } catch (Exception e) {

                }
            }
        });
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_myautocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                try {
                    Log.i("esty", "Place: " + place.getName());

                    String placeDetailsStr = place.getName() + "\n"
                            + place.getId() + "\n"
                            + place.getLatLng().toString() + "\n"
                            + place.getAddress() + "\n"
                            + place.getAttributions();
                    String PlaceLatLon = place.getLatLng().toString();
                    String[] LatLonArr = PlaceLatLon.split(",");
                    String[] LatArr = LatLonArr[0].split("\\(");
                    String Lat = LatArr[1];
                    String[] LonArr = LatLonArr[1].split("\\)");
                    String Lon = LonArr[0];
                    Log.i("esty", "Place: " + placeDetailsStr);
                    //txtPlaceDetails.setText(placeDetailsStr);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lon))).zoom(12).build();//zoom level was 15. changed to 19
                    gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (Exception e) {
                    Log.e("esty", "Error: " + e.getMessage());
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e("esty", "An error occurred: " + status);
            }
        });
    }

    void getSetMapLocations(final String latitude, final String longitude) {

        String URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&sensor=true";
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                new GetAllLocationInfoFromLatLon(context, (String) response, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {
                            JSONObject jo = new JSONObject((String) response);
                            LocationAddress.PlaceID = jo.getString("PlaceID");
                            LocationAddress.CountryName = jo.getString("CountryName");
                            LocationAddress.StateName = jo.getString("StateName");
                            LocationAddress.CityName = jo.getString("CityName");
                            LocationAddress.LocalityName = jo.getString("LocalityName");
                            LocationAddress.NeighborhoodName = jo.getString("NeighborhoodName");
                            LocationAddress.Address = jo.getString("Address");
                            LocationAddress.StateTicker = jo.getString("StateTicker");
                            LocationAddress.ZipCode = jo.getString("ZipCode");
                            LocationAddress.Latitude = latitude;
                            LocationAddress.Longitude = longitude;

                            lblMyCountry.setText(LocationAddress.CountryName);
                            lblMyState.setText(LocationAddress.StateName);
                            lblMyCity.setText(LocationAddress.CityName);
                            lblMyArea.setText(LocationAddress.LocalityName);
                            lblMyNeighbourHood.setText(LocationAddress.NeighborhoodName);
                            txtMyZipcode.setText(LocationAddress.ZipCode);
                            txtMyAddress.setText(LocationAddress.Address);

                        } catch (JSONException e) {
                            Log.e("esty", "JSONError: " + response);
                        }
                    }

                    @Override
                    public void RequestFailed(String response) {
                        Log.e("esty", "InnerError: " + response);
                    }
                }).execute();

            }

            @Override
            public void RequestFailed(String response) {
                Log.e("esty", "Error: " + response);
            }
        }).JSONObjectGetRequest(null, URL);

    }


    public void btnSaveUserLocation(View view) {
        LocationAddress.Address = txtMyAddress.getText().toString();
        LocationAddress.ZipCode = txtMyZipcode.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("UserLocation", (Serializable) LocationAddress);
        setResult(121, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent();
            setResult(0, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
