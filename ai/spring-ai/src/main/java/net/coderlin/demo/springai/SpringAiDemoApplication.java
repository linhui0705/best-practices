package net.coderlin.demo.springai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring AI Spring Boot 3 演示应用启动类
 *
 * <p>本Demo展示了Spring AI与Spring Boot 3的集成，涵盖以下核心功能：</p>
 * <ul>
 *   <li><b>基础对话</b>：使用ChatClient进行简单文本生成</li>
 *   <li><b>流式响应</b>：Flux流式输出，实现打字机效果</li>
 *   <li><b>记忆功能</b>：ChatMemory实现多轮对话上下文管理</li>
 *   <li><b>RAG检索</b>：VectorStore实现文档检索增强生成</li>
 *   <li><b>工具调用</b>：Function Calling实现AI调用自定义方法</li>
 *   <li><b>结构化输出</b>：BeanOutputConverter实现POJO转换</li>
 * </ul>
 *
 * <p><b>关键依赖说明：</b></p>
 * <ul>
 *   <li>spring-ai-openai-spring-boot-starter：OpenAI模型支持</li>
 *   <li>spring-ai-ollama-spring-boot-starter：本地Ollama模型支持</li>
 *   <li>spring-ai-pgvector-store-spring-boot-starter：向量数据库存储</li>
 * </ul>
 *
 * <p><b>Spring AI 核心概念：</b></p>
 * <ul>
 *   <li>ChatClient：对话客户端，提供流畅的API风格</li>
 *   <li>ChatModel：底层模型接口，直接与LLM交互</li>
 *   <li>Prompt：提示词封装，包含消息和选项</li>
 *   <li>Advisor：拦截器机制，实现记忆、RAG等功能</li>
 *   <li>VectorStore：向量存储接口，支持多种后端</li>
 * </ul>
 *
 * @author
 * @version 1.0.0
 * @since 2024
 */
@Slf4j
@SpringBootApplication(exclude = {
        // 排除Ollama自动配置，避免与OpenAI冲突
        // 如需使用Ollama本地模型，请移除此排除并排除OpenAiAutoConfiguration
        OllamaAutoConfiguration.class
})
public class SpringAiDemoApplication {

    /**
     * 应用入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringAiDemoApplication.class, args);
        log.info("==============================================");
        log.info("Spring AI Demo 启动成功！");
        log.info("访问地址：http://localhost:8080");
        log.info("API端点：");
        log.info("  POST /api/ai/chat        - 基础对话");
        log.info("  POST /api/ai/chat/stream - 流式对话");
        log.info("  POST /api/ai/memory      - 记忆对话");
        log.info("  POST /api/ai/rag         - RAG知识库问答");
        log.info("  POST /api/ai/function    - 函数调用");
        log.info("  POST /api/ai/structured  - 结构化输出");
        log.info("==============================================");
    }
}
