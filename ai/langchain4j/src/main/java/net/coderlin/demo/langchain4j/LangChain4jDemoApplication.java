package net.coderlin.demo.langchain4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LangChain4j Spring Boot 3 演示应用启动类
 * 
 * <p>本Demo展示了LangChain4j与Spring Boot 3的集成，涵盖以下核心功能：</p>
 * <ul>
 *   <li><b>基础对话</b>：使用ChatLanguageModel进行简单文本生成</li>
 *   <li><b>AI Service</b>：声明式AI服务接口，简化开发</li>
 *   <li><b>记忆功能</b>：ChatMemory实现多轮对话上下文管理</li>
 *   <li><b>RAG检索</b>：ContentRetriever实现文档检索增强生成</li>
 *   <li><b>工具调用</b>：@Tool注解实现AI调用自定义方法</li>
 * </ul>
 * 
 * <p><b>关键依赖说明：</b></p>
 * <ul>
 *   <li>langchain4j-spring-boot-starter：核心Spring Boot集成</li>
 *   <li>langchain4j-open-ai-spring-boot-starter：OpenAI模型支持</li>
 *   <li>langchain4j-embeddings-all-minilm-l6-v2：本地Embedding模型</li>
 * </ul>
 * 
 * @author
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
public class LangChain4jDemoApplication {

    /**
     * 应用入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(LangChain4jDemoApplication.class, args);
        System.out.println("==============================================");
        System.out.println("LangChain4j Demo 启动成功！");
        System.out.println("访问地址：http://localhost:8080");
        System.out.println("API文档：http://localhost:8080/swagger-ui.html (如配置了Swagger)");
        System.out.println("==============================================");
    }
}
