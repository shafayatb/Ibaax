package com.ibaax.com.ibaax;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Entity.AppGlobal;
import Entity.GeoLocationModel;
import Entity.User;
import Event.IHttpResponse;
import JSOINParsing.parseUser;
import Plugins.TextBoxHandler;
import ServiceInvoke.GetAllLocationInfoFromLatLon;
import ServiceInvoke.HttpRequest;
import UI.WorkaroundMapFragment;
import sensor.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompleteProfileAboutFragment extends Fragment {

    List<User> UserInfoList = new ArrayList<>();
    TextView lblMyCountry;
    TextView lblMyState;
    TextView lblMyCity;
    TextView lblMyArea;
    TextView CompanyNameError;
    EditText txtMyAddress;
    EditText txtAboutDesc;
    EditText txtCompanyname;
    Button btnSavemyAbout;
    LinearLayout lnrCompanyName;
    AppPrefs pref;
    String Latitude, Longitude;
    ScrollView ScrlStep3;
    GoogleMap gMap;
    double CURRENT_latitude = 23.7000, CURRENT_longitude = 90.3667;
    PlaceAutocompleteFragment autocompleteFragment;
    Context context;
    GeoLocationModel LocationAddress;

    public CompleteProfileAboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_complete_profile_about, container, false);
        context = getActivity();
        pref = new AppPrefs(context);
        LocationAddress = new GeoLocationModel();
        findViews(rootView);

        HttpGetUser();
        return rootView;
    }

    void HttpGetUser() {
        if (pref.getUserCategory().equals("1") || pref.getUserCategory().equals("6")) {
            String URL = AppGlobal.localHost + "/api/User/GetUserPersonalInfo?userId=" + pref.getUserID() + "&lang=en";
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    UserInfoList = new parseUser().parse((String) response, UserInfoList, context);
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).MakeJsonArrayReq(URL);
        }

    }

    private void findViews(View rootView) {
        ScrlStep3 = (ScrollView) rootView.findViewById(R.id.aboutscrl);
        lblMyCountry = (TextView) rootView.findViewById(R.id.lbl_myCountry);
        lblMyState = (TextView) rootView.findViewById(R.id.lbl_myState);
        lblMyCity = (TextView) rootView.findViewById(R.id.lbl_myCity);
        lblMyArea = (TextView) rootView.findViewById(R.id.lbl_myArea);
        txtMyAddress = (EditText) rootView.findViewById(R.id.txt_my_address);
        txtAboutDesc = (EditText) rootView.findViewById(R.id.txt_about_desc);
        btnSavemyAbout = (Button) rootView.findViewById(R.id.btn_savemyAbout);
        lnrCompanyName = (LinearLayout) rootView.findViewById(R.id.lnr_company_name);
        CompanyNameError = (TextView) rootView.findViewById(R.id.lbl_req_compnay_name);
        txtCompanyname = (EditText) rootView.findViewById(R.id.txt_company_name);
        if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
            lnrCompanyName.setVisibility(View.VISIBLE);
            CompanyNameError.setVisibility(View.GONE);
        }
        initMap();


        btnSavemyAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> jsonParams = new HashMap<>();
                String URL;

                if (pref.getUserCategory().equals("1") || pref.getUserCategory().equals("6")) {
                    URL = AppGlobal.localHost + "/api/MProfile/EditProfile";
                    jsonParams.put("UserId", TextBoxHandler.IsNullOrEmpty(pref.getUserID()));
                    jsonParams.put("FirstName", TextBoxHandler.IsNullOrEmpty(pref.getFirstName()));
                    jsonParams.put("LastName", TextBoxHandler.IsNullOrEmpty(pref.getLastName()));
                    jsonParams.put("MobileCode", TextBoxHandler.IsNullOrEmpty(UserInfoList.get(0).MobileCode));
                    jsonParams.put("Mobile", TextBoxHandler.IsNullOrEmpty(UserInfoList.get(0).Phone));
                    jsonParams.put("CountryName", TextBoxHandler.IsNullOrEmpty(LocationAddress.CountryName));
                    jsonParams.put("StateName", TextBoxHandler.IsNullOrEmpty(LocationAddress.StateName));
                    jsonParams.put("CityName", TextBoxHandler.IsNullOrEmpty(LocationAddress.CityName));
                    jsonParams.put("LocalityName", TextBoxHandler.IsNullOrEmpty(LocationAddress.LocalityName));
                    jsonParams.put("PlaceID", TextBoxHandler.IsNullOrEmpty(LocationAddress.PlaceID));
                    jsonParams.put("StateTicker", TextBoxHandler.IsNullOrEmpty(LocationAddress.StateTicker));
                    jsonParams.put("NeighborhoodName", TextBoxHandler.IsNullOrEmpty(LocationAddress.NeighborhoodName));
                    jsonParams.put("ZipCode", TextBoxHandler.IsNullOrEmpty(LocationAddress.ZipCode));
                    jsonParams.put("Latitude", TextBoxHandler.IsNullOrEmpty(LocationAddress.Latitude));
                    jsonParams.put("Longitude", TextBoxHandler.IsNullOrEmpty(LocationAddress.Longitude));
                    jsonParams.put("Address", TextBoxHandler.IsNullOrEmpty(LocationAddress.Address));
                    jsonParams.put("ProfileSummary", TextBoxHandler.IsNullOrEmpty(txtAboutDesc.getText().toString()));
                    jsonParams.put("GenderID", "1");
                    jsonParams.put("LanguageIds", "");
                    jsonParams.put("CompanyName", "");
                    jsonParams.put("PersonalBlogAddress", "");
                    jsonParams.put("FacebookProfileAddress", "");
                    jsonParams.put("IsFacebookValidated", "false");
                    jsonParams.put("TwitterAddress", "");
                    jsonParams.put("GooglePlusAddress", "");
                    jsonParams.put("SkypeAddress", "");
                    new HttpRequest(context, new IHttpResponse() {
                        @Override
                        public void RequestSuccess(Object response) {
                            try {
                                JSONObject jo = new JSONObject((String) response);
                                if (jo.getString("StatusCode").equals("200")) {
                                    ((CompleteProfileActivity) context).changePager();
                                }
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void RequestFailed(String response) {

                        }
                    }).HttpPostProperty(jsonParams, URL);
                } else {
                    URL = AppGlobal.localHost + "/api/MCompany/EditCompanyProfile/";
                    if (txtCompanyname.getText().toString().trim().length() > 0) {

                        jsonParams.put("CompanyId", TextBoxHandler.IsNullOrEmpty(pref.getCompanyID()));
                        jsonParams.put("Name", TextBoxHandler.IsNullOrEmpty(txtCompanyname.getText().toString()));
                        CompanyNameError.setVisibility(View.GONE);
                        jsonParams.put("CountryName", TextBoxHandler.IsNullOrEmpty(LocationAddress.CountryName));
                        jsonParams.put("StateName", TextBoxHandler.IsNullOrEmpty(LocationAddress.StateName));
                        jsonParams.put("CityName", TextBoxHandler.IsNullOrEmpty(LocationAddress.CityName));
                        jsonParams.put("LocalityName", TextBoxHandler.IsNullOrEmpty(LocationAddress.LocalityName));
                        jsonParams.put("PlaceID", TextBoxHandler.IsNullOrEmpty(LocationAddress.PlaceID));
                        jsonParams.put("StateTicker", TextBoxHandler.IsNullOrEmpty(LocationAddress.StateTicker));
                        jsonParams.put("NeighborhoodName", TextBoxHandler.IsNullOrEmpty(LocationAddress.NeighborhoodName));
                        jsonParams.put("ZipCode", TextBoxHandler.IsNullOrEmpty(LocationAddress.ZipCode));
                        jsonParams.put("Latitude", TextBoxHandler.IsNullOrEmpty(LocationAddress.Latitude));
                        jsonParams.put("Longitude", TextBoxHandler.IsNullOrEmpty(LocationAddress.Longitude));
                        jsonParams.put("Address1", TextBoxHandler.IsNullOrEmpty(LocationAddress.Address));
                        jsonParams.put("GenderID", "1");
                        jsonParams.put("LanguageIds", "");
                        jsonParams.put("AboutTheCompany", TextBoxHandler.IsNullOrEmpty(txtAboutDesc.getText().toString()));
                        //jsonParams.put("CompanyName", "");
                        jsonParams.put("PersonalBlogAddress", "");
                        jsonParams.put("FacebookProfileAddress", "");
                        jsonParams.put("IsFacebookValidated", "false");
                        jsonParams.put("TwitterAddress", "");
                        jsonParams.put("GooglePlusAddress", "");
                        jsonParams.put("SkypeAddress", "");
                        new HttpRequest(context, new IHttpResponse() {
                            @Override
                            public void RequestSuccess(Object response) {
                                try {
                                    JSONObject jo = new JSONObject((String) response);
                                    pref.setCompanyName(TextBoxHandler.IsNullOrEmpty(txtCompanyname.getText().toString()));
                                    if (jo.getString("StatusCode").equals("200")) {
                                        ((CompleteProfileActivity) context).changePager();
                                    }
                                } catch (Exception e) {

                                }
                            }

                            @Override
                            public void RequestFailed(String response) {

                            }
                        }).HttpPostProperty(jsonParams, URL);
                    } else {
                        CompanyNameError.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }

    private void initMap() {
        GPSTracker gps = new GPSTracker(context);

        if (gps.canGetLocation()) {


            if (gps.getLatitude() > 0 && gps.getLongitude() > 0) {
                CURRENT_latitude = gps.getLatitude();
                CURRENT_longitude = gps.getLongitude();
                //IS_LOCATION_FOUND_FROM_GPS = true;
            }


        } else {

        }

        WorkaroundMapFragment mapFragment = ((WorkaroundMapFragment) getChildFragmentManager().findFragmentById(R.id.aboutmap));
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
                getActivity().getFragmentManager().findFragmentById(R.id.place_about_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                try {
                    Log.e("esty", "Place" + place.getAddress().toString());
                    String PlaceLatLon = place.getLatLng().toString();
                    String[] LatLonArr = PlaceLatLon.split(",");
                    String[] LatArr = LatLonArr[0].split("\\(");
                    String Lat = LatArr[1];
                    String[] LonArr = LatLonArr[1].split("\\)");
                    String Lon = LonArr[0];
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
                            lblMyArea.setText(LocationAddress.Latitude + ", " + LocationAddress.Longitude);
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


}
