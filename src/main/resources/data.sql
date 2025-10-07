-- WMS 全流程两天模拟数据（覆盖各状态）
SET NAMES utf8mb4;
SET time_zone = '+08:00';

SET @today := DATE(NOW());
SET @yday  := DATE_SUB(@today, INTERVAL 1 DAY);

-- 可重复执行：清空与重建
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE picking_task;
TRUNCATE TABLE outbound_order_item;
TRUNCATE TABLE outbound_order;
TRUNCATE TABLE inventory_transaction;
TRUNCATE TABLE inventory;
TRUNCATE TABLE putaway_task;
TRUNCATE TABLE inbound_order_item;
TRUNCATE TABLE inbound_order;
TRUNCATE TABLE storage_location;
TRUNCATE TABLE storage_zone;
TRUNCATE TABLE product_sku;
TRUNCATE TABLE users;
TRUNCATE TABLE customer;
TRUNCATE TABLE supplier;
TRUNCATE TABLE warehouse;
SET FOREIGN_KEY_CHECKS = 1;

-- 1) 基础主数据
INSERT INTO supplier (supplier_name, supplier_code, contact_person, phone, email, address, rating, is_enabled, created_time) VALUES
('华北电子', 'SUP001', '张三', '010-10000001', 'sales@north.cn', '北京海淀', 2, 1, CONCAT(@yday,' 09:00:00')),
('华东制造', 'SUP002', '李四', '021-10000002', 'sales@east.cn',  '上海浦东', 3, 1, CONCAT(@yday,' 09:00:00'));

INSERT INTO customer (customer_name, customer_code, customer_type, contact_person, phone, email, address, credit_rating, credit_limit, is_enabled, created_time) VALUES
('京东商城', 'CUST001', 2, '王一', '400-000-0001', 'jd@corp.cn', '北京朝阳', 3, 500000.00, 1, CONCAT(@yday,' 09:00:00')),
('天猫超市', 'CUST002', 2, '赵六', '400-000-0002', 'tmall@corp.cn','杭州余杭', 3, 600000.00, 1, CONCAT(@yday,' 09:00:00'));

INSERT INTO warehouse (name, code, address, is_enabled, created_time) VALUES
('北京主仓', 'WH001', '北京通州物流园', 1, CONCAT(@yday,' 09:05:00'));

-- zone: 2收货/1存储/6拣货/3发货
INSERT INTO storage_zone (warehouse_id, zone_code, zone_name, zone_type, capacity, used_capacity, is_enabled, created_time) VALUES
(1, 'BJ-RCV',  '收货区', 2, 1000.00, 0.00, 1, CONCAT(@yday,' 09:10:00')),
(1, 'BJ-A',    '存储区A', 1, 5000.00, 0.00, 1, CONCAT(@yday,' 09:10:00')),
(1, 'BJ-PICK', '拣货区', 6, 2000.00, 0.00, 1, CONCAT(@yday,' 09:10:00')),
(1, 'BJ-SHIP', '发货区', 3, 1000.00, 0.00, 1, CONCAT(@yday,' 09:10:00'));

INSERT INTO storage_location (zone_id, location_code, location_name, location_type, capacity, current_volume, status, created_time) VALUES
(1, 'BJ-RCV-01',  '收货暂存01', 2, 100.00, 0.00, 1, CONCAT(@yday,' 09:15:00')),
(2, 'BJ-A-01-01', 'A区01排01层01位', 1, 50.00, 0.00, 1, CONCAT(@yday,' 09:15:00')),
(2, 'BJ-A-01-02', 'A区01排01层02位', 1, 50.00, 0.00, 1, CONCAT(@yday,' 09:15:00')),
(3, 'BJ-PICK-01','拣货位01', 1, 30.00, 0.00, 1, CONCAT(@yday,' 09:15:00')),
(4, 'BJ-SHIP-01','发货暂存01', 2, 20.00, 0.00, 1, CONCAT(@yday,' 09:15:00'));

