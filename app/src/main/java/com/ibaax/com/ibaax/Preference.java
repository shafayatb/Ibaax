package com.ibaax.com.ibaax;

/**
 * Created by S.R Rain on 1/13/2016.
 */
import android.content.Context;
        import android.content.SharedPreferences;
        import android.util.Log;

/**
 * Created by Shafayat Bin Mamun on 10/28/2015.
 */
public class Preference {
    //private static Preference ourInstance = new Preference();

    private static Preference ourInstance = null;
    private static SharedPreferences SharedPref;
    //private static SharedPreferences.Editor SPEditor;

    public static void init(Context context) {
        SharedPref = context.getSharedPreferences("PREFERENCES_NAME", Context.MODE_PRIVATE);
    }
    public static Preference getInstance() {
        if(ourInstance==null)
            ourInstance= new Preference ();
        return ourInstance;
    }

    private Preference() {
    }


    public static SharedPreferences getPrefs() {
        return SharedPref;
    }

    public void saveCookie(String cookie) {
        if (cookie == null) {
            //the server did not return a cookie so we wont have anything to save
            return;
        }
        Log.v("Success", "Cookie: " + cookie);
        // Save in the preferences
        SharedPreferences prefs = getPrefs();
        if (null == prefs) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("cookie", cookie);
        editor.commit();
    }

    public String getCookie()
    {
        SharedPreferences prefs = getPrefs();
        String cookie = prefs.getString("cookie", "");
        Log.v("Success", "SavedCookie: " + cookie);
       /* if (cookie.contains("expires")) {
/** you might need to make sure that your cookie returns expires when its expired. I also noted that cokephp returns deleted
            removeCookie();
            return "";
        }*/
        return cookie;
    }

    public void removeCookie() {
        SharedPreferences prefs = getPrefs();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("cookie");
        editor.commit();
        Log.v("Success", "Removed");
    }
}
