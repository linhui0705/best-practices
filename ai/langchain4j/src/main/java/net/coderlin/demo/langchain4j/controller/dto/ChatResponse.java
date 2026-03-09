package net.coderlin.demo.langchain4j.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通用对话响应DTO
 * 
 * <p>封装AI回复的标准响应格式。</p>
 * 
 * @author
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * 响应状态
     * 
     * <p>success - 成功</p>
     * <p>error - 失败</p>
     */
    private String status;

    /**
     * AI回复内容
     */
    private String content;

    /**
     * 错误信息（失败时）
     */
    private String errorMessage;

    /**
     * 响应时间戳
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 会话ID（如适用）
     */
    private String sessionId;

    /**
     * 创建成功响应
     * 
     * @param content AI回复内容
     * @return ChatResponse
     */
    public static ChatResponse success(String content) {
        return ChatResponse.builder()
                .status("success")
                .content(content)
                .build();
    }

    /**
     * 创建带sessionId的成功响应
     * 
     * @param content AI回复内容
     * @param sessionId 会话ID
     * @return ChatResponse
     */
    public static ChatResponse success(String content, String sessionId) {
        return ChatResponse.builder()
                .status("success")
                .content(content)
                .sessionId(sessionId)
                .build();
    }

    /**
     * 创建失败响应
     * 
     * @param errorMessage 错误信息
     * @return ChatResponse
     */
    public static ChatResponse error(String errorMessage) {
        return ChatResponse.builder()
                .status("error")
                .errorMessage(errorMessage)
                .build();
    }
}
