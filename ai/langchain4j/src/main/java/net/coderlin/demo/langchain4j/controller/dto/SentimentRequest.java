package net.coderlin.demo.langchain4j.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 情感分析请求DTO
 * 
 * <p>用于请求AI分析文本情感倾向。</p>
 * 
 * @author
 * @since 1.0.0
 */
@Data
public class SentimentRequest {

    /**
     * 待分析的文本内容
     */
    @NotBlank(message = "文本内容不能为空")
    private String text;
}