INSERT INTO product_sku (sku_code, sku_name, specification, brand, category_id, supplier_id, barcode, weight, volume, is_batch_managed, is_expiry_managed, shelf_life_days, safety_stock, is_enabled, created_time) VALUES
('SKU1001', '智能手环', '黑色 标准款', '智品', NULL, 1, '690000100001', 0.0500, 0.0008, 0, 0, NULL, 100, 1, CONCAT(@yday,' 09:20:00')),
('SKU1002', '蓝牙耳机', '降噪版',     '声浪', NULL, 1, '690000100002', 0.0300, 0.0006, 0, 0, NULL, 100, 1, CONCAT(@yday,' 09:20:00')),
('SKU1003', '有机大米', '5kg',        '稻香', NULL, 2, '690000100003', 5.0000, 0.0080, 1, 1, 365, 50, 1, CONCAT(@yday,' 09:20:00'));

-- 2) 入库域：构造 4 种状态（1待收货/2部分收货/3已完成/4已取消）
-- 已完成（3）：昨日完成并上架
INSERT INTO inbound_order (order_no, warehouse_id, supplier_id, status, total_expected_quantity, total_received_quantity, created_time) VALUES
(CONCAT('IN', DATE_FORMAT(@yday,'%Y%m%d'),'001'), 1, 1, 3, 160, 160, CONCAT(@yday,' 10:00:00')),
-- 部分收货（2）：收到一部分
(CONCAT('IN', DATE_FORMAT(@yday,'%Y%m%d'),'002'), 1, 2, 2, 100, 60, CONCAT(@yday,' 11:00:00')),
-- 待收货（1）：今天创建未收
(CONCAT('IN', DATE_FORMAT(@today,'%Y%m%d'),'003'), 1, 1, 1, 80, 0, CONCAT(@today,' 09:00:00')),
-- 已取消（4）
(CONCAT('IN', DATE_FORMAT(@today,'%Y%m%d'),'004'), 1, 2, 4, 50, 0, CONCAT(@today,' 09:10:00'));

INSERT INTO inbound_order_item (inbound_order_id, product_sku_id, expected_quantity, received_quantity, created_time) VALUES
-- IN001 完成
(1, 1, 60, 60, CONCAT(@yday,' 10:05:00')),
(1, 2, 50, 50, CONCAT(@yday,' 10:05:00')),
(1, 3, 50, 50, CONCAT(@yday,' 10:05:00')),
-- IN002 部分收货（只收两行）
(2, 1, 50, 30, CONCAT(@yday,' 11:05:00')),
(2, 2, 50, 30, CONCAT(@yday,' 11:05:00')),
(2, 3, 0,   0,  CONCAT(@yday,' 11:05:00')),
-- IN003 待收货
(3, 1, 50, 0, CONCAT(@today,' 09:05:00')),
(3, 2, 30, 0, CONCAT(@today,' 09:05:00'));

-- 上架任务（1待执行/2执行中/3已完成/4已取消）
INSERT INTO putaway_task (task_no, inbound_order_item_id, from_location_id, to_location_id, quantity, status, operator, completed_time, created_time) VALUES
-- 对应 IN001：完成
(CONCAT('PUT',DATE_FORMAT(@yday,'%Y%m%d'),'001'), 1, 1, 2, 60, 3, 1, CONCAT(@yday,' 10:30:00'), CONCAT(@yday,' 10:10:00')),
(CONCAT('PUT',DATE_FORMAT(@yday,'%Y%m%d'),'002'), 2, 1, 3, 50, 3, 1, CONCAT(@yday,' 10:35:00'), CONCAT(@yday,' 10:10:00')),
(CONCAT('PUT',DATE_FORMAT(@yday,'%Y%m%d'),'003'), 3, 1, 2, 50, 3, 1, CONCAT(@yday,' 10:40:00'), CONCAT(@yday,' 10:10:00')),
-- 对应 IN002：一条执行中、一条待执行、一条取消
(CONCAT('PUT',DATE_FORMAT(@yday,'%Y%m%d'),'004'), 4, 1, 2, 30, 2, 2, NULL, CONCAT(@yday,' 11:10:00')),
(CONCAT('PUT',DATE_FORMAT(@yday,'%Y%m%d'),'005'), 5, 1, 3, 30, 1, NULL, NULL, CONCAT(@yday,' 11:10:00')),
(CONCAT('PUT',DATE_FORMAT(@yday,'%Y%m%d'),'006'), 6, 1, 2,  0, 4, NULL, NULL, CONCAT(@yday,' 11:10:00'));

