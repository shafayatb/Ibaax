package com.ibaax.com.ibaax;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import DataAccessLayer.CountryLayer;
import DataAccessLayer.CurrencyTypeLayer;
import DataAccessLayer.PropertyTypeLayer;
import DataAccessLayer.ReportTypeLayer;
import DataAccessLayer.UnitTypeLayer;
import Entity.AppGlobal;
import Entity.Country;
import Entity.CurrencyRate;
import Entity.Dictunary;
import Entity.UnitConverter;
import Event.IHttpResponse;
import JSOINParsing.parseCountry;
import Plugins.JSONParser;
import ServiceInvoke.HttpRequest;


public class MainActivity extends AppCompatActivity {
    List<Dictunary> PropertyList = new ArrayList<>();
    List<Dictunary> UnitList = new ArrayList<>();
    List<Dictunary> CurrencyList = new ArrayList<>();
    List<Dictunary> RepostTypeList = new ArrayList<>();
    List<Country> CountryList = new ArrayList<>();
    AppPrefs prefs;
    PropertyTypeLayer dbPropertyType;
    CountryLayer dbCountry;
    UnitTypeLayer dbUnit;
    CurrencyTypeLayer dbCurrency;
    ReportTypeLayer dbReport;
    LinearLayout lnrMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        prefs = new AppPrefs(this);
        lnrMain = (LinearLayout) findViewById(R.id.lnr_main_layout);
        dbPropertyType = new PropertyTypeLayer(this);
        dbCountry = new CountryLayer(this);
        dbUnit = new UnitTypeLayer(this);
        dbCurrency = new CurrencyTypeLayer(this);
        dbReport = new ReportTypeLayer(this);
        /*String dpi = String
                .valueOf(getResources().getConfiguration().smallestScreenWidthDp);
        Log.v("esty", "Screen DP: " + dpi);*/


