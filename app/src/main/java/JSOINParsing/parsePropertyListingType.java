package JSOINParsing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Entity.Dictunary;
import Plugins.JSONParser;

/**
 * Created by iBaax on 2/8/16.
 */
public class parsePropertyListingType {


    public List<Dictunary> parse(String response, List<Dictunary> list) {
        //   Log.v("response",response.toString());

        try {
            JSONArray array = new JSONArray((String) response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jo = array.getJSONObject(i);
                    Dictunary p = new Dictunary();
                    p.ID = JSONParser.parseInt(jo, "Value");
                    p.Title = JSONParser.parseString(jo, "Text");
                    p.IsSelected=true;
                    list.add(p);
                    // Log.v("esty","name:"+name);
                    // Log.v("esty","agentThumbnailUrl:"+agentThumbnailUrl);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception ex) {

        }
        return list;

    }

}
