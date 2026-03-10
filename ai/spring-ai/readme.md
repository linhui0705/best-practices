# Spring AI Spring Boot 3 Demo

基于 Spring AI 1.0.0-M5 和 Spring Boot 3.4.0 的 AI 应用开发最佳实践示例项目。

## 项目概述

本项目展示了 Spring AI 与 Spring Boot 3 的完整集成方案，涵盖以下核心功能：

- **基础对话**：使用 ChatClient 进行简单文本生成
- **流式响应**：Flux 流式输出，实现打字机效果
- **记忆功能**：ChatMemory 实现多轮对话上下文管理
- **RAG 检索**：VectorStore 实现文档检索增强生成
- **工具调用**：Function Calling 实现 AI 调用自定义方法
- **结构化输出**：BeanOutputConverter 实现 POJO 转换

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | Spring Boot 3 最低要求 |
| Spring Boot | 3.4.0 | 基础框架 |
| Spring AI | 1.0.0-M5 | AI 集成框架 |
| Spring AI OpenAI Starter | 1.0.0-M5 | OpenAI 模型支持 |
| Spring AI Ollama Starter | 1.0.0-M5 | 本地模型支持 |
| Spring AI Vector Store | 1.0.0-M5 | 向量存储支持 |
| Lombok | 1.18.30 | 代码简化 |

## 项目结构

```
spring-ai/
├── src/main/java/net/coderlin/demo/springai/
│   ├── SpringAiDemoApplication.java    # 应用启动类
│   ├── config/
│   │   └── SpringAiConfig.java         # 核心配置类
│   ├── controller/
│   │   ├── ChatController.java         # AI 对话接口
│   │   └── dto/                        # 请求/响应 DTO
│   │       ├── ChatRequest.java
│   │       ├── ChatResponse.java
│   │       ├── MemoryChatRequest.java
│   │       ├── RagQueryRequest.java
│   │       ├── DocumentRequest.java
│   │       ├── SentimentRequest.java
│   │       └── PoemRequest.java
│   └── service/
│       └── ChatService.java            # AI 对话服务
├── src/main/resources/
│   └── application.yml                 # 配置文件
└── pom.xml                             # Maven 配置
```

## 快速开始

### 1. 环境准备

- JDK 17 或更高版本
- Maven 3.8+
- OpenAI API Key 或兼容的 API（如通义千问、DeepSeek）

### 2. 配置 API Key

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-api-key-here}
      base-url: https://dashscope.aliyuncs.com/compatible-mode
      chat:
        options:
          model: qwen3.5-plus
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

### 基础路径

所有 API 接口的基础路径为：`/api/ai`

### 1. 基础对话接口

#### 1.1 POST 基础对话

```http
POST /api/ai/chat
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

#### 1.2 GET 基础对话（便捷测试）

```http
GET /api/ai/chat?message=你好
```

#### 1.3 流式对话（SSE）

```http
GET /api/ai/chat/stream?message=你好
```

**前端使用示例**：

```javascript
const eventSource = new EventSource('/api/ai/chat/stream?message=你好');
eventSource.onmessage = (event) => {
    document.getElementById('output').innerHTML += event.data;
};
```

#### 1.4 POST 流式对话

```http
POST /api/ai/chat/stream
Content-Type: application/json

{
  "message": "你好"
}
```

---

### 2. 记忆功能接口

#### 2.1 带记忆的多轮对话

```http
POST /api/ai/memory
Content-Type: application/json

{
  "conversationId": "user-123",
  "message": "我叫张三"
}
```

**第二轮对话（同一 conversationId）**：

```http
POST /api/ai/memory
Content-Type: application/json

{
  "conversationId": "user-123",
  "message": "我叫什么名字？"
}
// AI 回复：你叫张三。
```

#### 2.2 流式记忆对话

```http
POST /api/ai/memory/stream
Content-Type: application/json

