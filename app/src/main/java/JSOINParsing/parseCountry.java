package JSOINParsing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Entity.Country;

/**
 * Created by iBaax on 2/7/16.
 */
public class parseCountry {

    public List<Country> ParseCountry(String response,List<Country> list){

        try {

            JSONArray jsonArray=new JSONArray(response);
            for(int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject=jsonArray.getJSONObject(i);

                Country c=new Country();


                c.CountryID=jsonObject.getString("CountryID");
                c.CountryTicker=jsonObject.getString("CountryTicker");
                c.Name=jsonObject.getString("Name");
                c.PhoneCode=jsonObject.getString("PhoneCode");

                list.add(c);
            }

        }catch (JSONException ex){
            Log.e("esty", "parseCountry/ParseCountry/Error "+ex.getMessage());
        }

        return list;
    }
}