-- 昨日形成库存（来自 IN001 完成）
INSERT INTO inventory (warehouse_id, location_id, product_sku_id, batch_no, production_date, expiry_date, quantity, locked_quantity, created_time) VALUES
(1, 2, 1, 'BATCH-HR-001', @yday, NULL, 60, 0, CONCAT(@yday,' 10:45:00')),
(1, 3, 2, 'BATCH-EJ-001', @yday, NULL, 50, 0, CONCAT(@yday,' 10:45:00')),
(1, 2, 3, 'BATCH-MI-001', @yday, DATE_ADD(@yday, INTERVAL 365 DAY), 50, 0, CONCAT(@yday,' 10:45:00'));

-- 入库流水（类型1 入库）
INSERT INTO inventory_transaction (product_sku_id, batch_no, warehouse_id, location_id, transaction_type, related_order_no, quantity_change, quantity_after, transaction_time, operator) VALUES
(1,'BATCH-HR-001',1,2,1,CONCAT('IN',DATE_FORMAT(@yday,'%Y%m%d'),'001'), 60, 60, CONCAT(@yday,' 10:45:00'),1),
(2,'BATCH-EJ-001',1,3,1,CONCAT('IN',DATE_FORMAT(@yday,'%Y%m%d'),'001'), 50, 50, CONCAT(@yday,' 10:45:00'),1),
(3,'BATCH-MI-001',1,2,1,CONCAT('IN',DATE_FORMAT(@yday,'%Y%m%d'),'001'), 50,110, CONCAT(@yday,' 10:45:00'),1);

-- 3) 出库域：构造 4 种状态（1待处理/2已分配/3拣货中/4已发货）
INSERT INTO outbound_order (order_no, warehouse_id, customer_id, status, customer_info, created_time) VALUES
(CONCAT('OUT',DATE_FORMAT(@today,'%Y%m%d'),'001'), 1, 1, 4, '{"name":"京东商城"}', CONCAT(@today,' 09:00:00')),
(CONCAT('OUT',DATE_FORMAT(@today,'%Y%m%d'),'002'), 1, 2, 3, '{"name":"天猫超市"}', CONCAT(@today,' 09:05:00')),
(CONCAT('OUT',DATE_FORMAT(@today,'%Y%m%d'),'003'), 1, 1, 2, '{"name":"京东商城"}', CONCAT(@today,' 09:10:00')),
(CONCAT('OUT',DATE_FORMAT(@today,'%Y%m%d'),'004'), 1, 2, 1, '{"name":"天猫超市"}', CONCAT(@today,' 09:15:00'));

INSERT INTO outbound_order_item (outbound_order_id, product_sku_id, quantity, allocated_quantity, picked_quantity, created_time) VALUES
-- 已发货：全部分配与拣选
(1,1,10,10,10, CONCAT(@today,' 09:05:00')),
(1,2, 8, 8, 8,  CONCAT(@today,' 09:05:00')),
-- 拣货中：已分配部分已拣
(2,3,12,12, 6,  CONCAT(@today,' 09:06:00')),
-- 已分配：仅分配未拣
(3,1, 5, 5, 0,  CONCAT(@today,' 09:11:00')),
-- 待处理：未分配
(4,2, 4, 0, 0,  CONCAT(@today,' 09:16:00'));

-- 拣货任务（1待拣货/2部分完成/3已完成）
INSERT INTO picking_task (task_no, wave_no, outbound_order_id, product_sku_id, from_location_id, quantity, status, picked_quantity, created_time) VALUES
(CONCAT('PICK',DATE_FORMAT(@today,'%Y%m%d'),'001'),'WAVE001',1,1,2,10,3,10,CONCAT(@today,' 09:10:00')),
(CONCAT('PICK',DATE_FORMAT(@today,'%Y%m%d'),'002'),'WAVE001',1,2,3, 8,3, 8,CONCAT(@today,' 09:10:00')),
(CONCAT('PICK',DATE_FORMAT(@today,'%Y%m%d'),'003'),'WAVE002',2,3,2,12,2, 6,CONCAT(@today,' 09:12:00')),
(CONCAT('PICK',DATE_FORMAT(@today,'%Y%m%d'),'004'),'WAVE003',3,1,2, 5,1, 0,CONCAT(@today,' 09:13:00'));

