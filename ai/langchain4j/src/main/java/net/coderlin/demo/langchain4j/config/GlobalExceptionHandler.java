package net.coderlin.demo.langchain4j.config;

import lombok.extern.slf4j.Slf4j;
import net.coderlin.demo.langchain4j.controller.dto.ChatResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * <p>统一处理应用中的各类异常，返回标准格式的错误响应。</p>
 *
 * <p><b>处理的异常类型：</b></p>
 * <ul>
 *   <li>参数校验异常：返回详细的校验错误信息</li>
 *   <li>业务逻辑异常：返回友好的错误提示</li>
 *   <li>系统异常：记录日志并返回通用错误信息</li>
 * </ul>
 *
 * <p><b>设计原则：</b></p>
 * <ul>
 *   <li>统一响应格式，便于前端处理</li>
 *   <li>隐藏敏感的系统错误信息</li>
 *   <li>完整记录异常日志便于排查</li>
 * </ul>
 *
 * @author
 * @since 1.0.0
 * @date 2024-01-15
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验异常错误码
     */
    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";

    /**
     * 系统错误码
     */
    private static final String SYSTEM_ERROR_CODE = "SYSTEM_ERROR";

    /**
     * AI服务错误码
     */
    private static final String AI_SERVICE_ERROR_CODE = "AI_SERVICE_ERROR";

    /**
     * 处理参数校验异常（WebFlux）
     *
     * <p>当请求参数不符合@Valid注解定义的校验规则时触发。</p>
     *
     * @param ex 参数绑定异常
     * @return 包含校验错误详情的响应
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ChatResponse handleWebExchangeBindException(WebExchangeBindException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        log.warn("参数校验失败: {}", errorMsg);
        return ChatResponse.error("[" + VALIDATION_ERROR_CODE + "] " + errorMsg);
    }

    /**
     * 处理参数校验异常（MVC）
     *
     * <p>传统MVC模式下的参数校验异常处理。</p>
     *
     * @param ex 参数校验异常
     * @return 包含校验错误详情的响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ChatResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        log.warn("参数校验失败: {}", errorMsg);
        return ChatResponse.error("[" + VALIDATION_ERROR_CODE + "] " + errorMsg);
    }

    /**
     * 处理非法参数异常
     *
     * <p>处理业务逻辑中主动抛出的参数异常。</p>
     *
     * @param ex 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ChatResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("参数错误: {}", ex.getMessage());
        return ChatResponse.error("[" + VALIDATION_ERROR_CODE + "] " + ex.getMessage());
    }

    /**
     * 处理非法状态异常
     *
     * <p>处理配置错误或状态异常。</p>
     *
     * @param ex 非法状态异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ChatResponse handleIllegalStateException(IllegalStateException ex) {
        log.error("系统状态异常: {}", ex.getMessage(), ex);
        return ChatResponse.error("[" + SYSTEM_ERROR_CODE + "] " + ex.getMessage());
    }

    /**
     * 处理运行时异常
     *
     * <p>捕获未被其他处理器处理的运行时异常。</p>
     *
     * @param ex 运行时异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ChatResponse handleRuntimeException(RuntimeException ex) {
        // 判断是否为AI服务相关异常
        String exceptionName = ex.getClass().getName();
        if (exceptionName.contains("langchain4j") || exceptionName.contains("openai")) {
            log.error("AI服务调用异常: {}", ex.getMessage(), ex);
            return ChatResponse.error("[" + AI_SERVICE_ERROR_CODE + "] AI服务调用失败: " + ex.getMessage());
        }
        
        log.error("运行时异常: {}", ex.getMessage(), ex);
        return ChatResponse.error("[" + SYSTEM_ERROR_CODE + "] 服务处理异常，请稍后重试");
    }

    /**
     * 处理通用异常
     *
     * <p>最后的异常兜底处理，防止异常信息泄露。</p>
     *
     * @param ex 通用异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ChatResponse handleException(Exception ex) {
        log.error("系统异常: {}", ex.getMessage(), ex);
        return ChatResponse.error("[" + SYSTEM_ERROR_CODE + "] 系统繁忙，请稍后重试");
    }
}
