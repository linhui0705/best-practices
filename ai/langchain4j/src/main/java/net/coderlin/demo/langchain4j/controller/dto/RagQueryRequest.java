package net.coderlin.demo.langchain4j.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RAG知识库查询请求DTO
 * 
 * <p>用于基于知识库的问答请求。</p>
 * 
 * @author
 * @since 1.0.0
 */
@Data
public class RagQueryRequest {

    /**
     * 用户问题
     */
    @NotBlank(message = "问题不能为空")
    private String question;

    /**
     * 会话ID（可选）
     * 
     * <p>如需多轮对话，提供sessionId以维护上下文</p>
     */
    private String sessionId;
}
