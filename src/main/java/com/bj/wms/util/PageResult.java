package com.bj.wms.util;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    
    private List<T> data;
    
    private long total;
    
    public PageResult() {}
    
    public PageResult(List<T> data, long total) {
        this.data = data;
        this.total = total;
    }
    
    public PageResult(List<T> data, int page, int size, long total) {
        this.data = data;
        this.total = total;
    }
}
