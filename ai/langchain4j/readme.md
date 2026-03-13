# LangChain4j Spring Boot 3 Demo

基于 LangChain4j 0.36.2 和 Spring Boot 3.4.1 的 AI 应用开发最佳实践示例项目。

## 项目概述

本项目展示了 LangChain4j 与 Spring Boot 3 的完整集成方案，涵盖以下核心功能：

- **基础对话**：使用 ChatLanguageModel 进行简单文本生成
- **AI Service**：声明式 AI 服务接口，简化开发
- **记忆功能**：ChatMemory 实现多轮对话上下文管理
- **RAG 检索**：ContentRetriever 实现文档检索增强生成
- **工具调用**：@Tool 注解实现 AI 调用自定义方法
- **全局异常处理**：统一的错误响应格式
- **配置属性类**：类型安全的配置管理

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | Spring Boot 3 最低要求 |
| Spring Boot | 3.4.1 | 基础框架 |
| LangChain4j | 0.36.2 | LLM 集成框架 |
| LangChain4j Spring Boot Starter | 0.36.2 | 核心集成包 |
| LangChain4j OpenAI Starter | 0.36.2 | OpenAI 模型支持 |
| LangChain4j Embeddings | 0.36.2 | 本地 Embedding 模型 |
| Lombok | 1.18.34 | 代码简化 |

## 项目结构

```
langchain4j/
├── src/main/java/net/coderlin/demo/langchain4j/
│   ├── LangChain4jDemoApplication.java    # 应用启动类
│   ├── config/
│   │   ├── LangChain4jConfig.java         # 核心配置类
│   │   ├── AiProperties.java              # AI配置属性类
│   │   ├── GlobalExceptionHandler.java    # 全局异常处理
│   │   └── RagDataInitializer.java        # RAG 数据初始化
│   ├── controller/
│   │   ├── AssistantController.java       # 基础 AI 接口
│   │   ├── MemoryController.java          # 记忆功能接口
│   │   ├── RagController.java             # RAG 检索接口
│   │   ├── ToolsController.java           # 工具调用接口
│   │   └── dto/                           # 请求/响应 DTO
│   ├── service/
│   │   ├── Assistant.java                 # 基础 AI 服务接口
│   │   ├── MemoryAssistant.java           # 记忆 AI 服务接口
│   │   ├── RagAssistant.java              # RAG AI 服务接口
│   │   └── ToolsAssistant.java            # 工具 AI 服务接口
│   └── tools/
│       ├── CalculatorTools.java           # 计算器工具
│       └── WeatherTools.java              # 天气查询工具
├── src/main/resources/
│   └── application.yml                    # 配置文件
└── pom.xml                                # Maven 配置
```

## 快速开始

### 1. 环境准备

- JDK 17 或更高版本
- Maven 3.8+
- OpenAI API Key 或兼容的 API（如通义千问）

### 2. 配置 API Key

编辑 `src/main/resources/application.yml`：

```yaml
langchain4j:
  open-ai:
    chat-model:
      api-key: ${OPENAI_API_KEY:your-api-key-here}
      model-name: qwen-plus
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
```

或通过环境变量设置：

```bash
export OPENAI_API_KEY=your-api-key-here
```

### 3. 启动应用

```bash
mvn spring-boot:run
```

应用启动后访问：`http://localhost:8080`

## API 接口文档

### 1. 基础 AI 助手接口

**Base URL**: `/api/assistant`

#### 1.1 基础对话

```http
POST /api/assistant/chat
Content-Type: application/json

{
  "message": "你好，请介绍一下自己"
}
```

**响应示例**：

```json
{
  "status": "success",
  "content": "你好！我是一个AI助手...",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 1.2 诗歌创作

```http
POST /api/assistant/poem
Content-Type: application/json

{
  "topic": "春天",
  "style": "七言绝句"
}
```

#### 1.3 情感分析

```http
POST /api/assistant/sentiment
Content-Type: application/json

