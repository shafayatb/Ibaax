package com.ibaax.com.ibaax;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.PropertySpecificationGridAdapter;
import Entity.AppGlobal;
import Entity.PropertySpecificationData;
import Event.IHttpResponse;
import Plugins.JSONParser;
import ServiceInvoke.HttpRequest;

public class PropertySetSpecificationActivity extends AppCompatActivity {

    UI.ExpandableHeightGridView gridIndoor, gridOutdoor, gridFurnishing, gridParking, gridViews, gridLocalAmen;
    List<PropertySpecificationData> SpecificationList = new ArrayList<>();
    String PropertyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_set_specification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PropertyID = getIntent().getStringExtra("ID");
        init();
    }

    void init() {
        gridIndoor = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_property_indoor);
        gridOutdoor = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_property_outdoor);
        gridFurnishing = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_property_furnishing);
        gridParking = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_property_parking);
        gridViews = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_property_views);
        gridLocalAmen = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_property_localamen);
        getAllSpecificationData();
    }

    void getAllSpecificationData() {

        String URL = AppGlobal.localHost + "/api/MProperty/GetAllSpecificationDataByPropertyID?propertyID=" + PropertyID;

        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                try {
                    JSONArray jsonArray = new JSONArray((String) response);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PropertySpecificationData pObj = new PropertySpecificationData();

                        pObj.PropertySpecificationDataID = jsonObject.getInt("PropertySpecificationDataID");
                        pObj.CategoryName = jsonObject.getString("CategoryName");
                        pObj.PropertySpecificationCategoryID = jsonObject.getInt("PropertySpecificationCategoryID");
                        pObj.SpecificationName = jsonObject.getString("SpecificationName");
                        pObj.InputDataType = jsonObject.getInt("InputDataType");
                        pObj.Data = JSONParser.parseString(jsonObject, "Data");
                        if (pObj.Data.equals("true")) {
                            pObj.IsChecked = true;
                        }
                        SpecificationList.add(pObj);
                    }

                    BindAllSpecification();
                } catch (JSONException e) {

                }

            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);
    }

    void BindAllSpecification() {

        try {
            List<PropertySpecificationData> IndoorList = new ArrayList<>();
            List<PropertySpecificationData> OutdoorList = new ArrayList<>();
            List<PropertySpecificationData> FurnishList = new ArrayList<>();
            List<PropertySpecificationData> ParkingList = new ArrayList<>();
            List<PropertySpecificationData> ViewsList = new ArrayList<>();
            List<PropertySpecificationData> LocalAmnList = new ArrayList<>();

            for (PropertySpecificationData sp : SpecificationList) {

                if (sp.CategoryName.equals("Indoor")) {
                    IndoorList.add(sp);
                }
                if (sp.CategoryName.equals("Outdoor")) {
                    OutdoorList.add(sp);
                }
                if (sp.CategoryName.equals("Furnishing")) {
                    FurnishList.add(sp);
                }
                if (sp.CategoryName.equals("Parking")) {
                    ParkingList.add(sp);
                }
                if (sp.CategoryName.equals("View")) {
                    ViewsList.add(sp);
                }
                if (sp.CategoryName.equals("LocalAmenities")) {
                    LocalAmnList.add(sp);
                }
            }
            gridIndoor.setAdapter(new PropertySpecificationGridAdapter(this, IndoorList));
            gridIndoor.setExpanded(true);
            gridOutdoor.setAdapter(new PropertySpecificationGridAdapter(this, OutdoorList));
            gridOutdoor.setExpanded(true);
            gridFurnishing.setAdapter(new PropertySpecificationGridAdapter(this, FurnishList));
            gridFurnishing.setExpanded(true);
            gridParking.setAdapter(new PropertySpecificationGridAdapter(this, ParkingList));
            gridParking.setExpanded(true);
            gridViews.setAdapter(new PropertySpecificationGridAdapter(this, ViewsList));
            gridViews.setExpanded(true);
            gridLocalAmen.setAdapter(new PropertySpecificationGridAdapter(this, LocalAmnList));
            gridLocalAmen.setExpanded(true);

        } catch (Exception e) {

        }
    }

    public void btn_Save_Specifications(View view) {
        try {
            StringBuilder sb = new StringBuilder();
            String URL = AppGlobal.localHost + "/api/MProperty/SaveSpecification";
            sb.append("PropertyID=" + PropertyID + "&");
            for (PropertySpecificationData sp : SpecificationList) {

                if (sp.InputDataType == 4) {
                    if (sp.IsChecked) {
                        sb.append("Boolean" + String.valueOf(sp.PropertySpecificationDataID) + "=on&");
                    }
                }
                if (sp.InputDataType == 1) {
                    if (sp.Data.length() > 0) {
                        sb.append("Text" + String.valueOf(sp.PropertySpecificationDataID) + "=" + sp.Data + "&");
                    }
                }
                if (sp.InputDataType == 2) {
                    if (sp.Data.length() > 0) {
                        sb.append("Number" + String.valueOf(sp.PropertySpecificationDataID) + "=" + sp.Data + "&");
                    }
                }
            }
            if (sb.toString().length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            Log.v("esty", "Params: " + sb.toString());

            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONObject jo = new JSONObject((String) response);
                        if (jo.getBoolean("success")) {
                            Toast.makeText(PropertySetSpecificationActivity.this, jo.getString("Message")
                                    , Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (JSONException e) {

                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).HttpSavePropertySpecification(sb.toString(), URL);
            sb.delete(0, sb.length());
        } catch (Exception e) {


        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
