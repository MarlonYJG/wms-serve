package com.bj.wms.mapper;

import com.bj.wms.dto.InboundChargeCreateRequest;
import com.bj.wms.dto.InboundChargeDTO;
import com.bj.wms.entity.InboundCharge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 入库费用Mapper
 */
@Mapper
public interface InboundChargeMapper {

    InboundChargeMapper INSTANCE = Mappers.getMapper(InboundChargeMapper.class);

    /**
     * 实体转DTO
     */
    @Mapping(target = "inboundOrderNo", source = "inboundOrder.orderNo")
    @Mapping(target = "chargeTypeName", source = "chargeDict.chargeName")
    InboundChargeDTO toDTO(InboundCharge entity);

    /**
     * 实体列表转DTO列表
     */
    List<InboundChargeDTO> toDTOList(List<InboundCharge> entities);
}
