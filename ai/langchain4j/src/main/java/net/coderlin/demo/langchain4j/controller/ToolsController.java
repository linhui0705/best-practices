package net.coderlin.demo.langchain4j.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coderlin.demo.langchain4j.controller.dto.ChatRequest;
import net.coderlin.demo.langchain4j.controller.dto.ChatResponse;
import net.coderlin.demo.langchain4j.controller.dto.MemoryChatRequest;
import net.coderlin.demo.langchain4j.service.ToolsAssistant;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 工具调用Controller
 *
 * <p>提供支持工具调用的AI对话API接口。</p>
 *
 * <p><b>可用工具：</b></p>
 * <ul>
 *   <li><b>CalculatorTools</b>：数学计算工具
 *     <ul>
 *       <li>add - 加法</li>
 *       <li>subtract - 减法</li>
 *       <li>multiply - 乘法</li>
 *       <li>divide - 除法</li>
 *       <li>sqrt - 平方根</li>
 *       <li>power - 幂运算</li>
 *     </ul>
 *   </li>
 *   <li><b>WeatherTools</b>：天气查询工具
 *     <ul>
 *       <li>getWeather - 查询天气</li>
 *       <li>getTemperature - 获取温度</li>
 *       <li>compareTemperature - 比较城市温度</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>API端点：</b></p>
 * <ul>
 *   <li>POST /api/tools/chat - 基础工具对话</li>
 *   <li>POST /api/tools/math - 数学专家模式</li>
 *   <li>POST /api/tools/weather - 天气助手模式</li>
 *   <li>POST /api/tools/memory - 带记忆的工具对话</li>
 * </ul>
 *
 * @author
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolsController {

    /**
     * 注入工具助手服务
     */
    private final ToolsAssistant toolsAssistant;

    /**
     * 基础工具对话接口
     *
     * <p>AI自动判断是否需要调用工具。</p>
     *
     * <p><b>使用示例：</b></p>
     *
     * <p>1. 数学计算：</p>
     * <pre>
     * POST /api/tools/chat
     * Content-Type: application/json
     *
     * {
     *   "message": "123乘以456等于多少？"
     * }
     * // AI调用multiply(123, 456)，返回计算结果
     * </pre>
     *
     * <p>2. 天气查询：</p>
     * <pre>
     * POST /api/tools/chat
     * Content-Type: application/json
     *
     * {
     *   "message": "北京今天天气怎么样？"
     * }
     * // AI调用getWeather("北京")，返回天气信息
     * </pre>
     *
     * <p>3. 普通对话（无需工具）：</p>
     * <pre>
     * POST /api/tools/chat
     * Content-Type: application/json
     *
     * {
     *   "message": "你好"
     * }
     * // AI直接回复，不调用工具
     * </pre>
     *
     * @param request 对话请求
     * @return AI回复
     */
    @PostMapping("/chat")
    public Mono<ChatResponse> chatWithTools(@Valid @RequestBody ChatRequest request) {
        log.info("收到工具对话请求: {}", request.getMessage());
        return Mono.fromCallable(() -> {
            String response = toolsAssistant.chat(request.getMessage());
            log.info("工具对话回复成功");
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("工具对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 数学专家模式
     *
     * <p>专注于数学计算，AI会详细展示计算过程。</p>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/tools/math
     * Content-Type: application/json
     *
     * {
     *   "message": "计算 (123 + 456) * 789 / 2 的值"
     * }
     * </pre>
     *
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "status": "success",
     *   "content": "让我分步计算：\n\n第一步：123 + 456 = 579\n第二步：579 * 789 = 456831\n第三步：456831 / 2 = 228415.5\n\n最终结果是：228415.5",
     *   "timestamp": "2024-01-15T10:30:00"
     * }
     * </pre>
     *
     * @param request 数学问题请求
     * @return 详细解答
     */
    @PostMapping("/math")
    public Mono<ChatResponse> solveMathProblem(@Valid @RequestBody ChatRequest request) {
        log.info("收到数学问题请求: {}", request.getMessage());
        return Mono.fromCallable(() -> {
            String response = toolsAssistant.solveMathProblem(request.getMessage());
            log.info("数学问题解答成功");
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("数学问题处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 天气助手模式
     *
     * <p>专注于天气查询和出行建议。</p>
     *
     * <p><b>请求示例：</b></p>
     * <pre>
     * POST /api/tools/weather
     * Content-Type: application/json
     *
     * {
     *   "message": "北京和上海哪个城市更热？"
     * }
     * // AI调用compareTemperature("北京", "上海")，返回比较结果
     * </pre>
     *
     * @param request 天气查询请求
     * @return 天气信息和建议
     */
    @PostMapping("/weather")
    public Mono<ChatResponse> weatherQuery(@Valid @RequestBody ChatRequest request) {
        log.info("收到天气查询请求: {}", request.getMessage());
        return Mono.fromCallable(() -> {
            String response = toolsAssistant.weatherQuery(request.getMessage());
            log.info("天气查询成功");
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("天气查询处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * 带记忆的工具对话
     *
     * <p>结合Memory和Tools，实现上下文感知的工具调用。</p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>多轮计算对话，记住中间结果</li>
     *   <li>旅行规划，记住查询过的城市</li>
     *   <li>数据分析，引用之前的数据</li>
     * </ul>
     *
     * <p><b>请求示例（第一轮）：</b></p>
     * <pre>
     * POST /api/tools/memory
     * Content-Type: application/json
     *
     * {
     *   "sessionId": "calc_session_001",
     *   "message": "计算 100 除以 4"
     * }
     * </pre>
     *
     * <p><b>请求示例（第二轮，引用之前结果）：</b></p>
     * <pre>
     * POST /api/tools/memory
     * Content-Type: application/json
     *
     * {
     *   "sessionId": "calc_session_001",
     *   "message": "把刚才的结果再乘以 5"
     * }
     * // AI记得"刚才的结果"是25，计算25 * 5 = 125
     * </pre>
     *
     * @param request 带记忆的对话请求
     * @return AI回复
     */
    @PostMapping("/memory")
    public Mono<ChatResponse> chatWithMemoryAndTools(@Valid @RequestBody MemoryChatRequest request) {
        log.info("收到带记忆的工具对话请求: sessionId={}, message={}", 
                request.getSessionId(), request.getMessage());
        return Mono.fromCallable(() -> {
            String response = toolsAssistant.chatWithMemoryAndTools(
                    request.getSessionId(), 
                    request.getMessage()
            );
            log.info("带记忆工具对话回复成功");
            return ChatResponse.success(response, request.getSessionId());
        }).onErrorResume(e -> {
            log.error("带记忆工具对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }

    /**
     * GET方式工具对话（便捷测试接口）
     *
     * <p><b>示例：</b></p>
     * <pre>
     * GET /api/tools/chat?message=123乘以456等于多少
     * </pre>
     *
     * @param message 用户消息
     * @return AI回复
     */
    @GetMapping("/chat")
    public Mono<ChatResponse> chatWithToolsGet(@RequestParam String message) {
        log.info("收到GET工具对话请求: {}", message);
        return Mono.fromCallable(() -> {
            String response = toolsAssistant.chat(message);
            return ChatResponse.success(response);
        }).onErrorResume(e -> {
            log.error("工具对话处理失败: {}", e.getMessage(), e);
            return Mono.just(ChatResponse.error("处理失败: " + e.getMessage()));
        });
    }
}
