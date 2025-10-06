-- WMS系统模拟数据初始化
-- 基于项目说明文档v1.0.0设计

-- 1. 插入供应商数据
INSERT INTO supplier (name, code, contact_person, phone, address, is_enabled) VALUES
('北京电子科技有限公司', 'SUP001', '张经理', '010-12345678', '北京市海淀区中关村大街1号', 1),
('上海机械制造有限公司', 'SUP002', '李总', '021-87654321', '上海市浦东新区张江路100号', 1),
('广州食品贸易公司', 'SUP003', '王主任', '020-11111111', '广州市天河区珠江路200号', 1),
('深圳智能设备有限公司', 'SUP004', '陈工程师', '0755-22222222', '深圳市南山区科技园300号', 1);

-- 2. 插入客户数据
INSERT INTO customer (name, code, contact_person, phone, address, is_enabled) VALUES
('京东商城', 'CUST001', '刘采购', '400-123-4567', '北京市朝阳区北辰世纪中心', 1),
('天猫超市', 'CUST002', '马经理', '400-888-9999', '杭州市余杭区文一西路969号', 1),
('苏宁易购', 'CUST003', '张总监', '400-836-5365', '南京市玄武区苏宁大道1号', 1),
('国美在线', 'CUST004', '李主管', '400-811-3333', '北京市朝阳区霄云路26号', 1);

-- 3. 插入仓库数据
INSERT INTO warehouse (name, code, address, is_enabled) VALUES
('北京主仓库', 'WH001', '北京市通州区物流园区A区1号', 1),
('上海分仓库', 'WH002', '上海市青浦区华新镇工业园区B区2号', 1),
('广州分仓库', 'WH003', '广州市白云区人和镇物流园C区3号', 1);

-- 4. 插入库区数据
INSERT INTO storage_zone (warehouse_id, name, type) VALUES
-- 北京主仓库的库区
(1, '收货区', 2),
(1, '存储区A', 1),
(1, '存储区B', 1),
(1, '拣货区', 1),
(1, '发货区', 3),
(1, '退货区', 1),
-- 上海分仓库的库区
(2, '收货区', 2),
(2, '存储区', 1),
(2, '发货区', 3),
-- 广州分仓库的库区
(3, '收货区', 2),
(3, '存储区', 1),
(3, '发货区', 3);

-- 5. 插入库位数据
INSERT INTO storage_location (zone_id, location_code, capacity, current_volume, status) VALUES
-- 北京主仓库收货区
(1, 'BJ-RCV-01', 100.00, 0.00, 1),
(1, 'BJ-RCV-02', 100.00, 0.00, 1),
(1, 'BJ-RCV-03', 100.00, 0.00, 1),
-- 北京主仓库存储区A
(2, 'BJ-A-01-01', 50.00, 0.00, 1),
(2, 'BJ-A-01-02', 50.00, 0.00, 1),
(2, 'BJ-A-01-03', 50.00, 0.00, 1),
(2, 'BJ-A-02-01', 50.00, 0.00, 1),
(2, 'BJ-A-02-02', 50.00, 0.00, 1),
(2, 'BJ-A-02-03', 50.00, 0.00, 1),
-- 北京主仓库存储区B
(3, 'BJ-B-01-01', 50.00, 0.00, 1),
(3, 'BJ-B-01-02', 50.00, 0.00, 1),
(3, 'BJ-B-01-03', 50.00, 0.00, 1),
(3, 'BJ-B-02-01', 50.00, 0.00, 1),
(3, 'BJ-B-02-02', 50.00, 0.00, 1),
(3, 'BJ-B-02-03', 50.00, 0.00, 1),
-- 北京主仓库拣货区
(4, 'BJ-PICK-01', 30.00, 0.00, 1),
(4, 'BJ-PICK-02', 30.00, 0.00, 1),
(4, 'BJ-PICK-03', 30.00, 0.00, 1),
-- 北京主仓库发货区
(5, 'BJ-SHIP-01', 20.00, 0.00, 1),
(5, 'BJ-SHIP-02', 20.00, 0.00, 1),
-- 上海分仓库库位
(7, 'SH-RCV-01', 80.00, 0.00, 1),
(8, 'SH-A-01-01', 40.00, 0.00, 1),
(8, 'SH-A-01-02', 40.00, 0.00, 1),
(8, 'SH-A-02-01', 40.00, 0.00, 1),
(8, 'SH-A-02-02', 40.00, 0.00, 1),
(9, 'SH-SHIP-01', 20.00, 0.00, 1),
-- 广州分仓库库位
(10, 'GZ-RCV-01', 80.00, 0.00, 1),
(11, 'GZ-A-01-01', 40.00, 0.00, 1),
(11, 'GZ-A-01-02', 40.00, 0.00, 1),
(11, 'GZ-A-02-01', 40.00, 0.00, 1),
(11, 'GZ-A-02-02', 40.00, 0.00, 1),
(12, 'GZ-SHIP-01', 20.00, 0.00, 1);

