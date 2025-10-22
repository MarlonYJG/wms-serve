-- WMS 全流程两天模拟数据（覆盖各状态）
SET NAMES utf8mb4;
SET time_zone = '+08:00';

SET @today := DATE(NOW());
SET @yday  := DATE_SUB(@today, INTERVAL 1 DAY);

-- 可重复执行：清空与重建
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE settlement_item;
TRUNCATE TABLE settlement;
TRUNCATE TABLE outbound_charge;
TRUNCATE TABLE charge_dict;
TRUNCATE TABLE quote_item;
TRUNCATE TABLE quote;
TRUNCATE TABLE picking_task;
TRUNCATE TABLE picking_wave;
TRUNCATE TABLE review_task;
TRUNCATE TABLE outbound_order_item;
TRUNCATE TABLE outbound_order;
TRUNCATE TABLE inventory_transaction;
TRUNCATE TABLE inventory;
TRUNCATE TABLE putaway_task;
TRUNCATE TABLE inbound_order_item;
TRUNCATE TABLE inbound_order;
TRUNCATE TABLE inbound_appointment_item;
TRUNCATE TABLE inbound_appointment;
TRUNCATE TABLE storage_location;
TRUNCATE TABLE storage_zone;
TRUNCATE TABLE product_sku;
TRUNCATE TABLE users;
TRUNCATE TABLE customer;
TRUNCATE TABLE supplier;
TRUNCATE TABLE warehouse;
SET FOREIGN_KEY_CHECKS = 1;

-- 1) 基础主数据
INSERT INTO supplier (supplier_name, supplier_code, contact_person, contact_phone, email, address, rating, is_enabled, created_time) VALUES
('华北电子', 'SUP001', '张三', '010-10000001', 'sales@north.cn', '北京海淀', 2, 1, CONCAT(@yday,' 09:00:00')),
('华东制造', 'SUP002', '李四', '021-10000002', 'sales@east.cn',  '上海浦东', 3, 1, CONCAT(@yday,' 09:00:00'));

INSERT INTO customer (customer_name, customer_code, customer_type, contact_person, contact_phone, email, address, credit_rating, credit_limit, is_enabled, created_time) VALUES
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

-- 2) 预约入库域：构造 5 种状态（1待审核/2已审核/3已拒绝/4已取消/5已完成）
INSERT INTO inbound_appointment (appointment_no, warehouse_id, supplier_id, appointment_date, appointment_time_start, appointment_time_end, status, total_expected_quantity, special_requirements, approved_by, approved_time, remark, created_time) VALUES
-- 待审核（1）：今天创建
(CONCAT('APT', DATE_FORMAT(@today,'%Y%m%d'),'001'), 1, 1, @today, '09:00:00', '11:00:00', 1, 100, '需要叉车协助卸货', NULL, NULL, NULL, CONCAT(@today,' 08:00:00')),
-- 已审核（2）：昨天审核通过
(CONCAT('APT', DATE_FORMAT(@yday,'%Y%m%d'),'002'), 1, 2, @yday, '14:00:00', '16:00:00', 2, 80, '需要冷藏车运输', 1, CONCAT(@yday,' 10:30:00'), '审核通过', CONCAT(@yday,' 09:00:00')),
-- 已拒绝（3）：昨天拒绝
(CONCAT('APT', DATE_FORMAT(@yday,'%Y%m%d'),'003'), 1, 1, @yday, '10:00:00', '12:00:00', 3, 50, NULL, 1, CONCAT(@yday,' 11:00:00'), '时间冲突，建议改期', CONCAT(@yday,' 08:30:00')),
-- 已取消（4）：今天取消
(CONCAT('APT', DATE_FORMAT(@today,'%Y%m%d'),'004'), 1, 2, @today, '15:00:00', '17:00:00', 4, 60, NULL, NULL, NULL, '供应商临时取消', CONCAT(@today,' 07:00:00')),
-- 已完成（5）：昨天完成
(CONCAT('APT', DATE_FORMAT(@yday,'%Y%m%d'),'005'), 1, 1, @yday, '08:00:00', '10:00:00', 5, 120, '需要特殊包装', 1, CONCAT(@yday,' 07:30:00'), '审核通过', CONCAT(@yday,' 07:00:00'));

