package demo.free.com.sqlite_db_demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by obaro on 02/04/2015.
 */
public class ExampleDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CallLogData.db";
    private static final int DATABASE_VERSION = 2;

    public static final String CALL_TABLE_NAME = "calldetails";
    public static final String CALL_COLUMN_ID = "_id";
    public static final String CALL_COLUMN_NUMBER = "mobile_number";
    public static final String CALL_COLUMN_TYPE = "call_type";
    public static final String CALL_COLUMN_DATE_TIME = "date_time";
    public static final String CALL_COLUMN_DURATION = "duration";

    public ExampleDBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + CALL_TABLE_NAME +
                        "(" + CALL_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        CALL_COLUMN_NUMBER + " TEXT, " +
                        CALL_COLUMN_TYPE + " TEXT, " +
                        CALL_COLUMN_DATE_TIME + " TEXT,"+
                        CALL_COLUMN_DURATION + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CALL_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertPerson(String mob_no, String call_type, String date_time,String duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CALL_COLUMN_NUMBER, mob_no);
        contentValues.put(CALL_COLUMN_TYPE, call_type);
        contentValues.put(CALL_COLUMN_DATE_TIME, date_time);
        contentValues.put(CALL_COLUMN_DURATION, duration);

        db.insert(CALL_TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CALL_TABLE_NAME);
        return numRows;
    }

    public boolean updatePerson(Integer id, String mob_no, String call_type, String date_time,String duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_COLUMN_NUMBER, mob_no);
        contentValues.put(CALL_COLUMN_TYPE, call_type);
        contentValues.put(CALL_COLUMN_DATE_TIME, date_time);
        contentValues.put(CALL_COLUMN_DURATION, duration);
        db.update(CALL_TABLE_NAME, contentValues, CALL_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deletePerson(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CALL_TABLE_NAME,
                CALL_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor getPerson(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + CALL_TABLE_NAME + " WHERE " +
                CALL_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAllPersons() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + CALL_TABLE_NAME, null );
        return res;
    }
}