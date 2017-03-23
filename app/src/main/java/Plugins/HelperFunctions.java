package Plugins;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.ibaax.com.ibaax.AppPrefs;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import Entity.AppGlobal;
import Entity.Dictunary;
import Entity.Property;

/**
 * Created by iBaax on 6/5/16.
 */
public class HelperFunctions {

    public static List<Property> ChangeCurrency(List<Property> list, AppPrefs prefs) {
        try {
            double NewPrice = 0.0;
            for (Property p : list) {
                if (p.CurrencyName.equals(prefs.getUserCurrency())) {
                    break;
                } else {
                    for (int j = 0; j < AppGlobal.currencyRateList.size(); j++) {
                        if (AppGlobal.currencyRateList.get(j).SecondaryCurrencyTicker.equals(p.CurrencyName)) {
                            NewPrice = p.Price / AppGlobal.currencyRateList.get(j).Rate;
                            break;
                        }
                    }
                    for (int j = 0; j < AppGlobal.currencyRateList.size(); j++) {
                        if (AppGlobal.currencyRateList.get(j).SecondaryCurrencyTicker.equals(prefs.getUserCurrency())) {
                            p.Price = NewPrice * AppGlobal.currencyRateList.get(j).Rate;
                            p.CurrencyName = prefs.getUserCurrency();
                            break;
                        }
                    }
                }

            }

        } catch (Exception e) {

        }

        return list;
    }

    public static List<Property> ChangeUnits(List<Property> list, AppPrefs prefs) {
        for (Property p : list) {
            for (int i = 0; i < AppGlobal.unitConverterList.size(); i++) {
                if (p.PropertyBuildUpAreaSizeTypeId == AppGlobal.unitConverterList.get(i).From
                        && prefs.getUserMeasurementType() == AppGlobal.unitConverterList.get(i).To) {
                    p.BuiltUpAreaSize = p.BuiltUpAreaSize * AppGlobal.unitConverterList.get(i).UnitsChanged;
                    p.PropertyBuildUpAreaSizeTypeId = prefs.getUserMeasurementType();
                    p.PropertyBuildUpAreaSizeTypeName = prefs.getUserMeasurement();
                    break;
                }

            }
        }
        return list;
    }

    public static List<Dictunary> ChangeDropdownUnits(List<Dictunary> list, AppPrefs prefs) {
        for (Dictunary p : list) {
            for (int i = 0; i < AppGlobal.unitConverterList.size(); i++) {
                if (p.ID == AppGlobal.unitConverterList.get(i).From
                        && prefs.getUserMeasurementType() == AppGlobal.unitConverterList.get(i).To) {
                    if (!(p.Title.equals("Any Size"))) {
                        if (prefs.getUserMeasurementType() == 1) {

                            p.Title = String.valueOf((int) (Integer.valueOf(p.Title)
                                    * AppGlobal.unitConverterList.get(i).UnitsChanged));
                            p.ID = prefs.getUserMeasurementType();
                            p.Name = prefs.getUserMeasurement().replaceAll("\\.", "");
                            break;
                        } else {
                            DecimalFormat df = new DecimalFormat("##.00");
                            p.Title = df.format(Float.valueOf(p.Title) * AppGlobal.unitConverterList.get(i).UnitsChanged);
                            p.ID = prefs.getUserMeasurementType();
                            p.Name = prefs.getUserMeasurement().replaceAll("\\.", "");
                            break;
                        }
                    }

                }
            }
        }
        return list;
    }

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static String ChangeDateFormat(String Date) {
        String newDateString;
        try {
            String[] str_array = Date.split("T");
            String stringa = str_array[0];
            final String New_FORMAT = "d MMM, yyyy";
            final String OLD_FORMAT = "yyyy-MM-dd";

            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            java.util.Date d = sdf.parse(stringa);
            sdf.applyPattern(New_FORMAT);
            newDateString = sdf.format(d);
        } catch (ParseException e) {
            newDateString = "N/A";
        }
        return newDateString;

    }
}
