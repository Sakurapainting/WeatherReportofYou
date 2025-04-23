package com.example.weatherreport.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherreport.R;
import com.example.weatherreport.db.FavoriteContract;

public class FavoriteCitiesAdapter extends RecyclerView.Adapter<FavoriteCitiesAdapter.FavoriteCityViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private final OnFavoriteCityClickListener mClickListener;

    // 创建接口用于处理城市点击和删除事件
    public interface OnFavoriteCityClickListener {
        void onCityClick(String cityName);
        void onCityDelete(long id);
    }

    public FavoriteCitiesAdapter(Context context, Cursor cursor, OnFavoriteCityClickListener listener) {
        mContext = context;
        mCursor = cursor;
        mClickListener = listener;
    }

    @NonNull
    @Override
    public FavoriteCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_favorite_city, parent, false);
        return new FavoriteCityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCityViewHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }

        String cityName = mCursor.getString(mCursor.getColumnIndexOrThrow(FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME));
        long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(FavoriteContract.FavoriteEntry._ID));

        // 设置城市名称
        holder.cityNameTextView.setText(cityName);

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onCityClick(cityName);
            }
        });

        // 设置删除按钮点击事件
        holder.deleteButton.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onCityDelete(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    // 更新Cursor数据
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    // ViewHolder类
    static class FavoriteCityViewHolder extends RecyclerView.ViewHolder {
        TextView cityNameTextView;
        Button deleteButton;

        FavoriteCityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.tv_favorite_city_name);
            deleteButton = itemView.findViewById(R.id.btn_delete_favorite);
        }
    }
}
