package com.bj.wms.mapper;

import com.bj.wms.dto.ChargeDictDTO;
import com.bj.wms.entity.ChargeDict;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 费用字典Mapper
 */
@Mapper
public interface ChargeDictMapper {

    ChargeDictMapper INSTANCE = Mappers.getMapper(ChargeDictMapper.class);

    ChargeDictDTO toDTO(ChargeDict entity);

    List<ChargeDictDTO> toDTOList(List<ChargeDict> entities);
}
