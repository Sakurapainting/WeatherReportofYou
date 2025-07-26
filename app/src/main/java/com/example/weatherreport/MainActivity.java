package com.example.weatherreport;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherreport.adapter.ForecastAdapter;
import com.example.weatherreport.api.RetrofitClient;
import com.example.weatherreport.api.WeatherApiService;
import com.example.weatherreport.db.FavoriteContract;
import com.example.weatherreport.fragment.WeatherChartFragment;
import com.example.weatherreport.model.ForecastResponse;
import com.example.weatherreport.model.WeatherData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private static final String API_KEY = BuildConfig.WEATHER_API_KEY; // 从 BuildConfig 读取 API 密钥

    private static final String UNITS = "metric"; // 使用摄氏度
    private static final int REQUEST_FAVORITES = 1001;

    // UI组件
    private EditText cityEditText;
    private Button searchButton;
    private Button favoriteButton;
    private CardView currentWeatherCard;
    private TextView cityNameTextView;
    private TextView weatherDescTextView;
    private TextView temperatureTextView;
    private TextView feelsLikeTextView;
    private TextView humidityTextView;
    private TextView windSpeedTextView;
    private ImageView weatherIconImageView;
    private TextView forecastTitleTextView;
    private RecyclerView forecastRecyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private MenuItem favoriteMenuItem;
    private MenuItem favoritesMenuItem;
    private CardView weatherChartCard;
    private FrameLayout chartContainer;

    // API服务
    private WeatherApiService weatherApiService;
    private ForecastAdapter forecastAdapter;
    
    // 图表Fragment
    private WeatherChartFragment weatherChartFragment;

    // 当前选中的城市
    private String currentCity = "";
    private boolean isFavorite = false;
    
    // 保存预报数据
    private List<ForecastResponse.ForecastItem> forecastItems;

    // 启动收藏城市Activity的结果处理器
    private ActivityResultLauncher<Intent> favoritesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String selectedCity = result.getData().getStringExtra("selected_city");
                    if (selectedCity != null && !selectedCity.isEmpty()) {
                        cityEditText.setText(selectedCity);
                        searchWeather();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // 验证 API key 配置
        if (!ApiKeyValidator.validateApiKey()) {
            Toast.makeText(this, "API key 未配置，请检查 local.properties 文件", Toast.LENGTH_LONG).show();
            Log.e("WeatherApp", "API key 配置错误");
        }


        // 初始化API服务
        weatherApiService = RetrofitClient.getWeatherApiService();

        // 初始化UI组件
        initViews();

        // 设置点击事件
        searchButton.setOnClickListener(v -> searchWeather());
    }

    private void initViews() {
        cityEditText = findViewById(R.id.edit_city);
        searchButton = findViewById(R.id.btn_search);
        favoriteButton = findViewById(R.id.btn_toggle_favorite);
        Button favoritesButton = findViewById(R.id.btn_favorites);
        currentWeatherCard = findViewById(R.id.current_weather_card);
        cityNameTextView = findViewById(R.id.tv_city_name);
        weatherDescTextView = findViewById(R.id.tv_weather_description);
        temperatureTextView = findViewById(R.id.tv_temperature);
        feelsLikeTextView = findViewById(R.id.tv_feels_like);
        humidityTextView = findViewById(R.id.tv_humidity);
        windSpeedTextView = findViewById(R.id.tv_wind_speed);
        weatherIconImageView = findViewById(R.id.iv_weather_icon);
        forecastTitleTextView = findViewById(R.id.tv_forecast_title);
        forecastRecyclerView = findViewById(R.id.recycler_forecast);
        progressBar = findViewById(R.id.progress_bar);
        errorTextView = findViewById(R.id.tv_error);
        weatherChartCard = findViewById(R.id.weather_chart_card);
        chartContainer = findViewById(R.id.chart_container);

        // 设置收藏按钮点击事件
        favoriteButton.setOnClickListener(v -> toggleFavorite());
        
        // 设置收藏城市列表按钮点击事件
        favoritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritesActivity.class);
            favoritesLauncher.launch(intent);
        });

        // 设置RecyclerView
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        forecastAdapter = new ForecastAdapter(this, new ArrayList<>());
        forecastRecyclerView.setAdapter(forecastAdapter);

        Button switchLangBtn = findViewById(R.id.btn_switch_language);
        switchLangBtn.setOnClickListener(v -> {
            Locale current = getResources().getConfiguration().locale;
            Locale newLocale = current.getLanguage().equals("zh") ? Locale.ENGLISH : Locale.CHINESE;
            switchLanguage(newLocale);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        favoriteMenuItem = menu.findItem(R.id.action_toggle_favorite);
        favoritesMenuItem = menu.findItem(R.id.action_favorites);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorites) {
            // 启动收藏城市列表Activity
            Intent intent = new Intent(this, FavoritesActivity.class);
            favoritesLauncher.launch(intent);
            return true;
        } else if (id == R.id.action_toggle_favorite) {
            // 切换城市收藏状态
            toggleFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchWeather() {
        String cityName = cityEditText.getText().toString().trim();
        if (cityName.isEmpty()) {
            Toast.makeText(this, "请输入城市名称", Toast.LENGTH_SHORT).show();
            return;
        }

        // 隐藏切换语言按钮
        Button switchLangBtn = findViewById(R.id.btn_switch_language);
        switchLangBtn.setVisibility(View.GONE);
        
        // 显示加载进度条，隐藏其他视图
        showLoading();

        // 更新当前城市
        currentCity = cityName;

        // 获取当前天气
        getCurrentWeather(cityName);

        // 获取天气预报
        getForecast(cityName);
    }

    private void switchLanguage(Locale locale) {
        // 更新应用语言
        android.content.res.Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        recreate(); // 重启Activity以应用新语言
    }
    private void getCurrentWeather(String cityName) {
        // 对于日本城市，确保使用英文名称
        final String finalCityName;
        if (cityName.equalsIgnoreCase("tokyo") || cityName.equalsIgnoreCase("东京")) {
            finalCityName = "Tokyo";
        } else {
            finalCityName = cityName;
        }

        weatherApiService.getCurrentWeather(finalCityName, UNITS, API_KEY).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    updateCurrentWeatherUI(response.body());
                    // 更新收藏图标状态
                    checkIfFavorite();
                } else {
                    // 显示详细错误信息
                    final StringBuilder errorBuilder = new StringBuilder();
                    if (response.code() == 404) {
                        errorBuilder.append("找不到城市: ").append(finalCityName).append("，请检查拼写");
                    } else if (response.code() == 401) {
                        errorBuilder.append("API密钥无效");
                    } else {
                        errorBuilder.append("错误码: ").append(response.code());
                    }
                    showError(errorBuilder.toString());
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                hideLoading();
                showError("网络连接失败: " + t.getMessage());
            }
        });
    }

    private void getForecast(String cityName) {
        // 对于日本城市，确保使用英文名称
        final String finalCityName;
        if (cityName.equalsIgnoreCase("tokyo") || cityName.equalsIgnoreCase("东京")) {
            finalCityName = "Tokyo";
        } else {
            finalCityName = cityName;
        }

        weatherApiService.getForecast(finalCityName, UNITS, API_KEY).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 保存预报数据
                    forecastItems = response.body().getForecastList();
                    if (forecastItems != null && !forecastItems.isEmpty()) {
                        // 对数据进行预处理，按时间排序
                        Collections.sort(forecastItems, (item1, item2) -> 
                            Long.compare(item1.getDateTime(), item2.getDateTime()));
                            
                        // 日志输出检查时间数据 (可选)
                        if (!forecastItems.isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Log.d("WeatherApp", "第一个时间点: " + sdf.format(new Date(forecastItems.get(0).getDateTime() * 1000)));
                            Log.d("WeatherApp", "最后时间点: " + sdf.format(new Date(forecastItems.get(forecastItems.size()-1).getDateTime() * 1000)));
                        }
                    }
                    updateForecastUI(response.body());
                    setupWeatherChart();
                } else {
                    // 记录错误但不显示，因为主要错误已在getCurrentWeather中显示
                    Log.e("WeatherApp", "获取预报失败: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                // 错误处理已在getCurrentWeather中处理
                Log.e("WeatherApp", "获取预报网络错误: " + t.getMessage());
            }
        });
    }

    private void updateCurrentWeatherUI(WeatherData weatherData) {
        currentWeatherCard.setVisibility(View.VISIBLE);
        forecastTitleTextView.setVisibility(View.VISIBLE);
        favoriteButton.setVisibility(View.VISIBLE);

        // 显示收藏按钮
        if (favoriteMenuItem != null) {
            favoriteMenuItem.setVisible(true);
        }

        // 设置城市名称
        cityNameTextView.setText(weatherData.getCityName());

        // 更新当前城市为响应返回的准确城市名
        currentCity = weatherData.getCityName();

        // 设置天气描述
        if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
            weatherDescTextView.setText(weatherData.getWeather().get(0).getDescription());

            // 加载天气图标
            String iconCode = weatherData.getWeather().get(0).getIcon();
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            Glide.with(this)
                    .load(iconUrl)
                    .into(weatherIconImageView);
        }

        // 设置温度信息
        temperatureTextView.setText(String.format(Locale.getDefault(), "%.1f°C", weatherData.getMain().getTemperature()));
        feelsLikeTextView.setText(String.format(Locale.getDefault(), "%.1f°C", weatherData.getMain().getFeelsLike()));
        humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", weatherData.getMain().getHumidity()));

        // 设置风速
        windSpeedTextView.setText(String.format(Locale.getDefault(), "%.1f m/s", weatherData.getWind().getSpeed()));
    }

    private void updateForecastUI(ForecastResponse forecastResponse) {
        if (forecastResponse.getForecastList() != null && !forecastResponse.getForecastList().isEmpty()) {
            forecastRecyclerView.setVisibility(View.VISIBLE);
            forecastAdapter.updateForecastList(forecastResponse.getForecastList());
        }
    }
    
    private void setupWeatherChart() {
        if (forecastItems == null || forecastItems.isEmpty()) {
            weatherChartCard.setVisibility(View.GONE);
            return;
        }
        
        weatherChartCard.setVisibility(View.VISIBLE);
        
        // 创建或更新天气图表Fragment
        if (weatherChartFragment == null) {
            weatherChartFragment = WeatherChartFragment.newInstance(forecastItems);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.chart_container, weatherChartFragment);
            transaction.commit();
        } else {
            weatherChartFragment.setForecastItems(forecastItems);
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        currentWeatherCard.setVisibility(View.GONE);
        forecastTitleTextView.setVisibility(View.GONE);
        forecastRecyclerView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
        favoriteButton.setVisibility(View.GONE);
        weatherChartCard.setVisibility(View.GONE);

        // 隐藏收藏按钮
        if (favoriteMenuItem != null) {
            favoriteMenuItem.setVisible(false);
        }
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String errorMessage) {
        errorTextView.setText(errorMessage);
        errorTextView.setVisibility(View.VISIBLE);
        currentWeatherCard.setVisibility(View.GONE);
        forecastTitleTextView.setVisibility(View.GONE);
        forecastRecyclerView.setVisibility(View.GONE);
        favoriteButton.setVisibility(View.GONE);
        weatherChartCard.setVisibility(View.GONE);

        // 显示Toast提示，增强用户体验
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

        // 隐藏收藏按钮
        if (favoriteMenuItem != null) {
            favoriteMenuItem.setVisible(false);
        }
    }

    // 兼容旧方法调用
    private void showError() {
        showError("获取天气数据失败，请检查网络连接或城市名称");
    }

    // 检查当前城市是否已收藏
    private void checkIfFavorite() {
        if (currentCity.isEmpty()) {
            return;
        }

        Cursor cursor = getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null,
                FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME + "=?",
                new String[]{currentCity},
                null
        );

        isFavorite = cursor != null && cursor.getCount() > 0;

        // 更新收藏按钮状态
        updateFavoriteButtonState();

        if (cursor != null) {
            cursor.close();
        }
    }

    // 更新收藏按钮的图标和文字
    private void updateFavoriteButtonState() {
        if (isFavorite) {
            favoriteButton.setText(R.string.remove_from_favorites);
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.ic_favorite), null, null, null);
        } else {
            favoriteButton.setText(R.string.add_to_favorites);
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.ic_favorite_border), null, null, null);
        }
    }

    // 切换收藏状态
    private void toggleFavorite() {
        if (currentCity.isEmpty()) {
            return;
        }

        if (isFavorite) {
            // 从收藏中删除
            int deleted = getContentResolver().delete(
                    FavoriteContract.FavoriteEntry.CONTENT_URI,
                    FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME + "=?",
                    new String[]{currentCity}
            );

            if (deleted > 0) {
                isFavorite = false;
                Toast.makeText(this, R.string.favorite_removed, Toast.LENGTH_SHORT).show();
            }
        } else {
            // 添加到收藏
            ContentValues values = new ContentValues();
            values.put(FavoriteContract.FavoriteEntry.COLUMN_CITY_NAME, currentCity);

            Uri uri = getContentResolver().insert(
                    FavoriteContract.FavoriteEntry.CONTENT_URI,
                    values
            );

            if (uri != null) {
                isFavorite = true;
                Toast.makeText(this, R.string.favorite_added, Toast.LENGTH_SHORT).show();
            }
        }

        // 更新收藏图标
        updateFavoriteButtonState();
    }
}
