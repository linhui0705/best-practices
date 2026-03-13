package net.coderlin.demo.langchain4j.config;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coderlin.demo.langchain4j.service.Assistant;
import net.coderlin.demo.langchain4j.service.MemoryAssistant;
import net.coderlin.demo.langchain4j.service.RagAssistant;
import net.coderlin.demo.langchain4j.service.ToolsAssistant;
import net.coderlin.demo.langchain4j.tools.CalculatorTools;
import net.coderlin.demo.langchain4j.tools.WeatherTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.time.Duration;

/**
 * LangChain4j 核心配置类
 *
 * <p>本配置类定义了LangChain4j所需的核心Bean，包括：</p>
 * <ul>
 *   <li>ChatMemory：对话记忆管理（请求级别作用域）</li>
 *   <li>EmbeddingStore：向量存储（用于RAG）</li>
 *   <li>EmbeddingModel：嵌入模型（用于生成向量）</li>
 *   <li>ContentRetriever：内容检索器（RAG核心组件）</li>
 *   <li>AI Services：各类AI助手服务</li>
 * </ul>
 *
 * <p><b>设计说明：</b></p>
 * <ul>
 *   <li>ChatMemory配置为请求作用域，每个用户会话独立维护对话历史</li>
 *   <li>EmbeddingStore使用内存存储，生产环境建议替换为向量数据库</li>
 *   <li>支持本地和远程两种Embedding模型</li>
 *   <li>使用AiProperties统一管理配置参数</li>
 * </ul>
 *
 * @author
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LangChain4jConfig {

    /**
     * AI配置属性（通过构造器注入）
     */
    private final AiProperties aiProperties;

    /**
     * 默认OpenAI Base URL
     */
    private static final String DEFAULT_OPENAI_BASE_URL = "https://api.openai.com/v1";

    /**
     * 默认OpenAI模型名称
     */
    private static final String DEFAULT_OPENAI_MODEL_NAME = "gpt-3.5-turbo";

    /**
     * 默认温度参数
     */
    private static final double DEFAULT_OPENAI_TEMPERATURE = 0.7;

    /**
     * OpenAI API Key
     */
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String openAiApiKey;

    /**
     * OpenAI 模型名称
     */
    @Value("${langchain4j.open-ai.chat-model.model-name:" + DEFAULT_OPENAI_MODEL_NAME + "}")
    private String openAiModelName;

    /**
     * OpenAI Base URL（可选，用于代理或兼容服务）
     */
    @Value("${langchain4j.open-ai.chat-model.base-url:" + DEFAULT_OPENAI_BASE_URL + "}")
    private String openAiBaseUrl;

    /**
     * OpenAI 超时时间（秒）
     */
    @Value("${langchain4j.open-ai.chat-model.timeout-seconds:60}")
    private int openAiTimeoutSeconds;

    /**
     * 配置ChatMemory Bean - 请求级别作用域
     * 
     * <p><b>作用域说明：</b></p>
     * <ul>
     *   <li>@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)</li>
     *   <li>每个HTTP请求创建一个新的ChatMemory实例</li>
     *   <li>确保多用户并发时对话历史互不干扰</li>
     * </ul>
     * 
     * <p><b>MessageWindowChatMemory：</b></p>
     * <ul>
     *   <li>基于消息窗口的记忆实现</li>
     *   <li>保留最近N条消息（由maxMemoryMessages控制）</li>
     *   <li>超出窗口的旧消息自动丢弃</li>
     * </ul>
     * 
     * @return ChatMemory 实例
     */
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ChatMemory chatMemory() {
        int maxMessages = aiProperties.getMaxMemoryMessages();
        log.debug("创建新的ChatMemory实例，窗口大小: {}", maxMessages);
        return MessageWindowChatMemory.builder()
                // 设置最大消息数，控制上下文长度
                .maxMessages(maxMessages)
                .build();
    }

    /**
     * 配置本地Embedding模型
     * 
     * <p><b>AllMiniLmL6V2EmbeddingModel：</b></p>
     * <ul>
     *   <li>基于ONNX Runtime的本地模型</li>
     *   <li>模型：all-MiniLM-L6-v2 (Sentence Transformers)</li>
     *   <li>维度：384维向量</li>
     *   <li>优势：无需API Key，离线可用，响应快</li>
     *   <li>适用：开发测试、数据敏感场景</li>
     * </ul>
     * 
     * <p><b>替代方案：</b>可使用OpenAiEmbeddingModel调用OpenAI API</p>
     * 
     * @return EmbeddingModel 本地嵌入模型实例
     */
    @Bean
    @Primary
    public EmbeddingModel embeddingModel() {
        log.info("初始化本地Embedding模型: all-MiniLM-L6-v2");
        return new AllMiniLmL6V2EmbeddingModel();
    }

    /**
     * 配置内存向量存储
     * 
     * <p><b>InMemoryEmbeddingStore：</b></p>
     * <ul>
     *   <li>纯内存存储，数据不持久化</li>
     *   <li>适合演示和测试环境</li>
     *   <li>生产环境建议替换为：Redis, Milvus, Pinecone等</li>
     * </ul>
     * 
     * <p><b>生产环境替代方案：</b></p>
     * <pre>
     * // Redis向量存储
     * return RedisEmbeddingStore.builder()
     *     .host("localhost")
     *     .port(6379)
     *     .dimension(384)
     *     .build();
     * </pre>
     * 
     * @return EmbeddingStore<TextSegment> 向量存储实例
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        log.info("初始化内存向量存储 (InMemoryEmbeddingStore)");
        return new InMemoryEmbeddingStore<>();
    }

    /**
     * 配置内容检索器（RAG核心组件）
     * 
     * <p><b>EmbeddingStoreContentRetriever：</b></p>
     * <ul>
     *   <li>基于向量相似度的内容检索</li>
     *   <li>工作流程：
     *     <ol>
     *       <li>将查询文本转为向量（EmbeddingModel）</li>
     *       <li>在向量存储中搜索相似向量</li>
     *       <li>返回最相关的文本片段</li>
     *     </ol>
     *   </li>
     *   <li>可配置参数：
     *     <ul>
     *       <li>maxResults：返回结果数量</li>
     *       <li>minScore：最小相似度阈值</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * @param embeddingStore 向量存储
     * @param embeddingModel 嵌入模型
     * @return ContentRetriever 内容检索器
     */
    @Bean
    public ContentRetriever contentRetriever(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {
        AiProperties.RagConfig ragConfig = aiProperties.getRag();
        log.info("初始化EmbeddingStoreContentRetriever, maxResults={}, minScore={}", 
                ragConfig.getMaxResults(), ragConfig.getMinScore());
        return EmbeddingStoreContentRetriever.builder()
                // 向量存储
                .embeddingStore(embeddingStore)
                // 嵌入模型
                .embeddingModel(embeddingModel)
                // 返回最相关的结果
                .maxResults(ragConfig.getMaxResults())
                // 最小相似度分数（0-1之间）
                .minScore(ragConfig.getMinScore())
                .build();
    }

    /**
     * 配置文档分割器
     * 
     * <p><b>DocumentSplitters.recursive()：</b></p>
     * <ul>
     *   <li>递归分割策略，优先按段落、句子、单词分割</li>
     *   <li>参数说明：
     *     <ul>
     *       <li>maxSegmentSize：每个片段最大字符数</li>
     *       <li>maxOverlapSize：相邻片段重叠字符数</li>
     *     </ul>
     *   </li>
     *   <li>重叠设计确保上下文连续性</li>
     * </ul>
     * 
     * @return DocumentSplitter 文档分割器
     */
    @Bean
    public DocumentSplitter documentSplitter() {
        AiProperties.DocumentSplitterConfig config = aiProperties.getDocumentSplitter();
        log.info("初始化文档分割器, maxSize={}, maxOverlap={}", 
                config.getMaxSize(), config.getMaxOverlap());
        return DocumentSplitters.recursive(config.getMaxSize(), config.getMaxOverlap());
    }

    // ==================== ChatLanguageModel 配置 ====================

    /**
     * 配置 OpenAI ChatLanguageModel
     * 
     * <p>手动创建 ChatLanguageModel Bean，用于 AI Services。</p>
     * <p>支持通过环境变量配置API Key和其他参数。</p>
     * 
     * @return ChatLanguageModel OpenAI 聊天模型
     */
    @Bean
    @Primary
    public ChatLanguageModel chatLanguageModel() {
        log.info("初始化 OpenAI ChatLanguageModel: {}", openAiModelName);
        
        if (openAiApiKey == null || StringUtils.isBlank(openAiApiKey)) {
            throw new IllegalStateException("请配置 OpenAI API Key。设置环境变量 OPENAI_API_KEY 或在 application.yml 中配置 langchain4j.open-ai.chat-model.api-key");
        }
        
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(openAiModelName)
                .temperature(DEFAULT_OPENAI_TEMPERATURE)
                .timeout(Duration.ofSeconds(openAiTimeoutSeconds));
        
        if (openAiBaseUrl != null && StringUtils.isNotBlank(openAiBaseUrl)) {
            builder.baseUrl(openAiBaseUrl);
            log.info("使用自定义 Base URL: {}", openAiBaseUrl);
        }
        
        return builder.build();
    }

    // ==================== AI Services 配置 ====================

    /**
     * 配置基础AI助手
     * 
     * <p>使用AiServices.builder()创建Assistant接口的实现。</p>
     * 
     * @param chatLanguageModel 语言模型（自动配置）
     * @return Assistant 基础助手
     */
    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel) {
        log.info("初始化基础AI助手 (Assistant)");
        return AiServices.create(Assistant.class, chatLanguageModel);
    }

    /**
     * 配置带记忆的AI助手
     * 
     * <p>配置ChatMemoryProvider，为每个会话提供独立的记忆。</p>
     * 
     * @param chatLanguageModel 语言模型
     * @return MemoryAssistant 带记忆的助手
     */
    @Bean
    public MemoryAssistant memoryAssistant(ChatLanguageModel chatLanguageModel) {
        int maxMessages = aiProperties.getMaxMemoryMessages();
        log.info("初始化带记忆AI助手 (MemoryAssistant), maxMessages={}", maxMessages);
        return AiServices.builder(MemoryAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .maxMessages(maxMessages)
                        .build())
                .build();
    }

    /**
     * 配置RAG AI助手
     * 
     * <p>集成ContentRetriever实现知识库问答。</p>
     * 
     * @param chatLanguageModel 语言模型
     * @param contentRetriever 内容检索器
     * @return RagAssistant RAG助手
     */
    @Bean
    public RagAssistant ragAssistant(ChatLanguageModel chatLanguageModel, 
                                      ContentRetriever contentRetriever) {
        int maxMessages = aiProperties.getMaxMemoryMessages();
        log.info("初始化RAG AI助手 (RagAssistant)");
        return AiServices.builder(RagAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .maxMessages(maxMessages)
                        .build())
                .build();
    }

    /**
     * 配置工具调用AI助手
     * 
     * <p>集成CalculatorTools和WeatherTools。</p>
     * 
     * @param chatLanguageModel 语言模型
     * @param calculatorTools 计算器工具
     * @param weatherTools 天气工具
     * @return ToolsAssistant 工具助手
     */
    @Bean
    public ToolsAssistant toolsAssistant(ChatLanguageModel chatLanguageModel,
                                          CalculatorTools calculatorTools,
                                          WeatherTools weatherTools) {
        int maxMessages = aiProperties.getMaxMemoryMessages();
        log.info("初始化工具调用AI助手 (ToolsAssistant)");
        return AiServices.builder(ToolsAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(calculatorTools, weatherTools)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .maxMessages(maxMessages)
                        .build())
                .build();
    }
}
