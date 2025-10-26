package com.bj.wms.service;

import com.bj.wms.dto.CustomerDTO;
import com.bj.wms.entity.Customer;
import com.bj.wms.mapper.CustomerMapper;
import com.bj.wms.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 分页查询客户列表
     */
    public Page<CustomerDTO> getCustomerList(Integer page, Integer size, String keyword, Boolean isEnabled) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        
        Specification<Customer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询未删除的客户
            predicates.add(cb.equal(root.get("deleted"), 0));
            
            // 关键词搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = "%" + keyword.trim() + "%";
                Predicate keywordPredicate = cb.or(
                    cb.like(root.get("customerCode"), searchKeyword),
                    cb.like(root.get("customerName"), searchKeyword),
                    cb.like(root.get("contactPerson"), searchKeyword)
                );
                predicates.add(keywordPredicate);
            }
            
            // 启用状态筛选
            if (isEnabled != null) {
                predicates.add(cb.equal(root.get("isEnabled"), isEnabled));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Customer> customerPage = customerRepository.findAll(spec, pageable);
        return customerPage.map(CustomerMapper.INSTANCE::toDTO);
    }

    /**
     * 获取所有启用的客户
     */
    public List<CustomerDTO> getEnabledCustomers() {
        List<Customer> customers = customerRepository.findEnabledCustomers();
        return CustomerMapper.INSTANCE.toDTOList(customers);
    }

    /**
     * 根据ID获取客户详情
     */
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("客户不存在"));
        return CustomerMapper.INSTANCE.toDTO(customer);
    }

    /**
     * 根据客户编码获取客户
     */
    public CustomerDTO getCustomerByCode(String customerCode) {
        Customer customer = customerRepository.findByCustomerCode(customerCode)
            .orElseThrow(() -> new RuntimeException("客户不存在"));
        return CustomerMapper.INSTANCE.toDTO(customer);
    }
}