package JSOINParsing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.Agent;
import Plugins.JSONParser;

/**
 * Created by iBaax on 2/14/16.
 */
public class parseSearchAgent {

    public List<Agent> parse(String response, List<Agent> list) {
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jo = array.getJSONObject(i);

                    Agent p = new Agent();
                    p.RowNumber = JSONParser.parseInt(jo, "ROWNUM");
                    p.TotalCount = JSONParser.parseString(jo, "TotalCount");
                    p.agentThumbnailUrl = JSONParser.parseString(jo, "PhotoFileUrl");
                    p.ContactID = JSONParser.parseInt(jo, "ContactId");
                    p.DisplayID = JSONParser.parseString(jo, "DisplayId");
                    p.PhotoFileName = "http://" + JSONParser.parseString(jo, "PhotoFileUrl");
                    if (JSONParser.parseString(jo, "Name").trim().length() > 0) {
                        p.name = JSONParser.parseString(jo, "Name");
                    } else {
                        p.name = JSONParser.parseString(jo, "ContactName");
                    }
                    p.mobile = JSONParser.parseString(jo, "Mobile");
                    p.country = JSONParser.parseString(jo, "Country");
                    p.state = JSONParser.parseString(jo, "State");
                    p.city = JSONParser.parseString(jo, "City");
                    p.locality = JSONParser.parseString(jo, "Locality");
                    p.address = JSONParser.parseString(jo, "Address");
                    p.zipCode = JSONParser.parseString(jo, "ZipCode");
                    p.Latitude = JSONParser.parseDouble(jo, "Latitude");
                    p.Longitude = JSONParser.parseDouble(jo, "Longitude");
                    p.verified = JSONParser.parseBoolien(jo, "IsVerified");
                    p.ProfileSummary = JSONParser.parseString(jo, "ProfileSummary");
                    p.AgentLicenseNumber = JSONParser.parseString(jo, "AgentLicenseNumber");
                    p.OfficePhone = JSONParser.parseString(jo, "OfficePhone");
                    p.PersonalBlogAddress = JSONParser.parseString(jo, "PersonalBlogAddress");
                    p.Facebook = JSONParser.parseString(jo, "Facebook");
                    p.IsLiked = JSONParser.parseBoolien(jo, "IsFavourite");
                    p.Twitter = JSONParser.parseString(jo, "Twitter");
                    p.LinkedIn = JSONParser.parseString(jo, "LinkedIn");
                    p.GooglePlus = JSONParser.parseString(jo, "GooglePlus");
                    p.activeListing = JSONParser.parseInt(jo, "ActiveListing");
                    p.recommendationCount = JSONParser.parseInt(jo, "RecommendationCount");
                    p.agentPermaLink = JSONParser.parseString(jo, "Permalink");
                    p.CompanyLogoUrl = JSONParser.parseString(jo, "CompanyLogoUrl");
                    p.CompanyName = JSONParser.parseString(jo, "CompanyName");
                    p.CompanyPermalink = JSONParser.parseString(jo, "CompanyPermalink");
                    p.Position = JSONParser.parseString(jo, "Position");
                    p.MemberSince = JSONParser.parseString(jo, "MemberSince");
                    p.CreateDate = JSONParser.parseString(jo, "CreateDate");

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
                    p.fulladdress = sb.toString();
                    // p.ImagePath="http://www.ibaax.com/property/ShowMarketplaceImage/"+p.PropertyID+"?PhotoFileName="+p.PhotoFileName;
                    list.add(p);
                    // Log.v("esty","name:"+name);
                    // Log.v("esty","agentThumbnailUrl:"+agentThumbnailUrl);
                    AddressList.clear();
                    if (sb.toString().length() > 0) {
                        sb.delete(0, sb.length());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }
            }


        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
        }
        return list;

    }
}
