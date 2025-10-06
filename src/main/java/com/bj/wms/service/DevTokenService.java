package com.bj.wms.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 开发环境简单 Token 服务：将 token 映射到 userId，并设置过期时间。
 * 生产应替换为 JWT。
 */
@Service
@Slf4j
public class DevTokenService {
    private static final Duration DEFAULT_TTL = Duration.ofHours(24);
    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

    public String issueToken(Long userId) {
        String token = "dev-" + userId + "-" + UUID.randomUUID();
        store.put(token, new Entry(userId, Instant.now().plus(DEFAULT_TTL)));
        return token;
    }

    public Optional<Long> resolveUserId(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        Entry e = store.get(token);
        if (e == null) return Optional.empty();
        if (Instant.now().isAfter(e.expireAt())) {
            store.remove(token);
            return Optional.empty();
        }
        return Optional.of(e.userId());
    }

    public void invalidate(String token) {
        if (token != null) store.remove(token);
    }

    private record Entry(Long userId, Instant expireAt) {}
}


