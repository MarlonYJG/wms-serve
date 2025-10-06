package com.bj.wms.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 分页工具类
 * 
 * 提供分页相关的工具方法
 */
public class PageUtil {

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 0;

    /**
     * 默认页大小
     */
    public static final int DEFAULT_SIZE = 10;

    /**
     * 最大页大小
     */
    public static final int MAX_SIZE = 100;

    /**
     * 创建分页对象
     */
    public static Pageable createPageable(int page, int size) {
        return createPageable(page, size, "id", Sort.Direction.DESC);
    }

    /**
     * 创建分页对象（指定排序字段）
     */
    public static Pageable createPageable(int page, int size, String sortBy) {
        return createPageable(page, size, sortBy, Sort.Direction.DESC);
    }

    /**
     * 创建分页对象（指定排序字段和方向）
     */
    public static Pageable createPageable(int page, int size, String sortBy, Sort.Direction direction) {
        // 验证页码
        if (page < 0) {
            page = DEFAULT_PAGE;
        }
        
        // 验证页大小
        if (size <= 0) {
            size = DEFAULT_SIZE;
        } else if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
        
        // 验证排序字段
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
        }
        
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }

    /**
     * 验证页码
     */
    public static int validatePage(int page) {
        return page < 0 ? DEFAULT_PAGE : page;
    }

    /**
     * 验证页大小
     */
    public static int validateSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        } else if (size > MAX_SIZE) {
            return MAX_SIZE;
        }
        return size;
    }

    /**
     * 验证排序字段
     */
    public static String validateSortBy(String sortBy) {
        return (sortBy == null || sortBy.trim().isEmpty()) ? "id" : sortBy.trim();
    }

    /**
     * 验证排序方向
     */
    public static Sort.Direction validateSortDirection(String sortDir) {
        if (sortDir == null || sortDir.trim().isEmpty()) {
            return Sort.Direction.DESC;
        }
        
        String direction = sortDir.trim().toLowerCase();
        return "asc".equals(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
}


