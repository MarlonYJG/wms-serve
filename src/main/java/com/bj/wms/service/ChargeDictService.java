package com.bj.wms.service;

import com.bj.wms.dto.ChargeDictCreateRequest;
import com.bj.wms.dto.ChargeDictDTO;
import com.bj.wms.entity.ChargeDict;
import com.bj.wms.mapper.ChargeDictMapper;
import com.bj.wms.repository.ChargeDictRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 费用字典服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeDictService {

    private final ChargeDictRepository chargeDictRepository;

    /**
     * 获取所有费用字典
     */
    public List<ChargeDictDTO> getAllChargeDicts() {
        List<ChargeDict> dicts = chargeDictRepository.findByDeletedFalseOrderByChargeCode();
        return ChargeDictMapper.INSTANCE.toDTOList(dicts);
    }

    /**
     * 获取启用的费用字典
     */
    public List<ChargeDictDTO> getEnabledChargeDicts() {
        List<ChargeDict> dicts = chargeDictRepository.findByIsEnabledTrueAndDeletedFalseOrderByChargeCode();
        return ChargeDictMapper.INSTANCE.toDTOList(dicts);
    }

    /**
     * 根据费用名称查询
     */
    public List<ChargeDictDTO> searchChargeDicts(String chargeName, Boolean isEnabled) {
        List<ChargeDict> dicts = chargeDictRepository.findChargeDicts(chargeName, isEnabled);
        return ChargeDictMapper.INSTANCE.toDTOList(dicts);
    }

    /**
     * 根据ID获取费用字典
     */
    public ChargeDictDTO getChargeDictById(Long id) {
        ChargeDict dict = chargeDictRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("费用字典不存在"));
        return ChargeDictMapper.INSTANCE.toDTO(dict);
    }

    /**
     * 根据费用编码获取费用字典
     */
    public ChargeDictDTO getChargeDictByCode(String chargeCode) {
        ChargeDict dict = chargeDictRepository.findByChargeCodeAndDeletedFalse(chargeCode)
            .orElseThrow(() -> new RuntimeException("费用字典不存在"));
        return ChargeDictMapper.INSTANCE.toDTO(dict);
    }

    /**
     * 创建费用字典
     */
    @Transactional
    public ChargeDictDTO createChargeDict(ChargeDictCreateRequest request) {
        // 检查费用编码是否已存在
        if (chargeDictRepository.existsByChargeCodeAndDeletedFalse(request.getChargeCode())) {
            throw new RuntimeException("费用编码已存在");
        }

        ChargeDict dict = new ChargeDict();
        dict.setChargeCode(request.getChargeCode());
        dict.setChargeName(request.getChargeName());
        dict.setDefaultTaxRate(request.getDefaultTaxRate());
        dict.setIsEnabled(request.getIsEnabled());
        dict.setRemark(request.getRemark());
        dict.setCreatedTime(LocalDateTime.now());
        dict.setUpdatedTime(LocalDateTime.now());

        dict = chargeDictRepository.save(dict);

        log.info("创建费用字典：{} - {}", request.getChargeCode(), request.getChargeName());

        return ChargeDictMapper.INSTANCE.toDTO(dict);
    }

    /**
     * 更新费用字典
     */
    @Transactional
    public ChargeDictDTO updateChargeDict(Long id, ChargeDictCreateRequest request) {
        ChargeDict dict = chargeDictRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("费用字典不存在"));

        // 检查费用编码是否已被其他记录使用
        if (!dict.getChargeCode().equals(request.getChargeCode()) && 
            chargeDictRepository.existsByChargeCodeAndDeletedFalse(request.getChargeCode())) {
            throw new RuntimeException("费用编码已存在");
        }

        dict.setChargeCode(request.getChargeCode());
        dict.setChargeName(request.getChargeName());
        dict.setDefaultTaxRate(request.getDefaultTaxRate());
        dict.setIsEnabled(request.getIsEnabled());
        dict.setRemark(request.getRemark());
        dict.setUpdatedTime(LocalDateTime.now());

        dict = chargeDictRepository.save(dict);

        log.info("更新费用字典：{} - {}", request.getChargeCode(), request.getChargeName());

        return ChargeDictMapper.INSTANCE.toDTO(dict);
    }

    /**
     * 删除费用字典
     */
    @Transactional
    public void deleteChargeDict(Long id) {
        ChargeDict dict = chargeDictRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("费用字典不存在"));

        dict.setDeleted(1);
        dict.setUpdatedTime(LocalDateTime.now());
        chargeDictRepository.save(dict);

        log.info("删除费用字典：{} - {}", dict.getChargeCode(), dict.getChargeName());
    }

    /**
     * 启用/禁用费用字典
     */
    @Transactional
    public void toggleChargeDictStatus(Long id) {
        ChargeDict dict = chargeDictRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("费用字典不存在"));

        dict.setIsEnabled(!dict.getIsEnabled());
        dict.setUpdatedTime(LocalDateTime.now());
        chargeDictRepository.save(dict);

        log.info("{}费用字典：{} - {}", dict.getIsEnabled() ? "启用" : "禁用", 
                dict.getChargeCode(), dict.getChargeName());
    }
}
