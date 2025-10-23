-- WMS系统数据库表结构定义（按文档与实体统一）

-- 完整重建：删除所有已存在表（先禁用外键约束，按依赖顺序删除）
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `quote_item`;

DROP TABLE IF EXISTS `quote`;

DROP TABLE IF EXISTS `packing_task`;

DROP TABLE IF EXISTS `packing_material`;

DROP TABLE IF EXISTS `picking_task`;

DROP TABLE IF EXISTS `picking_wave`;

DROP TABLE IF EXISTS `review_task`;

DROP TABLE IF EXISTS `outbound_order_item`;

DROP TABLE IF EXISTS `outbound_order`;

DROP TABLE IF EXISTS `inventory_transaction`;

DROP TABLE IF EXISTS `inventory`;

DROP TABLE IF EXISTS `inventory_count_item`;

DROP TABLE IF EXISTS `inventory_count`;

DROP TABLE IF EXISTS `putaway_task`;

DROP TABLE IF EXISTS `inbound_order_item`;

DROP TABLE IF EXISTS `inbound_order`;

DROP TABLE IF EXISTS `inbound_qc`;

DROP TABLE IF EXISTS `inbound_appointment`;

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
    `total_capacity` DECIMAL(12, 2) NULL COMMENT '总容量',
    `used_capacity` DECIMAL(12, 2) NULL COMMENT '已用容量',
    `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用'
) COMMENT='仓库表';

-- 供应商表（扩展邮箱、评级）
CREATE TABLE IF NOT EXISTS `supplier` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `supplier_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
    `supplier_name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
    `contact_person` VARCHAR(50) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `email` VARCHAR(100) COMMENT '邮箱',
    `address` VARCHAR(255) COMMENT '地址',
    `rating` INT DEFAULT 3 COMMENT '评级（1:A 2:B 3:C 4:D）',
    `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用'
) COMMENT='供应商表';

-- 客户表（扩展类型、信用）
CREATE TABLE IF NOT EXISTS `customer` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `customer_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '客户编码',
    `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
    `customer_type` INT DEFAULT 2 COMMENT '客户类型（1个人 2企业 3代理商 4经销商）',
    `contact_person` VARCHAR(50) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `email` VARCHAR(100) COMMENT '邮箱',
    `address` VARCHAR(255) COMMENT '地址',
    `credit_rating` INT DEFAULT 3 COMMENT '信用等级（1:AAA 2:AA 3:A 4:B 5:C）',
    `credit_limit` DECIMAL(12, 2) NULL COMMENT '信用额度',
    `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用'
) COMMENT='客户表';

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
    `zone_type` INT NOT NULL COMMENT '库区类型（1存储 2收货 3发货 6拣货...）',
    `capacity` DECIMAL(12, 2) NULL COMMENT '库区容量',
    `used_capacity` DECIMAL(12, 2) NULL COMMENT '已用容量',
    `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用',
    UNIQUE KEY `uk_zone_code` (`zone_code`),
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`)
) COMMENT='库区表';

CREATE TABLE IF NOT EXISTS `storage_location` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `zone_id` BIGINT NOT NULL COMMENT '所属库区ID',
    `location_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '库位编码（如A-01-01-01）',
    `location_name` VARCHAR(100) COMMENT '库位名称',
    `location_type` INT DEFAULT 1 COMMENT '库位类型（1货架 2地面 3冷藏 4危险）',
    `capacity` DECIMAL(12, 2) COMMENT '容量',
    `current_volume` DECIMAL(12, 2) DEFAULT 0 COMMENT '当前占用容量',
    `status` INT DEFAULT 1 COMMENT '状态（1空闲 2占用 3禁用）',
    UNIQUE KEY `uk_location_code` (`location_code`),
    FOREIGN KEY (`zone_id`) REFERENCES `storage_zone` (`id`)
) COMMENT='库位表';

