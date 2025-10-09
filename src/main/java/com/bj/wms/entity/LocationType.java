package com.bj.wms.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 库位类型
 */
public enum LocationType {
    SHELF(1, "货架位"),
    FLOOR(2, "地面位"),
    COLD(3, "冷藏位"),
    DANGEROUS(4, "危险品位");

    private final int code;
    private final String description;

    LocationType(int code, String description) {
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
    public String getJsonValue() {
        return name();
    }

    public static LocationType fromCode(Integer code) {
        if (code == null) return null;
        for (LocationType t : values()) {
            if (t.code == code) return t;
        }
        throw new IllegalArgumentException("无效的库位类型代码: " + code);
    }

    @JsonCreator
    public static LocationType from(Object value) {
        if (value == null) return null;
        String s = value.toString();
        try {
            return LocationType.valueOf(s);
        } catch (IllegalArgumentException ex) {
            try {
                return fromCode(Integer.parseInt(s));
            } catch (NumberFormatException ignore) {
                throw ex;
            }
        }
    }
}


