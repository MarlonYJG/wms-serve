# 复核管理功能说明

## 功能概述

复核管理是WMS系统中出库作业的重要环节，用于验证拣货结果的准确性，确保出库商品的数量和质量符合要求。

## 主要功能

### 1. 复核任务管理
- **任务创建**：系统根据已完成的拣货任务自动创建复核任务
- **任务查询**：支持多条件筛选和分页查询
- **状态跟踪**：实时跟踪复核任务的执行状态

### 2. 复核状态管理
- **待复核**：任务已创建，等待开始复核
- **复核中**：复核员正在执行复核作业
- **复核完成**：复核通过，数量一致
- **复核异常**：复核发现数量不一致或其他问题

### 3. 复核操作
- **开始复核**：将待复核状态的任务转为复核中
- **完成复核**：输入实际数量，系统自动判断复核结果
- **异常处理**：对复核异常进行标记和处理

### 4. 数量验证
- **预期数量**：基于拣货任务的已拣数量
- **实际数量**：复核员实际清点的数量
- **自动判断**：系统自动判断数量是否一致

## 数据库设计

### 复核任务表 (review_task)
```sql
CREATE TABLE review_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_no VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编号',
    outbound_order_id BIGINT NOT NULL COMMENT '出库单ID',
    product_sku_id BIGINT NOT NULL COMMENT '商品SKU ID',
    expected_quantity INT NOT NULL COMMENT '预期数量',
    actual_quantity INT DEFAULT 0 COMMENT '实际数量',
    status INT DEFAULT 1 COMMENT '状态（1：待复核，2：复核中，3：复核完成，4：复核异常）',
    reviewer_id BIGINT COMMENT '复核员ID',
    reviewer_name VARCHAR(50) COMMENT '复核员姓名',
    review_time TIMESTAMP NULL COMMENT '复核时间',
    remark VARCHAR(500) COMMENT '备注',
    -- 基础字段
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP NULL,
    deleted TINYINT DEFAULT 0
);
```

## API 接口

### 1. 获取复核任务列表
```
GET /review-tasks
参数：
- page: 页码
- size: 每页大小
- taskNo: 任务编号（模糊查询）
- outboundOrderNo: 出库单号（模糊查询）
- outboundOrderId: 出库单ID
- productSkuId: 商品SKU ID
- status: 状态
- startTime: 开始时间
- endTime: 结束时间
```

### 2. 获取复核任务详情
```
GET /review-tasks/{id}
```

### 3. 开始复核
```
POST /review-tasks/{id}/start
```

### 4. 完成复核
```
POST /review-tasks/complete
Body: {
    "taskId": 1,
    "actualQuantity": 10,
    "remark": "备注"
}
```

### 5. 为出库单创建复核任务
```
POST /review-tasks/create-for-order/{outboundOrderId}
```

## 前端页面

### 页面路径
- 前端页面：`/outbound/review`
- 路由配置：`wms-web/src/router/index.ts`

### 主要功能
1. **搜索筛选**：支持按任务编号、出库单号、状态等条件筛选
2. **任务列表**：显示任务的基本信息、数量和状态
3. **操作按钮**：
   - 开始复核（待复核状态）
   - 完成复核（复核中状态）
   - 已完成（复核完成状态，禁用）

### 状态显示
- 待复核：橙色标签
- 复核中：蓝色标签
- 复核完成：绿色标签
- 复核异常：红色标签

### 数量显示
- 预期数量：基于拣货任务的已拣数量
- 实际数量：复核员输入的实际数量
- 数量对比：不一致时红色高亮显示

## 业务流程

1. **任务创建**：系统根据已完成的拣货任务自动创建复核任务
2. **开始复核**：复核员开始执行复核任务
3. **复核作业**：复核员清点实际数量
4. **完成确认**：输入实际数量，系统判断复核结果
5. **异常处理**：对复核异常进行标记和处理

## 核心特性

### 1. 智能数量验证
- 自动对比预期数量和实际数量
- 智能判断复核结果（完成/异常）
- 提供详细的复核反馈

### 2. 状态管理
- 严格的状态流转控制
- 实时状态更新
- 状态历史记录

### 3. 异常处理
- 自动标记复核异常
- 异常情况记录和跟踪
- 支持异常处理流程

### 4. 用户体验
- 直观的数量对比显示
- 智能的输入提示
- 清晰的状态反馈

## 业务规则

### 1. 任务创建规则
- 只有已完成的拣货任务才能创建复核任务
- 每个拣货任务对应一个复核任务
- 避免重复创建复核任务

### 2. 状态流转规则
- 待复核 → 复核中 → 复核完成/复核异常
- 只有待复核状态的任务才能开始复核
- 只有复核中状态的任务才能完成复核

### 3. 数量验证规则
- 实际数量不能小于0
- 系统自动判断数量是否一致
- 不一致时标记为复核异常

### 4. 异常处理规则
- 复核异常需要人工处理
- 记录异常原因和备注
- 支持异常处理流程

## 注意事项

1. 只有已完成的拣货任务才能创建复核任务
2. 复核任务的状态流转是单向的
3. 实际数量不能小于0
4. 复核异常需要及时处理
5. 复核完成后会记录复核员和复核时间

## 扩展功能

未来可以考虑添加以下功能：
1. 复核质量评分
2. 复核效率统计
3. 复核异常分析
4. 复核员工作量统计
5. 复核路径优化
6. 移动端复核应用
7. 复核照片上传
8. 复核结果打印