-- 商品SKU表（按文档字段）
CREATE TABLE IF NOT EXISTS `product_sku` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `sku_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '商品SKU编码',
    `sku_name` VARCHAR(255) NOT NULL COMMENT '商品名称',
    `specification` VARCHAR(255) COMMENT '规格',
    `brand` VARCHAR(100) COMMENT '品牌',
    `category_id` BIGINT NULL COMMENT '分类ID',
    `supplier_id` BIGINT NULL COMMENT '默认供应商ID',
    `barcode` VARCHAR(100) COMMENT '条码',
    `weight` DECIMAL(12, 4) NULL COMMENT '重量',
    `volume` DECIMAL(12, 6) NULL COMMENT '体积',
    `is_batch_managed` BIT(1) DEFAULT b'0' COMMENT '是否批次管理',
    `is_expiry_managed` BIT(1) DEFAULT b'0' COMMENT '是否保质期管理',
    `shelf_life_days` INT NULL COMMENT '保质期天数',
    `safety_stock` INT NULL COMMENT '安全库存',
    `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用',
    FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) COMMENT='商品SKU表';

-- 2. 业务单据表

-- 入库单表（主单）
CREATE TABLE IF NOT EXISTS `inbound_order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    `warehouse_id` BIGINT NOT NULL COMMENT '目标仓库',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商',
    `status` INT NOT NULL COMMENT '状态（1：待收货， 2：部分收货， 3：已完成， 4：已取消）',
    `total_expected_quantity` INT COMMENT '预期总数量',
    `total_received_quantity` INT DEFAULT 0 COMMENT '实际收货总数量',
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`),
    FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) COMMENT='入库单表';

-- 入库单明细表
CREATE TABLE IF NOT EXISTS `inbound_order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `inbound_order_id` BIGINT NOT NULL,
    `product_sku_id` BIGINT NOT NULL,
    `expected_quantity` INT NOT NULL COMMENT '预期数量',
    `received_quantity` INT DEFAULT 0 COMMENT '已收数量',
    `unit_price` DECIMAL(10,2) COMMENT '单价',
    `batch_no` VARCHAR(100) COMMENT '批次号',
    `production_date` DATE COMMENT '生产日期',
    `expiry_date` DATE COMMENT '过期日期',
    FOREIGN KEY (`inbound_order_id`) REFERENCES `inbound_order` (`id`),
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`)
) COMMENT='入库单明细表';

-- 入库预约表
CREATE TABLE IF NOT EXISTS `inbound_appointment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `appointment_no` VARCHAR(50) NOT NULL UNIQUE,
    `warehouse_id` BIGINT NOT NULL,
    `supplier_id` BIGINT NOT NULL,
    `appointment_date` DATE NOT NULL,
    `appointment_time_start` TIME NOT NULL,
    `appointment_time_end` TIME NOT NULL,
    `status` INT NOT NULL COMMENT '1待审核 2已审核 3已拒绝 4已取消 5已完成',
    `total_expected_quantity` INT DEFAULT 0,
    `special_requirements` VARCHAR(500),
    `approved_by` BIGINT,
    `approved_time` TIMESTAMP NULL,
    `remark` VARCHAR(255),
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`),
    FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) COMMENT='入库预约表';

-- 入库预约明细表
CREATE TABLE IF NOT EXISTS `inbound_appointment_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `appointment_id` BIGINT NOT NULL,
    `product_sku_id` BIGINT NOT NULL,
    `expected_quantity` INT NOT NULL,
    `unit_price` DECIMAL(10,2),
    `batch_no` VARCHAR(100),
    `production_date` DATE,
    `expiry_date` DATE,
    FOREIGN KEY (`appointment_id`) REFERENCES `inbound_appointment` (`id`),
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`)
) COMMENT='入库预约明细表';

-- 入库质检表
CREATE TABLE IF NOT EXISTS `inbound_qc` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `inbound_order_item_id` BIGINT NOT NULL,
    `status` INT NOT NULL COMMENT '1待质检 2已完成',
    `qualified_quantity` INT DEFAULT 0,
    `unqualified_quantity` INT DEFAULT 0,
    `remark` VARCHAR(255),
    FOREIGN KEY (`inbound_order_item_id`) REFERENCES `inbound_order_item` (`id`)
) COMMENT='入库质检表';

