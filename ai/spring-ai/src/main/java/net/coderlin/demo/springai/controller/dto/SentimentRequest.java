package net.coderlin.demo.springai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 情感分析请求DTO
 *
 * <p>用于接收情感分析请求的参数。</p>
 *
 * @author
 * @since 1.0.0
 */
@Data
public class SentimentRequest {

    /**
     * 待分析的文本
     */
    @NotBlank(message = "文本内容不能为空")
    private String text;
}
