package net.coderlin.demo.langchain4j.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 带记忆功能的AI助手服务接口
 * 
 * <p>本接口演示了LangChain4j的记忆功能（ChatMemory），实现多轮对话上下文管理。</p>
 * 
 * <p><b>核心概念：</b></p>
 * <ul>
 *   <li><b>ChatMemory</b>：对话记忆，存储历史消息（UserMessage和AiMessage）</li>
 *   <li><b>MemoryId</b>：记忆标识符，区分不同用户/会话的对话历史</li>
 *   <li><b>消息窗口</b>：控制保留多少轮对话，防止超出模型上下文限制</li>
 * </ul>
 * 
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>客服机器人：记住用户问题和之前的回答</li>
 *   <li>编程助手：记住正在编写的代码上下文</li>
 *   <li>学习辅导：跟踪学习进度和已讲解内容</li>
 * </ul>
 * 
 * <p><b>工作流程：</b></p>
 * <ol>
 *   <li>用户发送消息时携带memoryId（如用户ID或会话ID）</li>
 *   <li>LangChain4j根据memoryId查找对应的ChatMemory</li>
 *   <li>将历史消息与新消息一起发送给模型</li>
 *   <li>模型回复后，更新ChatMemory</li>
 * </ol>
 * 
 * <p><b>重要注解：</b></p>
 * <ul>
 *   <li><b>@MemoryId</b>：标记参数为记忆标识符
     *     <ul>
     *       <li>相同memoryId共享同一份对话历史</li>
     *       <li>不同memoryId完全隔离</li>
     *     </ul>
 *   </li>
 * </ul>
 * 
 * @author
 * @since 1.0.0
 */
public interface MemoryAssistant {

    /**
     * 带记忆的多轮对话
     * 
     * <p>核心方法，演示如何使用@MemoryId实现上下文感知对话。</p>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li><b>@MemoryId String memoryId</b>：记忆标识符
     *     <ul>
     *       <li>可以是用户ID、会话ID、设备ID等</li>
     *       <li>相同ID的对话会共享上下文</li>
     *       <li>建议：user_123、session_abc、device_xyz</li>
     *     </ul>
     *   </li>
     *   <li><b>String message</b>：用户当前输入</li>
     * </ul>
     * 
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 第一轮对话 - memoryId为"user_001"
     * assistant.chatWithMemory("user_001", "我叫张三");
     * // AI: 你好张三，很高兴认识你！
     * 
     * // 第二轮对话 - 同一memoryId，AI记得之前的对话
     * assistant.chatWithMemory("user_001", "我叫什么名字？");
     * // AI: 你叫张三。
     * 
     * // 不同memoryId，没有之前的记忆
     * assistant.chatWithMemory("user_002", "我叫什么名字？");
     * // AI: 我不知道你的名字，你能告诉我吗？
     * </pre>
     * 
     * @param memoryId 记忆标识符，用于区分不同会话
     * @param message 用户输入的消息
     * @return AI的回复（基于当前消息+历史对话）
     */
    @SystemMessage("""
        你是一个 helpful AI 助手，请用中文回答问题。
        请记住对话历史，根据上下文理解用户意图。
        如果用户提到之前的内容，要能够关联起来回答。
        """)
    String chatWithMemory(@MemoryId String memoryId, @UserMessage String message);

    /**
     * 角色扮演的多轮对话
     * 
     * <p>演示更复杂的SystemMessage和记忆结合使用。</p>
     * 
     * <p><b>场景说明：</b></p>
     * <ul>
     *   <li>AI扮演Java技术专家角色</li>
     *   <li>记住用户的技术水平和已讨论的话题</li>
     *   <li>提供连贯的技术指导</li>
     * </ul>
     * 
     * @param sessionId 会话ID
     * @param question 技术问题
     * @return 技术解答
     */
    @SystemMessage("""
        你是一位资深的Java技术专家，擅长Spring Boot、微服务架构和分布式系统。
        你的职责是：
        1. 回答Java相关技术问题
        2. 根据用户的知识水平调整回答深度
        3. 记住用户之前问过的问题，避免重复解释
        4. 提供代码示例时，确保代码完整且可运行
        请用中文回答，保持专业但友好的语气。
        """)
    String techConsultation(@MemoryId String sessionId, @UserMessage String question);

    /**
     * 带记忆和模板的复杂对话
     * 
     * <p>演示@MemoryId、@UserMessage和@V的组合使用。</p>
     * 
     * <p><b>适用场景：</b></p>
     * <ul>
     *   <li>需要结构化输入的多轮对话</li>
     *   <li>表单式交互，收集多个字段信息</li>
     *   <li>步骤式引导流程</li>
     * </ul>
     * 
     * @param conversationId 对话ID
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @return AI确认和引导信息
     */
    @SystemMessage("""
        你是一个信息收集助手，正在帮助用户填写表单。
        你需要：
        1. 确认用户提供的每个字段值
        2. 记录已收集的信息
        3. 提示下一步需要填写的字段
        4. 当所有信息收集完毕后，给出完整汇总
        请保持友好和耐心。
        """)
    @UserMessage("字段{{fieldName}}的值是：{{fieldValue}}")
    String collectFormData(
            @MemoryId String conversationId,
            @V("fieldName") String fieldName,
            @V("fieldValue") String fieldValue
    );
}
