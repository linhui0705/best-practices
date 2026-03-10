package net.coderlin.demo.springai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * AI对话服务类
 *
 * <p>封装Spring AI的核心功能，提供对话、流式输出、记忆对话、RAG等功能。</p>
 *
 * <p><b>核心方法说明：</b></p>
 * <ul>
 *   <li>chat()：基础对话，单次调用</li>
 *   <li>chatStream()：流式对话，实时返回</li>
 *   <li>chatWithMemory()：带记忆的多轮对话</li>
 *   <li>chatWithRag()：基于知识库的RAG问答</li>
 *   <li>chatWithFunction()：支持函数调用的对话</li>
 *   <li>generateStructured()：生成结构化输出</li>
 * </ul>
 *
 * @author
 * @since 1.0.0
 */
@Slf4j
@Service
public class ChatService {

    /**
     * 基础ChatClient（无记忆、无RAG）
     */
    private final ChatClient chatClient;

    /**
     * 带记忆的ChatClient
     */
    private final ChatClient memoryChatClient;

    /**
     * 带RAG功能的ChatClient
     */
    private final ChatClient ragChatClient;

    /**
     * 向量存储（用于RAG文档管理）
     */
    private final VectorStore vectorStore;

    /**
     * 构造方法注入
     *
     * @param chatClient 基础ChatClient（@Primary）
     * @param memoryChatClient 带记忆的ChatClient
     * @param ragChatClient 带RAG功能的ChatClient
     * @param vectorStore 向量存储
     */
    @Autowired
    public ChatService(ChatClient chatClient,
                       @Qualifier("memoryChatClient") ChatClient memoryChatClient,
                       @Qualifier("ragChatClient") ChatClient ragChatClient,
                       VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.memoryChatClient = memoryChatClient;
        this.ragChatClient = ragChatClient;
        this.vectorStore = vectorStore;
    }

    /**
     * 基础对话
     *
     * <p>最简单的AI调用方式，直接传递用户消息获取回复。</p>
     *
     * <p><b>执行流程：</b></p>
     * <ol>
     *   <li>构建Prompt，包含系统提示词和用户消息</li>
     *   <li>调用ChatClient发送请求</li>
     *   <li>返回AI生成的文本响应</li>
     * </ol>
     *
     * <p><b>特点：</b></p>
     * <ul>
     *   <li>无记忆功能，每次对话独立</li>
     *   <li>适合简单问答场景</li>
     *   <li>响应速度快</li>
     * </ul>
     *
     * @param message 用户消息
     * @return AI回复内容
     */
    public String chat(String message) {
        log.info("执行基础对话: message={}", message);
        return chatClient.prompt()
                // 设置用户消息
                .user(message)
                // 执行调用
                .call()
                // 获取内容
                .content();
    }

    /**
     * 流式对话
     *
     * <p>使用Flux实现流式响应，适合实现打字机效果。</p>
     *
     * <p><b>执行流程：</b></p>
     * <ol>
     *   <li>构建Prompt</li>
     *   <li>调用stream()方法开启流式输出</li>
     *   <li>返回Flux&lt;String&gt;，每个元素是生成的token</li>
     * </ol>
     *
     * <p><b>前端使用示例（SSE）：</b></p>
     * <pre>
     * const eventSource = new EventSource('/api/ai/chat/stream?message=你好');
     * eventSource.onmessage = (event) => {
     *     console.log(event.data);
     * };
     * </pre>
     *
     * @param message 用户消息
     * @return Flux&lt;String&gt; 流式响应
     */
    public Flux<String> chatStream(String message) {
        log.info("执行流式对话: message={}", message);
        return chatClient.prompt()
                .user(message)
                // 开启流式输出
                .stream()
                // 获取内容流
                .content();
    }

