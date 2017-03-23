package ServiceInvoke;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Entity.GeoLocationModel;
import Event.IHttpResponse;
import Plugins.JSONParser;

/**
 * Created by iBaax on 2/23/16.
 */
public class GetAllLocationInfoFromLatLon extends AsyncTask<Void, Void, String> {

    String street_address="", street_number="",state_ticker="",
            route="", intersection="", country="",
            administrative_area_level_1="",
            administrative_area_level_2="",
            administrative_area_level_3="",
            administrative_area_level_4="",
            administrative_area_level_5="",
            colloquial_area="", locality="",
            sublocality="",
            sublocality_level_1="",
            sublocality_level_2="",
            sublocality_level_3="",
            sublocality_level_4="",
            sublocality_level_5="",
            neighborhood="", premise="",
            subpremise="", postal_code="",
            natural_feature="", airport="",
            park, post_box="", floor="", room="";

    int TIMEOUT_MILLISEC = 10000;

    IHttpResponse listener;
    Context context;
    String Result;

    public GetAllLocationInfoFromLatLon(Context context, String Result, IHttpResponse listener) {
        this.context = context;
        this.listener = listener;
        this.Result=Result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {



        try {
            JSONObject geobj = new JSONObject(Result);

            if (geobj.getString("status").equals("OK")) {
                JSONArray GeoArr = new JSONArray(geobj.getString("results"));
                JSONObject ComponentOBJ = GeoArr.getJSONObject(0);

                GeoLocationModel g = new GeoLocationModel();
                g.PlaceID = JSONParser.parseString(ComponentOBJ, "place_id");

                JSONArray ComponentArr = new JSONArray(ComponentOBJ.getString("address_components"));
                for (int i = 0; i < ComponentArr.length(); i++) {
                    JSONObject addObj = ComponentArr.getJSONObject(i);
                    JSONArray typessArr = new JSONArray(addObj.getString("types"));

                    if (typessArr.getString(0).equals("street_address")) {street_address = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("street_number")) { street_number = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("route")) {route = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("intersection")) {intersection = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("country")) {country = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("administrative_area_level_1")) {administrative_area_level_1 = JSONParser.parseString(addObj, "long_name");
                    state_ticker=JSONParser.parseString(addObj,"short_name");}
                    if (typessArr.getString(0).equals("administrative_area_level_2")) { administrative_area_level_2 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("administrative_area_level_3")) {administrative_area_level_3 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("administrative_area_level_4")) {administrative_area_level_4 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("administrative_area_level_5")) {administrative_area_level_5 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("colloquial_area")) {colloquial_area = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("locality")) {locality = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("locality")) {sublocality = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("sublocality_level_1")) {sublocality_level_1 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("sublocality_level_2")) {sublocality_level_2 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("sublocality_level_3")) {sublocality_level_3 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("sublocality_level_4")) {sublocality_level_4 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("sublocality_level_5")) {sublocality_level_5 = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("neighborhood")) {neighborhood = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("premise")) {premise = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("subpremise")) { subpremise = JSONParser.parseString(addObj, "long_name"); }
                    if (typessArr.getString(0).equals("postal_code")) {postal_code = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("natural_feature")) {natural_feature = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("airport")) {airport = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("park")) {park = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("post_box")) { post_box = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("floor")) {floor = JSONParser.parseString(addObj, "long_name");}
                    if (typessArr.getString(0).equals("room")) {room = JSONParser.parseString(addObj, "long_name");}

                }

                if(room.length()>0){g.AptSuiteNo=room;}
                if(subpremise.length()>0){ g.BuildingName=subpremise;}
                if(postal_code.length()>0){g.ZipCode=postal_code;}

                if(street_address.length()>0){g.Address=street_address;}
                else if(street_number.length()>0 && route.length()>0){g.Address=street_number+" "+route;}

                if(neighborhood.length()>0){g.NeighborhoodName=neighborhood;}

                if(sublocality_level_5.length()>0){g.LocalityName=sublocality_level_5;}
                else if(sublocality_level_4.length()>0){g.LocalityName=sublocality_level_4;}
                else if(sublocality_level_3.length()>0){g.LocalityName=sublocality_level_3;}
                else if(sublocality_level_2.length()>0){g.LocalityName=sublocality_level_2;}
                else if(sublocality_level_1.length()>0){g.LocalityName=sublocality_level_1;}

                if(locality.length()>0){g.CityName=locality;}
                else if(administrative_area_level_5.length()>0){g.CityName=administrative_area_level_5;}
                else if(administrative_area_level_4.length()>0){g.CityName=administrative_area_level_4;}
                else if(administrative_area_level_3.length()>0){g.CityName=administrative_area_level_3;}
                else if(administrative_area_level_2.length()>0){g.CityName=administrative_area_level_2;}

                if(administrative_area_level_1.length()>0){g.StateName=administrative_area_level_1;}
                if(state_ticker.length()>0){g.StateTicker=state_ticker;}
                if(country.length()>0){g.CountryName=country;}


                JSONObject LocationObj = new JSONObject();
                LocationObj.put("AptSuiteNo", getData(g.AptSuiteNo));
                LocationObj.put("BuildingName", getData(g.BuildingName));
                LocationObj.put("ZipCode", getData(g.ZipCode));
                LocationObj.put("Address",getData(g.Address));
                LocationObj.put("NeighborhoodName", getData(g.NeighborhoodName));
                LocationObj.put("LocalityName", getData(g.LocalityName));
                LocationObj.put("CityName", getData(g.CityName));
                LocationObj.put("StateName",getData(g.StateName));
                LocationObj.put("StateTicker",getData(g.StateTicker));
                LocationObj.put("CountryName", getData(g.CountryName));
                LocationObj.put("PlaceID", getData(g.PlaceID));


                return LocationObj.toString();

            }
            return "";

        } catch (JSONException e) {
            Log.e("esty", "ParseGeoLocation Error: " + e.getMessage());
            return e.getMessage();
        }


    }

    @Override
    protected void onPostExecute(String s) {
        try {

            Log.v("esty2", "Detail " + s);
            //dialog.dismiss();

            if (s != null) {
                listener.RequestSuccess(s);
            }

            super.onPostExecute(s);
        } catch (Exception ex) {

            Log.v("error", ex.getMessage());

        }
    }

    public String getData(String text) {


        if (text == null) {

            return "";
        }

        return  text;


    }
}
