package net.coderlin.demo.langchain4j.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 诗歌创作请求DTO
 * 
 * <p>用于请求AI创作诗歌的参数。</p>
 * 
 * @author
 * @since 1.0.0
 */
@Data
public class PoemRequest {

    /**
     * 诗歌主题
     * 
     * <p>如：春天、月亮、故乡等</p>
     */
    @NotBlank(message = "主题不能为空")
    private String topic;

    /**
     * 诗歌风格/体裁
     * 
     * <p>如：七言绝句、五言律诗、现代诗等</p>
     */
    @NotBlank(message = "风格不能为空")
    private String style;
}