    /**
     * 带记忆的多轮对话
     *
     * <p>使用MessageChatMemoryAdvisor实现对话历史管理。</p>
     *
     * <p><b>工作原理：</b></p>
     * <ul>
     *   <li>通过conversationId区分不同会话</li>
     *   <li>Advisor自动加载历史消息</li>
     *   <li>Advisor自动保存新的对话</li>
     *   <li>支持多用户并发，会话隔离</li>
     * </ul>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>客服机器人</li>
     *   <li>个人助手</li>
     *   <li>多轮交互场景</li>
     * </ul>
     *
     * @param conversationId 会话ID（用于区分不同用户/会话）
     * @param message 用户消息
     * @return AI回复内容
     */
    public String chatWithMemory(String conversationId, String message) {
        log.info("执行记忆对话: conversationId={}, message={}", conversationId, message);
        return memoryChatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 流式记忆对话
     *
     * @param conversationId 会话ID
     * @param message 用户消息
     * @return Flux&lt;String&gt; 流式响应
     */
    public Flux<String> chatWithMemoryStream(String conversationId, String message) {
        log.info("执行流式记忆对话: conversationId={}, message={}", conversationId, message);
        return memoryChatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    /**
     * RAG知识库问答
     *
     * <p>基于向量检索的增强生成，结合私有知识库回答。</p>
     *
     * <p><b>执行流程：</b></p>
     * <ol>
     *   <li>将用户问题向量化</li>
     *   <li>在VectorStore中检索相似文档</li>
     *   <li>将检索结果作为上下文注入Prompt</li>
     *   <li>调用模型生成回答</li>
     * </ol>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>企业知识库问答</li>
     *   <li>产品文档查询</li>
     *   <li>私有数据问答</li>
     * </ul>
     *
     * @param conversationId 会话ID
     * @param message 用户问题
     * @return AI回复内容
     */
    public String chatWithRag(String conversationId, String message) {
        log.info("执行RAG问答: conversationId={}, message={}", conversationId, message);
        return ragChatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 带函数调用的对话
     *
     * <p>AI可根据需要调用预定义的Java函数。</p>
     *
     * <p><b>工作原理：</b></p>
     * <ol>
     *   <li>注册Function Bean到ChatClient</li>
     *   <li>AI判断是否需要调用函数</li>
     *   <li>如需调用，AI生成函数参数</li>
     *   <li>应用执行函数并返回结果</li>
     *   <li>AI基于结果生成最终回复</li>
     * </ol>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>天气查询</li>
     *   <li>数据计算</li>
     *   <li>API调用</li>
     *   <li>数据库查询</li>
     * </ul>
     *
     * @param message 用户消息
     * @return AI回复内容
     */
    public String chatWithFunction(String message) {
        log.info("执行函数调用对话: message={}", message);
        return chatClient.prompt()
                .user(message)
                // 注册工具函数
                .functions("weatherFunction", "calculatorFunction")
                .call()
                .content();
    }

    /**
     * 生成结构化输出
     *
     * <p>使用BeanOutputConverter将AI输出转换为Java对象。</p>
     *
     * <p><b>工作原理：</b></p>
     * <ol>
     *   <li>定义目标Java类（POJO）</li>
     *   <li>创建BeanOutputConverter</li>
     *   <li>将schema注入Prompt指导AI输出JSON</li>
     *   <li>框架自动将JSON反序列化为Java对象</li>
     * </ol>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>情感分析</li>
     *   <li>实体提取</li>
     *   <li>数据分类</li>
     *   <li>结构化报告生成</li>
     * </ul>
     *
     * @param text 待分析的文本
     * @return SentimentResult 情感分析结果
     */
    public SentimentResult analyzeSentiment(String text) {
        log.info("执行情感分析: text={}", text);

        // 创建输出转换器
        BeanOutputConverter<SentimentResult> converter = new BeanOutputConverter<>(SentimentResult.class);

        // 获取JSON Schema
        String format = converter.getFormat();

        // 构建系统提示词，指导AI输出指定格式的JSON
        SystemPromptTemplate promptTemplate = new SystemPromptTemplate("""
                你是一个情感分析专家。分析给定文本的情感倾向。
                请以JSON格式返回结果，格式如下：
                {format}
                """);

        Message systemMessage = promptTemplate.createMessage(Map.of("format", format));
        UserMessage userMessage = new UserMessage(text);

        // 创建Prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // 调用模型
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        // 转换输出为Java对象
        String content = response.getResult().getOutput().getContent();
        return converter.convert(content);
    }

    /**
     * 生成诗歌（结构化输出示例）
     *
     * @param topic 主题
     * @param style 风格
     * @return PoemResult 诗歌结果
     */
    public PoemResult generatePoem(String topic, String style) {
        log.info("生成诗歌: topic={}, style={}", topic, style);

        BeanOutputConverter<PoemResult> converter = new BeanOutputConverter<>(PoemResult.class);
        String format = converter.getFormat();

        String prompt = String.format("""
                你是一位才华横溢的诗人。请创作一首关于"%s"的%s。
                请以JSON格式返回结果，格式如下：
                %s
                """, topic, style, format);

        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return converter.convert(content);
    }

    // ==================== 知识库管理 ====================

    /**
     * 添加文档到知识库
     *
     * <p>将文本内容向量化并存储到VectorStore。</p>
     *
     * @param content 文档内容
     * @param metadata 文档元数据
     */
    public void addDocumentToKnowledgeBase(String content, Map<String, Object> metadata) {
        log.info("添加文档到知识库: content length={}, metadata={}", content.length(), metadata);
        Document document = new Document(content, metadata);
        vectorStore.add(List.of(document));
    }

    /**
     * 从知识库检索文档
     *
     * @param query 查询文本
     * @param topK 返回结果数量
     * @return 相关文档列表
     */
    public List<Document> searchKnowledgeBase(String query, int topK) {
        log.info("检索知识库: query={}, topK={}", query, topK);
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();
        return vectorStore.similaritySearch(request);
    }

    // ==================== 结构化输出类定义 ====================

    /**
     * 情感分析结果
     *
     * <p>用于接收AI返回的结构化数据。</p>
     *
     * @param sentiment 情感标签：POSITIVE（积极）、NEGATIVE（消极）、NEUTRAL（中性）
     * @param confidence 置信度分数：0.0-1.0
     * @param reason 分析理由说明
     */
    public record SentimentResult(String sentiment, Double confidence, String reason) {
    }

    /**
     * 诗歌生成结果
     *
     * @param title 诗歌标题
     * @param content 诗歌内容
     * @param explanation 诗歌赏析/注释
     */
    public record PoemResult(String title, String content, String explanation) {
    }
}
