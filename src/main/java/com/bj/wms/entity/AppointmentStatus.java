package com.bj.wms.entity;

/**
 * 预约入库状态
 * 1：待审核 2：已审核 3：已拒绝 4：已取消 5：已完成
 */
public enum AppointmentStatus {
    PENDING(1, "待审核"),
    APPROVED(2, "已审核"),
    REJECTED(3, "已拒绝"),
    CANCELED(4, "已取消"),
    COMPLETED(5, "已完成");

    private final int code;
    private final String description;

    AppointmentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AppointmentStatus fromCode(Integer code) {
        if (code == null) return null;
        for (AppointmentStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知的预约状态: " + code);
    }
}


