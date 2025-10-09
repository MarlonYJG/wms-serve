package com.bj.wms.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CustomerType {
    INDIVIDUAL(1, "个人客户"),
    ENTERPRISE(2, "企业客户"),
    AGENT(3, "代理商"),
    DEALER(4, "经销商");

    private final int code;
    private final String description;

    CustomerType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static CustomerType fromCode(Integer code) {
        if (code == null) return null;
        for (CustomerType t : values()) {
            if (t.code == code) return t;
        }
        throw new IllegalArgumentException("无效的客户类型代码: " + code);
    }

    @JsonValue
    public String getJsonValue() { return name(); }

    @JsonCreator
    public static CustomerType from(Object value) {
        if (value == null) return null;
        String s = value.toString();
        try {
            return CustomerType.valueOf(s);
        } catch (IllegalArgumentException ex) {
            try {
                return fromCode(Integer.parseInt(s));
            } catch (NumberFormatException ignore) {
                throw ex;
            }
        }
    }
}


