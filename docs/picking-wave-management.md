# 波次管理功能说明

## 功能概述

波次管理是WMS系统中拣货作业的核心功能，用于将多个出库单的拣货任务组织成批次，提高拣货效率。

## 主要功能

### 1. 波次创建
- **手动创建**：用户可以手动创建波次，指定波次号和仓库
- **自动生成**：系统根据待分配库存的出库单自动生成波次

### 2. 波次状态管理
- **待执行**：波次已创建，等待开始执行
- **执行中**：波次已开始执行，拣货任务进行中
- **已完成**：所有拣货任务已完成，波次结束

### 3. 波次操作
- **开始执行**：将待执行状态的波次转为执行中
- **完成波次**：将执行中状态的波次标记为已完成
- **查看详情**：查看波次的详细信息

## 数据库设计

### 波次表 (picking_wave)
```sql
CREATE TABLE picking_wave (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wave_no VARCHAR(50) NOT NULL UNIQUE COMMENT '波次号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    status INT DEFAULT 1 COMMENT '状态（1：待执行，2：执行中，3：已完成）',
    order_count INT DEFAULT 0 COMMENT '包含的订单数量',
    task_count INT DEFAULT 0 COMMENT '包含的任务数量',
    completed_task_count INT DEFAULT 0 COMMENT '已完成的任务数量',
    started_time TIMESTAMP NULL COMMENT '开始执行时间',
    completed_time TIMESTAMP NULL COMMENT '完成时间',
    operator_id BIGINT COMMENT '操作员ID',
    operator_name VARCHAR(50) COMMENT '操作员姓名',
    remark VARCHAR(500) COMMENT '备注',
    -- 基础字段
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP NULL,
    deleted TINYINT DEFAULT 0
);
```

### 拣货任务表更新
在原有的 `picking_task` 表中添加了 `picking_wave_id` 字段，用于关联波次：

```sql
ALTER TABLE picking_task ADD COLUMN picking_wave_id BIGINT COMMENT '波次ID';
ALTER TABLE picking_task ADD FOREIGN KEY (picking_wave_id) REFERENCES picking_wave(id);
```

## API 接口

### 1. 获取波次列表
```
GET /picking-waves
参数：
- page: 页码
- size: 每页大小
- waveNo: 波次号（模糊查询）
- warehouseId: 仓库ID
- status: 状态
- startTime: 开始时间
- endTime: 结束时间
```

### 2. 获取波次详情
```
GET /picking-waves/{id}
```

### 3. 创建波次
```
POST /picking-waves
Body: {
    "waveNo": "WAVE001",
    "warehouseId": 1,
    "remark": "备注"
}
```

### 4. 开始执行波次
```
POST /picking-waves/{id}/start
```

### 5. 完成波次
```
POST /picking-waves/{id}/complete
```

### 6. 删除波次
```
DELETE /picking-waves/{id}
```

### 7. 自动生成波次
```
POST /picking-waves/auto-generate?warehouseId=1
```

## 前端页面

### 页面路径
- 前端页面：`/outbound/waves`
- 路由配置：`wms-web/src/router/index.ts`

### 主要功能
1. **搜索筛选**：支持按波次号、状态等条件筛选
2. **波次列表**：显示波次的基本信息和完成率
3. **操作按钮**：
   - 自动生成波次
   - 创建波次
   - 开始执行
   - 完成波次
   - 查看详情

### 状态显示
- 待执行：橙色标签
- 执行中：蓝色标签
- 已完成：绿色标签

## 业务流程

1. **波次创建**：系统管理员或操作员创建波次
2. **任务分配**：将拣货任务分配到波次中
3. **开始执行**：操作员开始执行波次
4. **拣货作业**：按照波次进行拣货作业
5. **完成波次**：所有任务完成后标记波次完成

## 注意事项

1. 只有待执行状态的波次才能开始执行
2. 只有执行中状态的波次才能完成
3. 只有待执行状态的波次才能删除
4. 删除波次前需要确保没有关联的拣货任务
5. 自动生成波次会根据待分配库存的出库单创建

## 扩展功能

未来可以考虑添加以下功能：
1. 波次优先级设置
2. 波次执行路径优化
3. 波次执行时间预估
4. 波次执行进度实时监控
5. 波次执行异常处理
