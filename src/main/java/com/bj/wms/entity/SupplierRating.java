package com.bj.wms.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SupplierRating {
    A(1, "A级"),
    B(2, "B级"),
    C(3, "C级"),
    D(4, "D级");

    private final int code;
    private final String description;

    SupplierRating(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static SupplierRating fromCode(Integer code) {
        if (code == null) return null;
        for (SupplierRating r : values()) {
            if (r.code == code) return r;
        }
        throw new IllegalArgumentException("无效的供应商评级代码: " + code);
    }

    @JsonValue
    public String getJsonValue() { return name(); }

    @JsonCreator
    public static SupplierRating from(Object value) {
        if (value == null) return null;
        String s = value.toString();
        try {
            return SupplierRating.valueOf(s);
        } catch (IllegalArgumentException ex) {
            try {
                return fromCode(Integer.parseInt(s));
            } catch (NumberFormatException ignore) {
                throw ex;
            }
        }
    }
}


