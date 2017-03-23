package JSOINParsing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.ActiveListing;
import Entity.Agent;
import Entity.AgentReview;
import Plugins.JSONParser;

/**
 * Created by iBaax on 1/27/16.
 */
public class parseAgent {


    public List<String> CoverageArea(String response, List<String> list) {
        try {
            JSONArray array = new JSONArray((String) response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jo = array.getJSONObject(i);
                    //String AreaName = JSONParser.parseString(jo, "AreaName");
                    String AreaName = JSONParser.parseString(jo, "ServiceAreas");
                    list.add(AreaName);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception ex) {

        }
        return list;
    }


    public List<ActiveListing> parseActiveListining(String response, List<ActiveListing> list) {
        try {
            JSONArray array = new JSONArray((String) response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jo = array.getJSONObject(i);

                    ActiveListing p = new ActiveListing();
                    p.PropertyID = JSONParser.parseInt(jo, "PropertyID");
                    //p.agentThumbnailUrl="http://"+ JSONParser.parseString(jo, "agentThumbnailUrl");
                    p.PhotoFileName = JSONParser.parseString(jo, "PhotoFileName");
                    p.PropertyName = JSONParser.parseString(jo, "PropertyName");
                    p.BuildingName = JSONParser.parseString(jo, "BuildingName");
                    p.AptSuiteNo = JSONParser.parseString(jo, "AptSuiteNo");
                    p.BuildYearMonth = JSONParser.parseString(jo, "BuildYearMonth");
                    p.Address = JSONParser.parseString(jo, "Address");
                    p.LocalityName = JSONParser.parseString(jo, "LocalityName");
                    p.CountryName = JSONParser.parseString(jo, "CountryName");
                    p.CreateDate = JSONParser.parseString(jo, "CreateDate");
                    p.CityName = JSONParser.parseString(jo, "CityName");
                    p.StateName = JSONParser.parseString(jo, "StateName");
                    p.ZipCode = JSONParser.parseString(jo, "ZipCode");
                    p.Latitude = JSONParser.parseDouble(jo, "Latitude");
                    p.Longitude = JSONParser.parseDouble(jo, "Longitude");
                    p.Description = JSONParser.parseString(jo, "Description");
                    p.DisplayId = JSONParser.parseInt(jo, "DisplayId");
                    p.PropertyBedroomNumber = JSONParser.parseInt(jo, "PropertyBedroomNumber");
                    p.PropertyBathroomNumber = JSONParser.parseInt(jo, "PropertyBathroomNumber");
                    p.BuiltUpAreaSize = JSONParser.parseInt(jo, "BuiltUpAreaSize");
                    p.LotSize = JSONParser.parseString(jo, "LotSize");
                    p.BuiltUpAreaSizeName = JSONParser.parseString(jo, "BuiltUpAreaSizeName");
                    p.PhotoFileName = JSONParser.parseString(jo, "PhotoFileName");
                    p.LotSizeName = JSONParser.parseString(jo, "LotSizeName");
                    p.Price = JSONParser.parseDouble(jo, "Price");
                    p.CurrencyName = JSONParser.parseString(jo, "CurrencyName");
                    p.RatingScaleCategoryName = JSONParser.parseString(jo, "RatingScaleCategoryName");
                    p.PropertyTransactionTypeName = JSONParser.parseString(jo, "PropertyTransactionTypeName");
                    p.PropertyTransactionTypeCategoryName = JSONParser.parseString(jo, "PropertyTransactionTypeCategoryName");
                    p.PropertyTransactionTypeCategoryID = JSONParser.parseInt(jo, "PropertyTransactionTypeCategoryID");

                    p.PropertyTypeName = JSONParser.parseString(jo, "PropertyTypeName");
                    p.Permalink = JSONParser.parseString(jo, "Permalink");
                    p.IsLiked = JSONParser.parseBoolien(jo, "IsLiked");
                    p.PostedBy = JSONParser.parseString(jo, "PostedBy");
                    p.CompanyID = JSONParser.parseInt(jo, "CompanyID");
                    p.ContactID = JSONParser.parseInt(jo, "ContactID");
                    p.PostedBefore = JSONParser.parseInt(jo, "PostedBefore");

                    list.add(p);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception ex) {

        }
        return list;

    }

    public List<String> Specialites(String response, List<String> list) {

        try {
            JSONArray array = new JSONArray((String) response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jo = array.getJSONObject(i);
                    if (JSONParser.parseString(jo, "IsSelected").equals("True")) {
                        String CompetencyName = JSONParser.parseString(jo, "Name");
                        list.add(CompetencyName);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception ex) {

        }
        return list;

    }

    public List<AgentReview> Reviews(String response, List<AgentReview> list) {

        try {
            JSONArray array = new JSONArray((String) response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jo = array.getJSONObject(i);
                    AgentReview r = new AgentReview();

                    r.ContactWorkReferenceRecommendationID = jo.getString("ContactWorkReferenceRecommendationID");
                    r.ContactID = jo.getString("ContactID");
                    r.CompanyName = jo.getString("CompanyName");
                    r.CompanyID = jo.getString("CompanyID");
                    r.FirstName = jo.getString("FirstName");
                    r.LastName = jo.getString("LastName");
                    r.JobTitle = jo.getString("JobTitle");
                    r.OfficePhone = jo.getString("OfficePhone");
                    r.MobileNumber = jo.getString("MobileNumber");
                    r.EmailAddress = jo.getString("EmailAddress");
                    r.RecommendationInfo = jo.getString("RecommendationInfo");
                    r.IsVerified = jo.getString("IsVerified");
                    r.VerifiedByUserID = jo.getString("VerifiedByUserID");
                    r.VerificationNotes = jo.getString("VerificationNotes");
                    r.VerificationDate = jo.getString("VerificationDate");
                    r.IsPositive = jo.getString("IsPositive");
                    r.CreateDate = jo.getString("CreateDate");
                    r.CreatedByUserID = jo.getString("CreatedByUserID");
                    r.LastUpdateDate = jo.getString("LastUpdateDate");
                    r.LastUpdatedByUserID = jo.getString("LastUpdatedByUserID");
                    r.CreatedByCompanyID = jo.getString("CreatedByCompanyID");
                    r.VideoRecommendation = jo.getString("VideoRecommendation");
                    r.IsPubliclyVisible = jo.getString("IsPubliclyVisible");
                    r.DisplaySequence = jo.getString("DisplaySequence");
                    r.ReviewReferenceId = jo.getString("ReviewReferenceId");
                    r.ContactReviewServiceTypeID = jo.getString("ContactReviewServiceTypeID");
                    r.RatingScale = jo.getString("RatingScale");

                    list.add(r);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception ex) {

        }
        return list;

    }
}
