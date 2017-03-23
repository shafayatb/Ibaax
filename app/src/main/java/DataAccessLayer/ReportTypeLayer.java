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
 * Created by iBaax on 3/20/16.
 */
public class ReportTypeLayer extends DatabaseHelper{

    public ReportTypeLayer(Context context) {
        super(context);
    }

    public String Insert(List<Dictunary> list) {

        try {
            Clear();
            SQLiteDatabase db = this.getWritableDatabase();
            for (Dictunary f : list) {
                ContentValues value = new ContentValues();
                value.put("PropertyReportTypeID", f.ID);
                value.put("Name", f.Title);
                db.insert("dbo_ReportType", null, value);
            }
            db.close();
            return "dbo_ReportType Added Successfully";
        } catch (Exception ex) {
            Log.e("error", "dbo_ReportType Insert: " + ex.getMessage());
            return ex.getMessage();
        }
    }

    public List<Dictunary> getReportType() {

        try {
            List<Dictunary> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from dbo_ReportType", null);
            if (cursor.moveToFirst()) {
                do {
                    Dictunary f = new Dictunary();
                    f.ID = cursor.getInt(cursor.getColumnIndex("PropertyReportTypeID"));
                    f.Title = cursor.getString(cursor.getColumnIndex("Name"));
                    f.IsSelected = false;
                    list.add(f);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return list;
        } catch (Exception ex) {
            Log.e("error", "dbo_ReportType getReportType: " + ex.getMessage());
            return null;
        }

    }

    public int getCount(){
        try{
            SQLiteDatabase db=this.getWritableDatabase();
            Cursor mCount=db.rawQuery("select count(*) from dbo_ReportType", null);
            mCount.moveToFirst();
            int count= mCount.getInt(0);
            mCount.close();
            return count;
        }catch (Exception e){
            Log.v("error","dbo_ReportType getCount: "+ e.getMessage());
            return 0;
        }
    }

    public String Clear() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("dbo_ReportType", null, null);
        db.close();
        return "Data Delete Succcessfully";

    }

}

