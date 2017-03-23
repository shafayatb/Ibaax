package DataAccessLayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by iBaax on 3/5/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    static final int VERSION = 5;
    static final String DATABASE = "iBaaxDataBase";
    static final String TABLE = "dbo_User";

    Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, VERSION);
        this.context = context;

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.i(DatabaseHelper.class.getSimpleName(), "Create database:" + DATABASE);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS dbo_PropertyType (ID integer PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "PropertyID int, PropertyName text,ShortCut text,PropertyType text);");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS dbo_CountryList (ID integer PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "CountryID text, CountryTicker text, CountryName text, PhoneCode text);");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS dbo_UnitType (ID integer PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "UnitID int, UnitName text);");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS dbo_CurrencyType (ID integer PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "CurrencyID int, CurrencyName text,Name text);");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS dbo_ReportType (ID integer PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "PropertyReportTypeID int, Name text);");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS dbo_RecentViewed (ID integer PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "PropertyID int, Type text);");
            // Toast.makeText(context, "db is created", Toast.LENGTH_LONG).show();

            Log.v("esty", "Database Created");
        } catch (Exception e) {

            Log.e("esty", "DatabaseHelper/OnCreate-> Error: " + e.getMessage());

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS dbo_PropertyType");
        db.execSQL("DROP TABLE IF EXISTS dbo_CountryList");
        db.execSQL("DROP TABLE IF EXISTS dbo_UnitType");
        db.execSQL("DROP TABLE IF EXISTS dbo_CurrencyType");
        db.execSQL("DROP TABLE IF EXISTS dbo_ReportType");
        db.execSQL("DROP TABLE IF EXISTS dbo_RecentViewed");
        onCreate(db);
        Log.v("esty", "Database Upgraded");
    }

    public String DropTable() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DROP TABLE " + TABLE);
            return "Drop Table Succesfully";
        } catch (Exception ex) {

            return ex.getMessage();

        }
    }

    public String DropDb() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            // db.delete(DATABASE, whereClause, whereArgs)
            return "Drop Table Succesfully";
        } catch (Exception ex) {

            return ex.getMessage();

        }
    }

}