-- 6. 插入商品SKU数据
INSERT INTO product_sku (sku_code, name, specification, supplier_id, is_batch_managed, is_expiry_managed, shelf_life_days) VALUES
('SKU001', 'iPhone 15 Pro', '256GB 深空黑色', 1, 0, 0, NULL),
('SKU002', 'MacBook Pro 14', 'M3芯片 16GB+512GB', 1, 0, 0, NULL),
('SKU003', 'AirPods Pro', '第2代 主动降噪', 1, 0, 0, NULL),
('SKU004', '工业传感器', '温度传感器 PT100', 2, 1, 0, NULL),
('SKU005', '电机控制器', '伺服电机控制器 1KW', 2, 1, 0, NULL),
('SKU006', '有机大米', '东北五常大米 5kg装', 3, 1, 1, 365),
('SKU007', '进口奶粉', '荷兰原装进口 900g', 3, 1, 1, 730),
('SKU008', '智能手环', '健康监测 防水运动手环', 4, 0, 0, NULL),
('SKU009', '蓝牙耳机', '真无线 降噪耳机', 4, 0, 0, NULL),
('SKU010', '移动电源', '20000mAh 快充移动电源', 4, 0, 0, NULL);

-- 7. 插入入库单数据
INSERT INTO inbound_order (order_no, warehouse_id, supplier_id, status, total_expected_quantity, total_received_quantity) VALUES
('IN20241201001', 1, 1, 3, 100, 100),
('IN20241201002', 1, 2, 3, 50, 50),
('IN20241201003', 1, 3, 2, 200, 150),
('IN20241201004', 2, 4, 3, 80, 80),
('IN20241201005', 3, 1, 1, 60, 0);

-- 8. 插入入库单明细数据
INSERT INTO inbound_order_item (inbound_order_id, product_sku_id, expected_quantity, received_quantity) VALUES
-- 入库单1：iPhone相关产品
(1, 1, 30, 30),  -- iPhone 15 Pro
(1, 2, 20, 20),  -- MacBook Pro
(1, 3, 50, 50),  -- AirPods Pro
-- 入库单2：工业设备
(2, 4, 25, 25),  -- 工业传感器
(2, 5, 25, 25),  -- 电机控制器
-- 入库单3：食品
(3, 6, 100, 80), -- 有机大米
(3, 7, 100, 70), -- 进口奶粉
-- 入库单4：智能设备
(4, 8, 30, 30),  -- 智能手环
(4, 9, 30, 30),  -- 蓝牙耳机
(4, 10, 20, 20), -- 移动电源
-- 入库单5：待收货
(5, 1, 40, 0),   -- iPhone 15 Pro
(5, 8, 20, 0);   -- 智能手环

