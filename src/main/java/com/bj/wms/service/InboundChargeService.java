package com.bj.wms.service;

import com.bj.wms.dto.InboundChargeCreateRequest;
import com.bj.wms.dto.InboundChargeDTO;
import com.bj.wms.entity.InboundCharge;
import com.bj.wms.entity.InboundOrder;
import com.bj.wms.mapper.InboundChargeMapper;
import com.bj.wms.repository.InboundChargeRepository;
import com.bj.wms.repository.InboundOrderRepository;
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
 * 入库费用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboundChargeService {

    private final InboundChargeRepository inboundChargeRepository;
    private final InboundOrderRepository inboundOrderRepository;

    /**
     * 分页查询入库费用
     */
    public Page<InboundChargeDTO> getChargeList(Long inboundOrderId, String inboundOrderNo, Long chargeType,
                                               LocalDateTime startTime, LocalDateTime endTime,
                                               Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<InboundCharge> pageResult = inboundChargeRepository.findCharges(
            inboundOrderId, inboundOrderNo, chargeType, startTime, endTime, pageable);

        return pageResult.map(InboundChargeMapper.INSTANCE::toDTO);
    }

    /**
     * 根据入库单ID查询费用
     */
    public List<InboundChargeDTO> getChargesByOrderId(Long inboundOrderId) {
        List<InboundCharge> charges = inboundChargeRepository.findByInboundOrderIdAndDeletedFalse(inboundOrderId);
        return InboundChargeMapper.INSTANCE.toDTOList(charges);
    }

    /**
     * 创建入库费用
     */
    @Transactional
    public InboundChargeDTO createCharge(InboundChargeCreateRequest request) {
        // 验证入库单是否存在
        InboundOrder inboundOrder = inboundOrderRepository.findById(request.getInboundOrderId())
            .orElseThrow(() -> new RuntimeException("入库单不存在"));

        // 创建费用记录
        InboundCharge charge = new InboundCharge();
        charge.setInboundOrderId(request.getInboundOrderId());
        charge.setChargeType(request.getChargeType());
        charge.setAmount(request.getAmount());
        charge.setTaxRate(request.getTaxRate());
        charge.setCurrency(request.getCurrency());
        charge.setRemark(request.getRemark());

        charge = inboundChargeRepository.save(charge);

        log.info("为入库单 {} 创建费用记录，金额：{}", inboundOrder.getOrderNo(), request.getAmount());

        return InboundChargeMapper.INSTANCE.toDTO(charge);
    }

    /**
     * 更新入库费用
     */
    @Transactional
    public InboundChargeDTO updateCharge(Long id, InboundChargeCreateRequest request) {
        InboundCharge charge = inboundChargeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("费用记录不存在"));

        charge.setChargeType(request.getChargeType());
        charge.setAmount(request.getAmount());
        charge.setTaxRate(request.getTaxRate());
        charge.setCurrency(request.getCurrency());
        charge.setRemark(request.getRemark());

        charge = inboundChargeRepository.save(charge);

        log.info("更新费用记录 {}，金额：{}", id, request.getAmount());

        return InboundChargeMapper.INSTANCE.toDTO(charge);
    }

    /**
     * 删除入库费用
     */
    @Transactional
    public void deleteCharge(Long id) {
        InboundCharge charge = inboundChargeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("费用记录不存在"));

        charge.setDeleted(1);
        inboundChargeRepository.save(charge);

        log.info("删除费用记录 {}", id);
    }

    /**
     * 批量创建入库费用
     */
    @Transactional
    public List<InboundChargeDTO> batchCreateCharges(Long inboundOrderId, List<InboundChargeCreateRequest> requests) {
        // 验证入库单是否存在
        InboundOrder inboundOrder = inboundOrderRepository.findById(inboundOrderId)
            .orElseThrow(() -> new RuntimeException("入库单不存在"));

        List<InboundCharge> charges = requests.stream().map(request -> {
            InboundCharge charge = new InboundCharge();
            charge.setInboundOrderId(inboundOrderId);
            charge.setChargeType(request.getChargeType());
            charge.setAmount(request.getAmount());
            charge.setTaxRate(request.getTaxRate());
            charge.setCurrency(request.getCurrency());
            charge.setRemark(request.getRemark());
            return charge;
        }).toList();

        charges = inboundChargeRepository.saveAll(charges);

        log.info("为入库单 {} 批量创建 {} 条费用记录", inboundOrder.getOrderNo(), charges.size());

        return InboundChargeMapper.INSTANCE.toDTOList(charges);
    }
}
