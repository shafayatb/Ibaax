package JSOINParsing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.Agent;
import Plugins.JSONParser;

/**
 * Created by iBaax on 6/7/16.
 */
public class ParseDirectory {

    public List<Agent> parse(String response, List<Agent> list) {
        try {
            JSONArray array = new JSONArray((String) response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jo = array.getJSONObject(i);

                    Agent p = new Agent();
                    p.RowNumber = JSONParser.parseInt(jo, "ROWNUM");
                    p.TotalCount = JSONParser.parseString(jo, "TotalCount");
                    p.UserCategoryID = JSONParser.parseInt(jo, "UserCategoryID");
                    p.ContactID = JSONParser.parseInt(jo, "ContactID");
                    p.CompanyID = JSONParser.parseInt(jo, "CompanyID");
                    p.DisplayID = JSONParser.parseString(jo, "DisplayId");
                    p.PhotoFileName = JSONParser.parseString(jo, "ThumbnailUrl");
                    p.agentThumbnailUrl = JSONParser.parseString(jo, "ThumbnailUrl");
                    p.name = JSONParser.parseString(jo, "ContactName");
                    p.CompanyName = JSONParser.parseString(jo, "CompanyName");
                    p.mobile = JSONParser.parseString(jo, "Mobile");
                    p.country = JSONParser.parseString(jo, "Country");
                    p.state = JSONParser.parseString(jo, "State");
                    p.city = JSONParser.parseString(jo, "City");
                    p.locality = JSONParser.parseString(jo, "Locality");
                    p.address = JSONParser.parseString(jo, "Address1");
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
                    p.activeListing = JSONParser.parseInt(jo, "ActiveListingCount");
                    p.ActiveAgentCount = JSONParser.parseInt(jo, "ActiveAgentCount");
                    p.recommendationCount = JSONParser.parseInt(jo, "RecommendationCount");
                    p.agentPermaLink = JSONParser.parseString(jo, "Permalink");
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
                    list.add(p);
                    AddressList.clear();
                    if (sb.toString().length() > 0) {
                        sb.delete(0, sb.length());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {

        }
        return list;

    }


}
