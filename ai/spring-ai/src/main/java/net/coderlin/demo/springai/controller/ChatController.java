package net.coderlin.demo.springai.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coderlin.demo.springai.controller.dto.*;
import net.coderlin.demo.springai.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * AI对话Controller
 *
 * <p>提供完整的AI对话API接口，包括基础对话、流式输出、记忆对话、RAG问答、函数调用等功能。</p>
 *
 * <p><b>API端点说明：</b></p>
 * <ul>
 *   <li>POST /api/ai/chat - 基础对话</li>
 *   <li>GET /api/ai/chat/stream - 流式对话（SSE）</li>
 *   <li>POST /api/ai/memory - 记忆对话</li>
 *   <li>POST /api/ai/rag - RAG知识库问答</li>
 *   <li>POST /api/ai/function - 函数调用</li>
 *   <li>POST /api/ai/sentiment - 情感分析</li>
 *   <li>POST /api/ai/poem - 诗歌创作</li>
 *   <li>POST /api/ai/documents - 添加知识库文档</li>
 * </ul>
 *
 * @author
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatController {

    /**
     * 注入AI对话服务
     */
    private final ChatService chatService;

    /**
     * 基础对话接口
     *
     * <p>最简单的AI对话，无记忆功能，每次独立。</p>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/ai/chat
     * Content-Type: application/json
     *
     * {
     *   "message": "你好，请介绍一下自己"
     * }
     * </pre>
     *
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "status": "success",
     *   "content": "你好！我是一个AI助手...",
     *   "timestamp": "2024-01-15T10:30:00"
     * }
     * </pre>
     *
     * @param request 对话请求
     * @return AI回复
     */
    @PostMapping("/chat")
    public Mono<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("收到基础对话请求: {}", request.getMessage());
        return Mono.fromCallable(() -> {
            String response = chatService.chat(request.getMessage());
            log.info("AI回复成功");
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * GET方式基础对话（便捷接口）
     *
     * <p>用于快速测试，直接通过URL参数发送消息。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>
     * GET /api/ai/chat?message=你好
     * </pre>
     *
     * @param message 用户消息
     * @return AI回复
     */
    @GetMapping("/chat")
    public Mono<ChatResponse> chatGet(@RequestParam String message) {
        log.info("收到GET对话请求: {}", message);
        return Mono.fromCallable(() -> {
            String response = chatService.chat(message);
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 流式对话接口（SSE）
     *
     * <p>使用Server-Sent Events实现流式输出，适合前端打字机效果。</p>
     *
     * <p><b>使用说明：</b></p>
     * <ul>
     *   <li>返回Content-Type: text/event-stream</li>
     *   <li>每个token作为独立事件推送</li>
     *   <li>前端使用EventSource接收</li>
     * </ul>
     *
     * <p><b>前端示例：</b></p>
     * <pre>
     * const eventSource = new EventSource('/api/ai/chat/stream?message=你好');
     * eventSource.onmessage = (event) => {
     *     document.getElementById('output').innerHTML += event.data;
     * };
     * </pre>
     *
     * @param message 用户消息
     * @return Flux&lt;String&gt; 流式响应
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String message) {
        log.info("收到流式对话请求: {}", message);
        return chatService.chatStream(message)
                .doOnNext(token -> log.debug("流式输出token: {}", token))
                .doOnError(e -> log.error("流式对话失败: {}", e.getMessage(), e));
    }

    /**
     * POST方式流式对话接口
     *
     * @param request 对话请求
     * @return Flux&lt;String&gt; 流式响应
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStreamPost(@Valid @RequestBody ChatRequest request) {
        log.info("收到POST流式对话请求: {}", request.getMessage());
        return chatService.chatStream(request.getMessage())
                .doOnError(e -> log.error("流式对话失败: {}", e.getMessage(), e));
    }

    /**
     * 记忆对话接口
     *
     * <p>支持多轮对话，自动维护对话历史。</p>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/ai/memory
     * Content-Type: application/json
     *
     * {
     *   "conversationId": "user-123",
     *   "message": "你好"
     * }
     * </pre>
     *
     * <p><b>多轮对话示例：</b></p>
     * <pre>
     * // 第一轮
     * {"conversationId": "user-123", "message": "我叫张三"}
     * // 第二轮
     * {"conversationId": "user-123", "message": "我叫什么名字？"}
     * // AI会回答：你叫张三
     * </pre>
     *
     * @param request 记忆对话请求
     * @return AI回复
     */
    @PostMapping("/memory")
    public Mono<ChatResponse> chatWithMemory(@Valid @RequestBody MemoryChatRequest request) {
        log.info("收到记忆对话请求: conversationId={}, message={}",
                request.getConversationId(), request.getMessage());
        return Mono.fromCallable(() -> {
            String response = chatService.chatWithMemory(
                    request.getConversationId(),
                    request.getMessage()
            );
            log.info("记忆对话回复成功");
            return ChatResponse.success(response, request.getConversationId());
        }).onErrorResume(e -> {
            log.error("记忆对话失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 流式记忆对话接口
     *
     * @param request 记忆对话请求
     * @return Flux&lt;String&gt; 流式响应
     */
    @PostMapping(value = "/memory/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWithMemoryStream(@Valid @RequestBody MemoryChatRequest request) {
        log.info("收到流式记忆对话请求: conversationId={}", request.getConversationId());
        return chatService.chatWithMemoryStream(
                        request.getConversationId(),
                        request.getMessage()
                )
                .doOnError(e -> log.error("流式记忆对话失败: {}", e.getMessage(), e));
    }

    /**
     * RAG知识库问答接口
     *
     * <p>基于向量检索的增强生成，结合私有知识库回答。</p>
     *
     * <p><b>使用流程：</b></p>
     * <ol>
     *   <li>先调用POST /api/ai/documents添加文档到知识库</li>
     *   <li>再调用此接口进行问答</li>
     * </ol>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/ai/rag
     * Content-Type: application/json
     *
     * {
     *   "conversationId": "user-123",
     *   "question": "我们公司的主要产品是什么？"
     * }
     * </pre>
     *
     * @param request RAG查询请求
     * @return AI回复
     */
    @PostMapping("/rag")
    public Mono<ChatResponse> chatWithRag(@Valid @RequestBody RagQueryRequest request) {
        log.info("收到RAG问答请求: conversationId={}, question={}",
                request.getConversationId(), request.getQuestion());
        return Mono.fromCallable(() -> {
            String response = chatService.chatWithRag(
                    request.getConversationId(),
                    request.getQuestion()
            );
            log.info("RAG问答回复成功");
            return ChatResponse.success(response, request.getConversationId());
        }).onErrorResume(e -> {
            log.error("RAG问答失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 函数调用对话接口
     *
     * <p>AI可根据用户输入自动调用预定义的Java函数。</p>
     *
     * <p><b>支持的函数：</b></p>
     * <ul>
     *   <li>weatherFunction - 天气查询</li>
     *   <li>calculatorFunction - 数学计算</li>
     * </ul>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/ai/function
     * Content-Type: application/json
     *
     * {
     *   "message": "北京今天天气怎么样？"
     * }
     * // 或
     * {
     *   "message": "计算 123 乘以 456"
     * }
     * </pre>
     *
     * @param request 对话请求
     * @return AI回复
     */
    @PostMapping("/function")
    public Mono<ChatResponse> chatWithFunction(@Valid @RequestBody ChatRequest request) {
        log.info("收到函数调用请求: {}", request.getMessage());
        return Mono.fromCallable(() -> {
            String response = chatService.chatWithFunction(request.getMessage());
            log.info("函数调用回复成功");
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("函数调用失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 情感分析接口
     *
     * <p>分析文本的情感倾向，返回结构化结果。</p>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/ai/sentiment
     * Content-Type: application/json
     *
     * {
     *   "text": "今天天气真好，心情非常愉快！"
     * }
     * </pre>
     *
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "status": "success",
     *   "content": "{\n  \"sentiment\": \"POSITIVE\",\n  \"confidence\": 0.95,\n  \"reason\": \"文本中包含'好'、'愉快'等积极词汇\"\n}",
     *   "timestamp": "2024-01-15T10:30:00"
     * }
     * </pre>
     *
     * @param request 情感分析请求
     * @return 情感分析结果（JSON格式）
     */
    @PostMapping("/sentiment")
    public Mono<ChatResponse> analyzeSentiment(@Valid @RequestBody SentimentRequest request) {
        log.info("收到情感分析请求: {}", request.getText());
        return Mono.fromCallable(() -> {
            ChatService.SentimentResult result = chatService.analyzeSentiment(request.getText());
            log.info("情感分析完成: {} (置信度: {})", result.sentiment(), result.confidence());
            // 将结果转为JSON字符串返回
            String jsonResult = String.format(
                    "{\n  \"sentiment\": \"%s\",\n  \"confidence\": %.2f,\n  \"reason\": \"%s\"\n}",
                    result.sentiment(), result.confidence(), result.reason()
            );
            return ChatResponse.success(jsonResult);
        }).onErrorResume(e -> {
            log.error("情感分析失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("分析失败: " + e.getMessage()));
        });
    }

    /**
     * 诗歌创作接口
     *
     * <p>根据主题和风格创作诗歌，返回结构化结果。</p>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/ai/poem
     * Content-Type: application/json
     *
     * {
     *   "topic": "春天",
     *   "style": "七言绝句"
     * }
     * </pre>
     *
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "status": "success",
     *   "content": "{\n  \"title\": \"春日\",\n  \"content\": \"春风拂面柳丝长...\",\n  \"explanation\": \"这首诗描绘了...\"\n}",
     *   "timestamp": "2024-01-15T10:30:00"
     * }
     * </pre>
     *
     * @param request 诗歌创作请求
     * @return 诗歌结果（JSON格式）
     */
    @PostMapping("/poem")
    public Mono<ChatResponse> generatePoem(@Valid @RequestBody PoemRequest request) {
        log.info("收到诗歌创作请求: topic={}, style={}", request.getTopic(), request.getStyle());
        return Mono.fromCallable(() -> {
            ChatService.PoemResult result = chatService.generatePoem(request.getTopic(), request.getStyle());
            log.info("诗歌创作成功: {}", result.title());
            // 将结果转为JSON字符串返回
            String jsonResult = String.format(
                    "{\n  \"title\": \"%s\",\n  \"content\": \"%s\",\n  \"explanation\": \"%s\"\n}",
                    result.title(), result.content(), result.explanation()
            );
            return ChatResponse.success(jsonResult);
        }).onErrorResume(e -> {
            log.error("诗歌创作失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("创作失败: " + e.getMessage()));
        });
    }

    /**
     * 添加文档到知识库
     *
     * <p>将文本内容添加到向量知识库，供RAG检索使用。</p>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/ai/documents
     * Content-Type: application/json
     *
     * {
     *   "content": "我们公司的主要产品是AI助手平台，提供智能对话服务...",
     *   "metadata": {
     *     "source": "产品介绍",
     *     "category": "产品"
     *   }
     * }
     * </pre>
     *
     * @param request 文档添加请求
     * @return 操作结果
     */
    @PostMapping("/documents")
    public Mono<ChatResponse> addDocument(@Valid @RequestBody DocumentRequest request) {
        log.info("收到添加文档请求: content length={}", request.getContent().length());
        return Mono.fromCallable(() -> {
            chatService.addDocumentToKnowledgeBase(request.getContent(), request.getMetadata());
            log.info("文档添加成功");
            return ChatResponse.success("文档添加成功");
        }).onErrorResume(e -> {
            log.error("添加文档失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("添加失败: " + e.getMessage()));
        });
    }

    /**
     * 生成会话ID
     *
     * <p>便捷接口，用于获取新的会话ID。</p>
     *
     * <p><b>示例：</b></p>
     * <pre>
     * GET /api/ai/session-id
     *
     * {
     *   "status": "success",
     *   "content": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
     *   "timestamp": "2024-01-15T10:30:00"
     * }
     * </pre>
     *
     * @return 新生成的会话ID
     */
    @GetMapping("/session-id")
    public ChatResponse generateSessionId() {
        String sessionId = UUID.randomUUID().toString();
        log.info("生成新会话ID: {}", sessionId);
        return ChatResponse.success(sessionId);
    }
}