{
  "text": "今天天气真好，心情非常愉快！"
}
```

**响应示例**：

```json
{
  "status": "success",
  "content": "{\n  \"sentiment\": \"POSITIVE\",\n  \"confidence\": 0.95,\n  \"reason\": \"文本中包含'好'、'愉快'等积极词汇\"\n}",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 1.4 GET 方式对话（便捷测试）

```http
GET /api/assistant/chat?message=你好
```

---

### 2. 记忆功能接口

**Base URL**: `/api/memory`

#### 2.1 带记忆的多轮对话

```http
POST /api/memory/chat
Content-Type: application/json

{
  "sessionId": "user_001",
  "message": "我叫张三"
}
```

**第二轮对话（同一 sessionId）**：

```http
POST /api/memory/chat
Content-Type: application/json

{
  "sessionId": "user_001",
  "message": "我叫什么名字？"
}
// AI 回复：你叫张三。
```

#### 2.2 技术咨询

```http
POST /api/memory/tech
Content-Type: application/json

{
  "sessionId": "tech_session_001",
  "message": "Spring Boot是什么？"
}
```

#### 2.3 表单数据收集

```http
POST /api/memory/form
Content-Type: application/json

{
  "sessionId": "form_001",
  "message": "name:张三"
}
```

#### 2.4 GET 方式记忆对话

```http
GET /api/memory/chat?sessionId=user_001&message=你好
```

---

### 3. RAG 知识库接口

**Base URL**: `/api/rag`

#### 3.1 基础知识库查询

```http
POST /api/rag/query
Content-Type: application/json

{
  "question": "什么是LangChain4j？"
}
```

#### 3.2 带引用来源的查询

```http
POST /api/rag/citation
Content-Type: application/json

{
  "question": "LangChain4j有哪些核心特性？"
}
```

#### 3.3 带记忆的知识库对话

```http
POST /api/rag/chat
Content-Type: application/json

{
  "sessionId": "rag_session_001",
  "question": "什么是AI Service？"
}
```

**追问示例**：

```http
POST /api/rag/chat
Content-Type: application/json

{
  "sessionId": "rag_session_001",
  "question": "它有哪些核心注解？"
}
// AI 会理解"它"指的是 AI Service
```

#### 3.4 GET 方式知识库查询

```http
GET /api/rag/query?question=什么是LangChain4j
```

---

### 4. 工具调用接口

**Base URL**: `/api/tools`

#### 4.1 基础工具对话

**数学计算**：

```http
POST /api/tools/chat
Content-Type: application/json

{
  "message": "123乘以456等于多少？"
}
// AI 调用 multiply(123, 456)，返回计算结果
```

**天气查询**：

```http
POST /api/tools/chat
Content-Type: application/json

{
  "message": "北京今天天气怎么样？"
}
// AI 调用 getWeather("北京")，返回天气信息
```

#### 4.2 数学专家模式

```http
POST /api/tools/math
Content-Type: application/json

{
  "message": "计算 (123 + 456) * 789 / 2 的值"
}
```

**响应示例**：

```json
{
  "status": "success",
  "content": "让我分步计算：\n\n第一步：123 + 456 = 579\n第二步：579 * 789 = 456831\n第三步：456831 / 2 = 228415.5\n\n最终结果是：228415.5",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 4.3 天气助手模式

```http
POST /api/tools/weather
Content-Type: application/json

{
  "message": "北京和上海哪个城市更热？"
}
// AI 调用 compareTemperature("北京", "上海")，返回比较结果
```

#### 4.4 带记忆的工具对话

```http
POST /api/tools/memory
Content-Type: application/json

{
  "sessionId": "calc_session_001",
  "message": "计算 100 除以 4"
}
```

**第二轮（引用之前结果）**：

```http
POST /api/tools/memory
Content-Type: application/json

{
  "sessionId": "calc_session_001",
  "message": "把刚才的结果再乘以 5"
}
// AI 记得"刚才的结果"是 25，计算 25 * 5 = 125
```

#### 4.5 GET 方式工具对话

```http
GET /api/tools/chat?message=123乘以456等于多少
```

---

## 核心功能详解

### 1. AI Service 声明式接口

```java
public interface Assistant {
    
    @SystemMessage("你是一个 helpful AI 助手")
    String chat(@UserMessage String message);
    
    @SystemMessage("你是一位才华横溢的诗人")
    @UserMessage("请创作一首关于{{topic}}的{{style}}")
    String writePoem(@V("topic") String topic, @V("style") String style);
    
    @SystemMessage("你是一个情感分析专家")
    @UserMessage("请分析以下文本的情感：{{text}}")
    SentimentResult analyzeSentiment(@V("text") String text);
}
```

**核心注解说明**：

| 注解 | 作用 |
|------|------|
| `@SystemMessage` | 设置 AI 角色和行为准则 |
| `@UserMessage` | 定义用户消息模板，支持 `{{变量}}` 占位符 |
| `@V` | 将方法参数映射到模板变量 |
| `@MemoryId` | 标记记忆标识符，区分不同会话 |

### 2. 记忆功能（ChatMemory）

```java
@Bean
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
            .maxMessages(10)  // 保留最近 10 条消息
            .build();
}
```

**使用方式**：

```java
public interface MemoryAssistant {
    @SystemMessage("你是一个 helpful AI 助手")
    String chatWithMemory(@MemoryId String memoryId, @UserMessage String message);
}
```

### 3. RAG 检索增强生成

**核心组件**：

```java
@Bean
public EmbeddingModel embeddingModel() {
    return new AllMiniLmL6V2EmbeddingModel();  // 本地 Embedding 模型
}

