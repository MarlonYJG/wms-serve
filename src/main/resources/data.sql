-- WMS 简化测试数据（H2 数据库兼容）

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
('华北电子', 'SUP001', '张三', '010-10000001', 'sales@north.cn', '北京海淀', 2, b'1', CURRENT_TIMESTAMP),
('华东制造', 'SUP002', '李四', '021-10000002', 'sales@east.cn',  '上海浦东', 3, b'1', CURRENT_TIMESTAMP);

INSERT INTO customer (customer_name, customer_code, customer_type, contact_person, contact_phone, email, address, credit_rating, credit_limit, is_enabled, created_time) VALUES
('京东商城', 'CUST001', 2, '王一', '400-000-0001', 'jd@corp.cn', '北京朝阳', 3, 500000.00, b'1', CURRENT_TIMESTAMP),
('天猫超市', 'CUST002', 2, '赵六', '400-000-0002', 'tmall@corp.cn','杭州余杭', 3, 600000.00, b'1', CURRENT_TIMESTAMP);

INSERT INTO warehouse (name, code, address, is_enabled, created_time) VALUES
('北京主仓', 'WH001', '北京通州物流园', b'1', CURRENT_TIMESTAMP);

-- zone: 2收货/1存储/6拣货/3发货
INSERT INTO storage_zone (warehouse_id, zone_code, zone_name, zone_type, capacity, used_capacity, is_enabled, created_time) VALUES
(1, 'BJ-RCV',  '收货区', 2, 1000.00, 0.00, b'1', CURRENT_TIMESTAMP),
(1, 'BJ-A',    '存储区A', 1, 5000.00, 0.00, b'1', CURRENT_TIMESTAMP),
(1, 'BJ-PICK', '拣货区', 6, 2000.00, 0.00, b'1', CURRENT_TIMESTAMP),
(1, 'BJ-SHIP', '发货区', 3, 1000.00, 0.00, b'1', CURRENT_TIMESTAMP);

INSERT INTO storage_location (zone_id, location_code, location_name, location_type, capacity, current_volume, status, created_time) VALUES
(1, 'BJ-RCV-01',  '收货暂存01', 2, 100.00, 0.00, 1, CURRENT_TIMESTAMP),
(2, 'BJ-A-01-01', 'A区01排01层01位', 1, 50.00, 0.00, 1, CURRENT_TIMESTAMP),
(2, 'BJ-A-01-02', 'A区01排01层02位', 1, 50.00, 0.00, 1, CURRENT_TIMESTAMP),
(3, 'BJ-PICK-01','拣货位01', 1, 30.00, 0.00, 1, CURRENT_TIMESTAMP),
(4, 'BJ-SHIP-01','发货暂存01', 2, 20.00, 0.00, 1, CURRENT_TIMESTAMP);

INSERT INTO product_sku (sku_code, sku_name, specification, brand, category_id, supplier_id, barcode, weight, volume, is_batch_managed, is_expiry_managed, shelf_life_days, safety_stock, is_enabled, created_time) VALUES
('SKU1001', '智能手环', '黑色 标准款', '智品', NULL, 1, '690000100001', 0.0500, 0.0008, b'0', b'0', NULL, 100, b'1', CURRENT_TIMESTAMP),
('SKU1002', '蓝牙耳机', '降噪版',     '声浪', NULL, 1, '690000100002', 0.0300, 0.0006, b'0', b'0', NULL, 100, b'1', CURRENT_TIMESTAMP),
('SKU1003', '有机大米', '5kg',        '稻香', NULL, 2, '690000100003', 5.0000, 0.0080, b'1', b'1', 365, 50, b'1', CURRENT_TIMESTAMP);

-- 2) 入库单数据
INSERT INTO inbound_order (order_no, warehouse_id, supplier_id, status, total_expected_quantity, total_received_quantity, created_time) VALUES
('IN001', 1, 1, 3, 160, 160, CURRENT_TIMESTAMP),
('IN002', 1, 2, 2, 100, 60, CURRENT_TIMESTAMP);

INSERT INTO inbound_order_item (inbound_order_id, product_sku_id, expected_quantity, received_quantity, created_time) VALUES
(1, 1, 60, 60, CURRENT_TIMESTAMP),
(1, 2, 50, 50, CURRENT_TIMESTAMP),
(1, 3, 50, 50, CURRENT_TIMESTAMP),
(2, 1, 50, 30, CURRENT_TIMESTAMP),
(2, 2, 50, 30, CURRENT_TIMESTAMP);

-- 3) 库存数据
INSERT INTO inventory (warehouse_id, location_id, product_sku_id, batch_no, production_date, expiry_date, quantity, locked_quantity, created_time) VALUES
(1, 2, 1, 'BATCH-HR-001', CURRENT_DATE, NULL, 60, 0, CURRENT_TIMESTAMP),
(1, 3, 2, 'BATCH-EJ-001', CURRENT_DATE, NULL, 50, 0, CURRENT_TIMESTAMP),
(1, 2, 3, 'BATCH-MI-001', CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 365 DAY), 50, 0, CURRENT_TIMESTAMP);

-- 4) 出库单数据
INSERT INTO outbound_order (order_no, warehouse_id, customer_id, status, customer_info, amount_total, created_time) VALUES
('OUT001', 1, 1, 4, '{"name":"京东商城"}', 0.00, CURRENT_TIMESTAMP),
('OUT002', 1, 2, 3, '{"name":"天猫超市"}', 0.00, CURRENT_TIMESTAMP);

INSERT INTO outbound_order_item (outbound_order_id, product_sku_id, quantity, allocated_quantity, picked_quantity, created_time) VALUES
(1, 1, 10, 10, 10, CURRENT_TIMESTAMP),
(1, 2, 8, 8, 8, CURRENT_TIMESTAMP),
(2, 3, 12, 12, 6, CURRENT_TIMESTAMP);

-- 5) 费用字典
INSERT INTO charge_dict (charge_code, charge_name, default_tax_rate, is_enabled, remark, created_time) VALUES
('FREIGHT', '运费', 0.00, b'1', '快递/物流', CURRENT_TIMESTAMP),
('INSTALL', '安装费', 0.00, b'1', '上门安装', CURRENT_TIMESTAMP),
('INSURE',  '保险费', 0.00, b'1', '保价',     CURRENT_TIMESTAMP);

-- 6) 出库费用（使用费用字典ID）
INSERT INTO outbound_charge (outbound_order_id, charge_type, amount, tax_rate, currency, remark, created_time) VALUES
(1, 1, 28.00, NULL, 'CNY', '运费-顺丰', CURRENT_TIMESTAMP),
(2, 1, 18.00, NULL, 'CNY', '运费-京东', CURRENT_TIMESTAMP),
(2, 3, 3.00, NULL, 'CNY', '保价费', CURRENT_TIMESTAMP);