package com.example.weatherreport;

import android.util.Log;

/**
 * 用于验证 API key 配置的工具类
 */
public class ApiKeyValidator {
    private static final String TAG = "ApiKeyValidator";
    
    public static boolean validateApiKey() {
        String apiKey = BuildConfig.WEATHER_API_KEY;
        
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key_here")) {
            Log.e(TAG, "API key 未配置或配置不正确");
            Log.e(TAG, "请在 local.properties 文件中设置 WEATHER_API_KEY=你的API密钥");
            Log.e(TAG, "或设置环境变量 WEATHER_API_KEY");
            return false;
        }
        
        Log.i(TAG, "API key 配置正确，长度: " + apiKey.length());
        return true;
    }
    
    public static String getApiKey() {
        return BuildConfig.WEATHER_API_KEY;
    }
}
