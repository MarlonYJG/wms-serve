package com.bj.wms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 应用配置类
 * 
 * 读取自定义配置属性
 */
@Configuration
@ConfigurationProperties(prefix = "wms")
@PropertySource("classpath:application.yml")
public class AppConfig {

    private Jwt jwt = new Jwt();
    private Page page = new Page();

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * JWT配置
     */
    public static class Jwt {
        private String secret;
        private Long expiration;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Long getExpiration() {
            return expiration;
        }

        public void setExpiration(Long expiration) {
            this.expiration = expiration;
        }
    }

    /**
     * 分页配置
     */
    public static class Page {
        private Integer defaultSize;
        private Integer maxSize;

        public Integer getDefaultSize() {
            return defaultSize;
        }

        public void setDefaultSize(Integer defaultSize) {
            this.defaultSize = defaultSize;
        }

        public Integer getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(Integer maxSize) {
            this.maxSize = maxSize;
        }
    }
}


