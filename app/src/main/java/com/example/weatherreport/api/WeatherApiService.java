package com.example.weatherreport.api;

import com.example.weatherreport.model.ForecastResponse;
import com.example.weatherreport.model.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("weather")
    Call<WeatherData> getCurrentWeather(
        @Query("q") String cityName,
        @Query("units") String units,
        @Query("appid") String apiKey
    );
    
    @GET("forecast")
    Call<ForecastResponse> getForecast(
        @Query("q") String cityName,
        @Query("units") String units,
        @Query("appid") String apiKey
    );
}