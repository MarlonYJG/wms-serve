package com.bj.wms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * 数据库配置类
 * 
 * 功能说明：
 * 1. 启用JPA审计功能，自动记录创建时间、修改时间等
 * 2. 配置审计用户信息
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class DatabaseConfig {

    /**
     * 审计用户提供者
     * 用于自动填充创建人、修改人字段
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                // 这里可以从Spring Security获取当前用户
                // 暂时返回固定值，后续可以集成认证系统
                return Optional.of("system");
            }
        };
    }
}


