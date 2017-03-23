package JSOINParsing;

import android.content.Context;
import android.util.Log;

import com.ibaax.com.ibaax.AppPrefs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.AppGlobal;
import Entity.Property;
import Plugins.JSONParser;

/**
 * Created by iBaax on 3/31/16.
 */
public class parseProperty2 {

    AppPrefs prefs;

    public parseProperty2(Context context) {
        prefs = new AppPrefs(context);
    }

    public List<Property> parse(String response, List<Property> list) {


        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {

                JSONObject jo = array.getJSONObject(i);

                Property p = new Property();
                p.PropertyID = JSONParser.parseInt(jo, "PropertyID");
                p.PropertyName = JSONParser.parseString(jo, "PropertyName");
                //p.Price = JSONParser.parseDouble(jo, "Price");
                p.Price = ConvertCurrency(jo);

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
                p.CountryName = JSONParser.parseString(jo, "CountryName");
                p.StateName = JSONParser.parseString(jo, "StateName");
                p.CityName = JSONParser.parseString(jo, "CityName");
                p.LocalityName = JSONParser.parseString(jo, "LocalityName");
                p.PropertyBedroomNumber = JSONParser.parseInt(jo, "PropertyBedroomNumber");
                p.PropertyBathroomNumber = JSONParser.parseInt(jo, "PropertyBathroomNumber");
                //p.BuiltUpAreaSize = JSONParser.parseInt(jo, "BuiltUpAreaSize");
                p.BuiltUpAreaSize = ConvertUnits(jo);
                p.PropertyLotSize = JSONParser.parseInt(jo, "PropertyLotSize");
                //p.PropertyBuildUpAreaSizeTypeName = JSONParser.parseString(jo, "PropertyBuildUpAreaSizeTypeName");
                p.PropertyBuildUpAreaSizeTypeId = prefs.getUserMeasurementType();
                p.PropertyBuildUpAreaSizeTypeName = prefs.getUserMeasurement();
                p.PropertyPlotAreaSizeTypeName = JSONParser.parseString(jo, "PropertyLotSizeTypeName");
                p.ZipCode = JSONParser.parseString(jo, "ZipCode");
                p.Address = JSONParser.parseString(jo, "Address");
                p.AptSuiteNo = JSONParser.parseString(jo, "AptSuiteNo");
                p.PropertyPermalink = JSONParser.parseString(jo, "PropertyPermaLink");
                p.IsLiked = JSONParser.parseBoolien(jo, "IsFavourite");
                p.IsFavourite = JSONParser.parseBoolien(jo, "IsFavourite");
                p.PostedBy = JSONParser.parseString(jo, "PostedBy");
                p.CreateDate = JSONParser.parseString(jo, "PostedOn");
                p.CompanyID = JSONParser.parseInt(jo, "PrimaryContCompanyID");
                p.ContactID = JSONParser.parseString(jo, "PrimaryContUserID");
                //p.CurrencyName = JSONParser.parseString(jo, "CurrencyName");
                p.CurrencyName = prefs.getUserCurrency();
                p.ImagePath = JSONParser.parseString(jo, "ThumbnailUrl").replace(" ", "%20") + "&dimension=2";
                //p.ImagePath="http://www.ibaax.com/property/ShowMarketplaceImage/"+p.PropertyID+"?PhotoFileName="+p.PhotoFileName;
                p.PostedBefore = JSONParser.parseInt(jo, "PostedBefore");
                p.TotalCount = JSONParser.parseInt(jo, "TotalCount");
                p.RowNumber = JSONParser.parseInt(jo, "ROWNUM");

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
            Log.e("error", "Parse Property2 Error: " + ex.getMessage());

        }

        return list;
    }


    double ConvertCurrency(JSONObject jo) {
        try {

            double NewPrice = 0.0;
            double ReturnPrice = 0.0;
            if (JSONParser.parseString(jo, "CurrencyName").equals(prefs.getUserCurrency())) {
                return JSONParser.parseDouble(jo, "Price");
            } else {
                for (int j = 0; j < AppGlobal.currencyRateList.size(); j++) {
                    if (AppGlobal.currencyRateList.get(j).SecondaryCurrencyTicker.equals(JSONParser.parseString(jo, "CurrencyName"))) {
                        NewPrice = JSONParser.parseDouble(jo, "Price") / AppGlobal.currencyRateList.get(j).Rate;
                        Log.v("Rate", "RATE: " + AppGlobal.currencyRateList.get(j).Rate);
                        break;
                    }
                }
                for (int j = 0; j < AppGlobal.currencyRateList.size(); j++) {
                    if (AppGlobal.currencyRateList.get(j).SecondaryCurrencyTicker.equals(prefs.getUserCurrency())) {
                        ReturnPrice = NewPrice * AppGlobal.currencyRateList.get(j).Rate;
                        Log.v("Rate", "RATE: " + AppGlobal.currencyRateList.get(j).Rate);
                        break;
                    }
                }
                return ReturnPrice;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    double ConvertUnits(JSONObject jo) {
        try {
            if (jo.getInt("PropertyBuildUpAreaSizeTypeId") > 0) {
                int count = AppGlobal.unitConverterList.size();
                for (int i = 0; i < count; i++) {
                    if (jo.getInt("PropertyBuildUpAreaSizeTypeId") == AppGlobal.unitConverterList.get(i).From
                            && prefs.getUserMeasurementType() == AppGlobal.unitConverterList.get(i).To) {
                        return jo.getDouble("BuiltUpAreaSize") * AppGlobal.unitConverterList.get(i).UnitsChanged;
                    }
                }
            } else {
                return jo.getDouble("BuiltUpAreaSize");
            }
        } catch (Exception e) {

        }
        return 0.0;
    }


}
