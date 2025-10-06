package com.bj.wms.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * 
 * 提供系统基本信息和健康检查接口
 */
@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class HomeController {

    /**
     * 系统首页
     * GET /api/
     */
    @GetMapping
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "欢迎使用WMS仓库管理系统API");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "running");
        
        // API文档链接
        Map<String, String> links = new HashMap<>();
        links.put("users", "/api/v1/users");
        links.put("products", "/api/v1/products");
        links.put("warehouses", "/api/v1/warehouses");
        links.put("health", "/api/v1/health");
        response.put("links", links);
        
        return response;
    }

    /**
     * 健康检查接口
     * GET /api/health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "WMS Backend");
        response.put("version", "1.0.0");
        
        // 系统信息
        Map<String, Object> system = new HashMap<>();
        system.put("java.version", System.getProperty("java.version"));
        system.put("os.name", System.getProperty("os.name"));
        system.put("os.version", System.getProperty("os.version"));
        response.put("system", system);
        
        return response;
    }
}


