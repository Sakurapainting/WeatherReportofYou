package com.example.weatherreport;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherreport.adapter.FavoriteCitiesAdapter;
import com.example.weatherreport.db.FavoriteContract;
import com.example.weatherreport.viewmodel.FavoritesViewModel;

public class FavoritesActivity extends AppCompatActivity implements
        FavoriteCitiesAdapter.OnFavoriteCityClickListener {
    
    private RecyclerView mRecyclerView;
    private FavoriteCitiesAdapter mAdapter;
    private TextView mEmptyView;
    private FavoritesViewModel mViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        
        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // 初始化视图
        mRecyclerView = findViewById(R.id.recycler_favorites);
        mEmptyView = findViewById(R.id.tv_empty_favorites);
        
        // 设置RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new FavoriteCitiesAdapter(this, null, this);
        mRecyclerView.setAdapter(mAdapter);
        
        // 初始化ViewModel并观察数据变化
        mViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        mViewModel.getFavorites().observe(this, cursor -> {
            if (cursor != null && cursor.getCount() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mAdapter.swapCursor(cursor);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        });
        
        // 加载数据
        mViewModel.loadFavorites(this);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 替换过时的onBackPressed()方法
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCityClick(String cityName) {
        // 点击城市项，返回MainActivity并使用选择的城市
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_city", cityName);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    
    @Override
    public void onCityDelete(long id) {
        // 删除收藏的城市
        int rowsDeleted = getContentResolver().delete(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                FavoriteContract.FavoriteEntry._ID + "=?",
                new String[]{String.valueOf(id)}
        );
        
        if (rowsDeleted > 0) {
            Toast.makeText(this, R.string.favorite_removed, Toast.LENGTH_SHORT).show();
            // 重新加载数据
            mViewModel.loadFavorites(this);
        }
    }
}
