package com.example.weatherreport.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteDbHelper extends SQLiteOpenHelper {
    
    // 数据库名和版本
    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 1;
    
    // 建表SQL语句
    private static final String SQL_CREATE_FAVORITES_TABLE =
            "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME + " (" +
                    FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                    FavoriteContract.FavoriteEntry.COLUMN_COUNTRY_CODE + " TEXT, " +
                    FavoriteContract.FavoriteEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE (" + FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME + 
                    ") ON CONFLICT REPLACE)";
    
    // 删表SQL语句
    private static final String SQL_DELETE_FAVORITES_TABLE =
            "DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME;
    
    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单版本升级策略：删除旧表，创建新表
        db.execSQL(SQL_DELETE_FAVORITES_TABLE);
        onCreate(db);
    }
}