{
  "conversationId": "user-123",
  "message": "你好"
}
```

---

### 3. RAG 知识库接口

#### 3.1 添加文档到知识库

```http
POST /api/ai/documents
Content-Type: application/json

{
  "content": "我们公司的主要产品是AI助手平台，提供智能对话服务...",
  "metadata": {
    "source": "产品介绍",
    "category": "产品"
  }
}
```

#### 3.2 RAG 知识库问答

```http
POST /api/ai/rag
Content-Type: application/json

{
  "conversationId": "user-123",
  "question": "我们公司的主要产品是什么？"
}
```

---

### 4. 工具调用接口

#### 4.1 函数调用对话

**支持的函数**：
- `weatherFunction` - 天气查询
- `calculatorFunction` - 数学计算

**天气查询**：

```http
POST /api/ai/function
Content-Type: application/json

{
  "message": "北京今天天气怎么样？"
}
// AI 调用 weatherFunction，返回天气信息
```

**数学计算**：

```http
POST /api/ai/function
Content-Type: application/json

{
  "message": "计算 123 乘以 456"
}
// AI 调用 calculatorFunction，返回计算结果
```

---

### 5. 结构化输出接口

#### 5.1 情感分析

```http
POST /api/ai/sentiment
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

#### 5.2 诗歌创作

```http
POST /api/ai/poem
Content-Type: application/json

{
  "topic": "春天",
  "style": "七言绝句"
}
```

**响应示例**：

