package com.bj.wms.mapper;

import com.bj.wms.dto.SettlementDTO;
import com.bj.wms.dto.SettlementItemDTO;
import com.bj.wms.entity.Settlement;
import com.bj.wms.entity.SettlementItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 结算单Mapper
 */
@Mapper
public interface SettlementMapper {

    SettlementMapper INSTANCE = Mappers.getMapper(SettlementMapper.class);

    @Mapping(target = "customerName", source = "customer.customerName")
    @Mapping(target = "statusText", expression = "java(getStatusText(entity.getStatus()))")
    @Mapping(target = "items", ignore = true)
    SettlementDTO toDTO(Settlement entity);

    @Mapping(target = "outboundOrderNo", source = "outboundOrder.orderNo")
    SettlementItemDTO toDTO(SettlementItem entity);

    List<SettlementDTO> toDTOList(List<Settlement> entities);

    List<SettlementItemDTO> toItemDTOList(List<SettlementItem> entities);

    default String getStatusText(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "草稿";
            case 2: return "待审核";
            case 3: return "已审核";
            case 4: return "已结清";
            case 5: return "已作废";
            default: return "未知";
        }
    }
}
