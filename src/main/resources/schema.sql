-- WMS系统数据库表结构定义
-- 基于项目说明文档v1.0.0设计

-- 1. 基础信息表

-- 供应商表
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
  `contact_person` VARCHAR(50) COMMENT '联系人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `address` VARCHAR(255) COMMENT '地址',
  `is_enabled` TINYINT DEFAULT 1 COMMENT '是否启用',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 客户表
CREATE TABLE IF NOT EXISTS `customer` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '客户名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '客户编码',
  `contact_person` VARCHAR(50) COMMENT '联系人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `address` VARCHAR(255) COMMENT '地址',
  `is_enabled` TINYINT DEFAULT 1 COMMENT '是否启用',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 仓库表
CREATE TABLE IF NOT EXISTS `warehouse` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '仓库名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库编码',
  `address` VARCHAR(255) COMMENT '地址',
  `is_enabled` TINYINT DEFAULT 1 COMMENT '是否启用',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 库区表
CREATE TABLE IF NOT EXISTS `storage_zone` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `warehouse_id` INT NOT NULL COMMENT '所属仓库ID',
  `name` VARCHAR(100) NOT NULL COMMENT '库区名（如：收货区、货架区）',
  `type` TINYINT NOT NULL COMMENT '库区类型（1：存储， 2：收货， 3：发货...）',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse`(`id`)
);

-- 库位表（核心）
CREATE TABLE IF NOT EXISTS `storage_location` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `zone_id` INT NOT NULL COMMENT '所属库区ID',
  `location_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '库位编码（如A-01-01）',
  `capacity` DECIMAL(10,2) COMMENT '容量',
  `current_volume` DECIMAL(10,2) DEFAULT 0 COMMENT '当前占用容量',
  `status` TINYINT DEFAULT 1 COMMENT '状态（1：空闲， 2：占用， 3：禁用）',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`zone_id`) REFERENCES `storage_zone`(`id`)
);

-- 商品SKU表
CREATE TABLE IF NOT EXISTS `product_sku` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `sku_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '商品SKU编码',
  `name` VARCHAR(255) NOT NULL COMMENT '商品名称',
  `specification` VARCHAR(255) COMMENT '规格',
  `supplier_id` INT COMMENT '默认供应商ID',
  `is_batch_managed` TINYINT DEFAULT 0 COMMENT '是否管理批次',
  `is_expiry_managed` TINYINT DEFAULT 0 COMMENT '是否管理保质期',
  `shelf_life_days` INT COMMENT '保质期天数',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier`(`id`)
);

-- 2. 业务单据表

-- 入库单表（主单）
CREATE TABLE IF NOT EXISTS `inbound_order` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
  `warehouse_id` INT NOT NULL COMMENT '目标仓库',
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

-- 库存明细表（最核心的表，记录每个库位上每个批次的库存）
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `warehouse_id` INT NOT NULL,
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
  `warehouse_id` INT NOT NULL,
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

-- 5. 用户与权限表

-- 用户表（与实体 com.bj.wms.entity.User 对应）
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

-- 4. 出库核心表

-- 出库单表（销售订单）
CREATE TABLE IF NOT EXISTS `outbound_order` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL UNIQUE,
  `warehouse_id` INT NOT NULL,
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