-- 上架任务表（由入库单生成）
CREATE TABLE IF NOT EXISTS `putaway_task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `task_no` VARCHAR(50) NOT NULL UNIQUE,
    `inbound_order_item_id` BIGINT NOT NULL COMMENT '关联的入库明细',
    `from_location_id` BIGINT COMMENT '来源库位（通常是收货暂存区）',
    `to_location_id` BIGINT NOT NULL COMMENT '目标上架库位',
    `quantity` INT NOT NULL COMMENT '上架数量',
    `status` INT DEFAULT 1 COMMENT '状态（1：待执行， 2：已完成）',
    `operator` INT COMMENT '操作员',
    `completed_time` DATETIME,
    FOREIGN KEY (`inbound_order_item_id`) REFERENCES `inbound_order_item` (`id`),
    FOREIGN KEY (`from_location_id`) REFERENCES `storage_location` (`id`),
    FOREIGN KEY (`to_location_id`) REFERENCES `storage_location` (`id`)
) COMMENT='上架任务表';

-- 3. 库存核心表

CREATE TABLE IF NOT EXISTS `inventory` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `warehouse_id` BIGINT NOT NULL,
    `location_id` BIGINT NOT NULL,
    `product_sku_id` BIGINT NOT NULL,
    `batch_no` VARCHAR(100) COMMENT '批次号',
    `production_date` DATE COMMENT '生产日期',
    `expiry_date` DATE COMMENT '过期日期',
    `quantity` INT NOT NULL COMMENT '当前数量',
    `locked_quantity` INT DEFAULT 0 COMMENT '锁定数量（用于出库）',
    UNIQUE KEY `unique_stock` (
        `warehouse_id`,
        `location_id`,
        `product_sku_id`,
        `batch_no`
    ),
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`),
    FOREIGN KEY (`location_id`) REFERENCES `storage_location` (`id`),
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`)
) COMMENT='库存明细表';

-- 盘点主表
CREATE TABLE IF NOT EXISTS `inventory_count` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `count_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '盘点单号',
    `warehouse_id` BIGINT NOT NULL,
    `status` INT NOT NULL DEFAULT 1 COMMENT '1草稿 2进行中 3已提交 4已完成',
    `remark` VARCHAR(255),
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`)
) COMMENT='库存盘点主表';

-- 盘点明细表
CREATE TABLE IF NOT EXISTS `inventory_count_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `count_id` BIGINT NOT NULL,
    `location_id` BIGINT NOT NULL,
    `product_sku_id` BIGINT NOT NULL,
    `batch_no` VARCHAR(100),
    `system_qty` INT,
    `counted_qty` INT,
    `difference_qty` INT,
    FOREIGN KEY (`count_id`) REFERENCES `inventory_count` (`id`),
    FOREIGN KEY (`location_id`) REFERENCES `storage_location` (`id`),
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`)
) COMMENT='库存盘点明细表';

CREATE TABLE IF NOT EXISTS `inventory_transaction` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `product_sku_id` BIGINT NOT NULL,
    `batch_no` VARCHAR(100),
    `warehouse_id` BIGINT NOT NULL,
    `location_id` BIGINT NOT NULL,
    `transaction_type` INT NOT NULL COMMENT '流水类型（1：入库， 2：出库， 3：移库， 4：盘点调整）',
    `related_order_no` VARCHAR(100) COMMENT '关联业务单号',
    `quantity_change` INT NOT NULL COMMENT '数量变化（正数为增，负数为减）',
    `quantity_after` INT NOT NULL COMMENT '变化后结余数量',
    `transaction_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `operator` INT COMMENT '操作人',
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`),
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`),
    FOREIGN KEY (`location_id`) REFERENCES `storage_location` (`id`)
) COMMENT='库存流水表';

-- 4. 出库核心表

CREATE TABLE IF NOT EXISTS `outbound_order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `order_no` VARCHAR(50) NOT NULL UNIQUE,
    `warehouse_id` BIGINT NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `status` INT NOT NULL COMMENT '状态（1：待处理， 2：已分配库存， 3：拣货中， 4：已发货）',
    `customer_info` VARCHAR(500) COMMENT '客户信息',
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`),
    FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) COMMENT='出库单表';

CREATE TABLE IF NOT EXISTS `outbound_order_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `outbound_order_id` BIGINT NOT NULL,
    `product_sku_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL COMMENT '需求数量',
    `allocated_quantity` INT DEFAULT 0 COMMENT '已分配库存数量',
    `picked_quantity` INT DEFAULT 0 COMMENT '已拣选数量',
    FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order` (`id`),
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`)
) COMMENT='出库单明细表';

