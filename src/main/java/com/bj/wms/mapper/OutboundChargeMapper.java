package com.bj.wms.mapper;

import com.bj.wms.dto.OutboundChargeDTO;
import com.bj.wms.entity.OutboundCharge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 出库费用Mapper
 */
@Mapper
public interface OutboundChargeMapper {

    OutboundChargeMapper INSTANCE = Mappers.getMapper(OutboundChargeMapper.class);

    @Mapping(target = "outboundOrderNo", source = "outboundOrder.orderNo")
    @Mapping(target = "chargeTypeName", source = "chargeDict.chargeName")
    OutboundChargeDTO toDTO(OutboundCharge entity);

    List<OutboundChargeDTO> toDTOList(List<OutboundCharge> entities);
}
