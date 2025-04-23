package com.example.weatherreport.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FavoriteContract {
    
    // 防止实例化
    private FavoriteContract() {}
    
    // ContentProvider的授权名称
    public static final String AUTHORITY = "com.example.weatherreport.provider";
    
    // 内容URI的基本部分
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    
    // 可能的路径
    public static final String PATH_FAVORITES = "favorites";
    
    // 收藏城市表结构定义
    public static class FavoriteEntry implements BaseColumns {
        // 表的内容URI
        public static final Uri CONTENT_URI = 
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        
        // 表名
        public static final String TABLE_NAME = "favorites";
        
        // 列名
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COUNTRY_CODE = "country_code";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        
        // MIME类型
        public static final String CONTENT_TYPE = 
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE = 
                "vnd.android.cursor.item/" + AUTHORITY + "/" + PATH_FAVORITES;
    }
}