        HttpAddCountry();
        HttpAddUnits();
        HttpAddCurrency();
        HttpAddReport();
        HttpGetCurrencyRate();
        HttpAddPropertyType();
        //region UnusedCode
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ibaax.com.ibaax",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
        // AppGlobal.setCountry();
        // String CurrentCountry=AppGlobal.getUserCountry(this);
        //prefs.setUserCountry(AppGlobal.getCurrentCountry(CurrentCountry));
        //endregion
    }


    void HttpAddCountry() {
        if (!(dbCountry.getCount() > 0)) {
            String URL = AppGlobal.localHost + "/api/Utility/GetAllCountries";

            new HttpRequest(this, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    CountryList = new parseCountry().ParseCountry((String) response, CountryList);
                    dbCountry.Insert(CountryList);
                }

                @Override
                public void RequestFailed(String response) {
                    Log.e("error", "MainActivity/onCreate/HttpAddCountry/HttpRequest/RequestFailed->Error: " + response);
                    Snackbar.make(lnrMain, "There seems to be a problem with your internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                }
            }).MakeJsonArrayReq(URL);
        }
    }

    void HttpAddUnits() {
        if (!(dbUnit.getCount() > 0)) {
            UnitList.add(new Dictunary(1, "Sq. Ft.", false));
            UnitList.add(new Dictunary(2, "Sq. Mt.", false));
            UnitList.add(new Dictunary(3, "Acres", false));
            UnitList.add(new Dictunary(4, "Hectares", false));
            dbUnit.Insert(UnitList);
        }

        AppGlobal.unitConverterList.add(new UnitConverter(1, 1, 1.0000000000));
        AppGlobal.unitConverterList.add(new UnitConverter(1, 2, 0.0929030400));
        AppGlobal.unitConverterList.add(new UnitConverter(1, 3, 0.0000229568));
        AppGlobal.unitConverterList.add(new UnitConverter(1, 4, 0.0000092903));
        AppGlobal.unitConverterList.add(new UnitConverter(2, 1, 10.7639104167));
        AppGlobal.unitConverterList.add(new UnitConverter(2, 2, 1.0000000000));
        AppGlobal.unitConverterList.add(new UnitConverter(2, 3, 0.0002471054));
        AppGlobal.unitConverterList.add(new UnitConverter(2, 4, 2.4710538147));
        AppGlobal.unitConverterList.add(new UnitConverter(3, 1, 43560.0000000000));
        AppGlobal.unitConverterList.add(new UnitConverter(3, 2, 4046.8564224000));
        AppGlobal.unitConverterList.add(new UnitConverter(3, 3, 1.0000000000));
        AppGlobal.unitConverterList.add(new UnitConverter(3, 4, 0.4046856422));
        AppGlobal.unitConverterList.add(new UnitConverter(4, 1, 107639.1041671000));
        AppGlobal.unitConverterList.add(new UnitConverter(4, 2, 10000.0000000000));
        AppGlobal.unitConverterList.add(new UnitConverter(4, 3, 2.4710538147));
        AppGlobal.unitConverterList.add(new UnitConverter(4, 4, 1.0000000000));

        prefs.setUserMeasurement("Sq. Ft.");
        prefs.setUserMeasurementType(1);
    }

    void HttpAddCurrency() {
        if (!(dbCurrency.getCount() > 0)) {
            CurrencyList.add(new Dictunary(1, "US Dollars", "USD"));
            CurrencyList.add(new Dictunary(2, "UAE Dirham", "AED"));
            CurrencyList.add(new Dictunary(5, "Australian Dollar", "AUD"));
            CurrencyList.add(new Dictunary(6, "Bangladesh Taka", "BDT"));
            CurrencyList.add(new Dictunary(7, "Bahraini Dinar", "BHD"));
            CurrencyList.add(new Dictunary(8, "Brunei Dollar", "BND"));
            CurrencyList.add(new Dictunary(9, "Brazilian Real", "BRL"));
            CurrencyList.add(new Dictunary(10, "Canadian Dollar", "CAD"));
            CurrencyList.add(new Dictunary(11, "Swiss Franc", "CHF"));
            CurrencyList.add(new Dictunary(12, "Egyptian Pound", "EGP"));
            CurrencyList.add(new Dictunary(13, "Euro", "EUR"));
            CurrencyList.add(new Dictunary(14, "French Franc", "FRF"));
            CurrencyList.add(new Dictunary(15, "UK Pound Sterling", "GBP"));
            CurrencyList.add(new Dictunary(16, "Hong Kong Dollar", "HKD"));
            CurrencyList.add(new Dictunary(17, "Israeli Sheqel", "ILS"));
            CurrencyList.add(new Dictunary(18, "Indian Rupee", "INR"));
            CurrencyList.add(new Dictunary(19, "Iraqi Dinar", "IQD"));
            CurrencyList.add(new Dictunary(20, "Iranian Rial", "IRR"));
            CurrencyList.add(new Dictunary(21, "Japan Yen", "JPY"));
            CurrencyList.add(new Dictunary(22, "Korea Won", "KRW"));
            CurrencyList.add(new Dictunary(23, "Kuwaiti Dinar", "KWD"));
            CurrencyList.add(new Dictunary(24, "Cayman Islands Dollar", "KYD"));
            CurrencyList.add(new Dictunary(25, "Lebanese Pound", "LBP"));
            CurrencyList.add(new Dictunary(26, "Sri Lankan Rupee", "LKR"));
            CurrencyList.add(new Dictunary(27, "Mexican Peso", "MXP"));
            CurrencyList.add(new Dictunary(28, "Malaysian Ringgit", "MYR"));
            CurrencyList.add(new Dictunary(29, "Nigeria Naira", "NGN"));
            CurrencyList.add(new Dictunary(30, "Nepalese Rupee", "NPR"));
            CurrencyList.add(new Dictunary(31, "New Zealand Dollar", "NZD"));
            CurrencyList.add(new Dictunary(32, "Omani Rial", "OMR"));
            CurrencyList.add(new Dictunary(33, "Philippine Peso", "PHP"));
            CurrencyList.add(new Dictunary(35, "Qatari Rial", "QAR"));
            CurrencyList.add(new Dictunary(36, "Russian Ruble", "RUB"));
            CurrencyList.add(new Dictunary(37, "Saudi Riyal", "SAR"));
            CurrencyList.add(new Dictunary(38, "Singapore Dollar", "SGD"));
            CurrencyList.add(new Dictunary(40, "Thailand Baht", "THB"));
            CurrencyList.add(new Dictunary(41, "Turkish Lira", "TRY"));
            CurrencyList.add(new Dictunary(42, "Rand South Africa", "ZAR"));
            CurrencyList.add(new Dictunary(44, "Pakistani Rupee", "PKR"));
            CurrencyList.add(new Dictunary(45, "Algerian Dinar", "DZD"));
            CurrencyList.add(new Dictunary(46, "Jordan Dinar", "JO"));

            dbCurrency.Insert(CurrencyList);
        }
    }

    void HttpAddReport() {
        if (!(dbReport.getCount() > 0)) {
            RepostTypeList.add(new Dictunary(1002, "Property Sold/ Rented out", false));
            RepostTypeList.add(new Dictunary(1003, "Wrong Location Info", false));
            RepostTypeList.add(new Dictunary(1004, "Fake Owner- Report as Broker", false));
            RepostTypeList.add(new Dictunary(1005, "Wrong Contact Information", false));
            RepostTypeList.add(new Dictunary(1006, "Wrong Pricing", false));
            RepostTypeList.add(new Dictunary(1007, "Misleading Images", false));
            RepostTypeList.add(new Dictunary(1008, "Other", false));

            dbReport.Insert(RepostTypeList);
        }
    }

    void HttpGetCurrencyRate() {

        String URL = AppGlobal.localHost + "/api/MCurrency/GetUpdateCurrencyRate";

        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONArray jsonArray = new JSONArray((String) response);
                    int count = jsonArray.length();
                    for (int i = 0; i < count; i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        CurrencyRate rate = new CurrencyRate();
                        rate.SecondaryCurrencyID = JSONParser.parseInt(jo, "SecondaryCurrencyID");
                        rate.SecondaryCurrencyTicker = JSONParser.parseString(jo, "SecondaryCurrencyTicker");
                        rate.SecondaryCurrencyIcon = JSONParser.parseString(jo, "SecondaryCurrencyIcon");
                        rate.Rate = JSONParser.parseDouble(jo, "Rate");
                        AppGlobal.currencyRateList.add(rate);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void RequestFailed(String response) {
                Log.e("error", "MainActivity/onCreate/HttpAddCountry/HttpRequest/RequestFailed->Error: " + response);
                Snackbar.make(lnrMain, "There seems to be a problem with your internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
            }
        }).MakeJsonArrayReq(URL);

    }

    void HttpAddPropertyType() {
        if (dbPropertyType.getCount() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (prefs.getFirstInstall()) {
                        Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, NavigationDrawerMainActivity.class);
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                    }

                }
            }, 1000);
        } else {

            PropertyList.add(new Dictunary(5, "House", "house", "Residential"));
            PropertyList.add(new Dictunary(7, "Townhouse", "townhouse", "Residential"));
            PropertyList.add(new Dictunary(9, "Apartment", "apartment", "Residential"));
            PropertyList.add(new Dictunary(10, "Multi-Family", "mfamily", "Residential"));
            PropertyList.add(new Dictunary(12, "Condo", "condo", "Residential"));
            PropertyList.add(new Dictunary(14, "Land/Plot", "land-plot", "Residential"));
            PropertyList.add(new Dictunary(15, "Others", "rothers", "Residential"));
            PropertyList.add(new Dictunary(20, "Land", "land", "Commercial"));
            PropertyList.add(new Dictunary(21, "Office", "office", "Commercial"));
            PropertyList.add(new Dictunary(22, "Retail", "retail", "Commercial"));
            PropertyList.add(new Dictunary(50, "Others", "cothers", "Commercial"));
            //PropertyList.add(new Dictunary(6, "Villa Roof", ""));
            //PropertyList.add(new Dictunary(8, "Farm/Ranch", ""));
            //PropertyList.add(new Dictunary(10, "Service Apartment", ""));
            //PropertyList.add(new Dictunary(11, "Studio Apartment", ""));
            //PropertyList.add(new Dictunary(12, "Penthouse", ""));
            // PropertyList.add(new Dictunary(13, "Duplex Apartment", ""));
            //PropertyList.add(new Dictunary(14, "Residential", ""));
            //PropertyList.add(new Dictunary(20, "Commercial Land", ""));
            //PropertyList.add(new Dictunary(24, "Hotel/Resorts", ""));
            //PropertyList.add(new Dictunary(25, "Commercial Building", ""));
            //PropertyList.add(new Dictunary(50, "Residential Land", ""));

            dbPropertyType.Insert(PropertyList);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (prefs.getFirstInstall()) {
                        Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, NavigationDrawerMainActivity.class);
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                    }

                }
            }, 1000);


        }


    }


}
