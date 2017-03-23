package com.ibaax.com.ibaax;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import Adapter.DropdownAdapter;
import DataAccessLayer.CurrencyTypeLayer;
import DataAccessLayer.UnitTypeLayer;
import Entity.AppGlobal;
import Entity.Dictunary;

public class MySettingsFragment extends Fragment {

    static List<Dictunary> CurrencyTypeList = new ArrayList<>();
    static List<Dictunary> UnitTypeList = new ArrayList<>();
    List<Dictunary> languagelist = new ArrayList<>();
    AppPrefs prefs;
    Spinner spnLanguage, spnCurrency, spnMeasurement;
    Button btnTerms, btnSubs, btnChnPass, btnLogout, btnFaq;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_my_settings, container, false);
        context = getActivity();
        prefs = new AppPrefs(context);
        init(rootView);

        BindLanguage();
        getAllCurrency();
        getAllPropertySizeType();
        return rootView;
    }

    void init(View rootView) {

        spnLanguage = (Spinner) rootView.findViewById(R.id.spnSettingsLanguage);
        spnCurrency = (Spinner) rootView.findViewById(R.id.spnSettingsCurrency);
        spnMeasurement = (Spinner) rootView.findViewById(R.id.spnSettingsMesurement);

        btnTerms = (Button) rootView.findViewById(R.id.btn_terms);
        btnFaq = (Button) rootView.findViewById(R.id.btn_faq);
        btnSubs = (Button) rootView.findViewById(R.id.btn_subs);
        btnChnPass = (Button) rootView.findViewById(R.id.btn_cng_pass);
        btnLogout = (Button) rootView.findViewById(R.id.btn_logout);

        if (prefs.gettOAuthToken().length() == 0) {
            btnChnPass.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }

        btnChnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(context, LoginActivity.class);
                prefs.setOAuthToken("");
                prefs.setUserID("0");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //((NavigationDrawerMainActivity) context).finish();
            }
        });

        btnTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebPageActivity.class);
                intent.putExtra("Title", "Terms & Conditions");
                intent.putExtra("Content", "/terms-of-service");
                startActivity(intent);
            }
        });

        btnFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebPageActivity.class);
                intent.putExtra("Title", "FAQ");
                intent.putExtra("Content", "/SupportManagement/FAQ/FAQ");
                startActivity(intent);
            }
        });
    }


    private void getAllCurrency() {

        if (CurrencyTypeList.size() > 0) {
            BindAllCurrency();
        } else {
            CurrencyTypeList = new CurrencyTypeLayer(context).getCurrencyType();
            BindAllCurrency();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefs.gettOAuthToken().length() == 0) {
            btnChnPass.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void BindAllCurrency() {
        try {
            DropdownAdapter Currencyadp = new DropdownAdapter(context, R.layout.row_postproperty_dropdown, CurrencyTypeList);
            spnCurrency.setAdapter(Currencyadp);
            if (AppGlobal.CurrentCountryTicker != null) {
                for (int i = 0; i < CurrencyTypeList.size(); i++) {
                    if (CurrencyTypeList.get(i).Name.toLowerCase().contains(AppGlobal.CurrentCountryTicker)) {
                        spnCurrency.setSelection(i, true);
                    }
                }
            }
            spnCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    prefs.setUserCurrency(CurrencyTypeList.get(i).Name);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            Log.e("error", "Currency Error: " + e.getMessage());
        }

    }

    private void getAllPropertySizeType() {

        if (UnitTypeList.size() > 0) {
            BindAllPropertySizeType();
        } else {
            UnitTypeList = new UnitTypeLayer(context).getUnitType();
            BindAllPropertySizeType();
        }

    }

    private void BindAllPropertySizeType() {
        DropdownAdapter SizeTypeadp = new DropdownAdapter(context, R.layout.row_postproperty_dropdown, UnitTypeList);
        spnMeasurement.setAdapter(SizeTypeadp);
        spnMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.setUserMeasurement(UnitTypeList.get(i).Title);
                prefs.setUserMeasurementType(UnitTypeList.get(i).ID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    private void BindLanguage() {

        Dictunary d1 = new Dictunary();
        d1.ID = 1;
        d1.Title = "English";

        Dictunary d2 = new Dictunary();
        d2.ID = 2;
        d2.Title = "Arabic";
        languagelist.add(d1);
        languagelist.add(d2);
        DropdownAdapter adapterProperyType = new DropdownAdapter(context, R.layout.row_postproperty_dropdown, languagelist);
        spnLanguage.setAdapter(adapterProperyType);
        spnLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  pref.setUserCountry(AppGlobal.countryList.get(i).CountryID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
