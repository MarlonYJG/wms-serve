/*
 * @Author: Marlon
 * @Date: 2025-10-06 16:16:47
 * @Description: 
 */
package com.bj.wms.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NumericCaptchaService {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(3);
    private static final int WIDTH = 100;
    private static final int HEIGHT = 40;
    private static final int CODE_LEN = 4;

    private final Map<String, Record> store = new ConcurrentHashMap<>();

    public InitResult init() {
        String token = UUID.randomUUID().toString();
        String code = generateCode(CODE_LEN);
        store.put(token, new Record(code, Instant.now().plus(DEFAULT_TTL)));
        InitResult res = new InitResult();
        res.setToken(token);
        res.setImageBase64(renderCodeImage(code));
        return res;
    }

    public boolean verify(String token, String code) {
        if (token == null || code == null) return false;
        Record record = store.remove(token);
        if (record == null) return false;
        if (Instant.now().isAfter(record.expireAt)) return false;
        return record.code.equalsIgnoreCase(code.trim());
    }

    private String generateCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    private String renderCodeImage(String code) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 背景
        g.setColor(new Color(245, 247, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // 干扰线
        g.setColor(new Color(200, 205, 215));
        for (int i = 0; i < 6; i++) {
            int x1 = (int) (Math.random() * WIDTH);
            int y1 = (int) (Math.random() * HEIGHT);
            int x2 = (int) (Math.random() * WIDTH);
            int y2 = (int) (Math.random() * HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }
        // 文本
        g.setColor(new Color(66, 66, 66));
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(code);
        int x = (WIDTH - textWidth) / 2;
        int y = (HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(code, x, y);
        g.dispose();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.warn("render captcha failed", e);
            return null;
        }
    }

    @Data
    public static class InitResult {
        private String token;
        private String imageBase64;
    }

    private record Record(String code, Instant expireAt) {}
}