-- 预约商品明细
INSERT INTO inbound_appointment_item (appointment_id, product_sku_id, expected_quantity, unit_price, batch_no, production_date, expiry_date, created_time) VALUES
-- APT001 待审核
(1, 1, 50, 99.00, 'BATCH-HR-002', @today, NULL, CONCAT(@today,' 08:05:00')),
(1, 2, 50, 129.00, 'BATCH-EJ-002', @today, NULL, CONCAT(@today,' 08:05:00')),
-- APT002 已审核
(2, 3, 80, 25.00, 'BATCH-MI-002', @yday, DATE_ADD(@yday, INTERVAL 365 DAY), CONCAT(@yday,' 09:05:00')),
-- APT003 已拒绝
(3, 1, 30, 99.00, 'BATCH-HR-003', @yday, NULL, CONCAT(@yday,' 08:35:00')),
(3, 2, 20, 129.00, 'BATCH-EJ-003', @yday, NULL, CONCAT(@yday,' 08:35:00')),
-- APT004 已取消
(4, 3, 60, 25.00, 'BATCH-MI-003', @today, DATE_ADD(@today, INTERVAL 365 DAY), CONCAT(@today,' 07:05:00')),
-- APT005 已完成
(5, 1, 60, 99.00, 'BATCH-HR-001', @yday, NULL, CONCAT(@yday,' 07:05:00')),
(5, 2, 60, 129.00, 'BATCH-EJ-001', @yday, NULL, CONCAT(@yday,' 07:05:00'));

-- 3) 入库域：构造 4 种状态（1待收货/2部分收货/3已完成/4已取消）
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

-- 拣货波次（1待执行/2执行中/3已完成）
INSERT INTO picking_wave (wave_no, warehouse_id, status, order_count, task_count, completed_task_count, started_time, completed_time, operator_name, remark, created_time) VALUES
('WAVE001', 1, 3, 1, 2, 2, CONCAT(@today,' 09:10:00'), CONCAT(@today,' 10:30:00'), '张三', '第一批拣货波次', CONCAT(@today,' 09:00:00')),
('WAVE002', 1, 2, 1, 1, 0, CONCAT(@today,' 09:12:00'), NULL, '李四', '第二批拣货波次', CONCAT(@today,' 09:05:00')),
('WAVE003', 1, 1, 1, 1, 0, NULL, NULL, NULL, '第三批拣货波次', CONCAT(@today,' 09:10:00'));

-- 拣货任务（1待拣货/2部分完成/3已完成）
INSERT INTO picking_task (task_no, wave_no, picking_wave_id, outbound_order_id, product_sku_id, from_location_id, quantity, status, picked_quantity, created_time) VALUES
(CONCAT('PICK',DATE_FORMAT(@today,'%Y%m%d'),'001'),'WAVE001',1,1,1,2,10,3,10,CONCAT(@today,' 09:10:00')),
(CONCAT('PICK',DATE_FORMAT(@today,'%Y%m%d'),'002'),'WAVE001',1,1,2,3, 8,3, 8,CONCAT(@today,' 09:10:00')),
(CONCAT('PICK',DATE_FORMAT(@today,'%Y%m%d'),'003'),'WAVE002',2,2,3,2,12,2, 6,CONCAT(@today,' 09:12:00')),
(CONCAT('PICK',DATE_FORMAT(@today,'%Y%m%d'),'004'),'WAVE003',3,3,1,2, 5,1, 0,CONCAT(@today,' 09:13:00'));

