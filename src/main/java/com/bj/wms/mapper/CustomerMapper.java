package com.bj.wms.mapper;

import com.bj.wms.dto.CustomerDTO;
import com.bj.wms.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 客户Mapper
 */
@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "customerType", expression = "java(entity.getCustomerType() != null ? entity.getCustomerType().name() : null)")
    @Mapping(target = "creditRating", expression = "java(entity.getCreditRating() != null ? entity.getCreditRating().name() : null)")
    CustomerDTO toDTO(Customer entity);

    List<CustomerDTO> toDTOList(List<Customer> entities);
}