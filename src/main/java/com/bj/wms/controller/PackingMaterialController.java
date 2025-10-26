package com.bj.wms.controller;

import com.bj.wms.dto.PackingMaterialDTO;
import com.bj.wms.service.PackingMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 包装材料控制器
 */
@Slf4j
@RestController
@RequestMapping("/packing-materials")
@RequiredArgsConstructor
public class PackingMaterialController {

    private final PackingMaterialService packingMaterialService;

    /**
     * 分页查询包装材料
     */
    @GetMapping
    public ResponseEntity<Page<PackingMaterialDTO>> getPackingMaterialList(
            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) Integer materialType,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PackingMaterialDTO> result = packingMaterialService.getPackingMaterialList(
                materialCode, materialName, materialType, isEnabled, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取包装材料详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<PackingMaterialDTO> getPackingMaterialById(@PathVariable Long id) {
        PackingMaterialDTO result = packingMaterialService.getPackingMaterialById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建包装材料
     */
    @PostMapping
    public ResponseEntity<PackingMaterialDTO> createPackingMaterial(@RequestBody PackingMaterialDTO request) {
        PackingMaterialDTO result = packingMaterialService.createPackingMaterial(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新包装材料
     */
    @PutMapping("/{id}")
    public ResponseEntity<PackingMaterialDTO> updatePackingMaterial(@PathVariable Long id, 
                                                                   @RequestBody PackingMaterialDTO request) {
        PackingMaterialDTO result = packingMaterialService.updatePackingMaterial(id, request);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除包装材料
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackingMaterial(@PathVariable Long id) {
        packingMaterialService.deletePackingMaterial(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取启用的包装材料列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<PackingMaterialDTO>> getEnabledPackingMaterials() {
        List<PackingMaterialDTO> result = packingMaterialService.getEnabledPackingMaterials();
        return ResponseEntity.ok(result);
    }

    /**
     * 根据类型获取启用的包装材料列表
     */
    @GetMapping("/enabled/type/{materialType}")
    public ResponseEntity<List<PackingMaterialDTO>> getEnabledPackingMaterialsByType(@PathVariable Integer materialType) {
        List<PackingMaterialDTO> result = packingMaterialService.getEnabledPackingMaterialsByType(materialType);
        return ResponseEntity.ok(result);
    }
}
