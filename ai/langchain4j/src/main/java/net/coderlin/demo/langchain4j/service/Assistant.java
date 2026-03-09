package net.coderlin.demo.langchain4j.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI助手服务接口 - 基础对话示例
 * 
 * <p>这是一个最基础的AI Service示例，展示了LangChain4j声明式接口的核心用法。</p>
 * 
 * <p><b>核心注解说明：</b></p>
 * <ul>
 *   <li><b>@SystemMessage</b>：系统提示词，设置AI的角色和行为准则
     *     <ul>
     *       <li>在每次对话开始时发送给模型</li>
     *       <li>影响AI的回答风格和方式</li>
     *     </ul>
 *   </li>
 *   <li><b>@UserMessage</b>：用户消息模板
     *     <ul>
     *       <li>支持使用{变量名}作为占位符</li>
     *       <li>方法参数通过@V注解映射到模板变量</li>
     *     </ul>
 *   </li>
 *   <li><b>@V</b>：Value缩写，将方法参数映射到模板变量</li>
 * </ul>
 * 
 * <p><b>使用示例：</b></p>
 * <pre>
 * @Autowired
 * private Assistant assistant;
 * 
 * // 简单对话
 * String response = assistant.chat("你好");
 * 
 * // 带变量的模板
 * String poem = assistant.writePoem("春天", "七言绝句");
 * </pre>
 * 
 * @author
 * @since 1.0.0
 */
public interface Assistant {

    /**
     * 基础对话方法
     * 
     * <p>最简单的AI调用方式，直接传递用户消息获取回复。</p>
     * 
     * <p><b>执行流程：</b></p>
     * <ol>
     *   <li>框架自动构建提示词：SystemMessage + UserMessage</li>
     *   <li>调用配置的ChatLanguageModel</li>
     *   <li>返回模型生成的文本响应</li>
     * </ol>
     * 
     * @param message 用户输入的消息
     * @return AI的回复文本
     */
    @SystemMessage("你是一个 helpful AI 助手，请用中文回答问题，回答要简洁明了。")
    String chat(@UserMessage String message);

    /**
     * 带模板的对话方法
     * 
     * <p>演示如何使用@UserMessage模板和@V参数映射。</p>
     * 
     * <p><b>模板语法：</b></p>
     * <ul>
     *   <li>使用双大括号{{变量名}}定义占位符</li>
     *   <li>方法参数使用@V("变量名")指定映射关系</li>
     *   <li>支持多个变量</li>
     * </ul>
     * 
     * @param topic 诗歌主题，映射到模板变量{{topic}}
     * @param style 诗歌风格，映射到模板变量{{style}}
     * @return AI生成的诗歌
     */
    @SystemMessage("你是一位才华横溢的诗人，擅长创作各种风格的诗歌。")
    @UserMessage("请创作一首关于{{topic}}的{{style}}。")
    String writePoem(@V("topic") String topic, @V("style") String style);

    /**
     * 结构化输出示例 - 情感分析
     * 
     * <p>演示如何让AI返回结构化数据（Java对象）。</p>
     * 
     * <p><b>工作原理：</b></p>
     * <ol>
     *   <li>定义返回类型的Java类（如SentimentResult）</li>
     *   <li>LangChain4j自动生成JSON Schema</li>
     *   <li>指导模型输出符合Schema的JSON</li>
     *   <li>框架自动将JSON反序列化为Java对象</li>
     * </ol>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>返回类型必须是具体的类（不能是接口）</li>
     *   <li>类需要有默认构造函数</li>
     *   <li>建议使用record或Lombok的@Data</li>
     * </ul>
     * 
     * @param text 待分析的文本
     * @return 情感分析结果对象
     */
    @SystemMessage("你是一个情感分析专家。分析给定文本的情感倾向，以JSON格式返回结果。")
    @UserMessage("请分析以下文本的情感：{{text}}")
    SentimentResult analyzeSentiment(@V("text") String text);

    /**
     * 情感分析结果类
     *
     * <p>用于接收AI返回的结构化数据。</p>
     */
    class SentimentResult {
        /**
         * 情感标签：POSITIVE（积极）、NEGATIVE（消极）、NEUTRAL（中性）
         */
        private String sentiment;
        /**
         * 置信度分数：0.0-1.0
         */
        private Double confidence;
        /**
         * 分析理由说明
         */
        private String reason;

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }

        public Double getConfidence() {
            return confidence;
        }

        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        @Override
        public String toString() {
            return String.format("SentimentResult{sentiment='%s', confidence=%.2f, reason='%s'}",
                    sentiment, confidence, reason);
        }
    }
}
