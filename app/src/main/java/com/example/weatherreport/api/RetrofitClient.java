package com.example.weatherreport.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.util.Log;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            // 创建日志拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
                Log.d(TAG, message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // 创建OkHttpClient，设置超时和拦截器
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    public static WeatherApiService getWeatherApiService() {
        return getClient().create(WeatherApiService.class);
    }
}