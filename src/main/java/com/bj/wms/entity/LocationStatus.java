package com.bj.wms.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 库位状态
 */
public enum LocationStatus {
    AVAILABLE(1, "空闲"),
    OCCUPIED(2, "占用"),
    DISABLED(3, "禁用");

    private final int code;
    private final String description;

    LocationStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonValue
    public String getJsonValue() { return name(); }

    public static LocationStatus fromCode(Integer code) {
        if (code == null) return null;
        for (LocationStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("无效的库位状态代码: " + code);
    }

    @JsonCreator
    public static LocationStatus from(Object value) {
        if (value == null) return null;
        String s = value.toString();
        try {
            return LocationStatus.valueOf(s);
        } catch (IllegalArgumentException ex) {
            try {
                return fromCode(Integer.parseInt(s));
            } catch (NumberFormatException ignore) {
                throw ex;
            }
        }
    }
}


