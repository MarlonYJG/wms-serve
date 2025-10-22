package com.bj.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 拣货波次创建请求
 */
@Data
public class PickingWaveCreateRequest {
    @NotBlank(message = "波次号不能为空")
    private String waveNo;

    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    private String remark;
}