```json
{
  "status": "success",
  "content": "{\n  \"title\": \"春日\",\n  \"content\": \"春风拂面柳丝长...\",\n  \"explanation\": \"这首诗描绘了...\"\n}",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

### 6. 工具接口

#### 6.1 生成会话 ID

```http
GET /api/ai/session-id
```

**响应示例**：

```json
{
  "status": "success",
  "content": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 核心功能详解

### 1. ChatClient 对话客户端

```java
@Bean
@Primary
public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
    return chatClientBuilder
            .defaultSystem("你是一个 helpful AI 助手，请用中文回答问题，回答要简洁明了。")
            .build();
}
```

**使用示例**：

```java
String response = chatClient.prompt()
    .user("你好")
    .call()
    .content();
```

### 2. 记忆功能（ChatMemory）

```java
@Bean
public ChatMemory chatMemory() {
    return new InMemoryChatMemory();
}

@Bean(name = "memoryChatClient")
public ChatClient memoryChatClient(ChatClient.Builder chatClientBuilder, 
                                    ChatMemory chatMemory) {
    return chatClientBuilder
            .defaultSystem("你是一个 helpful AI 助手...")
            .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
            .build();
}
```

**使用方式**：

```java
public String chatWithMemory(String conversationId, String message) {
    return memoryChatClient.prompt()
            .user(message)
            .call()
            .content();
}
```

### 3. RAG 检索增强生成

```java
@Bean
public VectorStore vectorStore(EmbeddingModel embeddingModel) {
    return SimpleVectorStore.builder(embeddingModel).build();
}

@Bean(name = "ragChatClient")
public ChatClient ragChatClient(ChatClient.Builder chatClientBuilder,
                                 VectorStore vectorStore,
                                 ChatMemory chatMemory) {
    SearchRequest searchRequest = SearchRequest.builder()
            .topK(5)
            .similarityThreshold(0.7)
            .build();

    return chatClientBuilder
            .defaultAdvisors(
                    new QuestionAnswerAdvisor(vectorStore, searchRequest),
                    new MessageChatMemoryAdvisor(chatMemory)
            )
            .build();
}
```

### 4. 工具调用（Function Calling）

**定义工具函数**：

```java
@Bean
@Description("获取指定城市的当前天气信息，包括温度、湿度、天气状况等")
public Function<WeatherRequest, WeatherResponse> weatherFunction() {
    return request -> {
        return new WeatherResponse(
                request.city(),
                "晴天",
                25,
                60,
                "适宜出行"
        );
    };
}

public record WeatherRequest(String city) {}
public record WeatherResponse(String city, String condition, int temperature,
                               int humidity, String suggestion) {}
```

**使用方式**：

```java
public String chatWithFunction(String message) {
    return chatClient.prompt()
            .user(message)
            .functions("weatherFunction", "calculatorFunction")
            .call()
            .content();
}
```

### 5. 结构化输出

```java
public SentimentResult analyzeSentiment(String text) {
    BeanOutputConverter<SentimentResult> converter = 
            new BeanOutputConverter<>(SentimentResult.class);
    String format = converter.getFormat();

    String content = chatClient.prompt()
            .user("分析以下文本的情感：" + text + "\n请按格式返回：" + format)
            .call()
            .content();

    return converter.convert(content);
}

public record SentimentResult(String sentiment, Double confidence, String reason) {}
```

---

## 配置说明

### application.yml 完整配置

```yaml
spring:
  application:
    name: spring-ai-demo

  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-api-key}
      base-url: https://dashscope.aliyuncs.com/compatible-mode
      chat:
        options:
          model: qwen3.5-plus
          temperature: 0.7
          max-tokens: 2000
      embedding:
        options:
          model: text-embedding-ada-002

    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: qwen:7b
          temperature: 0.7

logging:
  level:
    org.springframework.ai: DEBUG
    org.springframework.web: INFO

app:
  ai:
    default-system-message: "你是一个 helpful AI 助手，请用中文回答问题，回答要简洁明了。"
    max-memory-messages: 10
    rag-max-results: 5
    rag-min-score: 0.7
```

### 支持的模型提供商

- **OpenAI**: gpt-4, gpt-3.5-turbo, gpt-4o
- **通义千问**: qwen-turbo, qwen3.5-plus
- **DeepSeek**: deepseek-chat, deepseek-coder
- **Azure OpenAI**: 通过 base-url 配置
- **本地模型**: Ollama (llama2, mistral, qwen 等)

---

## 测试示例

### 使用 curl 测试

```bash
# 基础对话
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"你好"}'

# 带记忆对话
curl -X POST http://localhost:8080/api/ai/memory \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"test_001","message":"我叫张三"}'

# RAG 问答
curl -X POST http://localhost:8080/api/ai/rag \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"test_001","question":"什么是Spring AI"}'

# 工具调用
curl -X POST http://localhost:8080/api/ai/function \
  -H "Content-Type: application/json" \
  -d '{"message":"北京今天天气怎么样"}'

# 情感分析
curl -X POST http://localhost:8080/api/ai/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text":"今天天气真好，心情非常愉快！"}'

# 诗歌创作
curl -X POST http://localhost:8080/api/ai/poem \
  -H "Content-Type: application/json" \
  -d '{"topic":"春天","style":"七言绝句"}'
```

---

## 生产环境建议

1. **向量存储**：将 `SimpleVectorStore` 替换为 PGVector、Redis、Milvus、Pinecone 等持久化存储
2. **记忆存储**：使用 RedisChatMemory 或数据库存储 ChatMemory，支持分布式部署
3. **API Key 管理**：使用密钥管理服务（如 AWS Secrets Manager、HashiCorp Vault）
4. **限流保护**：添加 API 限流，防止滥用
5. **监控告警**：集成 Prometheus、Grafana 监控模型调用延迟和错误率

---

## Spring AI vs LangChain4j

| 特性 | Spring AI | LangChain4j |
|------|-----------|-------------|
| 开发团队 | Spring 官方 | 社区驱动 |
| 集成方式 | Spring Boot Starter | 多框架支持 |
| API 风格 | 流畅 API (Fluent API) | 声明式注解 |
| 流式支持 | 原生 Flux 支持 | 需额外处理 |
| 生态兼容 | Spring 生态无缝集成 | 多语言支持 |
| 版本状态 | 1.0.0-M5（里程碑版） | 0.35.0（稳定版） |

---

## 参考资料

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [OpenAI API 文档](https://platform.openai.com/docs)

## License

MIT License
