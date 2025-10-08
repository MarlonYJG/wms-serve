/*
 * @Author: Marlon
 * @Date: 2025-10-08 20:07:09
 * @Description: 
 */
package com.bj.wms.controller;

import com.bj.wms.dto.StorageZoneDTO;
import com.bj.wms.dto.StorageZoneQueryDTO;
import com.bj.wms.entity.StorageZone;
import com.bj.wms.service.StorageZoneService;
import com.bj.wms.mapper.StorageZoneMapper;
import com.bj.wms.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/storage-zones")
@RequiredArgsConstructor
public class StorageZoneController {

    private final StorageZoneService storageZoneService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> page(StorageZoneQueryDTO queryDTO) {
        Page<StorageZone> result = storageZoneService.page(queryDTO);
        List<StorageZoneDTO> content = result.getContent().stream()
                .map(StorageZoneMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseUtil.pageSuccess(content, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        StorageZone zone = storageZoneService.getById(id).orElseThrow(() -> new IllegalArgumentException("库区不存在"));
        return ResponseUtil.success(StorageZoneMapper.toDTO(zone));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody StorageZoneDTO dto) {
        StorageZone zone = StorageZoneMapper.toEntity(dto);
        StorageZone created = storageZoneService.create(zone);
        return ResponseUtil.created(StorageZoneMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody StorageZoneDTO dto) {
        StorageZone exist = storageZoneService.getById(id).orElseThrow(() -> new IllegalArgumentException("库区不存在"));
        StorageZone zone = StorageZoneMapper.toEntity(dto);
        zone.setId(exist.getId());
        StorageZone saved = storageZoneService.update(zone);
        return ResponseUtil.success(StorageZoneMapper.toDTO(saved));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> changeStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        StorageZone exist = storageZoneService.getById(id).orElseThrow(() -> new IllegalArgumentException("库区不存在"));
        Boolean enabled = body.get("isEnabled");
        if (enabled != null) {
            exist.setIsEnabled(enabled);
        }
        StorageZone saved = storageZoneService.update(exist);
        return ResponseUtil.success(StorageZoneMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        storageZoneService.deleteById(id);
        return ResponseUtil.successMsg("删除成功");
    }
}


