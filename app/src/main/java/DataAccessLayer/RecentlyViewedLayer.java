package DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iBaax on 3/30/16.
 */
public class RecentlyViewedLayer extends DatabaseHelper {


    public RecentlyViewedLayer(Context context) {
        super(context);
    }

    public String Insert(int f,String type) {

        try {
            //Clear();
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues value = new ContentValues();
            value.put("PropertyID", f);
            value.put("Type",type);
            db.insert("dbo_RecentViewed", null, value);
            db.close();
            Log.v("viewed", "dbo_RecentViewed IntsertList: ");
            return "dbo_RecentViewed Added Successfully";
        } catch (Exception ex) {
            Log.e("viewed", "dbo_RecentViewed Insert: " + ex.getMessage());
            return ex.getMessage();
        }
    }

    public List<Integer> getRecentlyViewed(String Type) {

        try {
            List<Integer> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from dbo_RecentViewed", null);
            if (cursor.moveToFirst()) {
                do {
                    if(cursor.getString(cursor.getColumnIndex("Type")).equals(Type)) {
                        int f;
                        f = cursor.getInt(cursor.getColumnIndex("PropertyID"));
                        list.add(f);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            Log.v("viewed", "dbo_RecentViewed getList: ");
            return list;
        } catch (Exception ex) {
            Log.e("viewed", "dbo_RecentViewed getRecentViewed: " + ex.getMessage());
            return null;
        }

    }

    public int getCount() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor mCount = db.rawQuery("select count(*) from dbo_RecentViewed", null);
            mCount.moveToFirst();
            int count = mCount.getCount();
            mCount.close();
            return count;
        } catch (Exception e) {
            Log.v("viewed", "dbo_RecentViewed getCount: " + e.getMessage());
            return 0;
        }
    }

    public String Clear() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("dbo_RecentViewed", null, null);
        db.close();
        return "Data Delete Succcessfully";

    }

    public String deleteLastItem(int lastItem) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("dbo_RecentViewed", "PropertyID= ? ", new String[] { Integer.toString(lastItem) });
            db.close();
            Log.v("viewed", "dbo_RecentViewed Delete Success");
            return "Data Delete Successfully";

        } catch (Exception e) {
            Log.v("viewed", "dbo_RecentViewed Delete Fail");
            return "Data Delete UnSuccessful";
        }
    }

}
