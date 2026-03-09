package net.coderlin.demo.langchain4j.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RAG数据初始化器
 * 
 * <p>应用启动时自动加载示例文档到向量存储，用于演示检索增强生成功能。</p>
 * 
 * <p><b>初始化流程：</b></p>
 * <ol>
 *   <li>创建示例文档（模拟知识库内容）</li>
 *   <li>使用DocumentSplitter分割文档为小块</li>
 *   <li>使用EmbeddingModel将文本块转为向量</li>
 *   <li>存储到EmbeddingStore</li>
 * </ol>
 * 
 * <p><b>文档内容说明：</b></p>
 * <ul>
 *   <li>LangChain4j简介：框架概述和核心特性</li>
 *   <li>Spring Boot集成：与Spring Boot的整合方式</li>
 *   <li>AI Service：声明式服务接口说明</li>
 * </ul>
 * 
 * @author
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RagDataInitializer {

    /**
     * 向量存储，用于保存文档向量
     */
    private final EmbeddingStore<TextSegment> embeddingStore;

    /**
     * 嵌入模型，用于生成文本向量
     */
    private final EmbeddingModel embeddingModel;

    /**
     * 文档分割器，将长文档分割为小块
     */
    private final DocumentSplitter documentSplitter;

    /**
     * 应用启动后执行的初始化方法
     * 
     * <p>使用@PostConstruct确保Spring容器初始化完成后执行</p>
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化RAG知识库数据...");
        
        try {
            // 加载示例文档
            List<Document> documents = loadSampleDocuments();
            
            // 处理并存储文档
            for (Document document : documents) {
                ingestDocument(document);
            }
            
            log.info("RAG知识库数据初始化完成，共加载 {} 个文档", documents.size());
        } catch (Exception e) {
            log.error("RAG数据初始化失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 加载示例文档
     * 
     * <p>模拟知识库内容，实际项目中可从数据库、文件系统等加载</p>
     * 
     * @return 文档列表
     */
    private List<Document> loadSampleDocuments() {
        return List.of(
            // 文档1：LangChain4j简介
            Document.from(
                """
                LangChain4j 是一个用于Java应用程序的LLM（大语言模型）集成框架。
                它提供了统一的API来与各种LLM提供商（如OpenAI、Google Vertex AI、Azure OpenAI等）进行交互。
                核心特性包括：
                1. 统一的ChatLanguageModel接口，支持多种模型提供商
                2. AI Service声明式接口，通过注解定义AI能力
                3. 内置记忆管理（ChatMemory），支持多轮对话
                4. RAG（检索增强生成）支持，集成向量存储
                5. 工具调用（Tool Calling），让AI执行自定义方法
                6. 结构化输出（Structured Output），将AI响应映射为Java对象
                LangChain4j的设计目标是简化Java开发者集成LLM能力的复杂度。
                """,
                Metadata.from("source", "langchain4j-intro.txt")
            ),
            
            // 文档2：Spring Boot集成
            Document.from(
                """
                LangChain4j Spring Boot集成提供了自动配置功能。
                主要Starter包括：
                - langchain4j-spring-boot-starter：核心自动配置
                - langchain4j-open-ai-spring-boot-starter：OpenAI模型支持
                - langchain4j-anthropic-spring-boot-starter：Anthropic Claude支持
                - langchain4j-ollama-spring-boot-starter：Ollama本地模型支持
                - langchain4j-local-ai-spring-boot-starter：LocalAI支持
                配置方式：
                在application.yml中配置模型参数，如api-key、model-name、temperature等。
                LangChain4j会自动创建ChatLanguageModel、EmbeddingModel等Bean。
                开发者只需注入使用，无需手动创建实例。
                """,
                Metadata.from("source", "spring-boot-integration.txt")
            ),
            
            // 文档3：AI Service详解
            Document.from(
                """
                AI Service是LangChain4j的高级抽象，通过接口定义AI能力。
                核心注解：
                @AiService：标记接口为AI服务，Spring会自动生成实现类
                @SystemMessage：定义系统提示词，设置AI角色和行为
                @UserMessage：定义用户消息模板，支持{变量}占位符
                @MemoryId：标识记忆ID，用于区分不同对话会话
                @V：参数映射，将方法参数映射到模板变量
                示例：
                @AiService
                public interface Assistant {
                    @SystemMessage("你是一个 helpful 助手")
                    String chat(@UserMessage String message);
                }
                AI Service会自动处理提示词构建、模型调用、响应解析等流程。
                """,
                Metadata.from("source", "ai-service-guide.txt")
            ),
            
            // 文档4：ChatMemory使用
            Document.from(
                """
                ChatMemory用于管理对话历史，支持多轮对话上下文。
                主要实现类：
                1. MessageWindowChatMemory：基于消息窗口，保留最近N条消息
                2. TokenWindowChatMemory：基于Token数量，控制总Token数
                使用方法：
                - 在AI Service中，使用@MemoryId参数指定会话ID
                - 在编程式API中，手动管理ChatMemory实例
                配置示例：
                ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .maxMessages(10)  // 保留最近10条消息
                    .build();
                高级用法：
                - 可自定义消息存储（如Redis、数据库）
                - 支持系统消息持久化
                - 可设置消息过期策略
                """,
                Metadata.from("source", "chat-memory-guide.txt")
            ),
            
            // 文档5：Tools工具调用
            Document.from(
                """
                Tools（工具调用）允许AI执行自定义Java方法。
                使用步骤：
                1. 创建工具类，方法使用@Tool注解标记
                2. 在AI Service接口上使用@Tool注解指定工具类
                3. AI会根据需要自动调用工具方法
                @Tool注解说明：
                - value属性：描述工具功能，帮助AI理解何时使用
                - 方法参数：AI会从对话中提取参数值
                - 返回值：方法返回结果会传递给AI继续处理
                示例场景：
                - 天气查询：AI提取地点参数，调用天气API
                - 订单查询：AI提取订单号，查询数据库
                - 计算器：AI提取表达式，执行计算
                工具调用让AI能力从对话延伸到实际操作。
                """,
                Metadata.from("source", "tools-guide.txt")
            )
        );
    }

    /**
     * 处理单个文档：分割 -> 嵌入 -> 存储
     * 
     * @param document 原始文档
     */
    private void ingestDocument(Document document) {
        log.debug("处理文档: {}", document.metadata().getString("source"));
        
        // 1. 分割文档为小块
        List<TextSegment> segments = documentSplitter.split(document);
        log.debug("文档分割为 {} 个片段", segments.size());
        
        // 2. 为每个片段生成向量并存储
        for (TextSegment segment : segments) {
            // 生成向量
            Embedding embedding = embeddingModel.embed(segment).content();
            
            // 存储到向量库
            embeddingStore.add(embedding, segment);
        }
        
        log.debug("文档 {} 已存入向量库", document.metadata().getString("source"));
    }
}
