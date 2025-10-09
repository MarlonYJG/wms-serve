package com.bj.wms.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditRating {
    AAA(1, "AAA级"),
    AA(2, "AA级"),
    A(3, "A级"),
    B(4, "B级"),
    C(5, "C级");

    private final int code;
    private final String description;

    CreditRating(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static CreditRating fromCode(Integer code) {
        if (code == null) return null;
        for (CreditRating r : values()) {
            if (r.code == code) return r;
        }
        throw new IllegalArgumentException("无效的信用等级代码: " + code);
    }

    @JsonValue
    public String getJsonValue() { return name(); }

    @JsonCreator
    public static CreditRating from(Object value) {
        if (value == null) return null;
        String s = value.toString();
        try {
            return CreditRating.valueOf(s);
        } catch (IllegalArgumentException ex) {
            try {
                return fromCode(Integer.parseInt(s));
            } catch (NumberFormatException ignore) {
                throw ex;
            }
        }
    }
}


