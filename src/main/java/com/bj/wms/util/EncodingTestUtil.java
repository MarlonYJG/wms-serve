package com.bj.wms.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 编码测试工具类
 * 用于测试和验证中文字符编码是否正确
 */
@Slf4j
public class EncodingTestUtil {
    
    /**
     * 测试中文字符编码
     */
    public static void testChineseEncoding() {
        log.info("=== 中文编码测试 ===");
        log.info("系统默认编码: {}", System.getProperty("file.encoding"));
        log.info("控制台编码: {}", System.getProperty("console.encoding"));
        log.info("用户语言: {}", System.getProperty("user.language"));
        log.info("用户国家: {}", System.getProperty("user.country"));
        
        // 测试中文字符
        String[] testStrings = {
            "出库单利润计算完成",
            "总收入: 3710.00",
            "总成本: 2670.00", 
            "毛利润: 1040.00",
            "利润率: 28.03%"
        };
        
        for (String str : testStrings) {
            log.info("测试字符串: {}", str);
        }
        
        log.info("=== 编码测试完成 ===");
    }
}
