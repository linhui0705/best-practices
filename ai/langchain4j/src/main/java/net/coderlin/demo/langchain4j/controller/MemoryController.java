package net.coderlin.demo.langchain4j.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coderlin.demo.langchain4j.controller.dto.ChatResponse;
import net.coderlin.demo.langchain4j.controller.dto.MemoryChatRequest;
import net.coderlin.demo.langchain4j.service.MemoryAssistant;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 带记忆功能的对话Controller
 * 
 * <p>提供多轮对话API，支持上下文记忆。</p>
 * 
 * <p><b>核心概念：</b></p>
 * <ul>
 *   <li>sessionId：会话标识符，相同ID共享对话历史</li>
 *   <li>ChatMemory：自动管理多轮对话上下文</li>
 * </ul>
 * 
 * <p><b>API端点：</b></p>
 * <ul>
 *   <li>POST /api/memory/chat - 基础记忆对话</li>
 *   <li>POST /api/memory/tech - 技术咨询（专家模式）</li>
 *   <li>POST /api/memory/form - 表单数据收集</li>
 * </ul>
 * 
 * @author
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/memory")
@RequiredArgsConstructor
public class MemoryController {

    /**
     * 注入带记忆的AI助手服务
     */
    private final MemoryAssistant memoryAssistant;

    /**
     * 带记忆的多轮对话接口
     * 
     * <p>核心接口，演示ChatMemory的基本用法。</p>
     * 
     * <p><b>使用流程：</b></p>
     * <ol>
     *   <li>第一轮：发送sessionId和第一条消息</li>
     *   <li>后续轮：使用相同sessionId，AI会记住之前的对话</li>
     * </ol>
     * 
     * <p><b>请求示例（第一轮）：</b></p>
     * <pre>
     * POST /api/memory/chat
     * Content-Type: application/json
     * 
     * {
     *   "sessionId": "user_001",
     *   "message": "我叫张三"
     * }
     * </pre>
     * 
     * <p><b>请求示例（第二轮，同一sessionId）：</b></p>
     * <pre>
     * POST /api/memory/chat
     * Content-Type: application/json
     * 
     * {
     *   "sessionId": "user_001",
     *   "message": "我叫什么名字？"
     * }
     * // AI回复：你叫张三。
     * </pre>
     * 
     * @param request 带记忆的对话请求
     * @return AI回复
     */
    @PostMapping("/chat")
    public Mono<ChatResponse> chatWithMemory(@Valid @RequestBody MemoryChatRequest request) {
        log.info("收到记忆对话请求: sessionId={}, message={}", 
                request.getSessionId(), request.getMessage());
        return Mono.fromCallable(() -> {
            String response = memoryAssistant.chatWithMemory(
                    request.getSessionId(), 
                    request.getMessage()
            );
            log.info("记忆对话回复成功");
            return ChatResponse.success(response, request.getSessionId());
        }).onErrorResume(e -> {
            log.error("记忆对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 技术咨询接口
     * 
     * <p>AI扮演Java技术专家，提供技术咨询服务。</p>
     * 
     * <p><b>特点：</b></p>
     * <ul>
     *   <li>记住用户的技术水平</li>
     *   <li>避免重复解释已讨论的内容</li>
     *   <li>提供连贯的技术指导</li>
     * </ul>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/memory/tech
     * Content-Type: application/json
     * 
     * {
     *   "sessionId": "tech_session_001",
     *   "message": "Spring Boot是什么？"
     * }
     * </pre>
     * 
     * @param request 技术咨询请求
     * @return 技术解答
     */
    @PostMapping("/tech")
    public Mono<ChatResponse> techConsultation(@Valid @RequestBody MemoryChatRequest request) {
        log.info("收到技术咨询请求: sessionId={}, question={}", 
                request.getSessionId(), request.getMessage());
        return Mono.fromCallable(() -> {
            String response = memoryAssistant.techConsultation(
                    request.getSessionId(), 
                    request.getMessage()
            );
            log.info("技术咨询回复成功");
            return ChatResponse.success(response, request.getSessionId());
        }).onErrorResume(e -> {
            log.error("技术咨询处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 表单数据收集接口
     * 
     * <p>演示如何使用AI收集结构化表单数据。</p>
     * 
     * <p><b>使用方式：</b></p>
     * <ol>
     *   <li>逐个字段提交数据</li>
     *   <li>AI确认并记录每个字段</li>
     *   <li>提示下一个需要填写的字段</li>
     * </ol>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/memory/form
     * Content-Type: application/json
     * 
     * {
     *   "sessionId": "form_001",
     *   "message": "name:张三"  // 格式：字段名:字段值
     * }
     * </pre>
     * 
     * @param request 表单数据请求
     * @return 确认信息和下一步提示
     */
    @PostMapping("/form")
    public Mono<ChatResponse> collectFormData(@Valid @RequestBody MemoryChatRequest request) {
        log.info("收到表单数据: sessionId={}, data={}", 
                request.getSessionId(), request.getMessage());
        return Mono.fromCallable(() -> {
            // 解析字段名和值（格式：fieldName:fieldValue）
            String[] parts = request.getMessage().split(":", 2);
            if (parts.length != 2) {
                return ChatResponse.error("格式错误，请使用 '字段名:字段值' 格式");
            }
            
            String fieldName = parts[0].trim();
            String fieldValue = parts[1].trim();
            
            String response = memoryAssistant.collectFormData(
                    request.getSessionId(), 
                    fieldName, 
                    fieldValue
            );
            log.info("表单数据处理成功");
            return ChatResponse.success(response, request.getSessionId());
        }).onErrorResume(e -> {
            log.error("表单数据处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * GET方式记忆对话（便捷测试接口）
     * 
     * <p>用于快速测试记忆功能。</p>
     * 
     * <p><b>示例：</b></p>
     * <pre>
     * GET /api/memory/chat?sessionId=user_001&message=你好
     * </pre>
     * 
     * @param sessionId 会话ID
     * @param message 用户消息
     * @return AI回复
     */
    @GetMapping("/chat")
    public Mono<ChatResponse> chatWithMemoryGet(
            @RequestParam String sessionId,
            @RequestParam String message) {
        log.info("收到GET记忆对话请求: sessionId={}, message={}", sessionId, message);
        return Mono.fromCallable(() -> {
            String response = memoryAssistant.chatWithMemory(sessionId, message);
            return ChatResponse.success(response, sessionId);
        }).onErrorResume(e -> {
            log.error("记忆对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }
}
