package ServiceInvoke;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Event.IHttpResponse;

/**
 * Created by iBaax on 2/13/16.
 */


public class getCountryFormLatLon extends AsyncTask<Void, Void, String> {
    int TIMEOUT_MILLISEC = 10000;

    IHttpResponse listener;
    Context context;
    double latitude;
    double longitude;
    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        //dialog = ProgressDialog.show(context, "Wait", "Please wait sometime.");

    }

    public getCountryFormLatLon(Context context,double latitude, double longitude, IHttpResponse listener) {
        this.context = context;
        this.listener = listener;
        this.latitude=latitude;
        this.longitude=longitude;

    }

    @Override
    protected void onPostExecute(String result) {

        try {

            Log.v("esty2", "Detail" + result);
            //dialog.dismiss();

            if(result!=null) {
                listener.RequestSuccess(result);
            }

            super.onPostExecute(result);
        } catch (Exception ex) {

            Log.v("error", ex.getMessage());

        }
    }

    @Override
    protected String doInBackground(Void... params) {

        try {

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    return addresses.get(0).getCountryName();
                }
                return null;
            } catch (IOException ignored) {
                //do something
            }
            return "";

        } catch (Exception ex) {

            return ex.getMessage();
        }

    }

}