package net.coderlin.demo.langchain4j.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * AI应用配置属性类
 *
 * <p>使用@ConfigurationProperties实现类型安全的配置绑定。</p>
 *
 * <p><b>配置方式：</b></p>
 * <pre>
 * app:
 *   ai:
 *     default-system-message: "你是一个helpful AI助手"
 *     max-memory-messages: 10
 *     rag:
 *       max-results: 3
 *       min-score: 0.7
 * </pre>
 *
 * <p><b>设计优势：</b></p>
 * <ul>
 *   <li>类型安全：编译时检查配置项类型</li>
 *   <li>IDE支持：配置文件中有代码提示</li>
 *   <li>校验支持：使用JSR-303校验注解</li>
 *   <li>集中管理：所有AI相关配置统一管理</li>
 * </ul>
 *
 * @author
 * @since 1.0.0
 * @date 2024-01-15
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    /**
     * 默认系统提示词
     *
     * <p>未指定SystemMessage时使用的默认提示词。</p>
     */
    private String defaultSystemMessage = "你是一个 helpful AI 助手，请用中文回答问题。";

    /**
     * 最大记忆消息数
     *
     * <p>ChatMemory保留的最大消息轮数。</p>
     * <p>范围：1-50，默认10</p>
     */
    @Min(value = 1, message = "记忆消息数最小为1")
    @Max(value = 50, message = "记忆消息数最大为50")
    private int maxMemoryMessages = 10;

    /**
     * RAG检索配置
     */
    private RagConfig rag = new RagConfig();

    /**
     * 文档分割配置
     */
    private DocumentSplitterConfig documentSplitter = new DocumentSplitterConfig();

    /**
     * 重试配置
     */
    private RetryConfig retry = new RetryConfig();

    /**
     * RAG检索配置类
     *
     * <p>配置内容检索器的行为参数。</p>
     */
    @Data
    public static class RagConfig {

        /**
         * 最大返回结果数
         *
         * <p>每次检索返回的最相关文档片段数量。</p>
         * <p>范围：1-10，默认3</p>
         */
        @Min(value = 1, message = "最大结果数最小为1")
        @Max(value = 10, message = "最大结果数最大为10")
        private int maxResults = 3;

        /**
         * 最小相似度分数
         *
         * <p>只返回相似度高于此阈值的结果。</p>
         * <p>范围：0.0-1.0，默认0.7</p>
         */
        @Min(value = 0, message = "最小分数不能小于0")
        @Max(value = 1, message = "最小分数不能大于1")
        private double minScore = 0.7;
    }

    /**
     * 文档分割配置类
     *
     * <p>配置文档分割器的行为参数。</p>
     */
    @Data
    public static class DocumentSplitterConfig {

        /**
         * 最大片段大小（字符数）
         *
         * <p>每个文档片段的最大字符数。</p>
         * <p>范围：100-2000，默认500</p>
         */
        @Min(value = 100, message = "片段大小最小为100")
        @Max(value = 2000, message = "片段大小最大为2000")
        private int maxSize = 500;

        /**
         * 最大重叠大小（字符数）
         *
         * <p>相邻片段之间的重叠字符数，确保上下文连续性。</p>
         * <p>范围：0-200，默认50</p>
         */
        @Min(value = 0, message = "重叠大小最小为0")
        @Max(value = 200, message = "重叠大小最大为200")
        private int maxOverlap = 50;
    }

    /**
     * 重试配置类
     *
     * <p>配置AI服务调用的重试策略。</p>
     */
    @Data
    public static class RetryConfig {

        /**
         * 是否启用重试
         */
        private boolean enabled = true;

        /**
         * 最大重试次数
         *
         * <p>范围：1-5，默认3</p>
         */
        @Min(value = 1, message = "重试次数最小为1")
        @Max(value = 5, message = "重试次数最大为5")
        private int maxAttempts = 3;

        /**
         * 重试间隔（毫秒）
         *
         * <p>两次重试之间的等待时间。</p>
         * <p>范围：100-5000，默认1000</p>
         */
        @Min(value = 100, message = "重试间隔最小为100ms")
        @Max(value = 5000, message = "重试间隔最大为5000ms")
        private long delayMs = 1000L;
    }
}
