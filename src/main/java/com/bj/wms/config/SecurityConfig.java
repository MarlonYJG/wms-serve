package com.bj.wms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * 
 * 配置安全策略、密码编码器等
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码编码器
     * 使用BCrypt算法加密密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链配置
     * 配置HTTP安全策略
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护（API项目通常不需要）
            .csrf(AbstractHttpConfigurer::disable)
            
            // 配置授权规则（注意：这里的匹配路径不包含 context-path /api）
            .authorizeHttpRequests(authz -> authz
                // 允许访问首页和健康检查接口（在 context-path 去除后匹配）
                .requestMatchers(HttpMethod.GET, "/", "/health").permitAll()

                // 允许访问H2数据库控制台（仅开发环境）
                .requestMatchers("/h2-console/**").permitAll()

                // 暂时放开所有API（如需开启认证，改为具体规则或 .authenticated()）
                .anyRequest().permitAll()
            )
            
            // 配置HTTP Basic认证（可选）
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // 配置表单登录（可选）
            .formLogin(AbstractHttpConfigurer::disable)
            
            // 允许iframe（H2控制台需要）
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}


