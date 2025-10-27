package com.bj.wms.service;

import com.bj.wms.dto.OutboundChargeCreateRequest;
import com.bj.wms.dto.OutboundChargeDTO;
import com.bj.wms.entity.OutboundCharge;
import com.bj.wms.entity.OutboundOrder;
import com.bj.wms.mapper.OutboundChargeMapper;
import com.bj.wms.repository.OutboundChargeRepository;
import com.bj.wms.repository.OutboundOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库费用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundChargeService {

    private final OutboundChargeRepository outboundChargeRepository;
    private final OutboundOrderRepository outboundOrderRepository;

    /**
     * 分页查询出库费用
     */
    public Page<OutboundChargeDTO> getChargeList(Long outboundOrderId, String outboundOrderNo, Long chargeType, 
                                               LocalDateTime startTime, LocalDateTime endTime,
                                               Integer page, Integer size) {
        // 确保分页参数有效
        int validPage = Math.max(1, page);
        int validSize = Math.max(1, Math.min(1000, size)); // 限制最大页面大小为1000
        Pageable pageable = PageRequest.of(validPage - 1, validSize);
        
        Page<OutboundCharge> pageResult = outboundChargeRepository.findCharges(
            outboundOrderId, outboundOrderNo, chargeType, startTime, endTime, pageable);
        
        return pageResult.map(OutboundChargeMapper.INSTANCE::toDTO);
    }

    /**
     * 根据出库单ID查询费用
     */
    public List<OutboundChargeDTO> getChargesByOutboundOrderId(Long outboundOrderId) {
        List<OutboundCharge> charges = outboundChargeRepository.findByOutboundOrderIdAndDeletedFalse(outboundOrderId);
        return OutboundChargeMapper.INSTANCE.toDTOList(charges);
    }

    /**
     * 创建出库费用
     */
    @Transactional
    public OutboundChargeDTO createCharge(OutboundChargeCreateRequest request) {
        // 验证出库单是否存在
        OutboundOrder outboundOrder = outboundOrderRepository.findById(request.getOutboundOrderId())
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        // 创建费用记录
        OutboundCharge charge = new OutboundCharge();
        charge.setOutboundOrderId(request.getOutboundOrderId());
        charge.setChargeType(request.getChargeType());
        charge.setAmount(request.getAmount());
        charge.setTaxRate(request.getTaxRate());
        charge.setCurrency(request.getCurrency());
        charge.setRemark(request.getRemark());
        charge.setCreatedTime(LocalDateTime.now());
        charge.setUpdatedTime(LocalDateTime.now());

        charge = outboundChargeRepository.save(charge);

        log.info("为出库单 {} 创建费用记录，金额：{}", outboundOrder.getOrderNo(), request.getAmount());

        return OutboundChargeMapper.INSTANCE.toDTO(charge);
    }

    /**
     * 更新出库费用
     */
    @Transactional
    public OutboundChargeDTO updateCharge(Long id, OutboundChargeCreateRequest request) {
        OutboundCharge charge = outboundChargeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("费用记录不存在"));

        charge.setChargeType(request.getChargeType());
        charge.setAmount(request.getAmount());
        charge.setTaxRate(request.getTaxRate());
        charge.setCurrency(request.getCurrency());
        charge.setRemark(request.getRemark());
        charge.setUpdatedTime(LocalDateTime.now());

        charge = outboundChargeRepository.save(charge);

        log.info("更新费用记录 {}，金额：{}", id, request.getAmount());

        return OutboundChargeMapper.INSTANCE.toDTO(charge);
    }

    /**
     * 删除出库费用
     */
    @Transactional
    public void deleteCharge(Long id) {
        OutboundCharge charge = outboundChargeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("费用记录不存在"));

        charge.setDeleted(1);
        charge.setUpdatedTime(LocalDateTime.now());
        outboundChargeRepository.save(charge);

        log.info("删除费用记录 {}", id);
    }

    /**
     * 批量创建出库费用
     */
    @Transactional
    public List<OutboundChargeDTO> batchCreateCharges(Long outboundOrderId, List<OutboundChargeCreateRequest> requests) {
        // 验证出库单是否存在
        OutboundOrder outboundOrder = outboundOrderRepository.findById(outboundOrderId)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        List<OutboundCharge> charges = requests.stream().map(request -> {
            OutboundCharge charge = new OutboundCharge();
            charge.setOutboundOrderId(outboundOrderId);
            charge.setChargeType(request.getChargeType());
            charge.setAmount(request.getAmount());
            charge.setTaxRate(request.getTaxRate());
            charge.setCurrency(request.getCurrency());
            charge.setRemark(request.getRemark());
            charge.setCreatedTime(LocalDateTime.now());
            charge.setUpdatedTime(LocalDateTime.now());
            return charge;
        }).toList();

        charges = outboundChargeRepository.saveAll(charges);

        log.info("为出库单 {} 批量创建 {} 条费用记录", outboundOrder.getOrderNo(), charges.size());

        return OutboundChargeMapper.INSTANCE.toDTOList(charges);
    }
}
