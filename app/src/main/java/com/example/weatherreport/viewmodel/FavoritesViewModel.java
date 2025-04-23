package com.example.weatherreport.viewmodel;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherreport.db.FavoriteContract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesViewModel extends ViewModel {
    
    private final MutableLiveData<Cursor> favoritesData = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    // 获取收藏城市数据的LiveData
    public LiveData<Cursor> getFavorites() {
        return favoritesData;
    }
    
    // 加载收藏城市数据
    public void loadFavorites(Context context) {
        // 使用Executor替代AsyncTask
        executor.execute(() -> {
            // 查询所有收藏城市
            Cursor cursor = context.getContentResolver().query(
                    FavoriteContract.FavoriteEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME + " ASC" // 按城市名称升序排列
            );
            
            // 在主线程上更新UI
            mainHandler.post(() -> favoritesData.setValue(cursor));
        });
    }
    
    // 清理资源
    @Override
    protected void onCleared() {
        super.onCleared();
        // 关闭Cursor
        Cursor cursor = favoritesData.getValue();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        // 关闭线程池
        executor.shutdown();
    }
}
