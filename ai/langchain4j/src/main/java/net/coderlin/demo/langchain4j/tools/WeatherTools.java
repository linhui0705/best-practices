package net.coderlin.demo.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 天气查询工具类 - 演示Tools功能
 * 
 * <p>本类模拟天气查询API，展示AI如何通过工具获取实时数据。</p>
 * 
 * <p><b>设计说明：</b></p>
 * <ul>
 *   <li>模拟真实天气API的响应格式</li>
 *   <li>支持多个城市天气查询</li>
 *   <li>返回结构化数据供AI解析</li>
 * </ul>
 * 
 * <p><b>实际应用场景：</b></p>
 * <ul>
 *   <li>接入真实天气API（如和风天气、OpenWeatherMap）</li>
 *   <li>查询数据库获取业务数据</li>
 *   <li>调用内部微服务获取信息</li>
 * </ul>
 * 
 * @author
 * @since 1.0.0
 */
@Slf4j
@Component
public class WeatherTools {

    /**
     * 模拟天气数据存储
     */
    private static final Map<String, WeatherData> WEATHER_DATABASE = new HashMap<>();

    /**
     * 天气数据内部类
     */
    public static class WeatherData {
        public String city;
        public double temperature;
        public String condition;
        public int humidity;
        public double windSpeed;
        public String updateTime;

        public WeatherData(String city, double temperature, String condition, 
                          int humidity, double windSpeed, String updateTime) {
            this.city = city;
            this.temperature = temperature;
            this.condition = condition;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.updateTime = updateTime;
        }

        @Override
        public String toString() {
            return String.format(
                "城市：%s，温度：%.1f°C，天气：%s，湿度：%d%%，风速：%.1f km/h，更新时间：%s",
                city, temperature, condition, humidity, windSpeed, updateTime
            );
        }
    }

    /**
     * 根据城市名称查询天气
     * 
     * <p>演示AI如何从自然语言中提取参数并调用工具。</p>
     * 
     * <p><b>使用示例：</b></p>
     * <pre>
     * 用户：北京今天天气怎么样？
     * AI识别需要调用getWeather工具，提取参数city="北京"
     * 工具返回天气数据
     * AI组织语言回复用户
     * </pre>
     * 
     * @param city 城市名称，如"北京"、"上海"
     * @return 天气信息字符串
     */
    @Tool("查询指定城市的当前天气信息。参数：city（城市名称，如：北京、上海、广州）。返回：包含温度、天气状况、湿度、风速的详细信息")
    public String getWeather(String city) {
        log.info("执行工具：getWeather(city={})", city);
        
        // 模拟天气数据（实际项目中调用真实API）
        WeatherData data = generateMockWeather(city);
        
        String result = data.toString();
        log.info("天气查询结果：{}", result);
        return result;
    }

    /**
     * 获取城市温度
     * 
     * <p>演示返回数值型数据的工具。</p>
     * 
     * @param city 城市名称
     * @return 温度值（摄氏度）
     */
    @Tool("获取指定城市的当前温度。参数：city（城市名称）。返回：温度数值（摄氏度）")
    public double getTemperature(String city) {
        log.info("执行工具：getTemperature(city={})", city);
        WeatherData data = generateMockWeather(city);
        log.info("{}当前温度：{}°C", city, data.temperature);
        return data.temperature;
    }

    /**
     * 比较两个城市的温度
     * 
     * <p>演示多参数工具方法。</p>
     * 
     * @param city1 第一个城市
     * @param city2 第二个城市
     * @return 温度比较结果
     */
    @Tool("比较两个城市的温度。参数：city1（第一个城市），city2（第二个城市）。返回：温度比较结果")
    public String compareTemperature(String city1, String city2) {
        log.info("执行工具：compareTemperature({}, {})", city1, city2);
        
        WeatherData data1 = generateMockWeather(city1);
        WeatherData data2 = generateMockWeather(city2);
        
        double diff = data1.temperature - data2.temperature;
        String result;
        if (diff > 0) {
            result = String.format("%s比%s热%.1f°C（%s %.1f°C vs %s %.1f°C）",
                city1, city2, diff, city1, data1.temperature, city2, data2.temperature);
        } else if (diff < 0) {
            result = String.format("%s比%s冷%.1f°C（%s %.1f°C vs %s %.1f°C）",
                city1, city2, -diff, city1, data1.temperature, city2, data2.temperature);
        } else {
            result = String.format("%s和%s温度相同，都是%.1f°C",
                city1, city2, data1.temperature);
        }
        
        log.info("温度比较结果：{}", result);
        return result;
    }

    /**
     * 生成模拟天气数据
     * 
     * <p>实际项目中应替换为真实API调用。</p>
     * 
     * @param city 城市名称
     * @return 天气数据对象
     */
    private WeatherData generateMockWeather(String city) {
        // 基于城市名称生成固定的随机数据（保证同一城市多次查询结果一致）
        int cityHash = Math.abs(city.hashCode());
        
        // 基础温度根据城市hash生成（10-35度之间）
        double baseTemp = 10 + (cityHash % 25);
        // 添加随机波动
        double temperature = baseTemp + ThreadLocalRandom.current().nextDouble() * 4 - 2;
        
        // 天气状况
        String[] conditions = {"晴", "多云", "阴", "小雨", "雷阵雨"};
        String condition = conditions[cityHash % conditions.length];
        
        // 湿度（30-90%）
        int humidity = 30 + (cityHash % 60);
        
        // 风速（0-20 km/h）
        double windSpeed = ThreadLocalRandom.current().nextDouble() * 20;
        
        // 更新时间
        String updateTime = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        
        return new WeatherData(city, temperature, condition, humidity, windSpeed, updateTime);
    }
}