@Bean
public EmbeddingStore<TextSegment> embeddingStore() {
    return new InMemoryEmbeddingStore<>();  // 内存向量存储
}

@Bean
public ContentRetriever contentRetriever(
        EmbeddingStore<TextSegment> embeddingStore,
        EmbeddingModel embeddingModel) {
    return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(3)      // 返回最相关的 3 个结果
            .minScore(0.7)      // 最小相似度阈值
            .build();
}
```

### 4. 工具调用（Tools）

**定义工具类**：

```java
@Component
public class CalculatorTools {
    
    @Tool("计算两个数的和")
    public double add(double a, double b) {
        return a + b;
    }
    
    @Tool("计算两个数的乘积")
    public double multiply(double a, double b) {
        return a * b;
    }
}
```

**配置到 AI Service**：

```java
@Bean
public ToolsAssistant toolsAssistant(ChatLanguageModel chatLanguageModel,
                                      CalculatorTools calculatorTools) {
    return AiServices.builder(ToolsAssistant.class)
            .chatLanguageModel(chatLanguageModel)
            .tools(calculatorTools)
            .build();
}
```

## 配置说明

### application.yml 完整配置

```yaml
spring:
  application:
    name: langchain4j-demo

server:
  port: ${SERVER_PORT:8080}

langchain4j:
  open-ai:
    chat-model:
      api-key: ${OPENAI_API_KEY:your-api-key}
      model-name: ${OPENAI_MODEL_NAME:qwen-plus}
      temperature: ${OPENAI_TEMPERATURE:0.7}
      max-tokens: ${OPENAI_MAX_TOKENS:2000}
      timeout-seconds: ${OPENAI_TIMEOUT_SECONDS:60}
      base-url: ${OPENAI_BASE_URL:https://dashscope.aliyuncs.com/compatible-mode/v1}
    
    embedding-model:
      api-key: ${OPENAI_API_KEY:your-api-key}
      model-name: ${OPENAI_EMBEDDING_MODEL:text-embedding-v3}
      timeout-seconds: ${OPENAI_TIMEOUT_SECONDS:60}

# 自定义AI配置
app:
  ai:
    default-system-message: "你是一个 helpful AI 助手"
    max-memory-messages: 10
    rag:
      max-results: 3
      min-score: 0.7
    document-splitter:
      max-size: 500
      max-overlap: 50
    retry:
      enabled: true
      max-attempts: 3
      delay-ms: 1000

logging:
  level:
    net.coderlin.demo.langchain4j: INFO
    dev.langchain4j: DEBUG
```

### 环境变量说明

| 环境变量 | 说明 | 默认值 |
|---------|------|-------|
| OPENAI_API_KEY | API 密钥 | - |
| OPENAI_MODEL_NAME | 模型名称 | qwen-plus |
| OPENAI_BASE_URL | API Base URL | 通义千问 |
| OPENAI_TEMPERATURE | 温度参数 | 0.7 |
| OPENAI_MAX_TOKENS | 最大Token数 | 2000 |
| OPENAI_TIMEOUT_SECONDS | 超时时间(秒) | 60 |
| AI_MAX_MEMORY_MESSAGES | 记忆窗口大小 | 10 |
| AI_RAG_MAX_RESULTS | RAG最大结果数 | 3 |
| AI_RAG_MIN_SCORE | RAG最小相似度 | 0.7 |

### 支持的模型提供商

- **OpenAI**: gpt-4, gpt-3.5-turbo
- **通义千问**: qwen-turbo, qwen3.5-plus
- **Azure OpenAI**: 通过 base-url 配置
- **本地模型**: Ollama, LocalAI

## 测试示例

### 使用 curl 测试

```bash
# 基础对话
curl -X POST http://localhost:8080/api/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"你好"}'

# 带记忆对话
curl -X POST http://localhost:8080/api/memory/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test_001","message":"我叫张三"}'

# RAG 查询
curl -X POST http://localhost:8080/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"question":"什么是LangChain4j"}'

# 工具调用
curl -X POST http://localhost:8080/api/tools/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"123乘以456等于多少"}'
```

## 生产环境建议

1. **向量存储**：将 `InMemoryEmbeddingStore` 替换为 Redis、Milvus、Pinecone 等持久化存储
2. **记忆存储**：使用 Redis 或数据库存储 ChatMemory，支持分布式部署
3. **API Key 管理**：使用密钥管理服务（如 AWS Secrets Manager、HashiCorp Vault）
4. **限流保护**：添加 API 限流，防止滥用
5. **监控告警**：集成 Prometheus、Grafana 监控模型调用延迟和错误率

## 参考资料

- [LangChain4j 官方文档](https://docs.langchain4j.dev/)
- [LangChain4j GitHub](https://github.com/langchain4j/langchain4j)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)

## License

MIT License