-- 锁定与扣减：已分配但未拣的，锁定库存
UPDATE inventory SET locked_quantity = locked_quantity + 5 WHERE warehouse_id=1 AND location_id=2 AND product_sku_id=1; -- OUT003 分配未拣

-- 扣减库存：对应已发货 OUT001 与部分拣货 OUT002
UPDATE inventory SET quantity = quantity - 10 WHERE warehouse_id=1 AND location_id=2 AND product_sku_id=1;
UPDATE inventory SET quantity = quantity - 8  WHERE warehouse_id=1 AND location_id=3 AND product_sku_id=2;
UPDATE inventory SET quantity = quantity - 6  WHERE warehouse_id=1 AND location_id=2 AND product_sku_id=3;

-- 出库流水（类型2 出库）
INSERT INTO inventory_transaction (product_sku_id, batch_no, warehouse_id, location_id, transaction_type, related_order_no, quantity_change, quantity_after, transaction_time, operator) VALUES
(1,'BATCH-HR-001',1,2,2,CONCAT('OUT',DATE_FORMAT(@today,'%Y%m%d'),'001'), -10, 50, CONCAT(@today,' 09:20:00'),1),
(2,'BATCH-EJ-001',1,3,2,CONCAT('OUT',DATE_FORMAT(@today,'%Y%m%d'),'001'),  -8, 42, CONCAT(@today,' 09:20:00'),1),
(3,'BATCH-MI-001',1,2,2,CONCAT('OUT',DATE_FORMAT(@today,'%Y%m%d'),'002'),  -6, 44, CONCAT(@today,' 09:25:00'),2);

-- 4) 额外：移库与盘点调整，补齐流水类型 3/4
-- 移库：将 2 号库位 SKU1001 移 5 件到 3 号库位
-- 负记录（来源库位）
INSERT INTO inventory_transaction (product_sku_id, batch_no, warehouse_id, location_id, transaction_type, related_order_no, quantity_change, quantity_after, transaction_time, operator) VALUES
(1,'BATCH-HR-001',1,2,3,'MOVE-001', -5, 45, CONCAT(@today,' 10:00:00'),1);
UPDATE inventory SET quantity = quantity - 5 WHERE warehouse_id=1 AND location_id=2 AND product_sku_id=1;
-- 正记录（目标库位）
INSERT INTO inventory_transaction (product_sku_id, batch_no, warehouse_id, location_id, transaction_type, related_order_no, quantity_change, quantity_after, transaction_time, operator) VALUES
(1,'BATCH-HR-001',1,3,3,'MOVE-001',  +5, 47, CONCAT(@today,' 10:00:05'),1);
INSERT INTO inventory (warehouse_id, location_id, product_sku_id, batch_no, production_date, expiry_date, quantity, locked_quantity, created_time)
VALUES (1,3,1,'BATCH-HR-001',@yday,NULL,5,0,CONCAT(@today,' 10:00:05'))
ON DUPLICATE KEY UPDATE quantity = quantity + 5;

-- 盘点调整：对 3 号库位 SKU1002 盘盈 +2（类型4）
INSERT INTO inventory_transaction (product_sku_id, batch_no, warehouse_id, location_id, transaction_type, related_order_no, quantity_change, quantity_after, transaction_time, operator) VALUES
(2,'BATCH-EJ-001',1,3,4,'ADJ-001', +2, 44, CONCAT(@today,' 10:10:00'),3);
UPDATE inventory SET quantity = quantity + 2 WHERE warehouse_id=1 AND location_id=3 AND product_sku_id=2;


-- 可选：更新库位使用量（示意）
UPDATE storage_location sl
JOIN (
  SELECT location_id, SUM(quantity) AS qty
  FROM inventory
  GROUP BY location_id
) t ON sl.id = t.location_id
SET sl.status = 2,
    sl.current_volume = t.qty;

