# 出库管理API测试指南

## 测试环境准备

1. 启动后端服务：
```bash
cd wms-serve
mvn spring-boot:run
```

2. 确保数据库中有测试数据：
   - 仓库数据
   - 客户数据
   - 商品SKU数据
   - 库存数据

## API测试用例

### 1. 创建出库单

**请求：**
```bash
curl -X POST http://localhost:8080/outbound-order \
  -H "Content-Type: application/json" \
  -d '{
    "warehouseId": 1,
    "customerId": 1,
    "customerInfo": "测试客户信息",
    "items": [
      {
        "productSkuId": 1,
        "quantity": 10
      }
    ]
  }'
```

**预期响应：**
```json
{
  "id": 1,
  "orderNo": "OUT20241201XXXX",
  "warehouseId": 1,
  "customerId": 1,
  "status": 1,
  "statusName": "待处理",
  "createdTime": "2024-12-01T10:00:00"
}
```

### 2. 查询出库单列表

**请求：**
```bash
curl -X GET "http://localhost:8080/outbound-order?page=1&size=10"
```

### 3. 获取出库单详情

**请求：**
```bash
curl -X GET http://localhost:8080/outbound-order/1
```

### 4. 分配库存

**请求：**
```bash
curl -X POST http://localhost:8080/outbound-order/1/allocate
```

### 5. 生成拣货任务

**请求：**
```bash
curl -X POST http://localhost:8080/outbound-order/1/generate-picking-tasks
```

### 6. 获取拣货任务列表

**请求：**
```bash
curl -X GET http://localhost:8080/outbound-order/1/picking-tasks
```

### 7. 完成拣货任务

**请求：**
```bash
curl -X POST http://localhost:8080/picking-task/1/complete \
  -H "Content-Type: application/json" \
  -d '{
    "pickedQuantity": 10
  }'
```

### 8. 确认发货

**请求：**
```bash
curl -X POST http://localhost:8080/outbound-order/1/ship \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "SF1234567890"
  }'
```

## 测试流程

1. **创建出库单** → 状态：待处理
2. **分配库存** → 状态：已分配库存
3. **生成拣货任务** → 状态：拣货中
4. **完成拣货任务** → 拣货任务状态：已完成
5. **确认发货** → 状态：已发货

## 预期结果

- 所有API调用返回200状态码
- 出库单状态按预期流转
- 库存正确扣减
- 拣货任务正确生成和完成
