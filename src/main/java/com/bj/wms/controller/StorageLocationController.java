package com.bj.wms.controller;

import com.bj.wms.dto.StorageLocationDTO;
import com.bj.wms.dto.StorageLocationQueryDTO;
import com.bj.wms.entity.LocationStatus;
import com.bj.wms.entity.StorageLocation;
import com.bj.wms.mapper.StorageLocationMapper;
import com.bj.wms.service.StorageLocationService;
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
@RequestMapping("/storage-locations")
@RequiredArgsConstructor
public class StorageLocationController {

    private final StorageLocationService storageLocationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> page(StorageLocationQueryDTO queryDTO) {
        Page<StorageLocation> result = storageLocationService.page(queryDTO);
        List<StorageLocationDTO> content = result.getContent().stream()
                .map(StorageLocationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseUtil.pageSuccess(content, result.getNumber(), result.getSize(), result.getTotalElements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        StorageLocation entity = storageLocationService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("库位不存在"));
        return ResponseUtil.success(StorageLocationMapper.toDTO(entity));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody StorageLocationDTO dto) {
        StorageLocation entity = StorageLocationMapper.toEntity(dto);
        StorageLocation created = storageLocationService.create(entity);
        return ResponseUtil.created(StorageLocationMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody StorageLocationDTO dto) {
        StorageLocation input = StorageLocationMapper.toEntity(dto);
        StorageLocation saved = storageLocationService.update(id, input);
        return ResponseUtil.success(StorageLocationMapper.toDTO(saved));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        LocationStatus s = status == null ? null : LocationStatus.from(status);
        StorageLocation saved = storageLocationService.updateStatus(id, s);
        return ResponseUtil.success(StorageLocationMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        storageLocationService.delete(id);
        return ResponseUtil.successMsg("删除成功");
    }

    /**
     * 根据仓库ID获取可用库位
     */
    @GetMapping("/warehouse/{warehouseId}/available")
    public ResponseEntity<List<StorageLocationDTO>> getAvailableLocationsByWarehouse(@PathVariable Long warehouseId) {
        List<StorageLocation> locations = storageLocationService.getAvailableLocationsByWarehouse(warehouseId);
        List<StorageLocationDTO> result = locations.stream()
                .map(StorageLocationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}


