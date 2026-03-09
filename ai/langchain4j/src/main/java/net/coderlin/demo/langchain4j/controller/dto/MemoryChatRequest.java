package net.coderlin.demo.langchain4j.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 带记忆的对话请求DTO
 * 
 * <p>用于需要上下文记忆的多轮对话场景。</p>
 * 
 * @author
 * @since 1.0.0
 */
@Data
public class MemoryChatRequest {

    /**
     * 会话ID/记忆标识符
     * 
     * <p>用于区分不同用户的对话历史。</p>
     * <p>建议格式：user_123、session_abc、device_xyz</p>
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}
