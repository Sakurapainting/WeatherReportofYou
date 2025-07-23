# API 文档

## OpenWeatherMap API 集成

WeatherReportofYou 使用 OpenWeatherMap API 来获取天气数据。

### API 端点

#### 1. 当前天气
```
GET https://api.openweathermap.org/data/2.5/weather
```

**参数：**
- `q`: 城市名称
- `units`: 单位制 (metric, imperial, kelvin)
- `appid`: API 密钥

**响应示例：**
```json
{
  "coord": {"lon": 139.69, "lat": 35.69},
  "weather": [
    {
      "id": 800,
      "main": "Clear",
      "description": "clear sky",
      "icon": "01d"
    }
  ],
  "main": {
    "temp": 25.3,
    "feels_like": 25.8,
    "humidity": 60,
    "pressure": 1013
  },
  "wind": {"speed": 3.5, "deg": 180},
  "name": "Tokyo"
}
```

#### 2. 天气预报
```
GET https://api.openweathermap.org/data/2.5/forecast
```

**参数：**
- `q`: 城市名称  
- `units`: 单位制
- `appid`: API 密钥

### 数据模型

#### WeatherData
```java
public class WeatherData {
    private String name;           // 城市名称
    private Main main;             // 主要天气数据
    private List<Weather> weather; // 天气状况
    private Wind wind;             // 风力数据
    // getters and setters
}
```

#### Main
```java
public class Main {
    private double temp;        // 温度
    private double feels_like;  // 体感温度
    private int humidity;       // 湿度
    private int pressure;       // 气压
    // getters and setters
}
```

#### Weather
```java
public class Weather {
    private int id;             // 天气ID
    private String main;        // 天气主要状况
    private String description; // 天气描述
    private String icon;        // 天气图标代码
    // getters and setters
}
```

### 错误处理

常见 HTTP 状态码：
- `200`: 成功
- `401`: API 密钥无效
- `404`: 城市未找到
- `429`: 请求频率超限