-- 拣货波次表
CREATE TABLE IF NOT EXISTS `picking_wave` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `wave_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '波次号',
    `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
    `status` INT DEFAULT 1 COMMENT '状态（1：待执行，2：执行中，3：已完成）',
    `order_count` INT DEFAULT 0 COMMENT '包含的订单数量',
    `task_count` INT DEFAULT 0 COMMENT '包含的任务数量',
    `completed_task_count` INT DEFAULT 0 COMMENT '已完成的任务数量',
    `started_time` TIMESTAMP NULL COMMENT '开始执行时间',
    `completed_time` TIMESTAMP NULL COMMENT '完成时间',
    `operator_id` BIGINT COMMENT '操作员ID',
    `operator_name` VARCHAR(50) COMMENT '操作员姓名',
    `remark` VARCHAR(500) COMMENT '备注',
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`)
) COMMENT='拣货波次表';

CREATE TABLE IF NOT EXISTS `picking_task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `task_no` VARCHAR(50) NOT NULL UNIQUE,
    `wave_no` VARCHAR(50) COMMENT '波次号',
    `picking_wave_id` BIGINT COMMENT '波次ID',
    `outbound_order_id` BIGINT NOT NULL,
    `product_sku_id` BIGINT NOT NULL,
    `from_location_id` BIGINT NOT NULL COMMENT '拣货库位',
    `quantity` INT NOT NULL COMMENT '需拣选数量',
    `status` INT DEFAULT 1 COMMENT '状态（1：待拣选， 2：部分完成， 3：已完成）',
    `picked_quantity` INT DEFAULT 0,
    FOREIGN KEY (`picking_wave_id`) REFERENCES `picking_wave` (`id`),
    FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order` (`id`),
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`),
    FOREIGN KEY (`from_location_id`) REFERENCES `storage_location` (`id`)
) COMMENT='拣货任务表';

-- 复核任务表
CREATE TABLE IF NOT EXISTS `review_task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `task_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编号',
    `outbound_order_id` BIGINT NOT NULL COMMENT '出库单ID',
    `product_sku_id` BIGINT NOT NULL COMMENT '商品SKU ID',
    `expected_quantity` INT NOT NULL COMMENT '预期数量',
    `actual_quantity` INT DEFAULT 0 COMMENT '实际数量',
    `status` INT DEFAULT 1 COMMENT '状态（1：待复核，2：复核中，3：复核完成，4：复核异常）',
    `reviewer_id` BIGINT COMMENT '复核员ID',
    `reviewer_name` VARCHAR(50) COMMENT '复核员姓名',
    `review_time` TIMESTAMP NULL COMMENT '复核时间',
    `remark` VARCHAR(500) COMMENT '备注',
    FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order` (`id`),
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`)
) COMMENT='复核任务表';

-- 打包任务表
CREATE TABLE IF NOT EXISTS `packing_task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `task_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编号',
    `outbound_order_id` BIGINT NOT NULL COMMENT '出库单ID',
    `packing_material_id` BIGINT COMMENT '包装材料ID',
    `weight` DECIMAL(12, 4) COMMENT '重量(kg)',
    `volume` DECIMAL(12, 6) COMMENT '体积(m³)',
    `dimensions` VARCHAR(100) COMMENT '尺寸(长x宽x高)',
    `status` INT DEFAULT 1 COMMENT '状态（1：待打包，2：打包中，3：已完成，4：已取消）',
    `packer_id` BIGINT COMMENT '打包员ID',
    `packer_name` VARCHAR(50) COMMENT '打包员姓名',
    `packed_time` TIMESTAMP NULL COMMENT '打包完成时间',
    `remark` VARCHAR(500) COMMENT '备注',
    FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order` (`id`)
) COMMENT='打包任务表';

-- 包装材料表
CREATE TABLE IF NOT EXISTS `packing_material` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `material_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '材料编码',
    `material_name` VARCHAR(100) NOT NULL COMMENT '材料名称',
    `material_type` INT NOT NULL COMMENT '材料类型（1：纸箱，2：泡沫箱，3：塑料袋，4：木箱，5：其他）',
    `specification` VARCHAR(100) COMMENT '规格',
    `unit_price` DECIMAL(10, 2) COMMENT '单价',
    `unit` VARCHAR(20) DEFAULT '个' COMMENT '单位',
    `is_enabled` BIT(1) DEFAULT b'1' COMMENT '是否启用',
    `remark` VARCHAR(255) COMMENT '备注'
) COMMENT='包装材料表';

