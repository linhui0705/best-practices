package net.coderlin.demo.springai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Primary;

import java.util.function.Function;

/**
 * Spring AI 核心配置类
 *
 * <p>本配置类定义了Spring AI所需的核心Bean，包括：</p>
 * <ul>
 *   <li>ChatClient：对话客户端，提供流畅的API风格</li>
 *   <li>ChatMemory：对话记忆管理</li>
 *   <li>Advisor：拦截器，实现记忆、RAG等功能</li>
 *   <li>Function：工具函数，供AI调用</li>
 * </ul>
 *
 * <p><b>Spring AI 架构说明：</b></p>
 * <ul>
 *   <li>ChatClient是高层抽象，封装了ChatModel的调用</li>
 *   <li>Advisor采用拦截器模式，可在请求前后进行处理</li>
 *   <li>ChatMemory默认使用InMemoryChatMemory，生产环境可替换为Redis等</li>
 *   <li>VectorStore支持多种后端：PGVector、Redis、Milvus等</li>
 * </ul>
 *
 * @author
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class SpringAiConfig {

    /**
     * 默认记忆窗口大小
     */
    private static final int DEFAULT_MAX_MEMORY_MESSAGES = 10;

    /**
     * RAG检索返回的最大结果数
     */
    private static final int DEFAULT_RAG_MAX_RESULTS = 5;

    /**
     * RAG最小相似度分数
     */
    private static final double DEFAULT_RAG_MIN_SCORE = 0.7;

    /**
     * 记忆窗口大小，从配置文件读取
     */
    @Value("${app.ai.max-memory-messages:" + DEFAULT_MAX_MEMORY_MESSAGES + "}")
    private int maxMemoryMessages;

    /**
     * RAG检索返回的最大结果数
     */
    @Value("${app.ai.rag-max-results:" + DEFAULT_RAG_MAX_RESULTS + "}")
    private int ragMaxResults;

    /**
     * RAG最小相似度分数
     */
    @Value("${app.ai.rag-min-score:" + DEFAULT_RAG_MIN_SCORE + "}")
    private double ragMinScore;

    /**
     * 配置ChatMemory Bean
     *
     * <p><b>InMemoryChatMemory：</b></p>
     * <ul>
     *   <li>基于内存的对话历史存储</li>
     *   <li>适合单实例部署和开发测试环境</li>
     *   <li>生产环境建议替换为：RedisChatMemory等持久化方案</li>
     * </ul>
     *
     * <p><b>生产环境替代方案：</b></p>
     * <pre>
     * // Redis对话记忆
     * return new RedisChatMemory(redisTemplate);
     * </pre>
     *
     * @return ChatMemory 对话记忆实例
     */
    @Bean
    public ChatMemory chatMemory() {
        log.info("初始化ChatMemory，窗口大小: {}", maxMemoryMessages);
        return new InMemoryChatMemory();
    }

    /**
     * 配置内存向量存储
     *
     * <p><b>SimpleVectorStore：</b></p>
     * <ul>
     *   <li>纯内存存储，数据不持久化</li>
     *   <li>无需外部数据库依赖</li>
     *   <li>适合演示和测试环境</li>
     *   <li>生产环境建议替换为：PGVector、Redis、Milvus等</li>
     * </ul>
     *
     * @param embeddingModel 嵌入模型
     * @return VectorStore 向量存储实例
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        log.info("初始化内存向量存储 (SimpleVectorStore)");
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * 配置基础ChatClient
     *
     * <p><b>ChatClient：</b></p>
     * <ul>
     *   <li>Spring AI的核心抽象，提供流畅的API风格</li>
     *   <li>封装了ChatModel的调用细节</li>
     *   <li>支持链式调用：prompt -> user -> system -> call</li>
     * </ul>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * String response = chatClient.prompt()
     *     .user("你好")
     *     .system("你是一个助手")
     *     .call()
     *     .content();
     * </pre>
     *
     * @param chatClientBuilder ChatClient构建器（由Spring Boot自动配置注入）
     * @return ChatClient 基础对话客户端
     */
    @Bean
    @Primary
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        log.info("初始化基础ChatClient");
        return chatClientBuilder
                // 设置默认系统提示词
                .defaultSystem("你是一个 helpful AI 助手，请用中文回答问题，回答要简洁明了。")
                .build();
    }

    /**
     * 配置带记忆功能的ChatClient
     *
     * <p><b>MessageChatMemoryAdvisor：</b></p>
     * <ul>
     *   <li>拦截器，自动管理对话历史</li>
     *   <li>在每次请求前加载历史消息</li>
     *   <li>在每次响应后保存新的对话</li>
     *   <li>通过conversationId区分不同会话</li>
     * </ul>
     *
     * @param chatClientBuilder ChatClient构建器
     * @param chatMemory 对话记忆
     * @return ChatClient 带记忆功能的对话客户端
     */
    @Bean(name = "memoryChatClient")
    public ChatClient memoryChatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        log.info("初始化带记忆功能的ChatClient");
        return chatClientBuilder
                .defaultSystem("你是一个 helpful AI 助手，请用中文回答问题，回答要简洁明了。")
                // 添加记忆拦截器
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    /**
     * 配置RAG功能的ChatClient
     *
     * <p><b>QuestionAnswerAdvisor：</b></p>
     * <ul>
     *   <li>实现RAG（检索增强生成）功能</li>
     *   <li>工作流程：
     *     <ol>
     *       <li>将用户查询向量化</li>
       *       <li>在VectorStore中检索相似文档</li>
     *       <li>将检索结果注入到提示词中</li>
     *       <li>调用模型生成回答</li>
     *     </ol>
     *   </li>
     *   <li>支持配置检索参数：maxResults、minScore等</li>
     * </ul>
     *
     * @param chatClientBuilder ChatClient构建器
     * @param vectorStore 向量存储
     * @param chatMemory 对话记忆
     * @return ChatClient 带RAG功能的对话客户端
     */
    @Bean(name = "ragChatClient")
    public ChatClient ragChatClient(ChatClient.Builder chatClientBuilder,
                                     VectorStore vectorStore,
                                     ChatMemory chatMemory) {
        log.info("初始化带RAG功能的ChatClient，maxResults={}, minScore={}", ragMaxResults, ragMinScore);

        // 配置检索请求
        SearchRequest searchRequest = SearchRequest.builder()
                // 返回结果数量
                .topK(ragMaxResults)
                // 相似度阈值
                .similarityThreshold(ragMinScore)
                .build();

        return chatClientBuilder
                .defaultSystem("你是一个 helpful AI 助手。请基于提供的上下文信息回答问题，" +
                        "如果上下文中没有相关信息，请明确说明。")
                // 添加RAG拦截器
                .defaultAdvisors(
                        new QuestionAnswerAdvisor(vectorStore, searchRequest),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    // ==================== Function Calling 工具函数 ====================

    /**
     * 天气查询工具函数
     *
     * <p><b>Function Calling机制：</b></p>
     * <ul>
     *   <li>AI根据用户输入判断是否需要调用函数</li>
     *   <li>如需调用，AI会生成函数参数</li>
     *   <li>应用执行函数并返回结果</li>
     *   <li>AI基于函数结果生成最终回复</li>
     * </ul>
     *
     * <p><b>@Description注解：</b>用于描述函数用途，帮助AI理解何时调用该函数</p>
     *
     * @return Function<WeatherRequest, WeatherResponse> 天气查询函数
     */
    @Bean
    @Description("获取指定城市的当前天气信息，包括温度、湿度、天气状况等")
    public Function<WeatherRequest, WeatherResponse> weatherFunction() {
        return request -> {
            log.info("执行天气查询: city={}", request.city());
            // 模拟天气查询，实际应用中调用真实天气API
            return new WeatherResponse(
                    request.city(),
                    "晴天",
                    25,
                    60,
                    "适宜出行"
            );
        };
    }

    /**
     * 天气查询请求
     *
     * @param city 城市名称
     */
    public record WeatherRequest(String city) {
    }

    /**
     * 天气查询响应
     *
     * @param city 城市名称
     * @param condition 天气状况
     * @param temperature 温度（摄氏度）
     * @param humidity 湿度（百分比）
     * @param suggestion 出行建议
     */
    public record WeatherResponse(String city, String condition, int temperature,
                                   int humidity, String suggestion) {
    }

    /**
     * 计算器工具函数
     *
     * @return Function<CalculatorRequest, CalculatorResponse> 计算器函数
     */
    @Bean
    @Description("执行数学计算，支持加、减、乘、除运算")
    public Function<CalculatorRequest, CalculatorResponse> calculatorFunction() {
        return request -> {
            log.info("执行计算: {} {} {}", request.num1(), request.operator(), request.num2());
            double result;
            switch (request.operator()) {
                case "+":
                    result = request.num1() + request.num2();
                    break;
                case "-":
                    result = request.num1() - request.num2();
                    break;
                case "*":
                    result = request.num1() * request.num2();
                    break;
                case "/":
                    if (request.num2() == 0) {
                        return new CalculatorResponse("错误：除数不能为零", 0);
                    }
                    result = request.num1() / request.num2();
                    break;
                default:
                    return new CalculatorResponse("错误：不支持的运算符" + request.operator(), 0);
            }
            return new CalculatorResponse("计算成功", result);
        };
    }

    /**
     * 计算器请求
     *
     * @param num1 第一个数字
     * @param operator 运算符：+、-、*、/
     * @param num2 第二个数字
     */
    public record CalculatorRequest(double num1, String operator, double num2) {
    }

    /**
     * 计算器响应
     *
     * @param message 消息
     * @param result 计算结果
     */
    public record CalculatorResponse(String message, double result) {
    }
}
