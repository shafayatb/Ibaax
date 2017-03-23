package com.ibaax.com.ibaax;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import DataAccessLayer.CountryLayer;
import Entity.AppGlobal;
import Entity.Country;
import Event.IHttpResponse;
import ServiceInvoke.HttpRequest;

public class MessageActivity extends AppCompatActivity {

    static List<Country> CountryList = new ArrayList<>();
    EditText txtName, txtPhone, txtEmail, txtMsg;
    AppPrefs pref;
    String PropertyName, PropertyAddress, PropertyDisplayID, ContactID, AgentEmail;
    long PropertyID;
    ProgressDialog progressDialog;
    Spinner countryCodeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtName = (EditText) findViewById(R.id.txt_Message_Name);
        txtPhone = (EditText) findViewById(R.id.txt_Message_Phone);
        txtEmail = (EditText) findViewById(R.id.txt_Message_Email);
        txtMsg = (EditText) findViewById(R.id.txt_Message_Msg);
        if (CountryList.size() > 0) {
            BindSpinners();
        } else {
            CountryList = new CountryLayer(this).getCountry();
            BindSpinners();
        }
        pref = new AppPrefs(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait.");

        PropertyName = getIntent().getStringExtra("Name");
        PropertyAddress = getIntent().getStringExtra("Address");
        PropertyDisplayID = getIntent().getStringExtra("DisplayID");
        ContactID = getIntent().getStringExtra("ContactID");
        AgentEmail = getIntent().getStringExtra("AgentEmail");
        PropertyID = getIntent().getLongExtra("ID", 0);

        txtMsg.setText("Hello, I found your property listed on iBaax Marketplace. Please send me more information about " +
                PropertyName + " -ID: " + String.valueOf(PropertyDisplayID) + " Address: " + PropertyAddress);

        if (pref.gettOAuthToken().length() > 0) {
            txtName.setText(pref.getName());
            txtPhone.setText(pref.getUserPhone());
            txtEmail.setText(pref.getEmail());
        }

        txtMsg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.txt_Message_Msg) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

    }

    public void BindSpinners() {
        try {

            countryCodeSpinner = (Spinner) findViewById(R.id.spn_country_code);


            final List<String> countryCodeList = new ArrayList<>();

            for (int i = 0; i < CountryList.size(); i++) {
                countryCodeList.add(CountryList.get(i).PhoneCode + " (" + CountryList.get(i).CountryTicker + ")");
            }
            /////////Country Code DropDown/////////////////


            ArrayAdapter<String> countryCodeAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, countryCodeList);
            countryCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countryCodeSpinner.setAdapter(countryCodeAdapter);

            if (countryCodeList.size() > 0) {
                countryCodeSpinner.setSelection(0, true);
                View countryView = countryCodeSpinner.getSelectedView();
                ((TextView) countryView).setTextColor(Color.parseColor("#000000"));
            }

            countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ((TextView) view).setTextColor(Color.parseColor("#000000"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            countryCodeSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    return false;
                }
            });
            /////////////////////////

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


    public void btnSendMessage_click(View view) {
        JSONObject js = new JSONObject();
        try {
            progressDialog.show();
            String[] CodeArray = countryCodeSpinner.getSelectedItem().toString().split(" ");
            String SelectedCountryCode = CodeArray[0];

            js.put("FirstName", txtName.getText().toString());
            js.put("MobileNo", txtPhone.getText().toString());
            js.put("MobileCode", SelectedCountryCode);
            js.put("Email", txtEmail.getText().toString());
            js.put("Notes", txtMsg.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("esty", "Json Object: " + js.toString());
        String url = AppGlobal.localHost + "/api/MProperty/SendMailToContact?id=" +
                ContactID + "&emails=" + AgentEmail + "&primaryagentid=" + ContactID +
                "&primaryEmail=" + AgentEmail + "&propertyId=" + PropertyID;
        //Log.v("esty", "URL: " + url);
        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                Log.v("esty", "MessageActivity/btnSendMessage_click/HttpRequest/RequestSuccess-> Success: "
                        + response);
                try {
                    JSONObject jo = new JSONObject(response.toString());

                    if (jo.getBoolean("success")) {
                        Toast.makeText(MessageActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                        finish();
                    } else {
                        progressDialog.hide();
                        Toast.makeText(MessageActivity.this, jo.getString("data"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                }

            }

            @Override
            public void RequestFailed(String response) {
                Log.v("esty", "MessageActivity/btnSendMessage_click/HttpRequest/RequestFailed-> Error: "
                        + response);
            }
        }).JSONObjectRequest(js, url);


    }


}
