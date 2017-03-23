package Entity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import com.ibaax.com.ibaax.AppPrefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by S.R Rain on 1/9/2016.
 */
public class AppGlobal {

    public static String Name;
    public static List<Country> countryList = new ArrayList<Country>();
    public static List<CurrencyRate> currencyRateList = new ArrayList<>();
    public static List<UnitConverter> unitConverterList = new ArrayList<>();
    public static String CurrentCountryID, CurrentCountryTicker, CurrentCountryName;

    public static String Host = "http://api.ibaax.com";
    public static String localHost = "http://api.rebaax.com";
    public static String ImageHost = "http://www.rebaax.com";


    public static boolean isNetworkConnected(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();

    }

    public static String getCurrentLocation(Context context) {

        try {
            String locale = context.getResources().getConfiguration().locale.getCountry();

            return locale;
        } catch (Exception ex) {

            return "";
        }
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
            return "us";
        }
        return "us";
    }

    public static String getCurrentCountry(String CountryTicker) {

        for (Country c : countryList) {
            if (c.CountryTicker.toLowerCase().equals(CountryTicker)) {
                return c.CountryID;
            } else if (c.Name.toLowerCase().equals(CountryTicker.toLowerCase())) {
                return c.CountryID;
            }
        }
        return "1";
    }

    public static void setCountry() {
        countryList.add(new Country("1", "US", "United States", "+1", "USD", 38.889931, -77.009003));
        countryList.add(new Country("7", "CA", "Canada", "+1", "CAD", 38.889931, -77.009003));
        countryList.add(new Country("15", "BD", "Bangladesh", "+880", "BDT", 23.7000, 90.3667));
        countryList.add(new Country("5", "IN", "India", "+91", "INR", 28.6139, 77.2090));
        countryList.add(new Country("9", "CN", "China", "+86", "CNY", 45.4000, 75.6667));
        countryList.add(new Country("1374", "PK", "Pakistan", "+92", "PKR", 33.7167, 73.0667));
        countryList.add(new Country("146", "SA", "Saudi Arabia", "+966", "SAR", 24.6333, 46.7167));
        countryList.add(new Country("77", "IR", "Iran", "+98", "IRR", 32.0000, 53.0000));
        countryList.add(new Country("78", "IQ", "Iraq", "+964", "IQD", 33.3333, 44.4333));
        countryList.add(new Country("3", "AE", "United Arab Emirates", "+971", "AED", 33.3333, 44.4333));
        countryList.add(new Country("56", "EG", "Egypt", "+20", "EGP", 26.0000, 30.0000));
        countryList.add(new Country("136", "PT", "Portugal", "+351", "EUR", 38.736946, -9.142685));
        countryList.add(new Country("1373", "DZ", "Algeria", "+213", "DZD", 36.7667, 3.2167));
        countryList.add(new Country("8", "AU", "Australia", "+61", "AUD", 35.3075, 149.1244));
    }

    public static void getCountryTicker(Context context) {
        AppPrefs pref = new AppPrefs(context);
        for (Country c : AppGlobal.countryList) {
            if (c.CountryID.equals(pref.getUserCountry())) {

                CurrentCountryTicker = c.CountryTicker;
                CurrentCountryID = c.CountryID;
                CurrentCountryName = c.Name;

            }
        }

    }

    public static boolean IsLoggedIn(Context context) {

        try {
            if (new AppPrefs(context).gettOAuthToken().length() == 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }
}
