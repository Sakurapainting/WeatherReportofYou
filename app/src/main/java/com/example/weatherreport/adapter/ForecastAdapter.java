package com.example.weatherreport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherreport.R;
import com.example.weatherreport.model.ForecastResponse.ForecastItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<ForecastItem> forecastItems;
    private Context context;

    public ForecastAdapter(Context context, List<ForecastItem> forecastItems) {
        this.context = context;
        this.forecastItems = forecastItems;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastItem forecastItem = forecastItems.get(position);
        
        // 格式化日期和时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM d", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date(forecastItem.getDateTime() * 1000);
        
        holder.dateTextView.setText(dateFormat.format(date));
        holder.timeTextView.setText(timeFormat.format(date));
        
        // 设置温度
        holder.tempTextView.setText(String.format(Locale.getDefault(), "%.1f°C", forecastItem.getMain().getTemperature()));
        
        // 加载天气图标
        if (forecastItem.getWeather() != null && !forecastItem.getWeather().isEmpty()) {
            String iconCode = forecastItem.getWeather().get(0).getIcon();
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            Glide.with(context)
                    .load(iconUrl)
                    .into(holder.iconImageView);
        }
    }

    @Override
    public int getItemCount() {
        return forecastItems != null ? forecastItems.size() : 0;
    }

    public void updateForecastList(List<ForecastItem> newForecastItems) {
        this.forecastItems = newForecastItems;
        notifyDataSetChanged();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView timeTextView;
        TextView tempTextView;
        ImageView iconImageView;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.tv_forecast_date);
            timeTextView = itemView.findViewById(R.id.tv_forecast_time);
            tempTextView = itemView.findViewById(R.id.tv_forecast_temp);
            iconImageView = itemView.findViewById(R.id.iv_forecast_icon);
        }
    }
}