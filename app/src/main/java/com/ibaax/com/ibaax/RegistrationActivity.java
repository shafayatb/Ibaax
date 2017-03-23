package com.ibaax.com.ibaax;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.iangclifton.android.floatlabel.FloatLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.DropdownAdapter;
import DataAccessLayer.CountryLayer;
import Entity.AppGlobal;
import Entity.Country;
import Entity.Dictunary;
import Event.IHttpResponse;
import Event.IValidation;
import ServiceInvoke.HttpLoginFromRegister;
import ServiceInvoke.HttpRequest;
import UI.EdittextRequired;
import UI.MessageBox;
import UI.PasswordLengthValidation;

public class RegistrationActivity extends AppCompatActivity {

    static List<Country> CountryList = new ArrayList<>();
    static List<Dictunary> UserTypeList = new ArrayList<>();
    FloatLabel firstName, lastName, email, password;
    CheckBox Agreement;
    EditText phoneNumber;
    Spinner countryCodeSpinner, userTypeSpinner;
    Spinner countrySpinner;
    Button SignUp;
    ImageView imgCancel, imgLogo;
    String SelectedCountryID, UserTypeID, IsRealEstateBusiness;
    TextView ValidateFirstName, ValidateLastName, ValidatePhnNo, ValidateEmail, ValidatePassword;

    IHttpResponse listener = new IHttpResponse() {
        @Override
        public void RequestSuccess(Object response) {
            new HttpLoginFromRegister(RegistrationActivity.this).LoginFromRegister(email.getEditText().getText().toString(),
                    password.getEditText().getText().toString());

        }

        @Override
        public void RequestFailed(String response) {
            if (response.equals("406")) {
                MessageBox.Show(RegistrationActivity.this, "Email address already exists.");
            } else {
                MessageBox.Show(RegistrationActivity.this, "Sorry, there seems to be a problem right now. Please try again later.");
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();
        init();
    }

    private void init() {

        firstName = (FloatLabel) findViewById(R.id.txt_first_Name);
        lastName = (FloatLabel) findViewById(R.id.txt_last_Name);
        email = (FloatLabel) findViewById(R.id.txt_email);
        password = (FloatLabel) findViewById(R.id.txt_password);
        Agreement = (CheckBox) findViewById(R.id.checkBox);
        phoneNumber = (EditText) findViewById(R.id.txt_phoneNumber);
        SignUp = (Button) findViewById(R.id.btn_Sign_Up);


        imgCancel = (ImageView) findViewById(R.id.img_sign_up_cross);
        imgLogo = (ImageView) findViewById(R.id.img_sign_up_logo);

        ValidateFirstName = (TextView) findViewById(R.id.validate_Fname);
        ValidateLastName = (TextView) findViewById(R.id.validate_Lname);
        ValidatePhnNo = (TextView) findViewById(R.id.validate_phnNo);
        ValidateEmail = (TextView) findViewById(R.id.validate_Email);
        ValidatePassword = (TextView) findViewById(R.id.validate_password);


        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(RegistrationActivity.this, NavigationDrawerMainActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, NavigationDrawerMainActivity.class);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                NavigationDrawerMainActivity.LastSelectedTab = 0;
                finish();
            }
        });

        if (Agreement.isChecked()) {
            SignUp.setEnabled(true);
        } else {
            SignUp.setEnabled(false);
        }

