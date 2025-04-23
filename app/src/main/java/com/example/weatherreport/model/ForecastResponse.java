package com.example.weatherreport.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    private List<ForecastItem> forecastList;
    
    @SerializedName("city")
    private City city;
    
    public List<ForecastItem> getForecastList() {
        return forecastList;
    }
    
    public City getCity() {
        return city;
    }
    
    public static class ForecastItem {
        @SerializedName("dt")
        private long dateTime;
        
        @SerializedName("main")
        private Main main;
        
        @SerializedName("weather")
        private List<Weather> weather;
        
        @SerializedName("dt_txt")
        private String dateTimeText;
        
        public long getDateTime() {
            return dateTime;
        }
        
        public Main getMain() {
            return main;
        }
        
        public List<Weather> getWeather() {
            return weather;
        }
        
        public String getDateTimeText() {
            return dateTimeText;
        }
    }
    
    public static class Main {
        @SerializedName("temp")
        private float temperature;
        
        @SerializedName("feels_like")
        private float feelsLike;
        
        @SerializedName("temp_min")
        private float tempMin;
        
        @SerializedName("temp_max")
        private float tempMax;
        
        @SerializedName("humidity")
        private int humidity;
        
        public float getTemperature() {
            return temperature;
        }
        
        public float getFeelsLike() {
            return feelsLike;
        }
        
        public float getTempMin() {
            return tempMin;
        }
        
        public float getTempMax() {
            return tempMax;
        }
        
        public int getHumidity() {
            return humidity;
        }
    }
    
    public static class Weather {
        @SerializedName("id")
        private int id;
        
        @SerializedName("main")
        private String main;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("icon")
        private String icon;
        
        public int getId() {
            return id;
        }
        
        public String getMain() {
            return main;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getIcon() {
            return icon;
        }
    }
    
    public static class City {
        @SerializedName("name")
        private String name;
        
        @SerializedName("country")
        private String country;
        
        public String getName() {
            return name;
        }
        
        public String getCountry() {
            return country;
        }
    }
}