-- 复核任务（1待复核/2复核中/3复核完成/4复核异常）
INSERT INTO review_task (task_no, outbound_order_id, product_sku_id, expected_quantity, actual_quantity, status, reviewer_name, review_time, remark, created_time) VALUES
(CONCAT('REV',DATE_FORMAT(@today,'%Y%m%d'),'001'),1,1,10,10,3,'张三',CONCAT(@today,' 11:00:00'),'复核通过',CONCAT(@today,' 10:30:00')),
(CONCAT('REV',DATE_FORMAT(@today,'%Y%m%d'),'002'),1,2, 8, 8,3,'李四',CONCAT(@today,' 11:15:00'),'复核通过',CONCAT(@today,' 10:35:00')),
(CONCAT('REV',DATE_FORMAT(@today,'%Y%m%d'),'003'),2,3,12,10,4,'王五',CONCAT(@today,' 11:30:00'),'数量不符',CONCAT(@today,' 10:40:00')),
(CONCAT('REV',DATE_FORMAT(@today,'%Y%m%d'),'004'),3,1, 5, 0,1,NULL,NULL,NULL,CONCAT(@today,' 10:45:00'));

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


-- 5) 财务结算与费用
-- 费用项字典
INSERT INTO charge_dict (charge_code, charge_name, default_tax_rate, is_enabled, remark, created_time) VALUES
('FREIGHT', '运费', 0.00, 1, '快递/物流', CONCAT(@today,' 09:30:00')),
('INSTALL', '安装费', 0.00, 1, '上门安装', CONCAT(@today,' 09:30:00')),
('INSURE',  '保险费', 0.00, 1, '保价',     CONCAT(@today,' 09:30:00'));

-- 出库费用：为 OUT001、OUT002 补充费用
INSERT INTO outbound_charge (outbound_order_id, charge_type, amount, tax_rate, currency, remark, created_time) VALUES
(1, 1, 28.00, NULL, 'CNY', '运费-顺丰', CONCAT(@today,' 09:35:00')),
(2, 1, 18.00, NULL, 'CNY', '运费-京东', CONCAT(@today,' 09:36:00')),
(2, 3,  3.00, NULL, 'CNY', '保价费',    CONCAT(@today,' 09:36:30'));

-- 结算单（客户1：京东商城），包含 OUT001、OUT002
INSERT INTO settlement (settlement_no, customer_id, period_start, period_end, status, currency, amount_goods, amount_charges, amount_total, remark, created_time) VALUES
(CONCAT('SET', DATE_FORMAT(@today,'%Y%m%d'), '001'), 1, @today, @today, 3, 'CNY', 0.00, 49.00, 49.00, '演示结算单', CONCAT(@today,' 10:00:00'));

-- 结算明细（示例商品金额用0，仅演示费用聚合；真实可按订单商品金额填充）
INSERT INTO settlement_item (settlement_id, outbound_order_id, amount_goods, amount_charges, amount_total, remark, created_time) VALUES
(1, 1, 0.00, 28.00, 28.00, 'OUT001 费用', CONCAT(@today,' 10:02:00')),
(1, 2, 0.00, 21.00, 21.00, 'OUT002 费用', CONCAT(@today,' 10:03:00'));


-- 6) 销售报价
-- 报价单：客户2（天猫超市），有效期今日起三天
INSERT INTO quote (quote_no, customer_id, currency, valid_from, valid_to, status, amount_total, remark, created_time)
VALUES (CONCAT('Q', DATE_FORMAT(@today,'%Y%m%d'),'001'), 2, 'CNY', @today, DATE_ADD(@today, INTERVAL 3 DAY), 3, 0.00, '标准报价', CONCAT(@today,' 11:00:00'));

-- 报价明细：SKU1001/1002
INSERT INTO quote_item (quote_id, product_sku_id, quantity, unit_price, discount_rate, tax_rate, amount_subtotal, remark, created_time) VALUES
(1, 1, 10, 99.00, 0.00, NULL, 990.00, '手环', CONCAT(@today,' 11:05:00')),
(1, 2,  8, 129.00, 5.00, NULL, 980.40, '耳机(95折)', CONCAT(@today,' 11:06:00'));
