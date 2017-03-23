package com.ibaax.com.ibaax;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import Adapter.DropdownAdapter;
import Adapter.DropdownSettingsAdapter;
import DataAccessLayer.CurrencyTypeLayer;
import DataAccessLayer.PropertyTypeLayer;
import DataAccessLayer.UnitTypeLayer;
import Entity.AppGlobal;
import Entity.Dictunary;
import Entity.Directory;
import Entity.PropertySerialize;
import Event.IHttpResponse;
import Event.IValidation;
import JSOINParsing.parsePropertyListingType;
import Plugins.TextBoxHandler;
import ServiceInvoke.GetAllLocationInfoFromLatLon;
import ServiceInvoke.HttpRequest;
import UI.EdittextRequired;
import UI.MessageBox;
import UI.WorkaroundMapFragment;
import sensor.GPSTracker;

public class MyPropertyEditBasicInfoActivity extends AppCompatActivity {

    static List<Dictunary> PropertyTypelist = new ArrayList<>();
    static List<Dictunary> CurrencyTypeList = new ArrayList<>();
    static List<Dictunary> UnitTypeList = new ArrayList<>();
    Context context;
    PropertySerialize property;
    LinearLayout step1, lnrFromMyPropertyDetails, lnrRentType, lnrDeposit;
    RelativeLayout stepmap;

    Spinner ddlPropertyType, ddlListingType, spnPriceType, spnDepositCurrency;
    Spinner spnBuiltIn, spnLotSize, spnYearBuilt, spnRentType, spnBedNo, spnBathNo;

    //ExpandableHeightGridView gridBedRoom, gridBathRoom;

    EditText txtPrice, txtDeposit;
    EditText txtBuiltIn, txtLotSize, txtPropertyTitle, txtPropertyDesc;
    EditText txtZipCode, txtAddress, txtSuiteNo, txtBuildingNo, txtStartDate;

    RadioGroup rdoPropertyType;
    CheckBox chkContactForPrice;

    Button btnRent, btnSale;
    Button btnStepTwoNext;
    Button btnSaveLocation;

    TextView ValidateTitle, lblPriceCategoryType, lblListingType, lblDepositPrice;
    TextView lblCountryName, lblCityName, lblStateName, lblLocalityName, lblNeighbourHood;

    List<Dictunary> ListingTypelist = new ArrayList<>();
    //List<NoBedBath> NumberBedList = new ArrayList<>();
    //List<NoBedBath> NumberBathList = new ArrayList<>();
    List<Directory> RentTypeList = new ArrayList<>();
    List<String> YearList = new ArrayList<>();
    List<String> NumberBedList = new ArrayList<>();
    List<String> NumberBathList = new ArrayList<>();
    int PropertyID;
    String PropertyTransactionTypeCategoryID, PropertyListingType, NoBed = "1", NoBath = "1", Price, PriceType = "1", PropertyType = "1", PostedBy;
    String BuiltInSize, LotSize, BuiltInSizeType = "1", LotSizeType = "1", PropertyTitle, PropertyDesc, YearBuilt;
    String CountryID, StateID, CityID, AreaID, ZipCode, Address, SuiteNo, BuildingNo;
    String CreateDate, LastUpdateDate, RentType = "1";
    String CountryName, StateName, CityName, LocationName, AddressName, NeighboorHood, PlaceID, Latitude, Longitude, StateTicker;

    //NumberofBedBathAdapter BedRoomAdapter;
    //NumberofBedBathAdapter BathRoomAdapter;

    boolean FlagInfo = true;

    AppPrefs prefs;

