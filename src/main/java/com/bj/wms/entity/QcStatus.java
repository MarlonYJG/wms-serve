package com.bj.wms.entity;

/**
 * 入库质检状态
 * 1：待质检，2：已完成
 */
public enum QcStatus {
    PENDING(1),
    COMPLETED(2);

    private final int code;

    QcStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static QcStatus fromCode(Integer code) {
        if (code == null) return null;
        for (QcStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知的质检状态: " + code);
    }
}


