package com.example.weatherreport.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.weatherreport.db.FavoriteContract;

public class FavoritesProvider extends ContentProvider {
    
    // 定义URI匹配码
    private static final int FAVORITES = 100;
    private static final int FAVORITE_WITH_ID = 101;
    
    // URI匹配器
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    
    // 数据库帮助类
    private FavoritesDbHelper mDbHelper;
    
    // 构建URI匹配器
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteContract.AUTHORITY;
        
        // 添加URI匹配模式
        matcher.addURI(authority, FavoriteContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, FavoriteContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);
        
        return matcher;
    }
    
    @Override
    public boolean onCreate() {
        mDbHelper = new FavoritesDbHelper(getContext());
        return true;
    }
    
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                cursor = db.query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
                
            case FAVORITE_WITH_ID:
                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};
                
                cursor = db.query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        FavoriteContract.FavoriteEntry._ID + "=?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder
                );
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        // 设置通知URI
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        
        return cursor;
    }
    
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;
        
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        // 通知数据变化
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        
        return returnUri;
    }
    
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                rowsDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
                
            case FAVORITE_WITH_ID:
                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};
                
                rowsDeleted = db.delete(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        FavoriteContract.FavoriteEntry._ID + "=?",
                        selectionArguments
                );
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        // 通知数据变化
        if (rowsDeleted > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        
        return rowsDeleted;
    }
    
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;
        
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                rowsUpdated = db.update(FavoriteContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
                
            case FAVORITE_WITH_ID:
                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};
                
                rowsUpdated = db.update(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        values,
                        FavoriteContract.FavoriteEntry._ID + "=?",
                        selectionArguments
                );
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        // 通知数据变化
        if (rowsUpdated > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        
        return rowsUpdated;
    }
    
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                return FavoriteContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return FavoriteContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    
    // 数据库帮助类，用于创建和管理数据库
    static class FavoritesDbHelper extends SQLiteOpenHelper {
        
        private static final String DATABASE_NAME = "favorites.db";
        private static final int DATABASE_VERSION = 1;
        
        FavoritesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                    FavoriteContract.FavoriteEntry.TABLE_NAME + " (" +
                    FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                    FavoriteContract.FavoriteEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE (" + FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME + ") ON CONFLICT REPLACE);";
            
            db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
