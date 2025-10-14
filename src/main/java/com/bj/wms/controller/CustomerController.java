package com.bj.wms.controller;

import com.bj.wms.dto.CustomerDTO;
import com.bj.wms.entity.Customer;
import com.bj.wms.mapper.CustomerMapper;
import com.bj.wms.service.CustomerService;
import com.bj.wms.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "customerName", required = false) String customerName,
            @RequestParam(value = "customerCode", required = false) String customerCode,
            @RequestParam(value = "isEnabled", required = false) Boolean isEnabled
    ) {
        Page<Customer> result = customerService.page(page, size, keyword, customerName, customerCode, isEnabled);
        List<CustomerDTO> content = result.getContent().stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseUtil.pageSuccess(content, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        return ResponseUtil.success(CustomerMapper.toDTO(customer));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CustomerDTO body) {
        Customer created = customerService.create(CustomerMapper.toEntity(body));
        return ResponseUtil.created(CustomerMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody CustomerDTO body) {
        Customer updated = customerService.update(id, CustomerMapper.toEntity(body));
        return ResponseUtil.success(CustomerMapper.toDTO(updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean isEnabled = body.get("isEnabled");
        if (isEnabled == null) {
            return ResponseUtil.error("isEnabled 不能为空");
        }
        Customer updated = customerService.updateStatus(id, isEnabled);
        return ResponseUtil.success(CustomerMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseUtil.successMsg("删除成功");
    }
}


