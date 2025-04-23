package com.example.weatherreport.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherData {
    @SerializedName("name")
    private String cityName;
    
    @SerializedName("main")
    private Main main;
    
    @SerializedName("weather")
    private List<Weather> weather;
    
    @SerializedName("wind")
    private Wind wind;
    
    @SerializedName("sys")
    private Sys sys;
    
    public String getCityName() {
        return cityName;
    }
    
    public Main getMain() {
        return main;
    }
    
    public List<Weather> getWeather() {
        return weather;
    }
    
    public Wind getWind() {
        return wind;
    }
    
    public Sys getSys() {
        return sys;
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
        
        @SerializedName("pressure")
        private int pressure;
        
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
        
        public int getPressure() {
            return pressure;
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
    
    public static class Wind {
        @SerializedName("speed")
        private float speed;
        
        @SerializedName("deg")
        private int degree;
        
        public float getSpeed() {
            return speed;
        }
        
        public int getDegree() {
            return degree;
        }
    }
    
    public static class Sys {
        @SerializedName("country")
        private String country;
        
        @SerializedName("sunrise")
        private long sunrise;
        
        @SerializedName("sunset")
        private long sunset;
        
        public String getCountry() {
            return country;
        }
        
        public long getSunrise() {
            return sunrise;
        }
        
        public long getSunset() {
            return sunset;
        }
    }
}