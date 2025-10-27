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

    /**
     * 创建新客户
     */
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        // 检查客户编码是否已存在
        if (customerRepository.existsByCustomerCode(customerDTO.getCustomerCode())) {
            throw new RuntimeException("客户编码已存在: " + customerDTO.getCustomerCode());
        }
        
        Customer customer = CustomerMapper.INSTANCE.toEntity(customerDTO);
        customer.setDeleted(0);
        customer.setIsEnabled(customerDTO.getIsEnabled() != null ? customerDTO.getIsEnabled() : true);
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("创建客户成功: ID={}, 编码={}, 名称={}", savedCustomer.getId(), savedCustomer.getCustomerCode(), savedCustomer.getCustomerName());
        
        return CustomerMapper.INSTANCE.toDTO(savedCustomer);
    }

    /**
     * 更新客户信息
     */
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("客户不存在"));
        
        // 检查客户编码是否被其他客户使用
        if (!existingCustomer.getCustomerCode().equals(customerDTO.getCustomerCode())) {
            if (customerRepository.existsByCustomerCode(customerDTO.getCustomerCode())) {
                throw new RuntimeException("客户编码已存在: " + customerDTO.getCustomerCode());
            }
        }
        
        // 更新客户信息
        existingCustomer.setCustomerCode(customerDTO.getCustomerCode());
        existingCustomer.setCustomerName(customerDTO.getCustomerName());
        existingCustomer.setContactPerson(customerDTO.getContactPerson());
        existingCustomer.setContactPhone(customerDTO.getContactPhone());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setIsEnabled(customerDTO.getIsEnabled());
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("更新客户成功: ID={}, 编码={}, 名称={}", updatedCustomer.getId(), updatedCustomer.getCustomerCode(), updatedCustomer.getCustomerName());
        
        return CustomerMapper.INSTANCE.toDTO(updatedCustomer);
    }

    /**
     * 删除客户（软删除）
     */
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("客户不存在"));
        
        // 软删除
        customer.setDeleted(1);
        customerRepository.save(customer);
        
        log.info("删除客户成功: ID={}, 编码={}, 名称={}", customer.getId(), customer.getCustomerCode(), customer.getCustomerName());
    }

    /**
     * 更新客户启用状态
     */
    @Transactional
    public CustomerDTO updateCustomerStatus(Long id, Boolean isEnabled) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("客户不存在"));
        
        customer.setIsEnabled(isEnabled);
        Customer updatedCustomer = customerRepository.save(customer);
        
        log.info("更新客户状态成功: ID={}, 编码={}, 名称={}, 启用状态={}", 
            updatedCustomer.getId(), updatedCustomer.getCustomerCode(), updatedCustomer.getCustomerName(), isEnabled);
        
        return CustomerMapper.INSTANCE.toDTO(updatedCustomer);
    }
}