-- 出库费用表
CREATE TABLE IF NOT EXISTS `outbound_charge` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `outbound_order_id` BIGINT NOT NULL,
    `charge_type` INT NOT NULL COMMENT '费用类型（1运费 2安装费 3保险费 99其他）',
    `amount` DECIMAL(12, 2) NOT NULL,
    `tax_rate` DECIMAL(5, 2) NULL,
    `currency` VARCHAR(10) DEFAULT 'CNY',
    `remark` VARCHAR(255),
    FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order` (`id`)
) COMMENT='出库费用表';

-- 费用项字典表
CREATE TABLE IF NOT EXISTS `charge_dict` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `charge_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '费用编码，如 FREIGHT',
    `charge_name` VARCHAR(100) NOT NULL COMMENT '费用名称',
    `default_tax_rate` DECIMAL(5, 2) NULL,
    `is_enabled` TINYINT DEFAULT 1 COMMENT '是否启用',
    `remark` VARCHAR(255)
) COMMENT='费用项字典表';

-- 结算主表
CREATE TABLE IF NOT EXISTS `settlement` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `settlement_no` VARCHAR(50) NOT NULL UNIQUE,
    `customer_id` BIGINT NOT NULL,
    `period_start` DATETIME NULL,
    `period_end` DATETIME NULL,
    `status` INT NOT NULL COMMENT '1草稿 2待审核 3已审核 4已结清 5已作废',
    `currency` VARCHAR(10) DEFAULT 'CNY',
    `amount_goods` DECIMAL(12, 2) NOT NULL DEFAULT 0,
    `amount_charges` DECIMAL(12, 2) NOT NULL DEFAULT 0,
    `amount_total` DECIMAL(12, 2) NOT NULL DEFAULT 0,
    `remark` VARCHAR(255),
    FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) COMMENT='结算主表';

-- 结算明细表
CREATE TABLE IF NOT EXISTS `settlement_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `settlement_id` BIGINT NOT NULL,
    `outbound_order_id` BIGINT NOT NULL,
    `amount_goods` DECIMAL(12, 2) NOT NULL DEFAULT 0,
    `amount_charges` DECIMAL(12, 2) NOT NULL DEFAULT 0,
    `amount_total` DECIMAL(12, 2) NOT NULL DEFAULT 0,
    `remark` VARCHAR(255),
    FOREIGN KEY (`settlement_id`) REFERENCES `settlement` (`id`),
    FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order` (`id`)
) COMMENT='结算明细表';

-- 6. 销售/报价表
CREATE TABLE IF NOT EXISTS `quote` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `quote_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '报价单号',
    `customer_id` BIGINT NOT NULL,
    `currency` VARCHAR(10) DEFAULT 'CNY',
    `valid_from` DATETIME NOT NULL,
    `valid_to` DATETIME NOT NULL,
    `status` INT NOT NULL COMMENT '1草稿 2待审核 3已生效 4已失效 5已作废',
    `amount_total` DECIMAL(12, 2) NOT NULL DEFAULT 0,
    `remark` VARCHAR(255),
    FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) COMMENT='报价单主表';

CREATE TABLE IF NOT EXISTS `quote_item` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `created_by` VARCHAR(50),
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_by` VARCHAR(50),
    `updated_time` TIMESTAMP NULL,
    `deleted` TINYINT DEFAULT 0,
    `quote_id` BIGINT NOT NULL,
    `product_sku_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(12, 2) NOT NULL,
    `discount_rate` DECIMAL(5, 2) NOT NULL DEFAULT 0 COMMENT '折扣%（0-100）',
    `tax_rate` DECIMAL(5, 2) NULL COMMENT '税率%（0-100）',
    `amount_subtotal` DECIMAL(12, 2) NOT NULL DEFAULT 0,
    `remark` VARCHAR(255),
    FOREIGN KEY (`quote_id`) REFERENCES `quote` (`id`),
    FOREIGN KEY (`product_sku_id`) REFERENCES `product_sku` (`id`)
) COMMENT='报价单明细表';

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
    `status` INT NOT NULL,
    `role` VARCHAR(20) NOT NULL
) COMMENT='用户表';