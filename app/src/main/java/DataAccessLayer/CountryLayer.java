package DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Entity.Country;
import Entity.Dictunary;

/**
 * Created by iBaax on 3/5/16.
 */
public class CountryLayer extends DatabaseHelper {

    public CountryLayer(Context context) {
        super(context);
    }

    public String Insert(List<Country> list) {

        try {
            Clear();
            SQLiteDatabase db = this.getWritableDatabase();
            for (Country f : list) {
                ContentValues value = new ContentValues();
                value.put("CountryID", f.CountryID);
                value.put("CountryTicker", f.CountryTicker);
                value.put("CountryName", f.Name);
                value.put("PhoneCode", f.PhoneCode);
                db.insert("dbo_CountryList", null, value);
            }
            db.close();
            return "Country Added Successfully";
        } catch (Exception ex) {
            Log.e("error", "dbo_CountryList Insert: " + ex.getMessage());
            return ex.getMessage();
        }
    }

    public List<Country> getCountry() {

        try {
            List<Country> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from dbo_CountryList", null);
            if (cursor.moveToFirst()) {
                do {
                    Country f = new Country();
                    f.CountryID = cursor.getString(cursor.getColumnIndex("CountryID"));
                    f.CountryTicker = cursor.getString(cursor.getColumnIndex("CountryTicker"));
                    f.Name = cursor.getString(cursor.getColumnIndex("CountryName"));
                    f.PhoneCode = cursor.getString(cursor.getColumnIndex("PhoneCode"));

                    list.add(f);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        } catch (Exception ex) {
            Log.e("error", "dbo_Country getCountry: " + ex.getMessage());
            return null;
        }

    }

    public int getCount() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor mCount = db.rawQuery("select count(*) from dbo_CountryList", null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count;
        } catch (Exception e) {
            Log.v("error", "dbo_CountryList getCount: " + e.getMessage());
            return 0;
        }
    }

    public String Clear() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("dbo_CountryList", null, null);
        db.close();
        return "Data Delete Successful";

    }
}

