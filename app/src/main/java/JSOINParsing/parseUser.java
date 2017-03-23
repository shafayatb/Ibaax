package JSOINParsing;

import android.content.Context;

import com.ibaax.com.ibaax.AppPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.User;
import Plugins.JSONParser;

/**
 * Created by iBaax on 4/13/16.
 */
public class parseUser {

    public List<User> parse(String response, List<User> list, Context context) {
        try {
            AppPrefs prefs = new AppPrefs(context);
            JSONArray array = new JSONArray((String) response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jo = array.getJSONObject(i);

                    User p = new User();
                    if (prefs.getUserCategory().equals("2") || prefs.getUserCategory().equals("4")) {
                        p.FirstName = JSONParser.parseString(jo, "Name");
                        p.MobileCode = JSONParser.parseString(jo, "MobileCode");
                        p.Phone = JSONParser.parseString(jo, "Mobile");
                        List<String> AddressList = new ArrayList<>();
                        AddressList.add(JSONParser.parseString(jo, "Locality"));
                        AddressList.add(JSONParser.parseString(jo, "City"));
                        AddressList.add(JSONParser.parseString(jo, "State"));
                        AddressList.add(JSONParser.parseString(jo, "Country"));
                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < AddressList.size(); j++) {
                            if (AddressList.get(j).length() > 0) {
                                sb.append(AddressList.get(j) + ", ");
                            }
                        }
                        if (sb.toString().length() > 0) {
                            sb.delete(sb.length() - 2, sb.length());
                        }
                        p.Address = sb.toString();
                        AddressList.clear();
                        if (sb.toString().length() > 0) {
                            sb.delete(0, sb.length());
                        }
                    } else {
                        p.FirstName = JSONParser.parseString(jo, "FirstName");
                        p.LastName = JSONParser.parseString(jo, "LastName");
                        p.MobileCode = JSONParser.parseString(jo, "MobileCode");
                        p.Phone = JSONParser.parseString(jo, "Mobile");
                        p.Address = JSONParser.parseString(jo, "Address");
                    }
                    p.DisplayId = JSONParser.parseString(jo, "DisplayId");
                    p.Latitude = JSONParser.parseDouble(jo, "Latitude");
                    p.Longitude = JSONParser.parseDouble(jo, "Longitude");
                    p.PositionName = JSONParser.parseString(jo, "PositionName");
                    p.CoverPhotoFileName = JSONParser.parseString(jo, "CoverPhotoFileName");
                    p.ProfileSummary = JSONParser.parseString(jo, "ProfileSummary");
                    p.CreateDate = JSONParser.parseString(jo, "CreateDate");
                    p.SkypeAddress = JSONParser.parseString(jo, "SkypeAddress");
                    p.ProfileSummary = JSONParser.parseString(jo, "ProfileSummary");
                    p.MemberSince = JSONParser.parseString(jo, "MemberSince");
                    p.EmailAddress = JSONParser.parseString(jo, "PrimaryEmailAddress");
                    p.LinkedInProfileAddress = JSONParser.parseString(jo, "LinkedInProfileAddress");
                    p.PersonalBlogAddress = JSONParser.parseString(jo, "PersonalBlogAddress");
                    p.FacebookProfileAddress = JSONParser.parseString(jo, "FacebookProfileAddress");
                    p.TwitterAddress = JSONParser.parseString(jo, "TwitterAddress");
                    p.GooglePlusAddress = JSONParser.parseString(jo, "GooglePlusAddress");
                    p.ActiveListing = JSONParser.parseInt(jo, "ActiveListing");
                    p.WebAddress = JSONParser.parseString(jo, "WebAddress");
                    p.CompanyName = JSONParser.parseString(jo, "CompanyName");
                    p.UserCategoryName = JSONParser.parseString(jo, "UserCategoryName");
                    p.Permalink = JSONParser.parseString(jo, "Permalink");
                    p.Languages = JSONParser.parseString(jo, "Languages");
                    p.License = JSONParser.parseString(jo, "License");


                    list.add(p);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception ex) {

        }
        return list;

    }
}
