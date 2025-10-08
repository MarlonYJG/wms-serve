package com.bj.wms.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 库区类型
 */
public enum ZoneType {
    STORAGE(1, "存储区"),
    RECEIVING(2, "收货区"),
    PICKING(3, "拣货区"),
    RETURN(4, "退货区"),
    DEFECTIVE(5, "不良品区"),
    SHIPPING(6, "发货区");

    private final int code;
    private final String description;

    ZoneType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 业务编码（供数据库转换器使用）
     */
    public int getCode() {
        return code;
    }

    @JsonValue
    public String getJsonValue() {
        return name();
    }

    public String getDescription() {
        return description;
    }

    public static ZoneType fromCode(Integer code) {
        if (code == null) return null;
        for (ZoneType z : values()) {
            if (z.code == code) return z;
        }
        throw new IllegalArgumentException("无效的库区类型代码: " + code);
    }

    @JsonCreator
    public static ZoneType from(Object value) {
        if (value == null) return null;
        String s = value.toString();
        try {
            return ZoneType.valueOf(s);
        } catch (IllegalArgumentException ex) {
            try {
                return fromCode(Integer.parseInt(s));
            } catch (NumberFormatException ignore) {
                throw ex;
            }
        }
    }
}