        ////////////Country API Request////////
        if (CountryList.size() > 0) {
            BindSpinners();

        } else {

            CountryList = new CountryLayer(this).getCountry();
            BindSpinners();
        }
        //////////////////////////////////////


    }

    public void BindSpinners() {
        try {
            countrySpinner = (Spinner) findViewById(R.id.spn_resistration_malefemale);
            countryCodeSpinner = (Spinner) findViewById(R.id.spn_country_code);
            userTypeSpinner = (Spinner) findViewById(R.id.spn_registration_usertype);


            final List<String> countryList = new ArrayList<>();
            final List<String> countryIDList = new ArrayList<>();
            final List<String> countryCodeList = new ArrayList<>();

            for (int i = 0; i < CountryList.size(); i++) {
                countryList.add(CountryList.get(i).Name);
                countryIDList.add(CountryList.get(i).CountryID);
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
                    imm.hideSoftInputFromWindow(firstName.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(lastName.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(phoneNumber.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                    return false;
                }
            });
            /////////////////////////

            /////////////Country DropDown//////////////


            ArrayAdapter<String> adp = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, countryList);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countrySpinner.setAdapter(adp);
            if (countryList.size() > 0) {
                for (int i = 0; i < countryList.size(); i++) {
                    if (countryList.get(i).equals(AppGlobal.CurrentCountryName)) {
                        countrySpinner.setSelection(i, true);
                        countryCodeSpinner.setSelection(i, true);
                        SelectedCountryID = countryIDList.get(countrySpinner.getSelectedItemPosition());
                        View countryView = countrySpinner.getSelectedView();
                        ((TextView) countryView).setTextColor(Color.parseColor("#000000"));
                        break;
                    }
                }


            }

            countrySpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(firstName.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(lastName.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(phoneNumber.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                    return false;
                }
            });

            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SelectedCountryID = countryIDList.get(countrySpinner.getSelectedItemPosition());
                    countryCodeSpinner.setSelection(i);
                    ((TextView) view).setTextColor(Color.parseColor("#000000"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            //////////////////


            if (UserTypeList.size() > 0) {
                BindUserType();
            } else {
                UserTypeList.add(new Dictunary(3, "Buyer", false));
                UserTypeList.add(new Dictunary(6, "Owner", true));
                UserTypeList.add(new Dictunary(1, "Agent", true));
                UserTypeList.add(new Dictunary(2, "Real Estate Brokerage", true));
                UserTypeList.add(new Dictunary(4, "Builder/Developer", true));

                BindUserType();
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    void BindUserType() {

        DropdownAdapter adapterUserType = new DropdownAdapter(this, R.layout.row_postproperty_dropdown, UserTypeList);
        userTypeSpinner.setAdapter(adapterUserType);
        userTypeSpinner.setSelection(2);
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UserTypeID = String.valueOf(UserTypeList.get(i).ID);
                IsRealEstateBusiness = String.valueOf(UserTypeList.get(i).IsSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void btnSignIn_click(View view) {

        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    public void btnRegistration_click(View view) {
        try {
            if (validatetextfields()) {
                if (Validate()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText().toString()).matches()
                            && Patterns.PHONE.matcher(phoneNumber.getText().toString()).matches()) {
                        try {
                            ValidateFirstName.setText("");
                            ValidateLastName.setText("");
                            ValidatePhnNo.setText("");
                            ValidateEmail.setText("");
                            ValidatePassword.setText("");
                            String[] CodeArray = countryCodeSpinner.getSelectedItem().toString().split(" ");
                            String SelectedCountryCode = CodeArray[0];
                            String URL = AppGlobal.localHost + "/api/user/register";
                            final Map<String, String> jsonParams = new HashMap<>();

                            jsonParams.put("FirstName", firstName.getEditText().getText().toString());
                            jsonParams.put("LastName", lastName.getEditText().getText().toString());
                            jsonParams.put("Mobile", phoneNumber.getText().toString());
                            jsonParams.put("Email", email.getEditText().getText().toString());
                            jsonParams.put("Password", password.getEditText().getText().toString());
                            jsonParams.put("IsRealEstateUser", "true");
                            jsonParams.put("CountryID", SelectedCountryID);
                            jsonParams.put("PhoneCode", SelectedCountryCode);
                            jsonParams.put("UserCategoryIDs", UserTypeID);
                            jsonParams.put("IsRealEstateBusiness", IsRealEstateBusiness);
                            Log.v("esty", jsonParams.toString());
                            new HttpRequest(this, listener).HttpPost(jsonParams, URL);
                        } catch (Exception e) {
                            MessageBox.Show(this, "Error: " + e.getMessage());
                        }
                    } else {
                        ValidateEmail.setText(R.string.content_error_invalid_email);
                        ValidatePhnNo.setText(R.string.content_error_invalid_phone);
                    }
                } else {
                    MessageBox.Show(this, "Please click on I agree to the terms of services.");
                }
            }
        } catch (Exception e) {
            MessageBox.Show(this, "Error: " + e.getMessage());
        }
    }

    private boolean Validate() {
        if (!Agreement.isChecked()) {
            return false;
        } else {
            return true;
        }

    }

    private Boolean validatetextfields() {
        Boolean isValid = true;
        List<IValidation> validation = new ArrayList<>();
        validation.add(new EdittextRequired(firstName.getEditText().getText().toString(),
                ValidateFirstName, "FirstName field is required"));
        validation.add(new EdittextRequired(lastName.getEditText().getText().toString(),
                ValidateLastName, "LastName field is required"));
        validation.add(new EdittextRequired(phoneNumber.getText().toString(),
                ValidatePhnNo, "Phone Number field is required"));
        validation.add(new EdittextRequired(email.getEditText().getText().toString(),
                ValidateEmail, "Email field is required"));
        validation.add(new EdittextRequired(password.getEditText().getText().toString(),
                ValidatePassword, "Password field is required"));
        // validation.add(new EdittextRequired(txtLastName.getText().toString(),
        // lblLastName, "Last Name field is required"));

        validation
                .add(new PasswordLengthValidation(password.getEditText().getText().toString(), ValidatePassword,
                        "Password length must be atleast 6 characters"));


        for (IValidation v : validation) {
            if (!v.Invoke()) {
                isValid = false;
            }
        }

        return isValid;
    }


}
