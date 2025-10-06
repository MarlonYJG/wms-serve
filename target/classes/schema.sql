-- WMS系统数据库表结构定义
-- 基于项目说明文档v1.0.0设计，兼容H2数据库

-- 1. 基础信息表

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(50) NOT NULL UNIQUE,
  contact_person VARCHAR(50),
  phone VARCHAR(20),
  address VARCHAR(255),
  is_enabled TINYINT DEFAULT 1,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 客户表
CREATE TABLE IF NOT EXISTS customer (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(50) NOT NULL UNIQUE,
  contact_person VARCHAR(50),
  phone VARCHAR(20),
  address VARCHAR(255),
  is_enabled TINYINT DEFAULT 1,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 仓库表
CREATE TABLE IF NOT EXISTS warehouse (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(50) NOT NULL UNIQUE,
  address VARCHAR(255),
  is_enabled TINYINT DEFAULT 1,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 库区表
CREATE TABLE IF NOT EXISTS storage_zone (
  id INT PRIMARY KEY AUTO_INCREMENT,
  warehouse_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  type TINYINT NOT NULL,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
);

-- 库位表（核心）
CREATE TABLE IF NOT EXISTS storage_location (
  id INT PRIMARY KEY AUTO_INCREMENT,
  zone_id INT NOT NULL,
  location_code VARCHAR(50) NOT NULL UNIQUE,
  capacity DECIMAL(10,2),
  current_volume DECIMAL(10,2) DEFAULT 0,
  status TINYINT DEFAULT 1,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (zone_id) REFERENCES storage_zone(id)
);

-- 商品SKU表
CREATE TABLE IF NOT EXISTS product_sku (
  id INT PRIMARY KEY AUTO_INCREMENT,
  sku_code VARCHAR(100) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  specification VARCHAR(255),
  supplier_id INT,
  is_batch_managed TINYINT DEFAULT 0,
  is_expiry_managed TINYINT DEFAULT 0,
  shelf_life_days INT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

-- 2. 业务单据表

-- 入库单表（主单）
CREATE TABLE IF NOT EXISTS inbound_order (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(50) NOT NULL UNIQUE,
  warehouse_id INT NOT NULL,
  supplier_id INT NOT NULL,
  status TINYINT NOT NULL,
  total_expected_quantity INT,
  total_received_quantity INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
  FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

-- 入库单明细表
CREATE TABLE IF NOT EXISTS inbound_order_item (
  id INT PRIMARY KEY AUTO_INCREMENT,
  inbound_order_id INT NOT NULL,
  product_sku_id INT NOT NULL,
  expected_quantity INT NOT NULL,
  received_quantity INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (inbound_order_id) REFERENCES inbound_order(id),
  FOREIGN KEY (product_sku_id) REFERENCES product_sku(id)
);

-- 上架任务表（由入库单生成）
CREATE TABLE IF NOT EXISTS putaway_task (
  id INT PRIMARY KEY AUTO_INCREMENT,
  task_no VARCHAR(50) NOT NULL UNIQUE,
  inbound_order_item_id INT NOT NULL,
  from_location_id INT,
  to_location_id INT NOT NULL,
  quantity INT NOT NULL,
  status TINYINT DEFAULT 1,
  operator INT,
  completed_time DATETIME,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (inbound_order_item_id) REFERENCES inbound_order_item(id),
  FOREIGN KEY (from_location_id) REFERENCES storage_location(id),
  FOREIGN KEY (to_location_id) REFERENCES storage_location(id)
);

-- 3. 库存核心表

-- 库存明细表（最核心的表，记录每个库位上每个批次的库存）
CREATE TABLE IF NOT EXISTS inventory (
  id INT PRIMARY KEY AUTO_INCREMENT,
  warehouse_id INT NOT NULL,
  location_id INT NOT NULL,
  product_sku_id INT NOT NULL,
  batch_no VARCHAR(100),
  production_date DATE,
  expiry_date DATE,
  quantity INT NOT NULL,
  locked_quantity INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT unique_stock UNIQUE (warehouse_id, location_id, product_sku_id, batch_no),
  FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
  FOREIGN KEY (location_id) REFERENCES storage_location(id),
  FOREIGN KEY (product_sku_id) REFERENCES product_sku(id)
);

-- 库存流水表（记录所有库存变动）
CREATE TABLE IF NOT EXISTS inventory_transaction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_sku_id INT NOT NULL,
  batch_no VARCHAR(100),
  warehouse_id INT NOT NULL,
  location_id INT NOT NULL,
  transaction_type TINYINT NOT NULL,
  related_order_no VARCHAR(100),
  quantity_change INT NOT NULL,
  quantity_after INT NOT NULL,
  transaction_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  operator INT,
  FOREIGN KEY (product_sku_id) REFERENCES product_sku(id),
  FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
  FOREIGN KEY (location_id) REFERENCES storage_location(id)
);

-- 4. 出库核心表

-- 出库单表（销售订单）
CREATE TABLE IF NOT EXISTS outbound_order (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(50) NOT NULL UNIQUE,
  warehouse_id INT NOT NULL,
  customer_id INT NOT NULL,
  status TINYINT NOT NULL,
  customer_info VARCHAR(500),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
  FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- 出库单明细表
CREATE TABLE IF NOT EXISTS outbound_order_item (
  id INT PRIMARY KEY AUTO_INCREMENT,
  outbound_order_id INT NOT NULL,
  product_sku_id INT NOT NULL,
  quantity INT NOT NULL,
  allocated_quantity INT DEFAULT 0,
  picked_quantity INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (outbound_order_id) REFERENCES outbound_order(id),
  FOREIGN KEY (product_sku_id) REFERENCES product_sku(id)
);

-- 拣货任务表
CREATE TABLE IF NOT EXISTS picking_task (
  id INT PRIMARY KEY AUTO_INCREMENT,
  task_no VARCHAR(50) NOT NULL UNIQUE,
  wave_no VARCHAR(50),
  outbound_order_id INT NOT NULL,
  product_sku_id INT NOT NULL,
  from_location_id INT NOT NULL,
  quantity INT NOT NULL,
  status TINYINT DEFAULT 1,
  picked_quantity INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (outbound_order_id) REFERENCES outbound_order(id),
  FOREIGN KEY (product_sku_id) REFERENCES product_sku(id),
  FOREIGN KEY (from_location_id) REFERENCES storage_location(id)
);