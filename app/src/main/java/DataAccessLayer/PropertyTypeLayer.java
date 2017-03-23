package DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Entity.Dictunary;

/**
 * Created by iBaax on 3/5/16.
 */
public class PropertyTypeLayer extends DatabaseHelper {

    public PropertyTypeLayer(Context context) {
        super(context);
    }

    public String Insert(List<Dictunary> list) {

        try {
            Clear();
            SQLiteDatabase db = this.getWritableDatabase();
            for (Dictunary f : list) {
                ContentValues value = new ContentValues();
                value.put("PropertyID", f.ID);
                value.put("PropertyName", f.Title);
                value.put("ShortCut", f.ShortCuts);
                value.put("PropertyType", f.Name);
                db.insert("dbo_PropertyType", null, value);
            }
            db.close();
            return "PropertyType Added Successfully";
        } catch (Exception ex) {
            Log.e("error", "dbo_PropertyType Insert: " + ex.getMessage());
            return ex.getMessage();
        }
    }

    public List<Dictunary> getPropertyType() {

        try {
            List<Dictunary> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from dbo_PropertyType", null);
            if (cursor.moveToFirst()) {
                do {
                    Dictunary f = new Dictunary();
                    f.Name = cursor.getString(cursor.getColumnIndex("PropertyType"));
                    if (f.Name.equals("Residential")) {
                        f.ID = cursor.getInt(cursor.getColumnIndex("PropertyID"));
                        f.Title = cursor.getString(cursor.getColumnIndex("PropertyName"));
                        f.ShortCuts = cursor.getString(cursor.getColumnIndex("ShortCut"));
                        f.IsSelected = true;
                        list.add(f);
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        } catch (Exception ex) {
            Log.e("error", "dbo_PropertyType getPropertyType: " + ex.getMessage());
            return null;
        }

    }

    public List<Dictunary> getPropertyTypeCommercial() {

        try {
            List<Dictunary> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from dbo_PropertyType", null);
            if (cursor.moveToFirst()) {
                do {
                    Dictunary f = new Dictunary();
                    f.Name = cursor.getString(cursor.getColumnIndex("PropertyType"));
                    if (f.Name.equals("Commercial")) {
                        f.ID = cursor.getInt(cursor.getColumnIndex("PropertyID"));
                        f.ShortCuts = cursor.getString(cursor.getColumnIndex("ShortCut"));
                        f.Title = cursor.getString(cursor.getColumnIndex("PropertyName"));
                        f.IsSelected = true;
                        list.add(f);
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        } catch (Exception ex) {
            Log.e("error", "dbo_PropertyType getPropertyType: " + ex.getMessage());
            return null;
        }

    }


    public int getCount() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor mCount = db.rawQuery("select count(*) from dbo_PropertyType", null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count;
        } catch (Exception e) {
            Log.v("error", "dbo_PropertyType getCount: " + e.getMessage());
            return 0;
        }
    }

    public String Clear() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("dbo_PropertyType", null, null);
        db.close();
        return "Data Delete Succcessfully";

    }

}
