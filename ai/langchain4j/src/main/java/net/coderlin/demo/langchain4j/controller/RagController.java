package net.coderlin.demo.langchain4j.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coderlin.demo.langchain4j.controller.dto.ChatResponse;
import net.coderlin.demo.langchain4j.controller.dto.RagQueryRequest;
import net.coderlin.demo.langchain4j.service.RagAssistant;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * RAG（检索增强生成）Controller
 *
 * <p>提供基于知识库的问答API接口。</p>
 *
 * <p><b>RAG工作流程：</b></p>
 * <ol>
 *   <li>用户提出问题</li>
 *   <li>系统从向量库检索相关知识</li>
 *   <li>将检索结果作为上下文传递给AI</li>
 *   <li>AI基于知识生成回答</li>
 * </ol>
 *
 * <p><b>API端点：</b></p>
 * <ul>
 *   <li>POST /api/rag/query - 基础知识库查询</li>
 *   <li>POST /api/rag/citation - 带引用来源的查询</li>
 *   <li>POST /api/rag/chat - 带记忆的知识库对话</li>
 * </ul>
 *
 * @author
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    /**
     * 注入RAG助手服务
     */
    private final RagAssistant ragAssistant;

    /**
     * 基础知识库查询接口
     *
     * <p>从预加载的知识库中检索信息并回答。</p>
     *
     * <p><b>知识库内容：</b></p>
     * <ul>
     *   <li>LangChain4j框架介绍</li>
     *   <li>Spring Boot集成指南</li>
     *   <li>AI Service使用说明</li>
     *   <li>ChatMemory功能介绍</li>
     *   <li>Tools工具调用说明</li>
     * </ul>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/rag/query
     * Content-Type: application/json
     *
     * {
     *   "question": "什么是LangChain4j？"
     * }
     * </pre>
     *
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "status": "success",
     *   "content": "LangChain4j是一个用于Java应用程序的LLM集成框架...",
     *   "timestamp": "2024-01-15T10:30:00"
     * }
     * </pre>
     *
     * @param request 查询请求
     * @return 基于知识库的回答
     */
    @PostMapping("/query")
    public Mono<ChatResponse> queryKnowledgeBase(@Valid @RequestBody RagQueryRequest request) {
        log.info("收到RAG查询请求: {}", request.getQuestion());
        return Mono.fromCallable(() -> {
            String response = ragAssistant.answerFromKnowledgeBase(request.getQuestion());
            log.info("RAG查询成功");
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("RAG查询失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("查询失败: " + e.getMessage()));
        });
    }

    /**
     * 带引用来源的知识库查询
     *
     * <p>AI会在回答中标注信息来源文档。</p>
     *
     * <p><b>适用场景：</b></p>
     * <ul>
     *   <li>需要验证回答准确性的场景</li>
     *   <li>学术、技术文档查询</li>
     *   <li>需要追溯原始出处的场景</li>
     * </ul>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/rag/citation
     * Content-Type: application/json
     *
     * {
     *   "question": "LangChain4j有哪些核心特性？"
     * }
     * </pre>
     *
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "status": "success",
     *   "content": "LangChain4j提供了统一的API [来源: langchain4j-intro.txt]，
     *              并且支持Spring Boot集成 [来源: spring-boot-integration.txt]。",
     *   "timestamp": "2024-01-15T10:30:00"
     * }
     * </pre>
     *
     * @param request 查询请求
     * @return 带引用来源的回答
     */
    @PostMapping("/citation")
    public Mono<ChatResponse> queryWithCitation(@Valid @RequestBody RagQueryRequest request) {
        log.info("收到带引用的RAG查询请求: {}", request.getQuestion());
        return Mono.fromCallable(() -> {
            String response = ragAssistant.answerWithCitation(request.getQuestion());
            log.info("带引用RAG查询成功");
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("带引用RAG查询失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("查询失败: " + e.getMessage()));
        });
    }

    /**
     * 带记忆的知识库对话
     *
     * <p>结合RAG和Memory，实现多轮技术咨询。</p>
     *
     * <p><b>特点：</b></p>
     * <ul>
     *   <li>记住之前的技术问题</li>
     *   <li>基于知识库回答</li>
     *   <li>支持追问和深入探讨</li>
     * </ul>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/rag/chat
     * Content-Type: application/json
     *
     * {
     *   "sessionId": "rag_session_001",
     *   "question": "什么是AI Service？"
     * }
     * </pre>
     *
     * <p><b>追问示例（同一sessionId）：</b></p>
     * <pre>
     * {
     *   "sessionId": "rag_session_001",
     *   "question": "它有哪些核心注解？"
     * }
     * // AI会理解"它"指的是AI Service
     * </pre>
     *
     * @param request 查询请求（包含sessionId）
     * @return 上下文感知的回答
     */
    @PostMapping("/chat")
    public Mono<ChatResponse> chatWithKnowledgeBase(@Valid @RequestBody RagQueryRequest request) {
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            // 如果没有提供sessionId，生成一个临时ID
            sessionId = "rag_" + System.currentTimeMillis();
            log.info("未提供sessionId，生成临时ID: {}", sessionId);
        }
        
        final String finalSessionId = sessionId;
        log.info("收到带记忆的RAG查询请求: sessionId={}, question={}", 
                finalSessionId, request.getQuestion());
        return Mono.fromCallable(() -> {
            String response = ragAssistant.chatWithKnowledgeBase(finalSessionId, request.getQuestion());
            log.info("带记忆RAG查询成功");
            return ChatResponse.success(response, finalSessionId);
        }).onErrorResume(e -> {
            log.error("带记忆RAG查询失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("查询失败: " + e.getMessage()));
        });
    }

    /**
     * GET方式知识库查询（便捷测试接口）
     *
     * <p><b>示例：</b></p>
     * <pre>
     * GET /api/rag/query?question=什么是LangChain4j
     * </pre>
     *
     * @param question 问题
     * @return 回答
     */
    @GetMapping("/query")
    public Mono<ChatResponse> queryKnowledgeBaseGet(@RequestParam String question) {
        log.info("收到GET RAG查询请求: {}", question);
        return Mono.fromCallable(() -> {
            String response = ragAssistant.answerFromKnowledgeBase(question);
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("RAG查询失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("查询失败: " + e.getMessage()));
        });
    }
}
