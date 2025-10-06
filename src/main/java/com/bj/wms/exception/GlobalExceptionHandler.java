package com.bj.wms.exception;

import lombok.extern.slf4j.Slf4j;
import com.bj.wms.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 全局异常处理器
 * 
 * 统一处理应用中的异常，返回友好的错误信息
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
                .collect(java.util.stream.Collectors.toMap(e -> ((FieldError) e).getField(),
                        org.springframework.validation.ObjectError::getDefaultMessage, (a, b) -> a));
        log.warn("参数验证失败: {}", errors);
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, "参数验证失败");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("业务异常: {}", ex.getMessage(), ex);
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.warn("参数错误: {}", ex.getMessage());
        return ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(
            NullPointerException ex) {
        log.error("空指针异常", ex);
        return ResponseUtil.serverError("系统内部错误");
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("未处理的异常", ex);
        return ResponseUtil.serverError("系统内部错误");
    }
}


