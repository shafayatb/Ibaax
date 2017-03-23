package JSOINParsing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.AppGlobal;
import Entity.Property;
import Entity.PropertyGallary;
import Plugins.JSONParser;

/**
 * Created by iBaax on 1/27/16.
 */
public class ParseProperty {


    public List<Property> parse(String response, List<Property> list) {


        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {

                JSONObject jo = array.getJSONObject(i);

                Property p = new Property();
                p.PropertyID = JSONParser.parseInt(jo, "PropertyID");
                p.PropertyName = JSONParser.parseString(jo, "PropertyName");
                p.Price = JSONParser.parseDouble(jo, "Price");
                p.BuildingName = JSONParser.parseString(jo, "BuildingName");
                p.BuildYearMonth = JSONParser.parseString(jo, "BuildYearMonth");
                p.DisplayId = JSONParser.parseString(jo, "DisplayId");
                p.Description = JSONParser.parseString(jo, "Description");
                p.PropertyTypeID = JSONParser.parseInt(jo, "PropertyTypeID");
                p.PropertyTypeName = JSONParser.parseString(jo, "PropertyTypeName");
                p.PropertyTransactionTypeID = JSONParser.parseInt(jo, "PropertyTransactionTypeID");
                p.PropertyTransactionTypeName = JSONParser.parseString(jo, "PropertyTransactionTypeName");
                p.PropertyTransactionTypeCategoryID = JSONParser.parseInt(jo, "PropertyTransactionTypeCategoryID");
                p.PropertyTransactionTypeCategoryName = JSONParser.parseString(jo, "PropertyTransactionTypeCategoryName");
                p.Latitude = JSONParser.parseDouble(jo, "Latitude");
                p.Longitude = JSONParser.parseDouble(jo, "Longitude");
                p.RecentPublishStatus = JSONParser.parseString(jo, "RecentPublishStatus");
                p.CountryName = JSONParser.parseString(jo, "CountryName");
                p.StateName = JSONParser.parseString(jo, "StateName");
                p.CityName = JSONParser.parseString(jo, "CityName");
                p.LocalityName = JSONParser.parseString(jo, "LocalityName");
                p.PropertyBedroomNumber = JSONParser.parseInt(jo, "PropertyBedroomNumber");
                p.PropertyBathroomNumber = JSONParser.parseInt(jo, "PropertyBathroomNumber");
                p.BuiltUpAreaSize = JSONParser.parseDouble(jo, "BuiltUpAreaSize");
                p.PropertyLotSize = JSONParser.parseInt(jo, "PropertyLotSize");
                p.PropertyBuildUpAreaSizeTypeName = JSONParser.parseString(jo, "PropertyBuildUpAreaSizeTypeName");
                p.PropertyPlotAreaSizeTypeName = JSONParser.parseString(jo, "PropertyLotSizeTypeName");
                p.ZipCode = JSONParser.parseString(jo, "ZipCode");
                p.Address = JSONParser.parseString(jo, "Address");
                p.AptSuiteNo = JSONParser.parseString(jo, "AptSuiteNo");
                p.PhotoFileName = JSONParser.parseString(jo, "PhotoFileName");
                p.ImageUrl = JSONParser.parseString(jo, "ImageUrl");
                p.Url = JSONParser.parseString(jo, "Url");
                p.PropertyPermalink = "http://www.rebaax.com/" + JSONParser.parseString(jo, "PropertyPermalink");
                p.IsLiked = JSONParser.parseBoolien(jo, "IsFavourite");
                p.IsFavourite = JSONParser.parseBoolien(jo, "IsLiked");
                if (JSONParser.parseString(jo, "CreatorName").length() > 0) {
                    p.PostedBy = JSONParser.parseString(jo, "CreatorName");
                } else {
                    p.PostedBy = JSONParser.parseString(jo, "PostedBy");
                }
                p.CompanyID = JSONParser.parseInt(jo, "CompanyID");
                p.ContactID = JSONParser.parseString(jo, "ContactID");
                p.CurrencyName = JSONParser.parseString(jo, "CurrencyName");
                //p.ImagePath = AppGlobal.ImageHost + "/property/ShowMarketplaceImage/" + p.PropertyID;

                p.ImagePath = "http://www.rebaax.com/property/ShowMarketplaceImage/" + p.PropertyID + "?PhotoFileName=" + p.PhotoFileName
                        + "&dimension=2";


                p.CreateDate = JSONParser.parseString(jo, "CreateDate");
                p.FeaturedCountryName = JSONParser.parseString(jo, "FeaturedCountryName");
                p.FeaturedCountryId = JSONParser.parseInt(jo, "FeaturedCountryId");
                p.CountryCode = JSONParser.parseString(jo, "CountryCode");
                p.PostedBefore = JSONParser.parseInt(jo, "PostedBefore");
                p.RowNumber = JSONParser.parseInt(jo, "RowNumber");

                List<String> AddressList = new ArrayList<>();
                AddressList.add(p.LocalityName);
                AddressList.add(p.CityName);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < AddressList.size(); j++) {
                    if (AddressList.get(j).length() > 0) {
                        sb.append(AddressList.get(j) + ", ");
                    }
                }
                if (sb.toString().length() > 0) {
                    sb.delete(sb.length() - 2, sb.length());
                }
                p.HalfAddress = sb.toString();
                if (sb.toString().length() > 0) {
                    sb.delete(0, sb.length());
                }
                AddressList.add(p.StateName);
                AddressList.add(p.CountryName);
                for (int j = 0; j < AddressList.size(); j++) {
                    if (AddressList.get(j).length() > 0) {
                        sb.append(AddressList.get(j) + ", ");
                    }
                }
                if (sb.toString().length() > 0) {
                    sb.delete(sb.length() - 2, sb.length());
                }
                p.FullAddress = sb.toString();

                list.add(p);

                AddressList.clear();
                if (sb.toString().length() > 0) {
                    sb.delete(0, sb.length());
                }
            }
        } catch (Exception ex) {
            Log.e("error", "Parse Property Error: " + ex.getMessage());

        }

        return list;
    }


    public List<PropertyGallary> parseGallary(String response, List<PropertyGallary> list) {

        try {

            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                PropertyGallary c = new PropertyGallary();
                c.PropertyPhotoPortfolioID = jsonObject.getInt("PropertyPhotoPortfolioID");
                c.DocumentFileName = jsonObject.getString("DocumentFileName");
                //c.PropertyPhotoPortfolio = "http://www.ibaax.com" + jsonObject.getString("PropertyPhotoPortfolio");
                c.PropertyPhotoPortfolio = AppGlobal.ImageHost + "/" + jsonObject.getString("PropertyPhotoPortfolio");


                list.add(c);
            }

        } catch (JSONException ex) {
            Log.e("esty", "parseCountry/ParseCountry/Error " + ex.getMessage());
        }

        return list;
    }


}
