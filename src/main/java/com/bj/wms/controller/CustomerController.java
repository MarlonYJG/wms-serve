package com.bj.wms.controller;

import com.bj.wms.dto.CustomerDTO;
import com.bj.wms.service.CustomerService;
import com.bj.wms.util.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户控制器
 */
@Slf4j
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 分页查询客户列表
     */
    @GetMapping
    public ResponseEntity<PageResult<CustomerDTO>> getCustomerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isEnabled) {
        Page<CustomerDTO> pageResult = customerService.getCustomerList(page, size, keyword, isEnabled);
        PageResult<CustomerDTO> result = new PageResult<>(pageResult.getContent(), pageResult.getTotalElements());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有启用的客户
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<CustomerDTO>> getEnabledCustomers() {
        List<CustomerDTO> result = customerService.getEnabledCustomers();
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取客户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        CustomerDTO result = customerService.getCustomerById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据客户编码获取客户
     */
    @GetMapping("/code/{customerCode}")
    public ResponseEntity<CustomerDTO> getCustomerByCode(@PathVariable String customerCode) {
        CustomerDTO result = customerService.getCustomerByCode(customerCode);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新客户
     */
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        log.info("创建新客户: {}", customerDTO.getCustomerName());
        CustomerDTO result = customerService.createCustomer(customerDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新客户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        log.info("更新客户信息: ID={}, 名称={}", id, customerDTO.getCustomerName());
        CustomerDTO result = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("删除客户: ID={}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 启用/禁用客户
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<CustomerDTO> updateCustomerStatus(@PathVariable Long id, @RequestParam Boolean isEnabled) {
        log.info("更新客户状态: ID={}, 启用状态={}", id, isEnabled);
        CustomerDTO result = customerService.updateCustomerStatus(id, isEnabled);
        return ResponseEntity.ok(result);
    }
}