package Plugins;

import org.json.JSONObject;


public class JSONParser {

    public static String parseString(JSONObject jo, String param) {

        try {

            String str = jo.getString(param);

            if (str.equals("null"))
                return "";

            return str;

        } catch (Exception ex) {

            //Log.v("error", "Exception parseString->parseString->Property-> " + param + "->errormsg->" + ex.getMessage());

            return "";
        }

    }


    public static int parseInt(JSONObject jo, String param) {

        try {

            int i = jo.getInt(param);


            return i;
        } catch (Exception ex) {

            //Log.v("error", "Exception parseInt->parseString->Property-> " + param + "->errormsg->" + ex.getMessage());

            return 0;
        }

    }


    public static long parseLong(JSONObject jo, String param) {

        try {

            return jo.getLong(param);
        } catch (Exception ex) {

            //Log.v("error", "Exception parseString->parseLong->Property-> " + param + "->errormsg->" + ex.getMessage());

            return 0;
        }

    }


    public static Boolean parseBoolien(JSONObject jo, String param) {

        try {

            return jo.getBoolean(param);
        } catch (Exception ex) {

            //Log.v("error", "Exception parseString->parseBoolian->Property-> " + param + "->errormsg->" + ex.getMessage());

            return false;
        }

    }


    public static Double parseDouble(JSONObject jo, String param) {

        try {

            return jo.getDouble(param);
        } catch (Exception ex) {

            //Log.v("error", "Exception parseString->parseDouble->Property-> " + param + "->errormsg->" + ex.getMessage());

            return 0.0;
        }

    }

}
