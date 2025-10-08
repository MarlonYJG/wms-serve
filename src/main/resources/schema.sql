-- WMS系统数据库表结构定义（按文档与实体统一）

-- 完整重建：删除所有已存在表（先禁用外键约束，按依赖顺序删除）
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `picking_task`;
DROP TABLE IF EXISTS `outbound_order_item`;
DROP TABLE IF EXISTS `outbound_order`;
DROP TABLE IF EXISTS `inventory_transaction`;
DROP TABLE IF EXISTS `inventory`;
DROP TABLE IF EXISTS `putaway_task`;
DROP TABLE IF EXISTS `inbound_order_item`;
DROP TABLE IF EXISTS `inbound_order`;
DROP TABLE IF EXISTS `storage_location`;
DROP TABLE IF EXISTS `storage_zone`;
DROP TABLE IF EXISTS `product_sku`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `customer`;
DROP TABLE IF EXISTS `supplier`;
DROP TABLE IF EXISTS `warehouse`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 基础信息表

-- 仓库表（与实体 com.bj.wms.entity.Warehouse 对齐）
CREATE TABLE IF NOT EXISTS `warehouse` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `created_by` VARCHAR(100),
  `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_by` VARCHAR(100),
  `updated_time` TIMESTAMP NULL,
  `deleted` TINYINT DEFAULT 0,
  `name` VARCHAR(200) NOT NULL COMMENT '仓库名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库编码',
  `address` VARCHAR(300) COMMENT '地址',
  `contact_person` VARCHAR(50) COMMENT '联系人',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `total_capacity` DECIMAL(12,2) NULL COMMENT '总容量',
  `used_capacity` DECIMAL(12,2) NULL COMMENT '已用容量',
  `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用'
);

-- 供应商表（扩展邮箱、评级）
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `supplier_name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
  `supplier_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
  `contact_person` VARCHAR(50) COMMENT '联系人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱',
  `address` VARCHAR(255) COMMENT '地址',
  `rating` TINYINT DEFAULT 3 COMMENT '评级（1:A 2:B 3:C 4:D）',
  `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 客户表（扩展类型、信用）
CREATE TABLE IF NOT EXISTS `customer` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
  `customer_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '客户编码',
  `customer_type` TINYINT DEFAULT 2 COMMENT '客户类型（1个人 2企业 3代理商 4经销商）',
  `contact_person` VARCHAR(50) COMMENT '联系人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱',
  `address` VARCHAR(255) COMMENT '地址',
  `credit_rating` TINYINT DEFAULT 3 COMMENT '信用等级（1:AAA 2:AA 3:A 4:B 5:C）',
  `credit_limit` DECIMAL(12,2) NULL COMMENT '信用额度',
  `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 库区表（按文档增加编码/容量/状态）
CREATE TABLE IF NOT EXISTS `storage_zone` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `created_by` VARCHAR(50),
  `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_by` VARCHAR(50),
  `updated_time` TIMESTAMP NULL,
  `deleted` TINYINT DEFAULT 0,
  `warehouse_id` BIGINT NOT NULL COMMENT '所属仓库ID',
  `zone_code` VARCHAR(50) NOT NULL COMMENT '库区编码',
  `zone_name` VARCHAR(100) NOT NULL COMMENT '库区名称',
  `zone_type` TINYINT NOT NULL COMMENT '库区类型（1存储 2收货 3发货 6拣货...）',
  `capacity` DECIMAL(12,2) NULL COMMENT '库区容量',
  `used_capacity` DECIMAL(12,2) NULL COMMENT '已用容量',
  `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用',
  UNIQUE KEY `uk_zone_code` (`zone_code`),
  FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse`(`id`)
);

-- 库位表（扩展名称/类型/状态）
CREATE TABLE IF NOT EXISTS `storage_location` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `zone_id` BIGINT NOT NULL COMMENT '所属库区ID',
  `location_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '库位编码（如A-01-01-01）',
  `location_name` VARCHAR(100) COMMENT '库位名称',
  `location_type` TINYINT DEFAULT 1 COMMENT '库位类型（1货架 2地面 3冷藏 4危险）',
  `capacity` DECIMAL(10,2) COMMENT '容量',
  `current_volume` DECIMAL(10,2) DEFAULT 0 COMMENT '当前占用容量',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1空闲 2占用 3禁用）',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_location_code` (`location_code`),
  FOREIGN KEY (`zone_id`) REFERENCES `storage_zone`(`id`)
);

-- 商品SKU表（按文档字段）
CREATE TABLE IF NOT EXISTS `product_sku` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `sku_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '商品SKU编码',
  `sku_name` VARCHAR(255) NOT NULL COMMENT '商品名称',
  `specification` VARCHAR(255) COMMENT '规格',
  `brand` VARCHAR(100) COMMENT '品牌',
  `category_id` INT NULL COMMENT '分类ID',
  `supplier_id` INT NULL COMMENT '默认供应商ID',
  `barcode` VARCHAR(100) COMMENT '条码',
  `weight` DECIMAL(12,4) NULL COMMENT '重量',
  `volume` DECIMAL(12,6) NULL COMMENT '体积',
  `is_batch_managed` BIT(1) DEFAULT b'0' COMMENT '是否批次管理',
  `is_expiry_managed` BIT(1) DEFAULT b'0' COMMENT '是否保质期管理',
  `shelf_life_days` INT NULL COMMENT '保质期天数',
  `safety_stock` INT NULL COMMENT '安全库存',
  `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`id`)
);

-- 2. 业务单据表

