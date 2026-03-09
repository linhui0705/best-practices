package net.coderlin.demo.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 计算器工具类 - 演示Tools功能
 * 
 * <p>本类展示了如何使用@Tool注解让AI调用自定义Java方法。</p>
 * 
 * <p><b>Tools核心概念：</b></p>
 * <ul>
 *   <li><b>@Tool注解</b>：标记方法为AI可调用的工具
     *     <ul>
     *       <li>value属性：描述工具功能，帮助AI理解何时调用</li>
     *       <li>方法参数：AI会从对话中自动提取参数值</li>
     *       <li>返回值：返回给AI继续处理</li>
     *     </ul>
 *   </li>
 *   <li><b>工具调用流程</b>：
     *     <ol>
     *       <li>用户发送消息</li>
     *       <li>AI分析是否需要调用工具</li>
     *       <li>如需调用，AI生成参数并请求执行</li>
     *       <li>框架执行对应Java方法</li>
     *       <li>方法返回值传给AI</li>
     *       <li>AI基于结果生成最终回复</li>
     *     </ol>
 *   </li>
 * </ul>
 * 
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>数学计算：AI不擅长精确计算，可调用计算器</li>
 *   <li>数据查询：查询数据库、调用API获取实时数据</li>
 *   <li>系统操作：执行特定业务逻辑</li>
 *   <li>外部服务：调用第三方服务</li>
 * </ul>
 * 
 * @author
 * @since 1.0.0
 */
@Slf4j
@Component
public class CalculatorTools {

    /**
     * 加法计算
     * 
     * <p>演示基础的数学计算工具。</p>
     * 
     * <p><b>@Tool注解说明：</b></p>
     * <ul>
     *   <li>value：工具描述，AI据此判断何时使用此工具</li>
     *   <li>描述要清晰具体，包含参数说明</li>
     * </ul>
     * 
     * @param a 第一个加数
     * @param b 第二个加数
     * @return 两数之和
     */
    @Tool("计算两个数的和。参数：a（第一个数），b（第二个数）。返回：a + b的结果")
    public double add(double a, double b) {
        log.info("执行工具：add({}, {})", a, b);
        double result = a + b;
        log.info("计算结果：{} + {} = {}", a, b, result);
        return result;
    }

    /**
     * 减法计算
     * 
     * @param a 被减数
     * @param b 减数
     * @return 两数之差
     */
    @Tool("计算两个数的差。参数：a（被减数），b（减数）。返回：a - b的结果")
    public double subtract(double a, double b) {
        log.info("执行工具：subtract({}, {})", a, b);
        double result = a - b;
        log.info("计算结果：{} - {} = {}", a, b, result);
        return result;
    }

    /**
     * 乘法计算
     * 
     * @param a 第一个因数
     * @param b 第二个因数
     * @return 两数之积
     */
    @Tool("计算两个数的乘积。参数：a（第一个数），b（第二个数）。返回：a * b的结果")
    public double multiply(double a, double b) {
        log.info("执行工具：multiply({}, {})", a, b);
        double result = a * b;
        log.info("计算结果：{} * {} = {}", a, b, result);
        return result;
    }

    /**
     * 除法计算
     * 
     * <p>包含除零检查。</p>
     * 
     * @param a 被除数
     * @param b 除数
     * @return 两数之商
     * @throws IllegalArgumentException 除数为零时抛出
     */
    @Tool("计算两个数的商。参数：a（被除数），b（除数）。返回：a / b的结果。注意：除数不能为零")
    public double divide(double a, double b) {
        log.info("执行工具：divide({}, {})", a, b);
        if (b == 0) {
            throw new IllegalArgumentException("除数不能为零");
        }
        double result = a / b;
        log.info("计算结果：{} / {} = {}", a, b, result);
        return result;
    }

    /**
     * 平方根计算
     * 
     * @param number 待开方的数
     * @return 平方根
     */
    @Tool("计算一个数的平方根。参数：number（待开方的数）。返回：number的平方根")
    public double sqrt(double number) {
        log.info("执行工具：sqrt({})", number);
        if (number < 0) {
            throw new IllegalArgumentException("不能计算负数的平方根");
        }
        double result = Math.sqrt(number);
        log.info("计算结果：sqrt({}) = {}", number, result);
        return result;
    }

    /**
     * 幂运算
     * 
     * @param base 底数
     * @param exponent 指数
     * @return base的exponent次方
     */
    @Tool("计算幂运算。参数：base（底数），exponent（指数）。返回：base的exponent次方")
    public double power(double base, double exponent) {
        log.info("执行工具：power({}, {})", base, exponent);
        double result = Math.pow(base, exponent);
        log.info("计算结果：{} ^ {} = {}", base, exponent, result);
        return result;
    }

    /**
     * 获取当前时间
     * 
     * <p>演示无参数工具方法。</p>
     * 
     * @return 格式化的当前时间字符串
     */
    @Tool("获取当前的日期和时间，返回格式化的字符串")
    public String getCurrentTime() {
        log.info("执行工具：getCurrentTime()");
        LocalDateTime now = LocalDateTime.now();
        String result = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("当前时间：{}", result);
        return result;
    }

    /**
     * 高精度计算 - 加法
     * 
     * <p>演示BigDecimal精确计算，适用于金融场景。</p>
     * 
     * @param a 第一个数
     * @param b 第二个数
     * @param scale 小数位精度
     * @return 精确计算结果
     */
    @Tool("高精度加法计算，适用于金融场景。参数：a（第一个数），b（第二个数），scale（保留小数位数）。返回：精确计算结果")
    public String addPrecise(String a, String b, int scale) {
        log.info("执行工具：addPrecise({}, {}, {})", a, b, scale);
        BigDecimal num1 = new BigDecimal(a);
        BigDecimal num2 = new BigDecimal(b);
        BigDecimal result = num1.add(num2).setScale(scale, RoundingMode.HALF_UP);
        log.info("计算结果：{} + {} = {}", a, b, result);
        return result.toString();
    }
}
