# 拣货管理功能说明

## 功能概述

拣货管理是WMS系统中拣货作业的核心功能，用于管理拣货任务的执行、进度跟踪和完成确认。

## 主要功能

### 1. 拣货任务查询
- **多条件筛选**：支持按任务编号、波次号、出库单ID、状态等条件筛选
- **分页显示**：支持分页查询，提高大数据量下的性能
- **实时状态**：显示任务的实时状态和进度

### 2. 拣货任务状态管理
- **待拣选**：任务已创建，等待开始执行
- **部分完成**：任务已开始执行，但未完全完成
- **已完成**：任务已完全完成

### 3. 拣货操作
- **开始拣货**：将待拣选状态的任务转为部分完成
- **完成拣货**：输入实际拣货数量，更新任务状态
- **数量验证**：确保拣货数量不超过剩余数量

### 4. 库存管理
- **自动扣减**：完成拣货后自动扣减对应库存
- **库存验证**：拣货前验证库存是否充足
- **位置管理**：支持从指定库位拣货

## 数据库设计

### 拣货任务表 (picking_task)
```sql
CREATE TABLE picking_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_no VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编号',
    wave_no VARCHAR(50) COMMENT '波次号',
    picking_wave_id BIGINT COMMENT '波次ID',
    outbound_order_id BIGINT NOT NULL COMMENT '出库单ID',
    product_sku_id BIGINT NOT NULL COMMENT '商品SKU ID',
    from_location_id BIGINT NOT NULL COMMENT '拣货库位ID',
    quantity INT NOT NULL COMMENT '需拣选数量',
    status INT DEFAULT 1 COMMENT '状态（1：待拣选，2：部分完成，3：已完成）',
    picked_quantity INT DEFAULT 0 COMMENT '已拣选数量',
    -- 基础字段
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP NULL,
    deleted TINYINT DEFAULT 0
);
```

## API 接口

### 1. 获取拣货任务列表
```
GET /picking-tasks
参数：
- page: 页码
- size: 每页大小
- taskNo: 任务编号（模糊查询）
- waveNo: 波次号（模糊查询）
- outboundOrderId: 出库单ID
- productSkuId: 商品SKU ID
- fromLocationId: 拣货库位ID
- status: 状态
- startTime: 开始时间
- endTime: 结束时间
```

### 2. 获取拣货任务详情
```
GET /picking-tasks/{id}
```

### 3. 开始拣货
```
POST /picking-tasks/{id}/start
```

### 4. 完成拣货
```
POST /picking-tasks/complete
Body: {
    "taskId": 1,
    "pickedQuantity": 10,
    "remark": "备注"
}
```

### 5. 为出库单创建拣货任务
```
POST /picking-tasks/create-for-order/{outboundOrderId}
```

## 前端页面

### 页面路径
- 前端页面：`/outbound/picking`
- 路由配置：`wms-web/src/router/index.ts`

### 主要功能
1. **搜索筛选**：支持按任务编号、波次号、出库单ID、状态等条件筛选
2. **任务列表**：显示任务的基本信息、进度和状态
3. **操作按钮**：
   - 开始拣货（待拣选状态）
   - 完成拣货（部分完成状态）
   - 已完成（已完成状态，禁用）

### 状态显示
- 待拣选：橙色标签
- 部分完成：蓝色标签
- 已完成：绿色标签

### 数量显示
- 需拣数量：任务的总数量
- 已拣数量：已经拣选的数量
- 剩余数量：还需拣选的数量（红色高亮显示）

## 业务流程

1. **任务创建**：系统根据出库单自动创建拣货任务
2. **开始拣货**：操作员开始执行拣货任务
3. **拣货作业**：操作员到指定库位进行拣货
4. **完成确认**：输入实际拣货数量，系统更新任务状态
5. **库存扣减**：系统自动扣减对应库存
6. **进度更新**：更新波次完成情况

## 核心特性

### 1. 智能数量管理
- 支持部分完成：可以分多次完成一个任务
- 数量验证：确保拣货数量不超过剩余数量
- 自动计算：自动计算剩余数量

### 2. 库存联动
- 实时库存：拣货时实时检查库存
- 自动扣减：完成拣货后自动扣减库存
- 位置管理：支持从指定库位拣货

### 3. 波次关联
- 波次进度：自动更新波次完成情况
- 任务统计：统计波次下的任务完成情况
- 状态同步：任务状态变化时同步更新波次状态

### 4. 用户体验
- 直观界面：清晰显示任务状态和进度
- 便捷操作：一键开始、完成拣货
- 数量输入：智能提示剩余数量

## 注意事项

1. 只有待拣选状态的任务才能开始拣货
2. 只有部分完成状态的任务才能完成拣货
3. 拣货数量不能超过剩余数量
4. 完成拣货前会验证库存是否充足
5. 任务完成后会自动更新库存和波次进度

## 扩展功能

未来可以考虑添加以下功能：
1. 拣货路径优化
2. 拣货员工作量统计
3. 拣货异常处理
4. 拣货质量检查
5. 拣货效率分析
6. 移动端拣货应用
