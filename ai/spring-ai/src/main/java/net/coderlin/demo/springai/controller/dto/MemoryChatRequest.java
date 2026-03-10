package net.coderlin.demo.springai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 记忆对话请求DTO
 *
 * <p>用于接收带记忆功能的多轮对话请求。</p>
 *
 * @author
 * @since 1.0.0
 */
@Data
public class MemoryChatRequest {

    /**
     * 会话ID
     *
     * <p>用于区分不同用户的对话历史。</p>
     * <p>相同的conversationId会共享对话历史。</p>
     */
    @NotBlank(message = "会话ID不能为空")
    private String conversationId;

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}
