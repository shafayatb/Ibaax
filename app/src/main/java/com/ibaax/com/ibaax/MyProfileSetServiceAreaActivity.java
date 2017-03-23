package com.ibaax.com.ibaax;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.ServiceAreaAdapter;
import Entity.AppGlobal;
import Entity.Dictunary;
import Entity.GeoLocationModel;
import Event.IHttpResponse;
import Plugins.HelperFunctions;
import Plugins.JSONParser;
import ServiceInvoke.GetAllLocationInfoFromLatLon;
import ServiceInvoke.HttpRequest;
import UI.GridSpacingItemDecoration;

public class MyProfileSetServiceAreaActivity extends AppCompatActivity {

    PlaceAutocompleteFragment autocompleteFragment;
    RecyclerView recyclerServiceArea;
    List<Dictunary> ServiceAreaList = new ArrayList<>();
    AppPrefs prefs;
    ServiceAreaAdapter adapter;
    String GetURL, SaveURL;
    String ParamID, ParamValue, IDKey;

    //AutocompleteFilter typeFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_set_service_area);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = new AppPrefs(this);

        recyclerServiceArea = (RecyclerView) findViewById(R.id.recycle_service_areas);

        if (autocompleteFragment == null) {
            autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_service_area_autocomplete_fragment);
        }
        /*typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TY)
                .build();*/

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                try {
                    Log.i("esty", "Place: " + place.toString());
                    String URL = "http://maps.googleapis.com/maps/api/geocode/json?address=" + Uri.encode(place.getAddress().toString())
                            + "&sensor=true";

                    new HttpRequest(MyProfileSetServiceAreaActivity.this, new IHttpResponse() {
                        @Override
                        public void RequestSuccess(Object response) {

                            new GetAllLocationInfoFromLatLon(MyProfileSetServiceAreaActivity.this, (String) response,
                                    new IHttpResponse() {
                                        @Override
                                        public void RequestSuccess(Object response) {
                                            try {
                                                JSONObject jo = new JSONObject((String) response);
                                                GeoLocationModel LocationAddress = new GeoLocationModel();
                                                LocationAddress.CountryName = jo.getString("CountryName");
                                                LocationAddress.StateName = jo.getString("StateName");
                                                LocationAddress.CityName = jo.getString("CityName");
                                                LocationAddress.LocalityName = jo.getString("LocalityName");
                                                LocationAddress.StateTicker = jo.getString("StateTicker");
                                                LocationAddress.NeighborhoodName = jo.getString("NeighborhoodName");

                                                HttpAddServiceArea(LocationAddress);

                                            } catch (JSONException e) {

                                            }
                                        }

                                        @Override
                                        public void RequestFailed(String response) {

                                        }
                                    }).execute();

                        }

                        @Override
                        public void RequestFailed(String response) {

                        }
                    }).JSONObjectGetRequest(null, URL);

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

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerServiceArea.setLayoutManager(mLayoutManager);
        recyclerServiceArea.addItemDecoration(new GridSpacingItemDecoration(1, HelperFunctions.dpToPx(this, 5), true));
        recyclerServiceArea.setItemAnimator(new DefaultItemAnimator());

        if (prefs.getUserCategory().equals("2") || prefs.getUserCategory().equals("4")) {
            GetURL = AppGlobal.localHost + "/api/MCompany/GetCompanyAreas?companyID="
                    + prefs.getCompanyID() + "&lang=en";
            SaveURL = AppGlobal.localHost + "/api/MCompany/AddCompanyServiceArea/";
            ParamID = "CompanyID";
            ParamValue = prefs.getCompanyID();
            IDKey = "CompanyPreferredWorkLocationID";
        } else {
            GetURL = AppGlobal.localHost + "/api/User/GetMyServiceArea?contactId=" +
                    prefs.getContactID() + "&lang='en'";
            SaveURL = AppGlobal.localHost + "/api/MContacts/AddContactServiceArea";
            ParamID = "ContactID";
            ParamValue = prefs.getContactID();
            IDKey = "ContactPreferredWorkLocationID";
        }


        HttpGetServiceArea();
    }


    void HttpAddServiceArea(GeoLocationModel geoLocationModel) {

        HashMap<String, String> jsonParams = new HashMap<>();
        jsonParams.put(ParamID, ParamValue);
        jsonParams.put("CountryName", geoLocationModel.CountryName);
        jsonParams.put("State.StateTicker", geoLocationModel.StateTicker);
        jsonParams.put("CreatedByCompanyID", prefs.getCompanyID());
        jsonParams.put("StateName", geoLocationModel.StateName);
        jsonParams.put("CityName", geoLocationModel.CityName);
        if (geoLocationModel.NeighborhoodName.length() > 0) {
            jsonParams.put("LocalityName", geoLocationModel.NeighborhoodName);
        } else {
            jsonParams.put("LocalityName", geoLocationModel.LocalityName);
        }

        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONArray jsonArray = new JSONArray((String) response);

                    if (jsonArray.length() > 0) {
                        ServiceAreaList.clear();
                        HttpGetServiceArea();
                        autocompleteFragment.setText("");
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).HttpPostProperty(jsonParams, SaveURL);

    }

    void HttpGetServiceArea() {

        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                try {
                    JSONArray array = new JSONArray((String) response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        Dictunary d = new Dictunary();
                        d.ID = JSONParser.parseInt(jo, IDKey);
                        d.Title = JSONParser.parseString(jo, "ServiceAreas");
                        ServiceAreaList.add(d);

                    }
                    if (ServiceAreaList.size() > 0) {
                        adapter = new ServiceAreaAdapter(MyProfileSetServiceAreaActivity.this,
                                ServiceAreaList);
                        recyclerServiceArea.setAdapter(adapter);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(GetURL);

    }

    /**
     * Converting dp to pixel
     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {

            finish();
            return true;

        }


        return super.onOptionsItemSelected(item);
    }


}
