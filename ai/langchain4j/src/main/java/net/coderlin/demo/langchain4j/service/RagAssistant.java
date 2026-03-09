package net.coderlin.demo.langchain4j.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.rag.content.retriever.ContentRetriever;

/**
 * RAG（检索增强生成）AI助手服务接口
 * 
 * <p>本接口演示了LangChain4j的RAG功能，实现基于私有知识库的问答。</p>
 * 
 * <p><b>RAG核心原理：</b></p>
 * <ol>
 *   <li><b>检索（Retrieval）</b>：根据用户问题，从知识库检索相关内容</li>
 *   <li><b>增强（Augmented）</b>：将检索结果作为上下文添加到提示词</li>
 *   <li><b>生成（Generation）</b>：模型基于上下文生成回答</li>
 * </ol>
 * 
 * <p><b>解决的问题：</b></p>
 * <ul>
 *   <li>模型幻觉：基于事实知识回答，减少编造</li>
 *   <li>知识时效：可更新知识库，无需重新训练模型</li>
 *   <li>数据隐私：私有数据不发送给模型训练</li>
 *   <li>可溯源：回答可追溯到具体知识来源</li>
 * </ul>
 * 
 * <p><b>关键组件：</b></p>
 * <ul>
 *   <li><b>ContentRetriever</b>：内容检索器，负责从向量库检索相关内容</li>
 *   <li><b>EmbeddingStore</b>：向量存储，存储文档向量</li>
 *   <li><b>EmbeddingModel</b>：嵌入模型，将文本转为向量</li>
 * </ul>
 * 
 * <p><b>配置方式：</b></p>
 * <pre>
 * // 在AI Service中指定ContentRetriever
 * @AiService
 * public interface RagAssistant {
 *     @SystemMessage("基于提供的信息回答问题")
 *     String answerFromKnowledgeBase(String question);
 * }
 * 
 * // 配置类中注入ContentRetriever
 * @Bean
 * public RagAssistant ragAssistant(ContentRetriever retriever) {
 *     return AiServices.builder(RagAssistant.class)
 *         .contentRetriever(retriever)
 *         .build();
 * }
 * </pre>
 * 
 * @author
 * @since 1.0.0
 */
public interface RagAssistant {

    /**
     * 基于知识库回答问题
     * 
     * <p>核心RAG方法，自动从知识库检索相关内容并生成回答。</p>
     * 
     * <p><b>执行流程：</b></p>
     * <ol>
     *   <li>接收用户问题</li>
     *   <li>使用EmbeddingModel将问题转为向量</li>
     *   <li>在EmbeddingStore中检索相似向量（Top-K）</li>
     *   <li>获取对应的文本片段作为上下文</li>
     *   <li>构建增强提示词：SystemMessage + 上下文 + UserMessage</li>
     *   <li>调用模型生成回答</li>
     * </ol>
     * 
     * <p><b>提示词工程：</b></p>
     * <ul>
     *   <li>明确指示AI基于提供的信息回答</li>
     *   <li>要求AI说明信息来源</li>
     *   <li>设定无法回答时的处理方式</li>
     * </ul>
     * 
     * @param question 用户问题
     * @return 基于知识库的回答
     */
    @SystemMessage("""
        你是一个专业的技术文档助手，基于提供的信息回答用户问题。
        
        回答规则：
        1. 严格基于提供的信息回答，不要编造内容
        2. 如果提供的信息不足以回答问题，请明确说明
        3. 回答要准确、简洁、专业
        4. 可以适当总结和归纳，但不要偏离原文意思
        5. 如果涉及多个方面，请分点说明
        
        请用中文回答。
        """)
    String answerFromKnowledgeBase(@UserMessage String question);

    /**
     * 带引用的知识库问答
     * 
     * <p>增强版RAG，要求AI标注信息来源。</p>
     * 
     * <p><b>适用场景：</b></p>
     * <ul>
     *   <li>需要验证回答准确性的场景</li>
     *   <li>学术、法律、医疗等对准确性要求高的领域</li>
     *   <li>用户需要追溯原始文档的场景</li>
     * </ul>
     * 
     * <p><b>实现方式：</b></p>
     * <ul>
     *   <li>在SystemMessage中明确要求标注来源</li>
     *   <li>LangChain4j会自动将文档元数据传递给模型</li>
     *   <li>模型可在回答中引用来源标识</li>
     * </ul>
     * 
     * @param question 用户问题
     * @return 带引用标注的回答
     */
    @SystemMessage("""
        你是一个专业的技术文档助手，基于提供的信息回答用户问题。
        
        回答规则：
        1. 基于提供的信息回答，不要编造内容
        2. **重要**：在回答中标注信息来源，格式为 [来源: 文档名]
        3. 如果信息来自多个文档，请分别标注
        4. 如果信息不足以回答，请说明"根据现有资料无法确定"
        5. 保持回答的准确性和专业性
        
        示例格式：
        "LangChain4j提供了统一的API [来源: langchain4j-intro.txt]，
         并且支持Spring Boot集成 [来源: spring-boot-integration.txt]。"
        
        请用中文回答。
        """)
    String answerWithCitation(@UserMessage String question);

    /**
     * 带记忆的RAG问答
     * 
     * <p>结合RAG和ChatMemory，实现上下文感知的知识库问答。</p>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>多轮技术咨询，需要记住之前的问题</li>
     *   <li>深入探讨某个技术话题</li>
     *   <li>基于历史对话理解当前问题的上下文</li>
     * </ul>
     * 
     * <p><b>工作流程：</b></p>
     * <ol>
     *   <li>根据memoryId获取对话历史</li>
     *   <li>将历史对话与新问题一起用于检索</li>
     *   <li>检索相关知识</li>
     *   <li>生成上下文感知的回答</li>
     * </ol>
     * 
     * @param sessionId 会话ID
     * @param question 用户问题
     * @return 基于知识库和对话历史的回答
     */
    @SystemMessage("""
        你是一个专业的LangChain4j技术顾问。
        
        回答规则：
        1. 基于提供的技术文档回答用户问题
        2. 记住对话历史，理解用户的追问和上下文
        3. 如果用户问题与之前的问题相关，要结合起来回答
        4. 提供准确的技术信息，不要猜测
        5. 可以适当提供代码示例帮助理解
        
        请用中文回答，保持专业友好的语气。
        """)
    String chatWithKnowledgeBase(@MemoryId String sessionId, @UserMessage String question);
}
