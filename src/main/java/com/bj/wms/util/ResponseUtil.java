package com.bj.wms.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应工具类
 *
 * 统一返回结构：{ code, data, msg }
 */
public class ResponseUtil {

    public static <T> ResponseEntity<Map<String, Object>> success(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", data);
        response.put("msg", "OK");
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<Map<String, Object>> successMsg(String msg) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", null);
        response.put("msg", msg);
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<Map<String, Object>> created(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", data);
        response.put("msg", "创建成功");
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<Map<String, Object>> error(String msg) {
        return error(HttpStatus.BAD_REQUEST, msg);
    }

    public static ResponseEntity<Map<String, Object>> error(HttpStatus status, String msg) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", status.value());
        response.put("data", null);
        response.put("msg", msg);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Map<String, Object>> notFound(String msg) {
        return error(HttpStatus.NOT_FOUND, msg);
    }

    public static ResponseEntity<Map<String, Object>> serverError(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }
}


