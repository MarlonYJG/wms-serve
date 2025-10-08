package com.bj.wms.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应工具类
 *
 * 统一返回结构：{ code, data, msg, error? }
 * 符合API接口规范文档的响应格式
 */
public class ResponseUtil {

    /**
     * 成功响应
     */
    public static <T> ResponseEntity<Map<String, Object>> success(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", data);
        response.put("msg", "操作成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 成功响应（带自定义消息）
     */
    public static <T> ResponseEntity<Map<String, Object>> success(T data, String msg) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", data);
        response.put("msg", msg);
        return ResponseEntity.ok(response);
    }

    /**
     * 成功响应（仅消息）
     */
    public static ResponseEntity<Map<String, Object>> successMsg(String msg) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", null);
        response.put("msg", msg);
        return ResponseEntity.ok(response);
    }

    /**
     * 创建成功响应
     */
    public static <T> ResponseEntity<Map<String, Object>> created(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 201);
        response.put("data", data);
        response.put("msg", "创建成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 分页响应
     */
    public static <T> ResponseEntity<Map<String, Object>> pageSuccess(
            java.util.List<T> content, 
            int pageNumber, 
            int pageSize, 
            long total) {
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("content", content);
        pageData.put("pageNumber", pageNumber);
        pageData.put("pageSize", pageSize);
        pageData.put("sorted", true);
        pageData.put("unsorted", false);
        pageData.put("total", total);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", pageData);
        response.put("msg", "查询成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 错误响应
     */
    public static ResponseEntity<Map<String, Object>> error(String msg) {
        return error(HttpStatus.BAD_REQUEST, msg, null);
    }

    /**
     * 错误响应（带错误详情）
     */
    public static ResponseEntity<Map<String, Object>> error(String msg, Map<String, Object> errorDetails) {
        return error(HttpStatus.BAD_REQUEST, msg, errorDetails);
    }

    /**
     * 错误响应（自定义状态码）
     */
    public static ResponseEntity<Map<String, Object>> error(HttpStatus status, String msg) {
        return error(status, msg, null);
    }

    /**
     * 错误响应（完整参数）
     */
    public static ResponseEntity<Map<String, Object>> error(HttpStatus status, String msg, Map<String, Object> errorDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", status.value());
        response.put("data", null);
        response.put("msg", msg);
        if (errorDetails != null) {
            response.put("error", errorDetails);
        }
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 参数验证错误响应
     */
    public static ResponseEntity<Map<String, Object>> validationError(String msg, java.util.List<Map<String, String>> details) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("type", "VALIDATION_ERROR");
        errorDetails.put("details", details);
        return error(HttpStatus.BAD_REQUEST, msg, errorDetails);
    }

    /**
     * 资源不存在响应
     */
    public static ResponseEntity<Map<String, Object>> notFound(String msg) {
        return error(HttpStatus.NOT_FOUND, msg);
    }

    /**
     * 权限不足响应
     */
    public static ResponseEntity<Map<String, Object>> forbidden(String msg) {
        return error(HttpStatus.FORBIDDEN, msg);
    }

    /**
     * 未授权响应
     */
    public static ResponseEntity<Map<String, Object>> unauthorized(String msg) {
        return error(HttpStatus.UNAUTHORIZED, msg);
    }

    /**
     * 服务器内部错误响应
     */
    public static ResponseEntity<Map<String, Object>> serverError(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }

    /**
     * 资源冲突响应
     */
    public static ResponseEntity<Map<String, Object>> conflict(String msg) {
        return error(HttpStatus.CONFLICT, msg);
    }
}


