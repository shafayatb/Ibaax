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
public class CurrencyTypeLayer extends DatabaseHelper {

    public CurrencyTypeLayer(Context context) {
        super(context);
    }

    public String Insert(List<Dictunary> list) {

        try {
            Clear();
            SQLiteDatabase db = this.getWritableDatabase();
            for (Dictunary f : list) {
                ContentValues value = new ContentValues();
                value.put("CurrencyID", f.ID);
                value.put("CurrencyName", f.Title);
                value.put("Name", f.Name);
                db.insert("dbo_CurrencyType", null, value);
            }
            db.close();
            return "dbo_CurrencyType Added Successfully";
        } catch (Exception ex) {
            Log.e("error", "dbo_CurrencyType Insert: " + ex.getMessage());
            return ex.getMessage();
        }
    }

    public List<Dictunary> getCurrencyType() {

        try {
            List<Dictunary> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from dbo_CurrencyType", null);
            if (cursor.moveToFirst()) {
                do {
                    Dictunary f = new Dictunary();
                    f.ID = cursor.getInt(cursor.getColumnIndex("CurrencyID"));
                    f.Title = cursor.getString(cursor.getColumnIndex("CurrencyName"));
                    f.Name = cursor.getString(cursor.getColumnIndex("Name"));
                    f.IsSelected = true;
                    list.add(f);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        } catch (Exception ex) {
            Log.e("error", "dbo_CurrencyType getCurrencyType: " + ex.getMessage());
            return null;
        }

    }

    public int getCount() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor mCount = db.rawQuery("select count(*) from dbo_CurrencyType", null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count;
        } catch (Exception e) {
            Log.v("error", "dbo_CurrencyType getCount: " + e.getMessage());
            return 0;
        }
    }

    public String Clear() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("dbo_CurrencyType", null, null);
        db.close();
        return "Data Delete Succcessfully";

    }

}
