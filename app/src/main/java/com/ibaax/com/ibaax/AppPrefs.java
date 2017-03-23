package com.ibaax.com.ibaax;

/**
 * Created by S.R Rain on 1/13/2016.
 */


import android.content.Context;
import android.content.SharedPreferences;


public class AppPrefs {

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;


    public AppPrefs(Context context) {


        this.appSharedPrefs = context.getSharedPreferences("USER_PREFS", 0 | Context.MODE_MULTI_PROCESS);
        this.prefsEditor = appSharedPrefs.edit();
    }


    public void setOAuthToken(String token) {
        prefsEditor.putString("OAuthToken", token).commit();
    }

    public String gettOAuthToken() {

        return appSharedPrefs.getString("OAuthToken", "").toString();

    }

    public String getName() {
        return appSharedPrefs.getString("Name", "");
    }

    public void setName(String token) {
        prefsEditor.putString("Name", token).commit();
    }

    public String getCompanyName() {
        return appSharedPrefs.getString("CompanyName", "");
    }

    public void setCompanyName(String Name) {
        prefsEditor.putString("CompanyName", Name).commit();
    }

    public String getEmail() {
        return appSharedPrefs.getString("Email", "");
    }

    public void setEmail(String token) {
        prefsEditor.putString("Email", token).commit();
    }

    public String getUserID() {
        return appSharedPrefs.getString("UserID", "0");
    }

    public void setUserID(String token) {
        prefsEditor.putString("UserID", token).commit();
    }

    public String getUserCountry() {
        return appSharedPrefs.getString("UserCountry", "us");
    }

    public void setUserCountry(String token) {
        prefsEditor.putString("UserCountry", token).commit();
    }

    public String getFirstName() {
        return appSharedPrefs.getString("FirstName", "");
    }

    public void setFirstName(String token) {
        prefsEditor.putString("FirstName", token).commit();
    }

    public String getLastName() {
        return appSharedPrefs.getString("LastName", "");
    }

    public void setLastName(String token) {
        prefsEditor.putString("LastName", token).commit();
    }

    public String getCompanyID() {
        return appSharedPrefs.getString("CompanyID", "");
    }

    public void setCompanyID(String CompanyID) {
        prefsEditor.putString("CompanyID", CompanyID).commit();
    }

    public String getUserPhone() {
        return appSharedPrefs.getString("UserPhone", "");
    }

    public void setUserPhone(String token) {
        prefsEditor.putString("UserPhone", token).commit();
    }

    public String getUserPhoneCode() {
        return appSharedPrefs.getString("PhoneCode", "+1");
    }

    public void setUserPhoneCode(String PhoneCode) {
        prefsEditor.putString("PhoneCode", PhoneCode).commit();
    }

    public String getContactID() {
        return appSharedPrefs.getString("ContactID", "");
    }

    public void setContactID(String ContactID) {
        prefsEditor.putString("ContactID", ContactID).commit();
    }

    public Boolean getFirstInstall() {
        return appSharedPrefs.getBoolean("IsFirstInstall", true);
    }

    public void setFirstInstall(Boolean token) {
        prefsEditor.putBoolean("IsFirstInstall", token).commit();
    }

    public String getUserCategory() {
        return appSharedPrefs.getString("UserCategory", "");
    }

    public void setUserCategory(String UserCategoryID) {
        prefsEditor.putString("UserCategory", UserCategoryID).commit();
    }

    public String getUserCurrency() {
        return appSharedPrefs.getString("UserCurrency", "");
    }

    public void setUserCurrency(String CurrencyTicker) {
        prefsEditor.putString("UserCurrency", CurrencyTicker).commit();
        //prefsEditor.putString("UserCurrency", CurrencyTicker).apply();
    }

    public String getUserMeasurement() {
        return appSharedPrefs.getString("UserArea", "");
    }

    public void setUserMeasurement(String Area) {
        prefsEditor.putString("UserArea", Area).commit();
    }

    public int getUserMeasurementType() {
        return appSharedPrefs.getInt("AreaType", 1);
    }

    public void setUserMeasurementType(int Area) {
        prefsEditor.putInt("AreaType", Area).commit();
    }
}
