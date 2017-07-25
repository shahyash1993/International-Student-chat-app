package com.example.yps.assignment_5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yPs on 4/12/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userDetails.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "userdata";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    //constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //methods
    @Override
    public void onCreate(SQLiteDatabase nameDb) {
        nameDb.execSQL("CREATE TABLE IF NOT EXISTS " +TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NICKNAME + " TEXT,"
                + COLUMN_LATITUDE + " DOUBLE,"
                + COLUMN_LONGITUDE + " DOUBLE,"
                + COLUMN_COUNTRY + " TEXT,"
                + COLUMN_STATE + " TEXT,"
                + COLUMN_CITY + " TEXT,"
                + COLUMN_YEAR + " INTEGER,"
                + COLUMN_TIMESTAMP + " TEXT"
                + ");");
        nameDb.execSQL("INSERT INTO "+TABLE_NAME+" ("+COLUMN_ID+" , "+COLUMN_NICKNAME+") VALUES (1,'YASH');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
