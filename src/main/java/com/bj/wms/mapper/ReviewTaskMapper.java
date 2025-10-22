package com.bj.wms.mapper;

import com.bj.wms.dto.ReviewTaskDTO;
import com.bj.wms.entity.ReviewTask;
import org.springframework.stereotype.Component;

/**
 * 复核任务映射器
 */
@Component
public class ReviewTaskMapper {

    public static ReviewTaskDTO toDTO(ReviewTask entity) {
        if (entity == null) {
            return null;
        }

        ReviewTaskDTO dto = new ReviewTaskDTO();
        dto.setId(entity.getId());
        dto.setTaskNo(entity.getTaskNo());
        dto.setOutboundOrderId(entity.getOutboundOrderId());
        dto.setProductSkuId(entity.getProductSkuId());
        dto.setExpectedQuantity(entity.getExpectedQuantity());
        dto.setActualQuantity(entity.getActualQuantity());
        dto.setStatus(entity.getStatus());
        dto.setReviewerId(entity.getReviewerId());
        dto.setReviewerName(entity.getReviewerName());
        dto.setReviewTime(entity.getReviewTime());
        dto.setRemark(entity.getRemark());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());

        // 设置关联信息
        if (entity.getOutboundOrder() != null) {
            dto.setOutboundOrderNo(entity.getOutboundOrder().getOrderNo());
        }
        if (entity.getProductSku() != null) {
            dto.setProductName(entity.getProductSku().getSkuName());
            dto.setSkuCode(entity.getProductSku().getSkuCode());
        }

        // 设置状态名称
        dto.setStatusName(getStatusName(entity.getStatus()));

        return dto;
    }

    private static String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 1 -> "待复核";
            case 2 -> "复核中";
            case 3 -> "复核完成";
            case 4 -> "复核异常";
            default -> "未知";
        };
    }
}