    GoogleMap gMap;
    double CURRENT_latitude = 23.7000, CURRENT_longitude = 90.3667;
    PlaceAutocompleteFragment autocompleteFragment;
    //endregion
    int Year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_property_edit_basic_info);
        context = this;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Post A Property");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        property = (PropertySerialize) getIntent().getSerializableExtra("Property");
        prefs = new AppPrefs(this);
        init();
        initMap();


    }

    private void initMap() {
        final GPSTracker gps = new GPSTracker(this);
        final ScrollView ScrlStep3 = (ScrollView) findViewById(R.id.scrl_step3);

        UI.WorkaroundMapFragment mapFragment = ((UI.WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.postpropertymap));
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
                    if (property.latitude > 0 || property.latitude < 0) {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(property.latitude, property.longitude)).zoom(13).build();//zoom level was 15. changed to 19
                        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        Latitude = String.valueOf(property.latitude);
                        Longitude = String.valueOf(property.longitude);
                        GetSetLocations(property.latitude, property.longitude);

                    } else if (gps.canGetLocation()) {

                        if (gps.getLatitude() > 0 || gps.getLatitude() < 0) {
                            CURRENT_latitude = gps.getLatitude();
                            CURRENT_longitude = gps.getLongitude();
                        }
                        Latitude = String.valueOf(CURRENT_latitude);
                        Longitude = String.valueOf(CURRENT_longitude);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(CURRENT_latitude, CURRENT_longitude)).zoom(13).build();//zoom level was 15. changed to 19
                        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        GetSetLocations(CURRENT_latitude, CURRENT_longitude);
                    } else {

                    }

                    gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition c) {
                            try {
                                final double latitude = c.target.latitude;
                                final double longitude = c.target.longitude;
                                Latitude = String.valueOf(latitude);
                                Longitude = String.valueOf(longitude);
                                GetSetLocations(latitude, longitude);
                            } catch (Exception e) {

                            }

                        }
                    });
                } catch (Exception e) {

                }
            }
        });

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
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

    void GetSetLocations(double latitude, double longitude) {

        String URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&sensor=true";
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                new GetAllLocationInfoFromLatLon(context, (String) response, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {
                            JSONObject jo = new JSONObject((String) response);
                            PlaceID = jo.getString("PlaceID");
                            CountryName = jo.getString("CountryName");
                            StateName = jo.getString("StateName");
                            CityName = jo.getString("CityName");
                            LocationName = jo.getString("LocalityName");
                            NeighboorHood = jo.getString("NeighborhoodName");
                            AddressName = jo.getString("Address");
                            StateTicker = jo.getString("StateTicker");
                            if (jo.getString("AptSuiteNo").length() > 0) {
                                txtSuiteNo.setText(jo.getString("AptSuiteNo"));
                            }
                            if (jo.getString("BuildingName").length() > 0) {
                                txtBuildingNo.setText(jo.getString("BuildingName"));
                            }
                            ;

                            if (jo.getString("ZipCode").length() > 0) {
                                txtZipCode.setText(jo.getString("ZipCode"));
                            }
                            if (AddressName.length() > 0) {
                                txtAddress.setText(AddressName);
                            }
                            lblCountryName.setText(CountryName);
                            lblStateName.setText(StateName);
                            lblCityName.setText(CityName);
                            lblLocalityName.setText(LocationName);
                            lblNeighbourHood.setText(NeighboorHood);
                        } catch (JSONException e) {
                            Log.e("esty", "JSONError: " + response);
                        }
                    }

                    @Override
                    public void RequestFailed(String response) {
                        Log.e("esty", "InnerError: " + response);
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }

            @Override
            public void RequestFailed(String response) {
                Log.e("esty", "Error: " + response);
            }
        }).JSONObjectGetRequest(null, URL);

    }

    private void init() {
        //initValues
        PropertyID = (int) property.PropertyID;
        CountryName = property.CountryName;
        CountryID = AppGlobal.CurrentCountryID;
        StateID = "0";
        CityID = "0";
        AreaID = "0";

        PropertyTransactionTypeCategoryID = String.valueOf(property.PropertyTransactionTypeCategoryID);

        //LinearLayout Include Views
        lnrRentType = (LinearLayout) findViewById(R.id.lnr_renttype);
        lblPriceCategoryType = (TextView) findViewById(R.id.lblpostPropertyPriceTitle);
        spnRentType = (Spinner) findViewById(R.id.spn_postproperty_renttype);
        txtStartDate = (EditText) findViewById(R.id.txt_postproperty_date);
        //stepmap = (LinearLayout) findViewById(R.id.lnrsetpinit);
        step1 = (LinearLayout) findViewById(R.id.lnrsetp1);
        //step2 = (LinearLayout) findViewById(R.id.lnrsetp2);
        stepmap = (RelativeLayout) findViewById(R.id.rel_maplayout);

        //stepone Views
        //buttons
        lnrFromMyPropertyDetails = (LinearLayout) findViewById(R.id.lnrPostPropertyTransactionType);
        btnSale = (Button) findViewById(R.id.btnFromDetailSale);
        btnRent = (Button) findViewById(R.id.btnFromDetailRent);
        lblListingType = (TextView) findViewById(R.id.lbl_listing_type);
        //LinearLayout
        lnrRentType = (LinearLayout) findViewById(R.id.lnr_renttype);
        lnrDeposit = (LinearLayout) findViewById(R.id.lnr_postproperty_deposit);
        //spinners
        ddlPropertyType = (Spinner) findViewById(R.id.ddlPropertyType);
        ddlListingType = (Spinner) findViewById(R.id.ddlListingType);
        spnRentType = (Spinner) findViewById(R.id.spn_postproperty_renttype);
        spnBedNo = (Spinner) findViewById(R.id.spn_postproperty_bedno);
        spnBathNo = (Spinner) findViewById(R.id.spn_postproperty_bathno);
        lblDepositPrice = (TextView) findViewById(R.id.lblpostPropertyDeposit);
        rdoPropertyType = (RadioGroup) findViewById(R.id.radio_group_propertytype);
        //gridviews
        //gridBedRoom = (ExpandableHeightGridView) findViewById(R.id.grid_postproperty_noofbedrooms);
        //gridBathRoom = (ExpandableHeightGridView) findViewById(R.id.grid_postproperty_noofbathrooms);
        //edittext
        txtPrice = (EditText) findViewById(R.id.txt_postproperty_price);
        txtStartDate = (EditText) findViewById(R.id.txt_postproperty_date);
        txtDeposit = (EditText) findViewById(R.id.txt_postproperty_deposit);

        txtPrice.setText(String.valueOf((int) property.Price));
        Price = String.valueOf(property.Price);
        PropertyListingType = String.valueOf(property.PropertyTransactionTypeID);
        PropertyType = String.valueOf(property.PropertyTypeID);


        //steptwo Views
        //buttons
        btnStepTwoNext = (Button) findViewById(R.id.btn_postproperty_steptwo_next);
        //spinners
        spnYearBuilt = (Spinner) findViewById(R.id.spn_postproperty_yearbuilt);
        spnBuiltIn = (Spinner) findViewById(R.id.spn_postproperty_builtin);
        spnLotSize = (Spinner) findViewById(R.id.spn_postproperty_lotsize);
        spnPriceType = (Spinner) findViewById(R.id.spn_postproperty_price);
        spnDepositCurrency = (Spinner) findViewById(R.id.spn_postproperty_deposit);
        //edittext
        txtBuiltIn = (EditText) findViewById(R.id.txt_postproperty_builtin);
        txtLotSize = (EditText) findViewById(R.id.txt_postproperty_lotsize);
        txtPropertyTitle = (EditText) findViewById(R.id.txt_postproperty_propertytitle);
        txtPropertyDesc = (EditText) findViewById(R.id.txt_postproperty_desc);
        //textviews
        ValidateTitle = (TextView) findViewById(R.id.validate_title);
        chkContactForPrice = (CheckBox) findViewById(R.id.chk_cont_for_price);
        txtBuiltIn.setText(String.valueOf(property.PropertyBuildUpArea));
        txtLotSize.setText(String.valueOf(property.PropertyPlotArea));
        txtPropertyTitle.setText(property.PropertyName);
        txtPropertyDesc.setText(Html.fromHtml(property.Description).toString());

        //txtBui

        //StepMap Views
        btnSaveLocation = (Button) findViewById(R.id.btn_savelocation);

        lblCountryName = (TextView) findViewById(R.id.lbl_postpropertyCountry);
        lblStateName = (TextView) findViewById(R.id.lbl_postpropertyState);
        lblCityName = (TextView) findViewById(R.id.lbl_postpropertyCity);
        lblLocalityName = (TextView) findViewById(R.id.lbl_postpropertyArea);
        lblNeighbourHood = (TextView) findViewById(R.id.lbl_postpropertyNeighbourHood);
        //edittext
        txtZipCode = (EditText) findViewById(R.id.txt_postproperty_zipcode);
        txtAddress = (EditText) findViewById(R.id.txt_postproperty_address);
        txtSuiteNo = (EditText) findViewById(R.id.txt_postproperty_suiteno);
        txtBuildingNo = (EditText) findViewById(R.id.txt_postproperty_buildname);

        lblCountryName.setText(property.CountryName);
        lblStateName.setText(property.StateName);
        lblCityName.setText(property.CityName);
        lblLocalityName.setText(property.LocalityName);
        //lblNeighbourHood.setText(property.);

        txtZipCode.setText(property.ZipCode);
        txtAddress.setText(property.Address);
        txtSuiteNo.setText(property.AptSuiteNo);
        txtBuildingNo.setText(property.BuildingName);

        ;
        // listeners and bind
        getListingType();
        getPropertyType();
        getAllCurrency();
        getAllPropertySizeType();
        buttonClicks();
        bindGridAdapter();
        DateFormats();
        bindSpinners();


    }

    //region GetAndBind
    private void getListingType() {
        String URL = AppGlobal.localHost + "/api/Mproperty/GetPropertyTransactionTypesForDropdown?PropertyTransactionTypeCategoryID="
                + PropertyTransactionTypeCategoryID;

        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                ListingTypelist.clear();
                ListingTypelist = new parsePropertyListingType().parse((String) response, ListingTypelist);
                BindListingType();


            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);

    }

    private void BindListingType() {
        DropdownAdapter adapterProperyType = new DropdownAdapter(context, R.layout.row_postproperty_dropdown, ListingTypelist);
        ddlListingType.setAdapter(adapterProperyType);
        for (int i = 0; i < ListingTypelist.size(); i++) {
            if (ListingTypelist.get(i).ID == property.PropertyTransactionTypeID) {
                ddlListingType.setSelection(i, true);
            }
        }
        ddlListingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PropertyListingType = String.valueOf(ListingTypelist.get(i).ID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getPropertyType() {
        if (PropertyTypelist.size() > 0) {
            BindPropertyType();
        } else {
            PropertyTypelist = new PropertyTypeLayer(context).getPropertyType();
            BindPropertyType();
        }


    }

    private void BindPropertyType() {
        DropdownAdapter adapterProperyType = new DropdownAdapter(context, R.layout.row_postproperty_dropdown, PropertyTypelist);
        ddlPropertyType.setAdapter(adapterProperyType);
        for (int i = 0; i < PropertyTypelist.size(); i++) {
            if (PropertyTypelist.get(i).ID == property.PropertyTypeID) {
                ddlPropertyType.setSelection(i, true);
            }
        }
        ddlPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PropertyType = String.valueOf(PropertyTypelist.get(i).ID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getAllCurrency() {

        if (CurrencyTypeList.size() > 0) {
            BindAllCurrency();
        } else {
            CurrencyTypeList = new CurrencyTypeLayer(this).getCurrencyType();
            BindAllCurrency();
        }


    }

    private void BindAllCurrency() {
        try {
            DropdownSettingsAdapter Currencyadp = new DropdownSettingsAdapter(context, android.R.layout.simple_list_item_1, CurrencyTypeList);
            spnPriceType.setAdapter(Currencyadp);
            spnDepositCurrency.setAdapter(Currencyadp);
            spnDepositCurrency.setEnabled(false);
            for (int i = 0; i < CurrencyTypeList.size(); i++) {
                if (CurrencyTypeList.get(i).Name.equals(property.CurrencyName)) {
                    spnPriceType.setSelection(i);
                    PriceType = String.valueOf(CurrencyTypeList.get(i).ID);
                    spnDepositCurrency.setSelection(i);
                }
            }
            spnPriceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //BuildMin = stripNonDigits(MinSizeList.get(i));
                    PriceType = String.valueOf(CurrencyTypeList.get(i).ID);
                    spnDepositCurrency.setSelection(i);
                    Log.v("esty", PriceType);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {

        }

    }

    private void getAllPropertySizeType() {

        if (UnitTypeList.size() > 0) {
            BindAllPropertySizeType();
        } else {
            UnitTypeList = new UnitTypeLayer(this).getUnitType();
            BindAllPropertySizeType();
        }

    }

    private void BindAllPropertySizeType() {
        DropdownAdapter SizeTypeadp = new DropdownAdapter(context, R.layout.row_postproperty_dropdown, UnitTypeList);
        spnBuiltIn.setAdapter(SizeTypeadp);
        for (int i = 0; i < UnitTypeList.size(); i++) {
            if (UnitTypeList.get(i).Title.equals(property.PropertyBuildUpAreaSizeTypeName)) {
                spnBuiltIn.setSelection(i, true);
                BuiltInSizeType = String.valueOf(UnitTypeList.get(i).ID);
            }
        }
        spnBuiltIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //BuildMin = stripNonDigits(MinSizeList.get(i));
                BuiltInSizeType = String.valueOf(UnitTypeList.get(i).ID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnLotSize.setAdapter(SizeTypeadp);
        for (int i = 0; i < UnitTypeList.size(); i++) {
            if (UnitTypeList.get(i).Title.equals(property.PropertyPlotAreaSizeTypeName)) {
                spnBuiltIn.setSelection(i, true);
                LotSizeType = String.valueOf(UnitTypeList.get(i).ID);
            }
        }
        spnLotSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //BuildMin = stripNonDigits(MinSizeList.get(i));
                LotSizeType = String.valueOf(UnitTypeList.get(i).ID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressWarnings("deprecation")
    private void buttonClicks() {

        if (PropertyTransactionTypeCategoryID.equals("1")) {
            btnSale.setBackgroundColor(Color.parseColor("#56698F"));
            btnSale.setTextColor(Color.parseColor("#FFFFFF"));
            btnRent.setTextColor(Color.parseColor("#000000"));
            btnRent.setBackgroundResource(R.drawable.btn_white_blackborder);
            lnrRentType.setVisibility(View.GONE);
            lblListingType.setVisibility(View.VISIBLE);
            ddlListingType.setVisibility(View.VISIBLE);
            lblDepositPrice.setVisibility(View.GONE);
            lnrDeposit.setVisibility(View.GONE);
            lnrRentType.setVisibility(View.GONE);
            lblPriceCategoryType.setText("PRICE");
        } else {
            btnRent.setBackgroundColor(Color.parseColor("#56698F"));
            btnRent.setTextColor(Color.parseColor("#FFFFFF"));
            btnSale.setTextColor(Color.parseColor("#000000"));
            btnSale.setBackgroundResource(R.drawable.btn_white_blackborder);
            lnrRentType.setVisibility(View.VISIBLE);
            lblListingType.setVisibility(View.GONE);
            ddlListingType.setVisibility(View.GONE);
            lblDepositPrice.setVisibility(View.VISIBLE);
            lnrDeposit.setVisibility(View.VISIBLE);
            lnrRentType.setVisibility(View.VISIBLE);
            PropertyListingType = "7";
            lblPriceCategoryType.setText("RENT RATE");
        }

        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSale.setBackgroundColor(Color.parseColor("#56698F"));
                btnSale.setTextColor(Color.parseColor("#FFFFFF"));
                btnRent.setTextColor(Color.parseColor("#000000"));
                btnRent.setBackgroundResource(R.drawable.btn_white_blackborder);
                PropertyTransactionTypeCategoryID = "1";
                lblDepositPrice.setVisibility(View.GONE);
                lnrDeposit.setVisibility(View.GONE);
                lnrRentType.setVisibility(View.GONE);
                lblPriceCategoryType.setText("PRICE");
                lblListingType.setVisibility(View.VISIBLE);
                ddlListingType.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getListingType();
            }
        });
        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRent.setBackgroundColor(Color.parseColor("#56698F"));
                btnRent.setTextColor(Color.parseColor("#FFFFFF"));
                btnSale.setTextColor(Color.parseColor("#000000"));
                btnSale.setBackgroundResource(R.drawable.btn_white_blackborder);
                PropertyTransactionTypeCategoryID = "2";
                lnrRentType.setVisibility(View.VISIBLE);
                lblPriceCategoryType.setText("RENT RATE");
                lblListingType.setVisibility(View.GONE);
                ddlListingType.setVisibility(View.GONE);
                lblDepositPrice.setVisibility(View.VISIBLE);
                lnrDeposit.setVisibility(View.VISIBLE);
                lnrRentType.setVisibility(View.VISIBLE);
                PropertyListingType = "7";
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        Calendar now = Calendar.getInstance();
        Year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH);
        day = now.get(Calendar.DAY_OF_MONTH);
        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                txtStartDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                                Year = year;
                                month = monthOfYear;
                                day = dayOfMonth;
                            }
                        },
                        Year,
                        month,
                        day
                );
                dpd.setThemeDark(false);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        rdoPropertyType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedID = radioGroup.getCheckedRadioButtonId();
                if (selectedID == R.id.radio_residential) {
                    PropertyTypelist.clear();
                    PropertyTypelist = new PropertyTypeLayer(MyPropertyEditBasicInfoActivity.this).getPropertyType();
                    BindPropertyType();
                } else if (selectedID == R.id.radio_commercial) {
                    PropertyTypelist.clear();
                    PropertyTypelist = new PropertyTypeLayer(MyPropertyEditBasicInfoActivity.this).getPropertyTypeCommercial();
                    BindPropertyType();
                }


            }
        });

        //StepTwo
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    StepOne();

                } catch (Exception e) {

                }
            }
        });

        btnStepTwoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (validatetextfields()) {
                        Price = txtPrice.getText().toString();
                        PostedBy = "owner";
                        BuiltInSize = txtBuiltIn.getText().toString();
                        LotSize = txtLotSize.getText().toString();
                        PropertyTitle = txtPropertyTitle.getText().toString();
                        PropertyDesc = txtPropertyDesc.getText().toString();
                        ZipCode = txtZipCode.getText().toString();
                        Address = txtAddress.getText().toString();
                        SuiteNo = txtSuiteNo.getText().toString();
                        BuildingNo = txtBuildingNo.getText().toString();
                        String URL = AppGlobal.localHost + "/api/MProperty/Save";

                        HashMap<String, String> jsonParams = new HashMap<String, String>();
                        jsonParams.put("PropertyTransactionTypeCategoryID", TextBoxHandler
                                .IsNullOrEmpty(PropertyTransactionTypeCategoryID));
                        jsonParams.put("PropertyTransactionTypeID", TextBoxHandler
                                .IsNullOrEmpty(PropertyListingType));
                        jsonParams.put("DisplayId",
                                TextBoxHandler.IsNullOrEmpty(property.DisplayID));
                        jsonParams.put("PropertyTypeID", TextBoxHandler
                                .IsNullOrEmpty(PropertyType));
                        jsonParams.put("PropertyBedroomNumber", TextBoxHandler
                                .IsNullOrEmpty(NoBed));
                        jsonParams.put("PropertyBathroomNumber", TextBoxHandler
                                .IsNullOrEmpty(NoBath));
                        jsonParams.put("Price", TextBoxHandler
                                .IsNullOrEmpty(Price));
                        jsonParams.put("CurrencyID", TextBoxHandler
                                .IsNullOrEmpty(PriceType));
                        jsonParams.put("BuiltUpAreaSize", TextBoxHandler
                                .IsNullOrEmpty(BuiltInSize));
                        jsonParams.put("PropertyBuildUpAreaSizeTypeID", TextBoxHandler
                                .IsNullOrEmpty(BuiltInSizeType));
                        jsonParams.put("PropertyLotSize", TextBoxHandler
                                .IsNullOrEmpty(LotSize));
                        jsonParams.put("PropertyLotSizeTypeID", TextBoxHandler
                                .IsNullOrEmpty(LotSizeType));
                        jsonParams.put("BuiltInYear", TextBoxHandler
                                .IsNullOrEmpty(YearBuilt));
                        jsonParams.put("PropertyName", TextBoxHandler
                                .IsNullOrEmpty(PropertyTitle));
                        jsonParams.put("Description", TextBoxHandler
                                .IsNullOrEmpty(PropertyDesc));
                        jsonParams.put("Latitude", TextBoxHandler
                                .IsNullOrEmpty(Latitude));
                        jsonParams.put("Longitude", TextBoxHandler
                                .IsNullOrEmpty(Longitude));
                        jsonParams.put("CountryName", TextBoxHandler
                                .IsNullOrEmpty(CountryName));
                        jsonParams.put("StateName", TextBoxHandler
                                .IsNullOrEmpty(StateName));
                        jsonParams.put("StateTicker", TextBoxHandler
                                .IsNullOrEmpty(StateTicker));
                        jsonParams.put("CityName", TextBoxHandler
                                .IsNullOrEmpty(CityName));
                        jsonParams.put("LocalityName", TextBoxHandler
                                .IsNullOrEmpty(LocationName));
                        jsonParams.put("NeighborhoodName", TextBoxHandler
                                .IsNullOrEmpty(NeighboorHood));
                        jsonParams.put("PlaceID", TextBoxHandler
                                .IsNullOrEmpty(PlaceID));
                        jsonParams.put("ZipCode", TextBoxHandler
                                .IsNullOrEmpty(ZipCode));
                        jsonParams.put("Address", TextBoxHandler
                                .IsNullOrEmpty(Address));
                        jsonParams.put("AptSuiteNo", TextBoxHandler
                                .IsNullOrEmpty(SuiteNo));
                        jsonParams.put("BuildingName", TextBoxHandler
                                .IsNullOrEmpty(BuildingNo));
                        jsonParams.put("PostedByUserCategoryID",
                                prefs.getUserCategory());
                        jsonParams.put("PropertyID", String.valueOf(PropertyID));
                        jsonParams.put("CreateDate", TextBoxHandler
                                .IsNullOrEmpty(CreateDate));
                        jsonParams.put("CreatedByUserID", prefs.getUserID());
                        jsonParams.put("LastUpdateDate", TextBoxHandler
                                .IsNullOrEmpty(LastUpdateDate));
                        jsonParams.put("LastUpdatedByUserID", prefs.getUserID());
                        jsonParams.put("CreatedByCompanyID", prefs.getCompanyID());
                        if (PropertyTransactionTypeCategoryID.equals("2")) {
                            jsonParams.put("PropertyRentRateCategoryID", RentType);
                            jsonParams.put("PropertyRentStartDate", TextBoxHandler
                                    .IsNullOrEmpty(txtStartDate.getText().toString()));
                            jsonParams.put("Downpayment", TextBoxHandler.IsNullOrEmpty(txtDeposit.getText().toString()));
                        }
                        if (property.RecentPublishStatus.equals("Published")) {
                            jsonParams.put("PropertyPublishStatusTypeID", "2");
                        } else {
                            jsonParams.put("PropertyPublishStatusTypeID", "3");
                        }
                        Log.v("esty", "Params: " + jsonParams.toString());
                        new HttpRequest(context, new IHttpResponse() {
                            @Override
                            public void RequestSuccess(Object response) {
                                try {
                                    Log.d("esty", "PostPropertyActivity/buttonclicks/btnStepThreeSave.setOnClickListener/OnClick/RequestSucess->Success: "
                                            + response.toString());
                                    JSONObject jo = new JSONObject((String) response);
                                    if (jo.getBoolean("success")) {
                                        Toast.makeText(context, jo.getString("Message"), Toast.LENGTH_LONG).show();

                                        finish();
                                    } else {
                                        MessageBox.Show(context, jo.getString("Message"));
                                    }

                                } catch (Exception e) {
                                    Log.e("esty", "PostPropertyActivity/buttonclicks/btnStepThreeSave.setOnClickListener/OnClick/RequestSucess->Error: " + e.getMessage());
                                }
                            }

                            @Override
                            public void RequestFailed(String response) {
                                Log.e("esty", "PostPropertyActivity/buttonclicks/btnStepThreeSave.setOnClickListener/OnClick/RequestFailed->Error: " + response);
                            }
                        }).HttpPostProperty(jsonParams, URL);
                    }
                } catch (Exception e) {
                    Log.e("esty", "PostPropertyActivity/buttonclicks/btnStepThreeSave.setOnClickListener/OnClick->Error: " + e.getMessage());
                }

            }
        });
    }

    private void DateFormats() {

        Calendar c = Calendar.getInstance();
        String Year = String.valueOf(c.get(Calendar.YEAR));
        String Month = String.valueOf(c.get(Calendar.MONTH) + 1);
        String Day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

        CreateDate = property.CreateDate;
        LastUpdateDate = Day + "-" + Month + "-" + Year;

    }

    private void bindGridAdapter() {

        NumberBedList.add("Select");
        NumberBedList.add("0");
        NumberBedList.add("1");
        NumberBedList.add("2");
        NumberBedList.add("3");
        NumberBedList.add("4");
        NumberBedList.add("5");
        NumberBedList.add("6");
        NumberBedList.add("7");
        NumberBedList.add("8");
        NumberBedList.add("9");
        NumberBedList.add("10");

        ArrayAdapter<String> NoBedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NumberBedList);
        NoBedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBedNo.setAdapter(NoBedAdapter);
        spnBedNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    NoBed = NumberBedList.get(i);
                } else {
                    NoBed = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        for (int i = 0; i < NumberBedList.size(); i++) {
            if (NumberBedList.get(i).equals(String.valueOf(property.PropertyBedroomNumber))) {
                spnBedNo.setSelection(i);
            }
        }
        NumberBathList.add("Select");
        NumberBathList.add("0");
        NumberBathList.add("1");
        NumberBathList.add("2");
        NumberBathList.add("3");
        NumberBathList.add("4");
        NumberBathList.add("5");
        NumberBathList.add("6");
        NumberBathList.add("7");
        NumberBathList.add("8");
        NumberBathList.add("9");
        NumberBathList.add("10");

        ArrayAdapter<String> NoBathAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NumberBathList);
        NoBathAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBathNo.setAdapter(NoBathAdapter);
        spnBathNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    NoBath = NumberBathList.get(i);
                } else {
                    NoBath = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        for (int i = 0; i < NumberBathList.size(); i++) {
            if (NumberBathList.get(i).equals(String.valueOf(property.PropertyBathroomNumber))) {
                spnBathNo.setSelection(i);
            }
        }

        /*NumberBedList.add(new NoBedBath("1", true));
        NumberBedList.add(new NoBedBath("2", false));
        NumberBedList.add(new NoBedBath("3", false));
        NumberBedList.add(new NoBedBath("4", false));
        NumberBedList.add(new NoBedBath("5", false));
        NumberBedList.add(new NoBedBath("6", false));
        NumberBedList.add(new NoBedBath("7", false));
        NumberBedList.add(new NoBedBath("8", false));
        NumberBedList.add(new NoBedBath("9", false));
        NumberBedList.add(new NoBedBath("10", false));

        BedRoomAdapter = new NumberofBedBathAdapter(context, NumberBedList, new IEvent() {
            @Override
            public void onClick(Object obj) {
                NoBedBath bed = (NoBedBath) obj;
                for (int i = 0; i < NumberBedList.size(); i++) {
                    NumberBedList.get(i).IsSelected = false;
                }

                bed.IsSelected = true;
                NoBed = bed.Numbers;
                BedRoomAdapter.notifyDataSetChanged();
                Log.v("esty", "NoBed " + NoBed);
            }
        });
        gridBedRoom.setAdapter(BedRoomAdapter);
        for (int i = 0; i < NumberBedList.size(); i++) {
            if (NumberBedList.get(i).Numbers.equals(String.valueOf(property.PropertyBedroomNumber))) {
                NumberBedList.get(i).IsSelected = true;
                NoBed = NumberBedList.get(i).Numbers;
            } else {
                NumberBedList.get(i).IsSelected = false;
            }
        }
        BedRoomAdapter.notifyDataSetChanged();

        NumberBathList.add(new NoBedBath("1", true));
        NumberBathList.add(new NoBedBath("2", false));
        NumberBathList.add(new NoBedBath("3", false));
        NumberBathList.add(new NoBedBath("4", false));
        NumberBathList.add(new NoBedBath("5", false));
        NumberBathList.add(new NoBedBath("6", false));
        NumberBathList.add(new NoBedBath("7", false));
        NumberBathList.add(new NoBedBath("8", false));
        NumberBathList.add(new NoBedBath("9", false));
        NumberBathList.add(new NoBedBath("10", false));


        BathRoomAdapter = new NumberofBedBathAdapter(context, NumberBathList, new IEvent() {
            @Override
            public void onClick(Object obj) {
                NoBedBath bath = (NoBedBath) obj;
                for (int i = 0; i < NumberBathList.size(); i++) {
                    NumberBathList.get(i).IsSelected = false;
                }

                bath.IsSelected = true;
                NoBath = bath.Numbers;
                BathRoomAdapter.notifyDataSetChanged();
                Log.v("esty", "NoBath " + NoBath);
            }
        });

        gridBathRoom.setAdapter(BathRoomAdapter);
        for (int i = 0; i < NumberBathList.size(); i++) {
            if (NumberBathList.get(i).Numbers.equals(String.valueOf(property.PropertyBathroomNumber))) {
                NumberBathList.get(i).IsSelected = true;
                NoBath = NumberBathList.get(i).Numbers;
            } else {
                NumberBathList.get(i).IsSelected = false;
            }
        }
        BathRoomAdapter.notifyDataSetChanged();*/
    }

    private void bindSpinners() {

        RentTypeList.add(new Directory("1", "Yearly"));
        RentTypeList.add(new Directory("2", "Monthly"));
        RentTypeList.add(new Directory("4", "Weekly"));
        RentTypeList.add(new Directory("5", "Daily"));

        final List<String> DirectoryTypeList = new ArrayList<>();
        for (int i = 0; i < RentTypeList.size(); i++) {
            DirectoryTypeList.add(RentTypeList.get(i).DirectoryType);
        }
        ArrayAdapter<String> DirectoryAdp = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, DirectoryTypeList);
        DirectoryAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRentType.setAdapter(DirectoryAdp);
        spnRentType.setSelection(1, true);
        spnRentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RentType = RentTypeList.get(i).DirectoryValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Steptwo
        // SpnYear
        Calendar c = Calendar.getInstance();
        YearList.add("Select");
        for (int i = 0; i <= 100; i++) {
            YearList.add(String.valueOf(c.get(Calendar.YEAR) - i));
        }
        ArrayAdapter<String> Yearadp = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, YearList);
        Yearadp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnYearBuilt.setAdapter(Yearadp);
        spnYearBuilt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    YearBuilt = "";
                } else {
                    YearBuilt = YearList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void StepOne() {
        stepmap.setVisibility(View.GONE);
        step1.setVisibility(View.VISIBLE);
        // step2.setVisibility(View.GONE);
        FlagInfo = false;
        getSupportActionBar().setTitle("Edit Property");
    }


    private void StepMap() {
        stepmap.setVisibility(View.VISIBLE);
        //step2.setVisibility(View.GONE);
        FlagInfo = true;
        step1.setVisibility(View.GONE);
        getSupportActionBar().setTitle("Change Property Location");
    }


    private Boolean validatetextfields() {
        Boolean isValid = true;
        List<IValidation> validation = new ArrayList<IValidation>();
        validation.add(new EdittextRequired(txtPropertyTitle.getText().toString(),
                ValidateTitle, "Property title is required!"));


        for (IValidation v : validation) {
            if (v.Invoke() == false) {
                isValid = false;
            }
        }

        return isValid;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                if (FlagInfo) {
                    finish();
                } else {
                    StepMap();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
