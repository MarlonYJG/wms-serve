/*
 * @Author: Marlon
 * @Date: 2025-10-06 20:35:55
 * @Description: 
 */
package com.bj.wms.config;

import com.bj.wms.entity.User;
import com.bj.wms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 数据初始化：保证至少存在一个可登录管理员账号
 * - 账号来源：环境变量/配置（默认 admin/12345678）
 * - 若已存在相同用户名则不重复创建
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${WMS_ADMIN_USERNAME:admin}")
    private String defaultAdminUsername;

    @Value("${WMS_ADMIN_PASSWORD:12345678}")
    private String defaultAdminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByUsername(defaultAdminUsername)) {
            log.info("Admin user '{}' already exists - skip init", defaultAdminUsername);
            return;
        }
        User admin = new User();
        admin.setUsername(defaultAdminUsername);
        admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
        admin.setRealName("系统管理员");
        admin.setEmail("admin@example.com");
        admin.setPhone("13800000000");
        admin.setStatus(1);
        admin.setRole(User.UserRole.ADMIN);
        userRepository.save(admin);
        log.info("Initialized default admin user '{}'", defaultAdminUsername);
    }
}


