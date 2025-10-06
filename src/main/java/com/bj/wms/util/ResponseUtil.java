package com.bj.wms.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应工具类
 * 
 * 提供统一的API响应格式
 */
public class ResponseUtil {

    /**
     * 成功响应
     */
    public static <T> ResponseEntity<Map<String, Object>> success(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * 成功响应（无数据）
     */
    public static ResponseEntity<Map<String, Object>> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    /**
     * 创建成功响应
     */
    public static <T> ResponseEntity<Map<String, Object>> created(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", "创建成功");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 错误响应
     */
    public static ResponseEntity<Map<String, Object>> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 错误响应（指定状态码）
     */
    public static ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 未找到响应
     */
    public static ResponseEntity<Map<String, Object>> notFound(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 服务器错误响应
     */
    public static ResponseEntity<Map<String, Object>> serverError(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


