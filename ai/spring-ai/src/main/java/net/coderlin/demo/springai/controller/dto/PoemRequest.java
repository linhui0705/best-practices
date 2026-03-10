package net.coderlin.demo.springai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 诗歌创作请求DTO
 *
 * <p>用于接收诗歌创作请求的参数。</p>
 *
 * @author
 * @since 1.0.0
 */
@Data
public class PoemRequest {

    /**
     * 诗歌主题
     *
     * <p>例如：春天、月亮、故乡等</p>
     */
    @NotBlank(message = "主题不能为空")
    private String topic;

    /**
     * 诗歌风格
     *
     * <p>例如：七言绝句、五言律诗、现代诗等</p>
     * <p>可选，默认为"七言绝句"</p>
     */
    private String style = "七言绝句";
}
