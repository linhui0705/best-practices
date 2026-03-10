package net.coderlin.demo.springai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RAG知识库查询请求DTO
 *
 * <p>用于接收RAG知识库问答的请求参数。</p>
 *
 * @author
 * @since 1.0.0
 */
@Data
public class RagQueryRequest {

    /**
     * 会话ID
     *
     * <p>用于区分不同用户的对话历史。</p>
     */
    @NotBlank(message = "会话ID不能为空")
    private String conversationId;

    /**
     * 用户问题
     */
    @NotBlank(message = "问题内容不能为空")
    private String question;
}
