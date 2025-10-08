package com.bj.wms.controller;

import com.bj.wms.dto.WarehouseCreateDTO;
import com.bj.wms.dto.WarehouseDTO;
import com.bj.wms.dto.WarehouseQueryDTO;
import com.bj.wms.dto.WarehouseUpdateDTO;
import com.bj.wms.entity.Warehouse;
import com.bj.wms.service.WarehouseService;
import com.bj.wms.util.DTOConverter;
import com.bj.wms.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * 查询仓库列表（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> page(WarehouseQueryDTO queryDTO) {
        Page<Warehouse> result = warehouseService.page(
            queryDTO.getPage(), 
            queryDTO.getSize(), 
            queryDTO.getSortBy(), 
            queryDTO.getSortDir(), 
            queryDTO.getKeyword(), 
            queryDTO.getName(), 
            queryDTO.getCode(), 
            queryDTO.getIsEnabled(),
            queryDTO.getStartTime(),
            queryDTO.getEndTime()
        );
        
        // 转换为DTO
        List<WarehouseDTO> content = result.getContent().stream()
            .map(DTOConverter::toWarehouseDTO)
            .collect(Collectors.toList());
        
        return ResponseUtil.pageSuccess(
            content, 
            result.getNumber(), 
            result.getSize(), 
            result.getTotalElements()
        );
    }

    /**
     * 获取仓库详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("仓库不存在"));
        WarehouseDTO dto = DTOConverter.toWarehouseDTO(warehouse);
        return ResponseUtil.success(dto);
    }

    /**
     * 创建仓库
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody WarehouseCreateDTO createDTO) {
        // 转换为实体
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(createDTO.getCode());
        warehouse.setName(createDTO.getName());
        warehouse.setAddress(createDTO.getAddress());
        warehouse.setContactPerson(createDTO.getContactPerson());
        warehouse.setContactPhone(createDTO.getContactPhone());
        warehouse.setIsEnabled(createDTO.getIsEnabled());
        warehouse.setTotalCapacity(createDTO.getTotalCapacity());
        
        Warehouse created = warehouseService.create(warehouse);
        WarehouseDTO dto = DTOConverter.toWarehouseDTO(created);
        return ResponseUtil.created(dto);
    }

    /**
     * 更新仓库
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody WarehouseUpdateDTO updateDTO) {
        Warehouse existing = warehouseService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("仓库不存在"));
        
        // 更新字段
        if (updateDTO.getName() != null) {
            existing.setName(updateDTO.getName());
        }
        if (updateDTO.getAddress() != null) {
            existing.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getContactPerson() != null) {
            existing.setContactPerson(updateDTO.getContactPerson());
        }
        if (updateDTO.getContactPhone() != null) {
            existing.setContactPhone(updateDTO.getContactPhone());
        }
        if (updateDTO.getIsEnabled() != null) {
            existing.setIsEnabled(updateDTO.getIsEnabled());
        }
        if (updateDTO.getTotalCapacity() != null) {
            existing.setTotalCapacity(updateDTO.getTotalCapacity());
        }
        
        Warehouse updated = warehouseService.update(id, existing);
        WarehouseDTO dto = DTOConverter.toWarehouseDTO(updated);
        return ResponseUtil.success(dto);
    }

    /**
     * 删除仓库
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseUtil.successMsg("删除成功");
    }

    /**
     * 启用/禁用仓库
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean isEnabled = body.get("isEnabled");
        if (isEnabled == null) {
            return ResponseUtil.error("isEnabled 不能为空");
        }
        Warehouse updated = warehouseService.updateStatus(id, isEnabled);
        WarehouseDTO dto = DTOConverter.toWarehouseDTO(updated);
        return ResponseUtil.success(dto);
    }

    /**
     * 获取仓库统计信息
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> stats(@PathVariable Long id) {
        // 占位实现：返回零值统计，后续可基于库存/库区/订单表计算
        warehouseService.getById(id).orElseThrow(() -> new IllegalArgumentException("仓库不存在"));
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLocations", 0);
        stats.put("occupiedLocations", 0);
        stats.put("totalInventory", 0);
        stats.put("totalValue", 0);
        stats.put("inboundOrders", 0);
        stats.put("outboundOrders", 0);
        return ResponseUtil.success(stats);
    }
}