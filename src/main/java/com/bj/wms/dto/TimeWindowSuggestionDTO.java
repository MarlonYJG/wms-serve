package com.bj.wms.dto;

import lombok.Data;

@Data
public class TimeWindowSuggestionDTO {
    
    private String timeStart;
    
    private String timeEnd;
    
    private Integer capacity;
    
    private Integer available;
    
    private Boolean isRecommended;
}
