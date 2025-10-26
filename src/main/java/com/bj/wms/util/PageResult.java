package com.bj.wms.util;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    
    private List<T> content;
    
    private int pageNumber;
    
    private int pageSize;
    
    private long total;
    
    public PageResult() {}
    
    public PageResult(List<T> content, long total) {
        this.content = content;
        this.total = total;
    }
    
    public PageResult(List<T> content, int pageNumber, int pageSize, long total) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.total = total;
    }
}
