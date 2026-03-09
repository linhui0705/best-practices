package net.coderlin.demo.langchain4j.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import net.coderlin.demo.langchain4j.tools.CalculatorTools;
import net.coderlin.demo.langchain4j.tools.WeatherTools;

/**
 * 工具调用AI助手服务接口
 * 
 * <p>本接口演示了LangChain4j的Tools功能，让AI能够调用自定义Java方法。</p>
 * 
 * <p><b>配置方式：</b></p>
 * <pre>
 * // 方式1：在AI Service接口上使用@Tools注解
 * @AiService
 * @Tools({CalculatorTools.class, WeatherTools.class})
 * public interface ToolsAssistant {
 *     String chat(String message);
 * }
 * 
 * // 方式2：在配置类中程序化配置
 * @Bean
 * public ToolsAssistant toolsAssistant(ChatLanguageModel model, 
 *                                      CalculatorTools calcTools,
 *                                      WeatherTools weatherTools) {
 *     return AiServices.builder(ToolsAssistant.class)
 *         .chatLanguageModel(model)
 *         .tools(calcTools, weatherTools)
 *         .build();
 * }
 * </pre>
 * 
 * <p><b>工具调用流程：</b></p>
 * <ol>
 *   <li>用户发送消息</li>
 *   <li>AI分析消息，判断是否需要调用工具</li>
 *   <li>如需调用，AI生成工具名称和参数</li>
 *   <li>LangChain4j执行对应的Java方法</li>
 *   <li>方法返回值传递给AI</li>
 *   <li>AI基于工具结果生成最终回复</li>
 * </ol>
 * 
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>工具描述要清晰，帮助AI理解何时使用</li>
 *   <li>参数类型要明确，便于AI正确提取</li>
 *   <li>返回值格式要规范，便于AI解析</li>
 *   <li>工具类使用@Component，便于Spring管理</li>
 * </ul>
 * 
 * @author
 * @since 1.0.0
 */
public interface ToolsAssistant {

    /**
     * 基础对话（支持工具调用）
     * 
     * <p>AI会自动判断是否需要调用工具。</p>
     * 
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 普通对话 - 不需要工具
     * assistant.chat("你好");
     * // AI直接回复：你好！有什么可以帮助你的吗？
     * 
     * // 数学计算 - 自动调用CalculatorTools
     * assistant.chat("123乘以456等于多少？");
     * // AI调用multiply(123, 456)，然后回复计算结果
     * 
     * // 天气查询 - 自动调用WeatherTools
     * assistant.chat("北京今天天气怎么样？");
     * // AI调用getWeather("北京")，然后组织语言回复
     * </pre>
     * 
     * @param message 用户消息
     * @return AI回复（可能包含工具调用结果）
     */
    @SystemMessage("""
        你是一个 helpful AI 助手，请用中文回答问题。
        
        你拥有以下工具能力：
        1. 数学计算：可以进行加减乘除、平方根、幂运算等精确计算
        2. 天气查询：可以查询指定城市的天气信息
        
        当用户需要计算时，请使用计算工具获得精确结果。
        当用户询问天气时，请使用天气查询工具获取实时数据。
        不要尝试心算，始终使用工具确保准确性。
        """)
    String chat(@UserMessage String message);

    /**
     * 数学专家模式
     * 
     * <p>专注于数学计算的工具调用。</p>
     * 
     * @param problem 数学问题描述
     * @return 解答过程和结果
     */
    @SystemMessage("""
        你是一位数学专家，擅长解决各种数学问题。
        
        解题步骤：
        1. 分析用户的问题，识别需要计算的部分
        2. 使用计算工具进行精确计算
        3. 展示计算过程和最终结果
        4. 如有必要，解释计算原理
        
        注意：
        - 所有计算必须使用计算工具，确保结果准确
        - 对于复杂计算，分步骤调用工具
        - 解释每一步的计算逻辑
        """)
    String solveMathProblem(@UserMessage String problem);

    /**
     * 天气助手模式
     * 
     * <p>专注于天气查询的工具调用。</p>
     * 
     * @param query 天气相关查询
     * @return 天气信息和建议
     */
    @SystemMessage("""
        你是一位天气助手，可以查询天气信息并提供相关建议。
        
        服务范围：
        1. 查询指定城市的当前天气
        2. 比较不同城市的温度
        3. 根据天气提供出行建议
        
        回答风格：
        - 提供温度、天气状况、湿度、风速等完整信息
        - 根据天气状况给出贴心建议
        - 语言友好亲切
        """)
    String weatherQuery(@UserMessage String query);

    /**
     * 带记忆的复杂对话（支持工具调用）
     * 
     * <p>结合Memory和Tools，实现上下文感知的工具调用。</p>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>多轮计算对话，记住之前的计算结果</li>
     *   <li>旅行规划，记住用户提到的城市和偏好</li>
     *   <li>数据分析，记住之前查询的数据</li>
     * </ul>
     * 
     * @param sessionId 会话ID
     * @param message 用户消息
     * @return AI回复
     */
    @SystemMessage("""
        你是一个智能助手，可以帮助用户进行计算和查询。
        
        能力：
        1. 数学计算：精确的四则运算、科学计算
        2. 天气查询：实时天气信息
        
        特点：
        - 记住对话历史，理解上下文
        - 如用户提到"刚才的结果"，要能关联到之前的计算
        - 主动使用工具确保信息准确
        """)
    String chatWithMemoryAndTools(@MemoryId String sessionId, @UserMessage String message);
}
