package com.bj.wms.entity;

/**
 * 预约入库状态
 * 1：待预约确认 2：已确认 3：已取消
 */
public enum AppointmentStatus {
    PENDING(1),
    CONFIRMED(2),
    CANCELED(3);

    private final int code;

    AppointmentStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static AppointmentStatus fromCode(Integer code) {
        if (code == null) return null;
        for (AppointmentStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知的预约状态: " + code);
    }
}


