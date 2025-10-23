package com.bj.wms;

import com.bj.wms.dto.PackingTaskCreateRequest;
import com.bj.wms.dto.PackingTaskDTO;
import com.bj.wms.dto.PackingTaskQueryRequest;
import com.bj.wms.entity.OutboundOrder;
import com.bj.wms.entity.PackingMaterial;
import com.bj.wms.repository.OutboundOrderRepository;
import com.bj.wms.repository.PackingMaterialRepository;
import com.bj.wms.service.PackingTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 打包任务服务测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PackingTaskServiceTest {

    @Autowired
    private PackingTaskService packingTaskService;

    @Autowired
    private OutboundOrderRepository outboundOrderRepository;

    @Autowired
    private PackingMaterialRepository packingMaterialRepository;

    @Test
    public void testCreatePackingTask() {
        // 创建测试数据
        OutboundOrder order = new OutboundOrder();
        order.setOrderNo("TEST001");
        order.setWarehouseId(1L);
        order.setCustomerId(1L);
        order.setStatus(3); // 拣货中状态
        order = outboundOrderRepository.save(order);

        PackingMaterial material = new PackingMaterial();
        material.setMaterialCode("BOX001");
        material.setMaterialName("标准纸箱");
        material.setMaterialType(1);
        material.setIsEnabled(true);
        material = packingMaterialRepository.save(material);

        // 创建打包任务请求
        PackingTaskCreateRequest request = new PackingTaskCreateRequest();
        request.setOutboundOrderId(order.getId());
        request.setPackingMaterialId(material.getId());
        request.setRemark("测试打包任务");

        // 执行测试
        PackingTaskDTO result = packingTaskService.createPackingTask(request);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getTaskNo());
        assertEquals(order.getId(), result.getOutboundOrderId());
        assertEquals(material.getId(), result.getPackingMaterialId());
        assertEquals(1, result.getStatus()); // 待打包状态
        assertEquals("测试打包任务", result.getRemark());
    }

    @Test
    public void testGetPackingTaskList() {
        // 创建查询请求
        PackingTaskQueryRequest request = new PackingTaskQueryRequest();
        request.setPage(1);
        request.setSize(10);

        // 执行测试
        Page<PackingTaskDTO> result = packingTaskService.getPackingTaskList(request);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 0);
    }
}
