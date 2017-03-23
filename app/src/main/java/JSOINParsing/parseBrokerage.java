package JSOINParsing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.Brokerages;
import Plugins.JSONParser;
import UI.MessageBox;

/**
 * Created by iBaax on 1/27/16.
 */
public class parseBrokerage {

    public  List<Brokerages> parse(String response,List<Brokerages>list)
    {
        try
        {
            JSONArray array=new JSONArray((String)response);
            for (int i = 0; i < array.length(); i++)
            {
                try {
                    JSONObject jo = array.getJSONObject(i);

                    Brokerages p=new Brokerages();
                    p.TotalCount = JSONParser.parseInt(jo, "TotalCount");
                    p.companyId = JSONParser.parseInt(jo, "CompanyID");
                    p.companyLogoUrl = JSONParser.parseString(jo, "ThumbnailUrl");
                    p.LogoFileName = JSONParser.parseString(jo, "LogoFileName");
                    p.displayID = JSONParser.parseString(jo, "DisplayId");
                    p.companyName = JSONParser.parseString(jo, "Name");
                    p.country = JSONParser.parseString(jo, "Country");
                    p.state = JSONParser.parseString(jo, "State");
                    p.city = JSONParser.parseString(jo, "City");
                    p.locality = JSONParser.parseString(jo, "Locality");
                    p.Latitude=JSONParser.parseDouble(jo, "Latitude");
                    p.Longitude=JSONParser.parseDouble(jo,"Longitude");
                    p.address = JSONParser.parseString(jo, "Address1");
                    p.zipcode = JSONParser.parseString(jo, "zipcode");
                    p.phone = JSONParser.parseString(jo, "MainOfficePhone");
                    p.LicenseNumber = JSONParser.parseString(jo, "LicenseNumber");
                    p.AboutTheCompany = JSONParser.parseString(jo, "AboutTheCompany");
                    p.Facebook = JSONParser.parseString(jo, "FacebookProfileAddress");
                    p.Twitter = JSONParser.parseString(jo, "TwitterAddress");
                    p.LinkedIn = JSONParser.parseString(jo, "LinkedInProfileAddress");
                    p.GooglePlus = JSONParser.parseString(jo, "GooglePlusAddress");
                    p.WebAddress = JSONParser.parseString(jo, "WebAddress");
                    p.EmailAddress = JSONParser.parseString(jo, "EmailAddress");
                    p.activeAgents = JSONParser.parseInt(jo, "AgentsCount");
                    p.activeListing = JSONParser.parseInt(jo, "ActiveListing");
                    p.UserCategoryID=JSONParser.parseInt(jo,"UserCategoryID");
                    p.companyPermaLink = JSONParser.parseString(jo, "Permalink");
                    p.IsFavorite=JSONParser.parseBoolien(jo, "IsFavourite");
                    p.isVerified = JSONParser.parseBoolien(jo, "isVerified");
                    p.MemberSince = JSONParser.parseString(jo, "MemberSince");
                    p.createDate = JSONParser.parseString(jo, "CreateDate");
                    p.RowNumber = JSONParser.parseInt(jo, "ROWNUM");

                    List<String> AddressList = new ArrayList<>();
                    AddressList.add(p.locality);
                    AddressList.add(p.city);
                    AddressList.add(p.state);
                    AddressList.add(p.country);
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < AddressList.size(); j++) {
                        if (AddressList.get(j).length() > 0) {
                            sb.append(AddressList.get(j) + ", ");
                        }
                    }
                    if (sb.toString().length() > 0) {
                        sb.delete(sb.length() - 2, sb.length());
                    }
                    p.FullAdress = sb.toString();
                    list.add(p);
                    AddressList.clear();
                    if (sb.toString().length() > 0) {
                        sb.delete(0, sb.length());
                    }



                } catch (JSONException e) {
                    Log.v("error","Fragment Home getFeaturedBrokerages: "+e.getMessage());
                }
            }






        }
        catch (Exception ex)
        {


        }

        return  list;
    }
}
