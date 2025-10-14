package com.bj.wms.entity;

/**
 * 入库单状态
 * 1：待收货，2：部分收货，3：已完成，4：已取消
 */
public enum InboundStatus {
    PENDING(1),
    PARTIAL(2),
    COMPLETED(3),
    CANCELED(4);

    private final int code;

    InboundStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static InboundStatus fromCode(Integer code) {
        if (code == null) return null;
        for (InboundStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知的入库状态: " + code);
    }
}