-- 9. 插入库存数据
INSERT INTO inventory (warehouse_id, location_id, product_sku_id, batch_no, production_date, expiry_date, quantity, locked_quantity) VALUES
-- 北京主仓库库存
(1, 4, 1, 'BATCH001', '2024-11-01', NULL, 30, 0),  -- iPhone 15 Pro
(1, 5, 2, 'BATCH002', '2024-11-01', NULL, 20, 0),  -- MacBook Pro
(1, 6, 3, 'BATCH003', '2024-11-01', NULL, 50, 0),  -- AirPods Pro
(1, 7, 4, 'BATCH004', '2024-11-15', NULL, 25, 0),  -- 工业传感器
(1, 8, 5, 'BATCH005', '2024-11-15', NULL, 25, 0),  -- 电机控制器
(1, 9, 6, 'BATCH006', '2024-10-01', '2025-10-01', 80, 0),  -- 有机大米
(1, 10, 7, 'BATCH007', '2024-09-01', '2026-09-01', 70, 0), -- 进口奶粉
-- 上海分仓库库存
(2, 20, 8, 'BATCH008', '2024-11-20', NULL, 30, 0),  -- 智能手环
(2, 21, 9, 'BATCH009', '2024-11-20', NULL, 30, 0),  -- 蓝牙耳机
(2, 22, 10, 'BATCH010', '2024-11-20', NULL, 20, 0), -- 移动电源
-- 广州分仓库库存
(3, 26, 1, 'BATCH011', '2024-11-25', NULL, 15, 0),  -- iPhone 15 Pro
(3, 27, 8, 'BATCH012', '2024-11-25', NULL, 10, 0);  -- 智能手环

-- 10. 插入库存流水数据
INSERT INTO inventory_transaction (product_sku_id, batch_no, warehouse_id, location_id, transaction_type, related_order_no, quantity_change, quantity_after, operator) VALUES
-- 入库流水
(1, 'BATCH001', 1, 4, 1, 'IN20241201001', 30, 30, 1),
(2, 'BATCH002', 1, 5, 1, 'IN20241201001', 20, 20, 1),
(3, 'BATCH003', 1, 6, 1, 'IN20241201001', 50, 50, 1),
(4, 'BATCH004', 1, 7, 1, 'IN20241201002', 25, 25, 1),
(5, 'BATCH005', 1, 8, 1, 'IN20241201002', 25, 25, 1),
(6, 'BATCH006', 1, 9, 1, 'IN20241201003', 80, 80, 1),
(7, 'BATCH007', 1, 10, 1, 'IN20241201003', 70, 70, 1),
(8, 'BATCH008', 2, 20, 1, 'IN20241201004', 30, 30, 1),
(9, 'BATCH009', 2, 21, 1, 'IN20241201004', 30, 30, 1),
(10, 'BATCH010', 2, 22, 1, 'IN20241201004', 20, 20, 1),
(1, 'BATCH011', 3, 26, 1, 'IN20241201005', 15, 15, 1),
(8, 'BATCH012', 3, 27, 1, 'IN20241201005', 10, 10, 1);

-- 11. 插入出库单数据
INSERT INTO outbound_order (order_no, warehouse_id, customer_id, status, customer_info) VALUES
('OUT20241201001', 1, 1, 4, '{"name":"京东商城","address":"北京市朝阳区北辰世纪中心","phone":"400-123-4567"}'),
('OUT20241201002', 1, 2, 3, '{"name":"天猫超市","address":"杭州市余杭区文一西路969号","phone":"400-888-9999"}'),
('OUT20241201003', 2, 3, 2, '{"name":"苏宁易购","address":"南京市玄武区苏宁大道1号","phone":"400-836-5365"}'),
('OUT20241201004', 3, 4, 1, '{"name":"国美在线","address":"北京市朝阳区霄云路26号","phone":"400-811-3333"}');

-- 12. 插入出库单明细数据
INSERT INTO outbound_order_item (outbound_order_id, product_sku_id, quantity, allocated_quantity, picked_quantity) VALUES
-- 出库单1：已发货
(1, 1, 5, 5, 5),   -- iPhone 15 Pro
(1, 2, 2, 2, 2),   -- MacBook Pro
(1, 3, 10, 10, 10), -- AirPods Pro
-- 出库单2：拣货中
(2, 6, 20, 20, 15), -- 有机大米
(2, 7, 15, 15, 10), -- 进口奶粉
-- 出库单3：已分配库存
(3, 8, 5, 5, 0),   -- 智能手环
(3, 9, 8, 8, 0),   -- 蓝牙耳机
-- 出库单4：待处理
(4, 1, 3, 0, 0),   -- iPhone 15 Pro
(4, 8, 2, 0, 0);   -- 智能手环

