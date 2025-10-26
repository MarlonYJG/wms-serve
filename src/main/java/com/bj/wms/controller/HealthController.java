package com.bj.wms.controller;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 提供数据库连接池状态监控
 */
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final DataSource dataSource;

    /**
     * 获取数据库连接池状态
     */
    @GetMapping("/datasource")
    public Map<String, Object> getDataSourceHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                HikariPoolMXBean poolBean = hikariDataSource.getHikariPoolMXBean();
                
                health.put("status", "UP");
                health.put("poolName", hikariDataSource.getPoolName());
                health.put("activeConnections", poolBean.getActiveConnections());
                health.put("idleConnections", poolBean.getIdleConnections());
                health.put("totalConnections", poolBean.getTotalConnections());
                health.put("threadsAwaitingConnection", poolBean.getThreadsAwaitingConnection());
                health.put("maxPoolSize", hikariDataSource.getMaximumPoolSize());
                health.put("minIdle", hikariDataSource.getMinimumIdle());
                health.put("maxLifetime", hikariDataSource.getMaxLifetime());
                health.put("idleTimeout", hikariDataSource.getIdleTimeout());
                health.put("connectionTimeout", hikariDataSource.getConnectionTimeout());
                
                log.info("数据库连接池状态: 活跃连接={}, 空闲连接={}, 总连接={}, 等待线程={}", 
                    poolBean.getActiveConnections(), 
                    poolBean.getIdleConnections(), 
                    poolBean.getTotalConnections(), 
                    poolBean.getThreadsAwaitingConnection());
            } else {
                health.put("status", "UNKNOWN");
                health.put("message", "数据源类型不是HikariCP");
            }
        } catch (Exception e) {
            log.error("获取数据库连接池状态失败", e);
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }
        
        return health;
    }

    /**
     * 简单的健康检查
     */
    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "WMS系统运行正常");
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return result;
    }
}
