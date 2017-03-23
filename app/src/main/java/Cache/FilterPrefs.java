package Cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by iBaax on 4/27/16.
 */
public class FilterPrefs {

    private SharedPreferences filterSharedPrefs;
    private SharedPreferences.Editor filterEditor;


    public FilterPrefs(Context context) {

        this.filterSharedPrefs = context.getSharedPreferences("FILTER_PREFS", 0 | Context.MODE_MULTI_PROCESS);
        this.filterEditor = filterSharedPrefs.edit();
    }

    public void clear() {
        filterEditor.clear().commit();
    }

    public Boolean getSwitchSale() {
        return filterSharedPrefs.getBoolean("SwitchSale", true);
    }

    public void setSwitchSale(boolean checked) {
        filterEditor.putBoolean("SwitchSale", checked).commit();
    }

    public Boolean getSwitchRent() {
        return filterSharedPrefs.getBoolean("SwitchRent", true);
    }

    public void setSwitchRent(boolean checked) {
        filterEditor.putBoolean("SwitchRent", checked).commit();
    }

    public int getSaleMinPrice() {
        return filterSharedPrefs.getInt("MinPos", 0);
    }

    public void setSaleMinPrice(int min) {
        filterEditor.putInt("MinPos", min).commit();
    }

    public int getSaleMax() {
        return filterSharedPrefs.getInt("MaxPos", 0);
    }

    public void setSaleMax(int min) {
        filterEditor.putInt("MaxPos", min).commit();
    }

    public String getBedNo() {
        return filterSharedPrefs.getString("BedNo", "");
    }

    public void setBedNo(String bed) {
        filterEditor.putString("BedNo", bed).commit();
    }

    public String getBathNo() {
        return filterSharedPrefs.getString("BathNo", "");
    }

    public void setBathNo(String bath) {
        filterEditor.putString("BathNo", bath).commit();
    }

    public int getYear() {
        return filterSharedPrefs.getInt("Year", 0);
    }

    public void setYear(int year) {
        filterEditor.putInt("MaxYear", year).commit();
    }

    public int getMaxYear() {
        return filterSharedPrefs.getInt("MaxYear", 0);
    }

    public void setMaxYear(int year) {
        filterEditor.putInt("Year", year).commit();
    }

    public int getMinBuild() {
        return filterSharedPrefs.getInt("MinBuild", 0);
    }

    public void setMinBuild(int minBuild) {
        filterEditor.putInt("MinBuild", minBuild).commit();
    }

    public int getMaxBuild() {
        return filterSharedPrefs.getInt("MaxBuild", 0);
    }

    public void setMaxBuild(int maxBuild) {
        filterEditor.putInt("MaxBuild", maxBuild).commit();
    }

    public int getMinLot() {
        return filterSharedPrefs.getInt("MinLot", 0);
    }

    public void setMinLot(int minLot) {
        filterEditor.putInt("MinLot", minLot).commit();
    }

    public int getMaxLot() {
        return filterSharedPrefs.getInt("MaxLot", 0);
    }

    public void setMaxLot(int maxLot) {
        filterEditor.putInt("MaxLot", maxLot).commit();
    }

}
