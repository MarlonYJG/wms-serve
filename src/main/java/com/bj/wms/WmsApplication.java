package com.bj.wms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WMS系统主启动类
 * 
 * @SpringBootApplication 注解包含以下功能：
 * 1. @Configuration: 标识这是一个配置类
 * 2. @EnableAutoConfiguration: 启用Spring Boot的自动配置
 * 3. @ComponentScan: 自动扫描当前包及子包下的组件
 */
@SpringBootApplication
public class WmsApplication {

    public static void main(String[] args) {
        // 启动Spring Boot应用
        SpringApplication.run(WmsApplication.class, args);
        System.out.println("WMS系统启动成功！访问地址：http://localhost:8080");
    }
}