-- 13. 插入拣货任务数据
INSERT INTO picking_task (task_no, wave_no, outbound_order_id, product_sku_id, from_location_id, quantity, status, picked_quantity) VALUES
-- 出库单1的拣货任务（已完成）
('PICK20241201001', 'WAVE001', 1, 1, 4, 5, 3, 5),
('PICK20241201002', 'WAVE001', 1, 2, 5, 2, 3, 2),
('PICK20241201003', 'WAVE001', 1, 3, 6, 10, 3, 10),
-- 出库单2的拣货任务（部分完成）
('PICK20241201004', 'WAVE002', 2, 6, 9, 20, 2, 15),
('PICK20241201005', 'WAVE002', 2, 7, 10, 15, 2, 10),
-- 出库单3的拣货任务（待拣选）
('PICK20241201006', 'WAVE003', 3, 8, 20, 5, 1, 0),
('PICK20241201007', 'WAVE003', 3, 9, 21, 8, 1, 0);

-- 14. 插入上架任务数据
INSERT INTO putaway_task (task_no, inbound_order_item_id, from_location_id, to_location_id, quantity, status, operator, completed_time) VALUES
-- 已完成的上架任务
('PUT20241201001', 1, 1, 4, 30, 2, 1, '2024-12-01 10:30:00'),
('PUT20241201002', 2, 1, 5, 20, 2, 1, '2024-12-01 10:35:00'),
('PUT20241201003', 3, 1, 6, 50, 2, 1, '2024-12-01 10:40:00'),
('PUT20241201004', 4, 1, 7, 25, 2, 1, '2024-12-01 11:00:00'),
('PUT20241201005', 5, 1, 8, 25, 2, 1, '2024-12-01 11:05:00'),
('PUT20241201006', 6, 1, 9, 80, 2, 1, '2024-12-01 11:10:00'),
('PUT20241201007', 7, 1, 10, 70, 2, 1, '2024-12-01 11:15:00'),
('PUT20241201008', 8, 7, 20, 30, 2, 2, '2024-12-01 14:00:00'),
('PUT20241201009', 9, 7, 21, 30, 2, 2, '2024-12-01 14:05:00'),
('PUT20241201010', 10, 7, 22, 20, 2, 2, '2024-12-01 14:10:00'),
-- 待执行的上架任务
('PUT20241201011', 11, 10, 26, 15, 1, NULL, NULL),
('PUT20241201012', 12, 10, 27, 10, 1, NULL, NULL);

-- 更新库存锁定数量（为已分配但未拣选的订单）
UPDATE inventory SET locked_quantity = 5 WHERE product_sku_id = 8 AND warehouse_id = 2 AND location_id = 20;
UPDATE inventory SET locked_quantity = 8 WHERE product_sku_id = 9 AND warehouse_id = 2 AND location_id = 21;

-- 更新库位状态（占用状态）
UPDATE storage_location SET status = 2, current_volume = 30.00 WHERE id = 4;  -- iPhone库存
UPDATE storage_location SET status = 2, current_volume = 20.00 WHERE id = 5;  -- MacBook库存
UPDATE storage_location SET status = 2, current_volume = 50.00 WHERE id = 6;  -- AirPods库存
UPDATE storage_location SET status = 2, current_volume = 25.00 WHERE id = 7;  -- 传感器库存
UPDATE storage_location SET status = 2, current_volume = 25.00 WHERE id = 8;  -- 控制器库存
UPDATE storage_location SET status = 2, current_volume = 80.00 WHERE id = 9;  -- 大米库存
UPDATE storage_location SET status = 2, current_volume = 70.00 WHERE id = 10; -- 奶粉库存
UPDATE storage_location SET status = 2, current_volume = 30.00 WHERE id = 20; -- 手环库存
UPDATE storage_location SET status = 2, current_volume = 30.00 WHERE id = 21; -- 耳机库存
UPDATE storage_location SET status = 2, current_volume = 20.00 WHERE id = 22; -- 移动电源库存
UPDATE storage_location SET status = 2, current_volume = 15.00 WHERE id = 26; -- 广州iPhone库存
UPDATE storage_location SET status = 2, current_volume = 10.00 WHERE id = 27; -- 广州手环库存

-- 15. 管理员账号由启动器 DataInitializer 保证存在（避免哈希漂移导致不一致）