package net.coderlin.demo.springai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 基础对话请求DTO
 *
 * <p>用于接收用户对话请求的参数。</p>
 *
 * @author
 * @since 1.0.0
 */
@Data
public class ChatRequest {

    /**
     * 用户消息内容
     *
     * <p>必填字段，不能为空或空白。</p>
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}