-- 入库单表（主单）
CREATE TABLE IF NOT EXISTS `inbound_order` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
  `warehouse_id` BIGINT NOT NULL COMMENT '目标仓库',
  `supplier_id` INT NOT NULL COMMENT '供应商',
  `status` TINYINT NOT NULL COMMENT '状态（1：待收货， 2：部分收货， 3：已完成， 4：已取消）',
  `total_expected_quantity` INT COMMENT '预期总数量',
  `total_received_quantity` INT DEFAULT 0 COMMENT '实际收货总数量',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse`(`id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`id`)
);

-- 入库单明细表
CREATE TABLE IF NOT EXISTS `inbound_order_item` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `inbound_order_id` INT NOT NULL,
  `product_sku_id` INT NOT NULL,
  `expected_quantity` INT NOT NULL COMMENT '预期数量',
  `received_quantity` INT DEFAULT 0 COMMENT '已收数量',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`inbound_order_id`) REFERENCES `inbound_order`(`id`),
  FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`)
);

-- 上架任务表（由入库单生成）
CREATE TABLE IF NOT EXISTS `putaway_task` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `task_no` VARCHAR(50) NOT NULL UNIQUE,
  `inbound_order_item_id` INT NOT NULL COMMENT '关联的入库明细',
  `from_location_id` INT COMMENT '来源库位（通常是收货暂存区）',
  `to_location_id` INT NOT NULL COMMENT '目标上架库位',
  `quantity` INT NOT NULL COMMENT '上架数量',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1：待执行， 2：已完成）',
  `operator` INT COMMENT '操作员',
  `completed_time` DATETIME,
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`inbound_order_item_id`) REFERENCES `inbound_order_item`(`id`),
  FOREIGN KEY (`from_location_id`) REFERENCES `storage_location`(`id`),
  FOREIGN KEY (`to_location_id`) REFERENCES `storage_location`(`id`)
);

-- 3. 库存核心表

-- 库存明细表（记录每个库位上每个批次的库存）
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `warehouse_id` BIGINT NOT NULL,
  `location_id` INT NOT NULL,
  `product_sku_id` INT NOT NULL,
  `batch_no` VARCHAR(100) COMMENT '批次号',
  `production_date` DATE COMMENT '生产日期',
  `expiry_date` DATE COMMENT '过期日期',
  `quantity` INT NOT NULL COMMENT '当前数量',
  `locked_quantity` INT DEFAULT 0 COMMENT '锁定数量（用于出库）',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `unique_stock` (`warehouse_id`, `location_id`, `product_sku_id`, `batch_no`),
  FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse`(`id`),
  FOREIGN KEY (`location_id`) REFERENCES `storage_location`(`id`),
  FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`)
);

-- 库存流水表（记录所有库存变动）
CREATE TABLE IF NOT EXISTS `inventory_transaction` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `product_sku_id` INT NOT NULL,
  `batch_no` VARCHAR(100),
  `warehouse_id` BIGINT NOT NULL,
  `location_id` INT NOT NULL,
  `transaction_type` TINYINT NOT NULL COMMENT '流水类型（1：入库， 2：出库， 3：移库， 4：盘点调整）',
  `related_order_no` VARCHAR(100) COMMENT '关联业务单号',
  `quantity_change` INT NOT NULL COMMENT '数量变化（正数为增，负数为减）',
  `quantity_after` INT NOT NULL COMMENT '变化后结余数量',
  `transaction_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `operator` INT COMMENT '操作人',
  FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`),
  FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse`(`id`),
  FOREIGN KEY (`location_id`) REFERENCES `storage_location`(`id`)
);

-- 4. 出库核心表

-- 出库单表（销售订单）
CREATE TABLE IF NOT EXISTS `outbound_order` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL UNIQUE,
  `warehouse_id` BIGINT NOT NULL,
  `customer_id` INT NOT NULL,
  `status` TINYINT NOT NULL COMMENT '状态（1：待处理， 2：已分配库存， 3：拣货中， 4：已发货）',
  `customer_info` VARCHAR(500) COMMENT '客户信息',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse`(`id`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer`(`id`)
);

-- 出库单明细表
CREATE TABLE IF NOT EXISTS `outbound_order_item` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `outbound_order_id` INT NOT NULL,
  `product_sku_id` INT NOT NULL,
  `quantity` INT NOT NULL COMMENT '需求数量',
  `allocated_quantity` INT DEFAULT 0 COMMENT '已分配库存数量',
  `picked_quantity` INT DEFAULT 0 COMMENT '已拣选数量',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order`(`id`),
  FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`)
);

-- 拣货任务表
CREATE TABLE IF NOT EXISTS `picking_task` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `task_no` VARCHAR(50) NOT NULL UNIQUE,
  `wave_no` VARCHAR(50) COMMENT '波次号',
  `outbound_order_id` INT NOT NULL,
  `product_sku_id` INT NOT NULL,
  `from_location_id` INT NOT NULL COMMENT '拣货库位',
  `quantity` INT NOT NULL COMMENT '需拣选数量',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1：待拣选， 2：部分完成， 3：已完成）',
  `picked_quantity` INT DEFAULT 0,
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order`(`id`),
  FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku`(`id`),
  FOREIGN KEY (`from_location_id`) REFERENCES `storage_location`(`id`)
);

-- 5. 用户与权限表（保留）
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `created_by` VARCHAR(100),
  `created_time` TIMESTAMP,
  `updated_by` VARCHAR(100),
  `updated_time` TIMESTAMP,
  `deleted` TINYINT DEFAULT 0,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `real_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100),
  `phone` VARCHAR(20),
  `status` TINYINT NOT NULL,
  `role` VARCHAR(20) NOT NULL
);

