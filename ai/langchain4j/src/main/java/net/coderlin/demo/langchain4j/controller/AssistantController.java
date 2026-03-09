package net.coderlin.demo.langchain4j.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coderlin.demo.langchain4j.controller.dto.*;
import net.coderlin.demo.langchain4j.service.Assistant;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 基础AI助手Controller
 * 
 * <p>提供基础对话、诗歌创作、情感分析等API接口。</p>
 * 
 * <p><b>API端点说明：</b></p>
 * <ul>
 *   <li>POST /api/assistant/chat - 基础对话</li>
 *   <li>POST /api/assistant/poem - 诗歌创作</li>
 *   <li>POST /api/assistant/sentiment - 情感分析</li>
 * </ul>
 * 
 * @author
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    /**
     * 注入AI助手服务
     */
    private final Assistant assistant;

    /**
     * 基础对话接口
     * 
     * <p>最简单的AI对话，无记忆功能，每次独立。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/assistant/chat
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
        log.info("收到对话请求: {}", request.getMessage());
        return Mono.fromCallable(() -> {
            String response = assistant.chat(request.getMessage());
            log.info("AI回复成功");
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 诗歌创作接口
     * 
     * <p>使用模板方法，根据主题和风格创作诗歌。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/assistant/poem
     * Content-Type: application/json
     * 
     * {
     *   "topic": "春天",
     *   "style": "七言绝句"
     * }
     * </pre>
     * 
     * @param request 诗歌创作请求
     * @return 创作的诗歌
     */
    @PostMapping("/poem")
    public Mono<ChatResponse> writePoem(@Valid @RequestBody PoemRequest request) {
        log.info("收到诗歌创作请求: 主题={}, 风格={}", request.getTopic(), request.getStyle());
        return Mono.fromCallable(() -> {
            String poem = assistant.writePoem(request.getTopic(), request.getStyle());
            log.info("诗歌创作成功");
            return ChatResponse.success(poem);
        }).onErrorResume(e -> {
            log.error("诗歌创作失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("创作失败: " + e.getMessage()));
        });
    }

    /**
     * 情感分析接口
     * 
     * <p>分析文本的情感倾向，返回结构化结果。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/assistant/sentiment
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
            Assistant.SentimentResult result = assistant.analyzeSentiment(request.getText());
            log.info("情感分析完成: {} (置信度: {})", result.getSentiment(), result.getConfidence());
            // 将结果转为JSON字符串返回
            String jsonResult = String.format(
                "{\n  \"sentiment\": \"%s\",\n  \"confidence\": %.2f,\n  \"reason\": \"%s\"\n}",
                result.getSentiment(), result.getConfidence(), result.getReason()
            );
            return ChatResponse.success(jsonResult);
        }).onErrorResume(e -> {
            log.error("情感分析失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("分析失败: " + e.getMessage()));
        });
    }

    /**
     * GET方式基础对话（便捷接口）
     * 
     * <p>用于快速测试，直接通过URL参数发送消息。</p>
     * 
     * <p><b>示例：</b></p>
     * <pre>
     * GET /api/assistant/chat?message=你好
     * </pre>
     * 
     * @param message 用户消息
     * @return AI回复
     */
    @GetMapping("/chat")
    public Mono<ChatResponse> chatGet(@RequestParam String message) {
        log.info("收到GET对话请求: {}", message);
        return Mono.fromCallable(() -> {
            String response = assistant.chat(message);
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }
}
