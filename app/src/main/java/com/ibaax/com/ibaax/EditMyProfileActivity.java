package com.ibaax.com.ibaax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DataAccessLayer.CountryLayer;
import Entity.AppGlobal;
import Entity.Country;
import Entity.GeoLocationModel;
import Entity.User;
import Event.IHttpResponse;
import Plugins.TextBoxHandler;
import ServiceInvoke.HttpRequest;

public class EditMyProfileActivity extends AppCompatActivity {

    private final static int LOCATION_REQUEST = 121;
    static List<Country> CountryList = new ArrayList<Country>();
    EditText txtFname;
    EditText txtLname;
    EditText txtEditPhone;
    EditText txtAboutMe;
    EditText txtCompanyName;
    EditText txtJobTitle;
    EditText txtWebsite;
    EditText txtFacebook;
    EditText txtTwitter;
    EditText txtGooglePlus;
    EditText txtSkype;
    Spinner spnEditPhncode;
    Spinner spnEditGender;
    TextView lblEditEmail;
    TextView lblEditLocation;
    TextView lblGender;
    Button btnEditEmail;
    Button btnEditLocation;

    AppPrefs pref;
    User user;
    GeoLocationModel UserAddress;
    List<String> AddressList = new ArrayList<>();
    String GenderID = "1", PhoneCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
        user = (User) getIntent().getSerializableExtra("UserInfo");
        pref = new AppPrefs(this);
        UserAddress = new GeoLocationModel();
        PhoneCode = user.MobileCode;
        findViews();
    }


    private void findViews() {
        txtFname = (EditText) findViewById(R.id.txt_fname);
        txtLname = (EditText) findViewById(R.id.txt_lname);
        txtEditPhone = (EditText) findViewById(R.id.txt_edit_phone);
        txtAboutMe = (EditText) findViewById(R.id.txt_about_me);
        txtWebsite = (EditText) findViewById(R.id.txt_website);
        txtJobTitle = (EditText) findViewById(R.id.txt_job_title);
        txtCompanyName = (EditText) findViewById(R.id.txt_company_name);
        txtFacebook = (EditText) findViewById(R.id.txt_facebook);
        txtTwitter = (EditText) findViewById(R.id.txt_twitter);
        txtGooglePlus = (EditText) findViewById(R.id.txt_google_plus);
        txtSkype = (EditText) findViewById(R.id.txt_skype);


        txtEditPhone.setText(user.Phone);
        txtWebsite.setText(user.WebAddress);
        txtJobTitle.setText(user.PositionName);
        txtAboutMe.setText(user.ProfileSummary);
        txtFacebook.setText(user.FacebookProfileAddress);
        txtTwitter.setText(user.TwitterAddress);
        txtGooglePlus.setText(user.GooglePlusAddress);
        txtSkype.setText(user.SkypeAddress);
        UserAddress.Latitude = String.valueOf(user.Latitude);
        UserAddress.Longitude = String.valueOf(user.Longitude);

        spnEditPhncode = (Spinner) findViewById(R.id.spn_edit_phncode);
        spnEditGender = (Spinner) findViewById(R.id.spn_edit_gender);

        setSpinner();

        lblEditEmail = (TextView) findViewById(R.id.lbl_edit_email);
        lblEditLocation = (TextView) findViewById(R.id.lbl_edit_Location);
        lblGender = (TextView) findViewById(R.id.lblGender);

        lblEditEmail.setText(user.EmailAddress);
        lblEditLocation.setText(user.Address);

        btnEditLocation = (Button) findViewById(R.id.btn_edit_location);
        btnEditEmail = (Button) findViewById(R.id.btn_edit_email);


        if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
            txtCompanyName.setText(user.FirstName);
            txtFname.setVisibility(View.GONE);
            txtLname.setVisibility(View.GONE);
            spnEditGender.setVisibility(View.GONE);
            lblGender.setVisibility(View.GONE);
            txtJobTitle.setVisibility(View.GONE);
        } else {
            txtFname.setText(user.FirstName);
            txtLname.setText(user.LastName);
            txtCompanyName.setText(user.CompanyName);
        }

    }

    void setSpinner() {

        final List<String> Gender = new ArrayList<>();
        Gender.add("Male");
        Gender.add("Female");
        Gender.add("Other");

        ArrayAdapter<String> Genderadp = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Gender);
        Genderadp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEditGender.setAdapter(Genderadp);
        spnEditGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    GenderID = "2";
                }
                if (i == 1) {
                    GenderID = "3";
                }
                if (i == 2) {
                    GenderID = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (CountryList.size() > 0) {
            setPhoneCodeSpinner();

        } else {

            CountryList = new CountryLayer(this).getCountry();
            setPhoneCodeSpinner();
        }


    }

    void setPhoneCodeSpinner() {
        final List<String> countryCodeList = new ArrayList<String>();
        for (int i = 0; i < CountryList.size(); i++) {
            countryCodeList.add(CountryList.get(i).PhoneCode);
        }
        ArrayAdapter<String> countryCodeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryCodeList);
        countryCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEditPhncode.setAdapter(countryCodeAdapter);
        if (user.MobileCode.length() > 0) {
            for (int i = 0; i < countryCodeList.size(); i++) {
                if (countryCodeList.get(i).equals(user.MobileCode)) {
                    spnEditPhncode.setSelection(i);
                }
            }
        }
        spnEditPhncode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PhoneCode = countryCodeList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void edtMyLocation(View view) {

        Intent intent = new Intent(this, EditMyLocationActivity.class);
        startActivityForResult(intent, LOCATION_REQUEST);
    }

    public void btnSaveUserProfile(View view) {

        if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
            String URL = AppGlobal.localHost + "/api/MCompany/EditCompanyProfile/";

            HashMap<String, String> jsonParams = new HashMap<>();

            jsonParams.put("CompanyID", TextBoxHandler.IsNullOrEmpty(pref.getCompanyID()));
            jsonParams.put("Name", TextBoxHandler.IsNullOrEmpty(txtCompanyName.getText().toString()));
            jsonParams.put("MainOfficePhoneCode", TextBoxHandler.IsNullOrEmpty(PhoneCode));
            jsonParams.put("MainOfficePhone", TextBoxHandler.IsNullOrEmpty(txtEditPhone.getText().toString()));
            jsonParams.put("Latitude", TextBoxHandler.IsNullOrEmpty(UserAddress.Latitude));
            jsonParams.put("Longitude", TextBoxHandler.IsNullOrEmpty(UserAddress.Longitude));
            //
            if (AddressList.size() > 0) {
                jsonParams.put("CountryName", TextBoxHandler.IsNullOrEmpty(UserAddress.CountryName));
                jsonParams.put("StateName", TextBoxHandler.IsNullOrEmpty(UserAddress.StateName));
                jsonParams.put("CityName", TextBoxHandler.IsNullOrEmpty(UserAddress.CityName));
                jsonParams.put("LocalityName", TextBoxHandler.IsNullOrEmpty(UserAddress.LocalityName));
                jsonParams.put("PlaceID", TextBoxHandler.IsNullOrEmpty(UserAddress.PlaceID));
                jsonParams.put("StateTicker", TextBoxHandler.IsNullOrEmpty(UserAddress.StateTicker));
                jsonParams.put("NeighborhoodName", TextBoxHandler.IsNullOrEmpty(UserAddress.NeighborhoodName));
                jsonParams.put("SuiteNo", "");
                jsonParams.put("ZipCode", TextBoxHandler.IsNullOrEmpty(UserAddress.ZipCode));
                jsonParams.put("Address", TextBoxHandler.IsNullOrEmpty(UserAddress.Address));
            }
            //
            jsonParams.put("GenderID", TextBoxHandler.IsNullOrEmpty(GenderID));
            jsonParams.put("LanguageIds", "");
            jsonParams.put("AboutTheCompany", TextBoxHandler.IsNullOrEmpty(txtAboutMe.getText().toString()));
            jsonParams.put("PersonalBlogAddress", TextBoxHandler.IsNullOrEmpty(txtWebsite.getText().toString()));
            jsonParams.put("FacebookProfileAddress", TextBoxHandler.IsNullOrEmpty(txtFacebook.getText().toString()));
            jsonParams.put("IsFacebookValidated", "false");
            jsonParams.put("TwitterAddress", TextBoxHandler.IsNullOrEmpty(txtTwitter.getText().toString()));
            jsonParams.put("CreateDate", user.CreateDate);
            jsonParams.put("CreatedByUserID", pref.getUserID());
            jsonParams.put("GooglePlusAddress", TextBoxHandler.IsNullOrEmpty(txtGooglePlus.getText().toString()));
            jsonParams.put("SkypeAddress", TextBoxHandler.IsNullOrEmpty(txtSkype.getText().toString()));

            Log.v("esty", jsonParams.toString());

            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONObject jo = new JSONObject((String) response);
                        if (jo.getString("StatusCode").equals("200")) {
                            Toast.makeText(EditMyProfileActivity.this, "Profile update successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).HttpPostProperty(jsonParams, URL);
        } else {
            String URL = AppGlobal.localHost + "/api/MProfile/EditProfile";

            HashMap<String, String> jsonParams = new HashMap<>();

            jsonParams.put("UserId", TextBoxHandler.IsNullOrEmpty(pref.getUserID()));
            jsonParams.put("FirstName", TextBoxHandler.IsNullOrEmpty(txtFname.getText().toString()));
            jsonParams.put("LastName", TextBoxHandler.IsNullOrEmpty(txtLname.getText().toString()));
            jsonParams.put("MobileCode", TextBoxHandler.IsNullOrEmpty(PhoneCode));
            jsonParams.put("Mobile", TextBoxHandler.IsNullOrEmpty(txtEditPhone.getText().toString()));
            jsonParams.put("PositionName", TextBoxHandler.IsNullOrEmpty(txtJobTitle.getText().toString()));
            jsonParams.put("Latitude", TextBoxHandler.IsNullOrEmpty(UserAddress.Latitude));
            jsonParams.put("Longitude", TextBoxHandler.IsNullOrEmpty(UserAddress.Longitude));
            jsonParams.put("ProfileSummary", TextBoxHandler.IsNullOrEmpty(txtAboutMe.getText().toString()));
            //
            if (AddressList.size() > 0) {
                jsonParams.put("CountryName", TextBoxHandler.IsNullOrEmpty(UserAddress.CountryName));
                jsonParams.put("StateName", TextBoxHandler.IsNullOrEmpty(UserAddress.StateName));
                jsonParams.put("CityName", TextBoxHandler.IsNullOrEmpty(UserAddress.CityName));
                jsonParams.put("LocalityName", TextBoxHandler.IsNullOrEmpty(UserAddress.LocalityName));
                jsonParams.put("PlaceID", TextBoxHandler.IsNullOrEmpty(UserAddress.PlaceID));
                jsonParams.put("StateTicker", TextBoxHandler.IsNullOrEmpty(UserAddress.StateTicker));
                jsonParams.put("NeighborhoodName", TextBoxHandler.IsNullOrEmpty(UserAddress.NeighborhoodName));
                jsonParams.put("SuiteNo", "");
                jsonParams.put("ZipCode", TextBoxHandler.IsNullOrEmpty(UserAddress.ZipCode));
                jsonParams.put("Address", TextBoxHandler.IsNullOrEmpty(UserAddress.Address));
            }
            //
            jsonParams.put("GenderID", TextBoxHandler.IsNullOrEmpty(GenderID));
            jsonParams.put("LanguageIds", "");
            jsonParams.put("CompanyName", TextBoxHandler.IsNullOrEmpty(txtCompanyName.getText().toString()));
            jsonParams.put("PersonalBlogAddress", TextBoxHandler.IsNullOrEmpty(txtWebsite.getText().toString()));
            jsonParams.put("FacebookProfileAddress", TextBoxHandler.IsNullOrEmpty(txtFacebook.getText().toString()));
            jsonParams.put("IsFacebookValidated", "false");
            jsonParams.put("TwitterAddress", TextBoxHandler.IsNullOrEmpty(txtTwitter.getText().toString()));
            jsonParams.put("GooglePlusAddress", TextBoxHandler.IsNullOrEmpty(txtGooglePlus.getText().toString()));
            jsonParams.put("SkypeAddress", TextBoxHandler.IsNullOrEmpty(txtSkype.getText().toString()));
            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        JSONObject jo = new JSONObject((String) response);
                        if (jo.getString("StatusCode").equals("200")) {
                            Toast.makeText(EditMyProfileActivity.this, "Profile update successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).HttpPostProperty(jsonParams, URL);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST) {
            if (resultCode == LOCATION_REQUEST) {
                AddressList.clear();
                UserAddress = (GeoLocationModel) data.getSerializableExtra("UserLocation");
                AddressList.add(UserAddress.Address);
                AddressList.add(UserAddress.ZipCode);
                AddressList.add(UserAddress.NeighborhoodName);
                AddressList.add(UserAddress.LocalityName);
                AddressList.add(UserAddress.CityName);
                AddressList.add(UserAddress.StateName);
                AddressList.add(UserAddress.CountryName);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < AddressList.size(); j++) {
                    if (AddressList.get(j).length() > 0) {
                        sb.append(AddressList.get(j) + ", ");
                    }
                }
                if (sb.toString().length() > 0) {
                    sb.delete(sb.length() - 2, sb.length());
                }
                String fulladdress = sb.toString();
                lblEditLocation.setText(fulladdress);
                if (sb.toString().length() > 0) {
                    sb.delete(0, sb.length());
                }
            }
        }
    }

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
