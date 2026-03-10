package net.coderlin.demo.springai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 文档添加请求DTO
 *
 * <p>用于向知识库添加文档的请求参数。</p>
 *
 * @author
 * @since 1.0.0
 */
@Data
public class DocumentRequest {

    /**
     * 文档内容
     */
    @NotBlank(message = "文档内容不能为空")
    private String content;

    /**
     * 文档元数据（可选）
     *
     * <p>可用于存储文档来源、分类、标签等信息。</p>
     */
    private Map<String, Object> metadata;
}
