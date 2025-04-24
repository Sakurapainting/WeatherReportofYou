package com.example.weatherreport.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherreport.R;
import com.example.weatherreport.model.ForecastResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherChartFragment extends Fragment {

    private LineChart mChart;
    private TabLayout mTabLayout;
    private List<ForecastResponse.ForecastItem> mForecastItems;
    private List<ForecastResponse.ForecastItem> mFilteredItems;
    
    // 图表类型常量
    public static final int CHART_TEMPERATURE = 0;
    public static final int CHART_FEELS_LIKE = 1;
    public static final int CHART_HUMIDITY = 2;
    public static final int CHART_PRESSURE = 3;
    public static final int CHART_WIND = 4;
    
    private int mCurrentChartType = CHART_TEMPERATURE;
    
    public static WeatherChartFragment newInstance(List<ForecastResponse.ForecastItem> forecastItems) {
        WeatherChartFragment fragment = new WeatherChartFragment();
        fragment.setForecastItems(forecastItems);
        return fragment;
    }
    
    public void setForecastItems(List<ForecastResponse.ForecastItem> forecastItems) {
        // 对天气预报数据按时间进行排序，确保时间轴数据正确
        if (forecastItems != null) {
            this.mForecastItems = new ArrayList<>(forecastItems);
            Collections.sort(this.mForecastItems, new Comparator<ForecastResponse.ForecastItem>() {
                @Override
                public int compare(ForecastResponse.ForecastItem o1, ForecastResponse.ForecastItem o2) {
                    return Long.compare(o1.getDateTime(), o2.getDateTime());
                }
            });
            
            // 筛选整点的数据
            filterHourlyData();
        } else {
            this.mForecastItems = null;
            this.mFilteredItems = null;
        }
        
        if (mChart != null && isAdded()) {
            updateChart();
        }
    }
    
    /**
     * 筛选整点数据，每天固定时间点 (例如：9:00, 12:00, 15:00, 18:00, 21:00)
     */
    private void filterHourlyData() {
        if (mForecastItems == null) return;
        
        mFilteredItems = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        
        // 要保留的整点小时列表 (9时, 12时, 15时, 18时, 21时)
        int[] hourlyPoints = {9, 12, 15, 18, 21};
        
        for (ForecastResponse.ForecastItem item : mForecastItems) {
            cal.setTimeInMillis(item.getDateTime() * 1000);
            int itemHour = cal.get(Calendar.HOUR_OF_DAY);
            
            // 检查当前小时是否是我们要保留的整点
            for (int hour : hourlyPoints) {
                if (itemHour == hour) {
                    mFilteredItems.add(item);
                    break;
                }
            }
        }
        
        // 如果过滤后数据太少，则保留原始数据
        if (mFilteredItems.size() < 4) {
            mFilteredItems = new ArrayList<>(mForecastItems);
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_chart, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mChart = view.findViewById(R.id.chart_weather);
        setupChart();
        
        mTabLayout = view.findViewById(R.id.tab_layout);
        setupTabLayout();
    }
    
    private void setupTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.temperature_trend));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.feels_like_trend));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.humidity_trend));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.pressure_trend));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.wind_trend));
        
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentChartType = tab.getPosition();
                updateChart();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 不需要处理
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 不需要处理
            }
        });
    }
    
    private void setupChart() {
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setDrawGridBackground(false);
        mChart.setExtraOffsets(10, 10, 10, 20); // 四周留出更多空间
        mChart.setBackgroundColor(Color.WHITE);
        
        // 设置X轴
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); 
        xAxis.setLabelCount(5); // 控制显示的标签数量
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new TimeAxisFormatter());
        xAxis.setLabelRotationAngle(-45); // 标签倾斜角度，避免重叠
        
        // 设置Y轴
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setAxisLineColor(Color.GRAY);
        leftAxis.setTextSize(10f);
        leftAxis.setAxisMinimum(0f); // 根据实际情况调整
        
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        
        // 设置图例
        Legend legend = mChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        legend.setTypeface(Typeface.DEFAULT_BOLD);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        
        // 为交互添加动画效果
        mChart.animateX(1500);
        
        // 若已有数据，立即更新图表
        if (mFilteredItems != null && !mFilteredItems.isEmpty()) {
            updateChart();
        } else {
            mChart.setNoDataText(getString(R.string.no_chart_data));
            mChart.setNoDataTextColor(Color.GRAY);
        }
    }
    
    private void updateChart() {
        if (mFilteredItems == null || mFilteredItems.isEmpty()) {
            return;
        }
        
        ArrayList<Entry> entries = new ArrayList<>();
        
        // 根据当前选中的图表类型获取相应数据
        for (int i = 0; i < mFilteredItems.size(); i++) {
            ForecastResponse.ForecastItem item = mFilteredItems.get(i);
            float value = 0;
            
            switch (mCurrentChartType) {
                case CHART_TEMPERATURE:
                    value = item.getMain().getTemperature();
                    break;
                case CHART_FEELS_LIKE:
                    value = item.getMain().getFeelsLike();
                    break;
                case CHART_HUMIDITY:
                    value = item.getMain().getHumidity();
                    break;
                case CHART_PRESSURE:
                    value = item.getMain().getPressure();
                    break;
                case CHART_WIND:
                    value = item.getWind() != null ? item.getWind().getSpeed() : 0;
                    break;
            }
            
            // 使用索引作为X轴的值，通过ValueFormatter来格式化显示
            entries.add(new Entry(i, value));
        }
        
        // 创建数据集
        LineDataSet dataSet;
        int color;
        String label;
        
        switch (mCurrentChartType) {
            case CHART_TEMPERATURE:
                label = getString(R.string.temperature) + " (°C)";
                color = Color.parseColor("#FF5722");  // 深橙色
                break;
            case CHART_FEELS_LIKE:
                label = getString(R.string.feels_like) + " (°C)";
                color = Color.parseColor("#FF9800");  // 橙色
                break;
            case CHART_HUMIDITY:
                label = getString(R.string.humidity) + " (%)";
                color = Color.parseColor("#2196F3");  // 蓝色
                break;
            case CHART_PRESSURE:
                label = getString(R.string.pressure) + " (hPa)";
                color = Color.parseColor("#4CAF50");  // 绿色
                break;
            case CHART_WIND:
                label = getString(R.string.wind_speed) + " (m/s)";
                color = Color.parseColor("#00BCD4");  // 青色
                break;
            default:
                label = "";
                color = Color.BLACK;
        }
        
        dataSet = new LineDataSet(entries, label);
        
        // 设置数据集样式
        dataSet.setColor(color);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(2.5f);
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.DKGRAY);
        
        // 设置数值格式化器
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (mCurrentChartType == CHART_TEMPERATURE || mCurrentChartType == CHART_FEELS_LIKE) {
                    return String.format(Locale.getDefault(), "%.1f°", value);
                } else if (mCurrentChartType == CHART_HUMIDITY) {
                    return String.format(Locale.getDefault(), "%.0f%%", value);
                } else if (mCurrentChartType == CHART_WIND) {
                    return String.format(Locale.getDefault(), "%.1f", value);
                } else {
                    return String.format(Locale.getDefault(), "%.0f", value);
                }
            }
        });
        
        // 更新图表数据
        LineData lineData = new LineData(dataSet);
        mChart.setData(lineData);
        
        // 调整Y轴范围以适应数据
        if (mCurrentChartType == CHART_TEMPERATURE || mCurrentChartType == CHART_FEELS_LIKE) {
            // 找出温度的最小和最大值
            float minTemp = Float.MAX_VALUE;
            float maxTemp = Float.MIN_VALUE;
            for (Entry entry : entries) {
                minTemp = Math.min(minTemp, entry.getY());
                maxTemp = Math.max(maxTemp, entry.getY());
            }
            // 设置Y轴范围，留出一些边距
            float margin = (maxTemp - minTemp) * 0.2f;
            mChart.getAxisLeft().setAxisMinimum(minTemp - margin);
            mChart.getAxisLeft().setAxisMaximum(maxTemp + margin);
        } else if (mCurrentChartType == CHART_HUMIDITY) {
            mChart.getAxisLeft().setAxisMinimum(0f);
            mChart.getAxisLeft().setAxisMaximum(100f);
        }
        
        // 刷新图表
        mChart.invalidate();
    }
    
    /**
     * 自定义X轴时间格式化器
     */
    private class TimeAxisFormatter extends ValueFormatter {
        private final SimpleDateFormat dayTimeFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
        
        @Override
        public String getFormattedValue(float value) {
            int index = (int) value;
            if (mFilteredItems != null && index >= 0 && index < mFilteredItems.size()) {
                long timestamp = mFilteredItems.get(index).getDateTime() * 1000;
                return dayTimeFormat.format(new Date(timestamp));
            }
            return "";
        }
    }
}
