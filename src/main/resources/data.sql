-- WMS 两天模拟数据（今天/昨天）
SET NAMES utf8mb4;

SET time_zone = '+08:00';

SET @today := DATE(NOW());

SET @yday := DATE_SUB(@today, INTERVAL 1 DAY);

-- 为保证可重复执行，清空数据（注意外键顺序）
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE picking_task;

TRUNCATE TABLE outbound_order_item;

TRUNCATE TABLE outbound_order;

TRUNCATE TABLE inventory_transaction;

TRUNCATE TABLE inventory;

TRUNCATE TABLE putaway_task;

TRUNCATE TABLE inbound_order_item;

TRUNCATE TABLE inbound_order;

TRUNCATE TABLE product_sku;

TRUNCATE TABLE storage_location;

TRUNCATE TABLE storage_zone;

TRUNCATE TABLE warehouse;

TRUNCATE TABLE customer;

TRUNCATE TABLE supplier;

SET FOREIGN_KEY_CHECKS = 1;

-- 1) 基础主数据
INSERT INTO
    supplier (
        name,
        code,
        contact_person,
        phone,
        address,
        is_enabled,
        created_time
    )
VALUES (
        '华北电子',
        'SUP001',
        '张三',
        '010-10000001',
        '北京海淀',
        1,
        CONCAT(@yday, ' 09:00:00')
    ),
    (
        '华东制造',
        'SUP002',
        '李四',
        '021-10000002',
        '上海浦东',
        1,
        CONCAT(@yday, ' 09:00:00')
    );

INSERT INTO
    customer (
        name,
        code,
        contact_person,
        phone,
        address,
        is_enabled,
        created_time
    )
VALUES (
        '京东商城',
        'CUST001',
        '王一',
        '400-000-0001',
        '北京朝阳',
        1,
        CONCAT(@yday, ' 09:00:00')
    ),
    (
        '天猫超市',
        'CUST002',
        '赵六',
        '400-000-0002',
        '杭州余杭',
        1,
        CONCAT(@yday, ' 09:00:00')
    );

INSERT INTO
    warehouse (
        name,
        code,
        address,
        is_enabled,
        created_time
    )
VALUES (
        '北京主仓',
        'WH001',
        '北京通州物流园',
        1,
        CONCAT(@yday, ' 09:05:00')
    );

-- 仓库ID 1
INSERT INTO
    storage_zone (
        warehouse_id,
        name,
        type,
        created_time
    )
VALUES (
        1,
        '收货区',
        2,
        CONCAT(@yday, ' 09:10:00')
    ), -- id=1
    (
        1,
        '存储区A',
        1,
        CONCAT(@yday, ' 09:10:00')
    ), -- id=2
    (
        1,
        '拣货区',
        6,
        CONCAT(@yday, ' 09:10:00')
    );
-- id=3

-- zone 2 为存储区
INSERT INTO
    storage_location (
        zone_id,
        location_code,
        capacity,
        current_volume,
        status,
        created_time
    )
VALUES (
        1,
        'BJ-RCV-01',
        100.00,
        0.00,
        1,
        CONCAT(@yday, ' 09:15:00')
    ), -- id=1 收货
    (
        2,
        'BJ-A-01-01',
        50.00,
        0.00,
        1,
        CONCAT(@yday, ' 09:15:00')
    ), -- id=2 存储
    (
        2,
        'BJ-A-01-02',
        50.00,
        0.00,
        1,
        CONCAT(@yday, ' 09:15:00')
    ), -- id=3 存储
    (
        3,
        'BJ-PICK-01',
        30.00,
        0.00,
        1,
        CONCAT(@yday, ' 09:15:00')
    );
-- id=4 拣货

-- SKU
INSERT INTO
    product_sku (
        sku_code,
        name,
        specification,
        supplier_id,
        is_batch_managed,
        is_expiry_managed,
        shelf_life_days,
        created_time
    )
VALUES (
        'SKU1001',
        '智能手环',
        '黑色 标准款',
        1,
        0,
        0,
        NULL,
        CONCAT(@yday, ' 09:20:00')
    ), -- id=1
    (
        'SKU1002',
        '蓝牙耳机',
        '降噪版',
        1,
        0,
        0,
        NULL,
        CONCAT(@yday, ' 09:20:00')
    ), -- id=2
    (
        'SKU1003',
        '有机大米',
        '5kg',
        2,
        1,
        1,
        365,
        CONCAT(@yday, ' 09:20:00')
    );
-- id=3

-- 2) 昨天：入库 → 上架 → 形成库存与入库流水
-- 入库单（完成）
INSERT INTO
    inbound_order (
        order_no,
        warehouse_id,
        supplier_id,
        status,
        total_expected_quantity,
        total_received_quantity,
        created_time
    )
VALUES (
        CONCAT(
            'IN',
            DATE_FORMAT(@yday, '%Y%m%d'),
            '001'
        ),
        1,
        1,
        3,
        160,
        160,
        CONCAT(@yday, ' 10:00:00')
    );
-- id=1

-- 明细：手环(60)、耳机(50)、大米(50)
INSERT INTO
    inbound_order_item (
        inbound_order_id,
        product_sku_id,
        expected_quantity,
        received_quantity,
        created_time
    )
VALUES (
        1,
        1,
        60,
        60,
        CONCAT(@yday, ' 10:05:00')
    ), -- id=1
    (
        1,
        2,
        50,
        50,
        CONCAT(@yday, ' 10:05:00')
    ), -- id=2
    (
        1,
        3,
        50,
        50,
        CONCAT(@yday, ' 10:05:00')
    );
-- id=3

-- 上架任务（完成）→ 存储区库位2/3
INSERT INTO
    putaway_task (
        task_no,
        inbound_order_item_id,
        from_location_id,
        to_location_id,
        quantity,
        status,
        operator,
        completed_time,
        created_time
    )
VALUES (
        CONCAT(
            'PUT',
            DATE_FORMAT(@yday, '%Y%m%d'),
            '001'
        ),
        1,
        1,
        2,
        60,
        3,
        1,
        CONCAT(@yday, ' 10:30:00'),
        CONCAT(@yday, ' 10:10:00')
    ),
    (
        CONCAT(
            'PUT',
            DATE_FORMAT(@yday, '%Y%m%d'),
            '002'
        ),
        2,
        1,
        3,
        50,
        3,
        1,
        CONCAT(@yday, ' 10:35:00'),
        CONCAT(@yday, ' 10:10:00')
    ),
    (
        CONCAT(
            'PUT',
            DATE_FORMAT(@yday, '%Y%m%d'),
            '003'
        ),
        3,
        1,
        2,
        50,
        3,
        1,
        CONCAT(@yday, ' 10:40:00'),
        CONCAT(@yday, ' 10:10:00')
    );

-- 昨天形成库存
INSERT INTO
    inventory (
        warehouse_id,
        location_id,
        product_sku_id,
        batch_no,
        production_date,
        expiry_date,
        quantity,
        locked_quantity,
        created_time
    )
VALUES (
        1,
        2,
        1,
        'BATCH-HR-001',
        @yday,
        NULL,
        60,
        0,
        CONCAT(@yday, ' 10:45:00')
    ),
    (
        1,
        3,
        2,
        'BATCH-EJ-001',
        @yday,
        NULL,
        50,
        0,
        CONCAT(@yday, ' 10:45:00')
    ),
    (
        1,
        2,
        3,
        'BATCH-MI-001',
        @yday,
        DATE_ADD(@yday, INTERVAL 365 DAY),
        50,
        0,
        CONCAT(@yday, ' 10:45:00')
    );

-- 入库流水（正数）
INSERT INTO
    inventory_transaction (
        product_sku_id,
        batch_no,
        warehouse_id,
        location_id,
        transaction_type,
        related_order_no,
        quantity_change,
        quantity_after,
        transaction_time,
        operator
    )
VALUES (
        1,
        'BATCH-HR-001',
        1,
        2,
        1,
        CONCAT(
            'IN',
            DATE_FORMAT(@yday, '%Y%m%d'),
            '001'
        ),
        60,
        60,
        CONCAT(@yday, ' 10:45:00'),
        1
    ),
    (
        2,
        'BATCH-EJ-001',
        1,
        3,
        1,
        CONCAT(
            'IN',
            DATE_FORMAT(@yday, '%Y%m%d'),
            '001'
        ),
        50,
        50,
        CONCAT(@yday, ' 10:45:00'),
        1
    ),
    (
        3,
        'BATCH-MI-001',
        1,
        2,
        1,
        CONCAT(
            'IN',
            DATE_FORMAT(@yday, '%Y%m%d'),
            '001'
        ),
        50,
        110,
        CONCAT(@yday, ' 10:45:00'),
        1
    );
-- 注：同库位同SKU累计后 quantity_after=110（手环60 + 大米50 共存在 location_id=2）

-- 3) 今天：出库 → 拣货 → 流水（负数）与库存扣减
-- 出库单（已发货）
INSERT INTO
    outbound_order (
        order_no,
        warehouse_id,
        customer_id,
        status,
        customer_info,
        created_time
    )
VALUES (
        CONCAT(
            'OUT',
            DATE_FORMAT(@today, '%Y%m%d'),
            '001'
        ),
        1,
        1,
        4,
        '{"name":"京东商城"}',
        CONCAT(@today, ' 09:00:00')
    );
-- id=1

-- 明细：手环出 10，耳机出 8，有机大米出 12
INSERT INTO
    outbound_order_item (
        outbound_order_id,
        product_sku_id,
        quantity,
        allocated_quantity,
        picked_quantity,
        created_time
    )
VALUES (
        1,
        1,
        10,
        10,
        10,
        CONCAT(@today, ' 09:05:00')
    ),
    (
        1,
        2,
        8,
        8,
        8,
        CONCAT(@today, ' 09:05:00')
    ),
    (
        1,
        3,
        12,
        12,
        12,
        CONCAT(@today, ' 09:05:00')
    );

-- 拣货任务（已完成，拣货从存储区库位）
INSERT INTO
    picking_task (
        task_no,
        wave_no,
        outbound_order_id,
        product_sku_id,
        from_location_id,
        quantity,
        status,
        picked_quantity,
        created_time
    )
VALUES (
        CONCAT(
            'PICK',
            DATE_FORMAT(@today, '%Y%m%d'),
            '001'
        ),
        'WAVE001',
        1,
        1,
        2,
        10,
        3,
        10,
        CONCAT(@today, ' 09:10:00')
    ),
    (
        CONCAT(
            'PICK',
            DATE_FORMAT(@today, '%Y%m%d'),
            '002'
        ),
        'WAVE001',
        1,
        2,
        3,
        8,
        3,
        8,
        CONCAT(@today, ' 09:10:00')
    ),
    (
        CONCAT(
            'PICK',
            DATE_FORMAT(@today, '%Y%m%d'),
            '003'
        ),
        'WAVE001',
        1,
        3,
        2,
        12,
        3,
        12,
        CONCAT(@today, ' 09:10:00')
    );

-- 库存扣减（与出库一致）
UPDATE inventory
SET
    quantity = quantity - 10
WHERE
    warehouse_id = 1
    AND location_id = 2
    AND product_sku_id = 1;
-- 手环 60->50
UPDATE inventory
SET
    quantity = quantity - 8
WHERE
    warehouse_id = 1
    AND location_id = 3
    AND product_sku_id = 2;
-- 耳机 50->42
UPDATE inventory
SET
    quantity = quantity - 12
WHERE
    warehouse_id = 1
    AND location_id = 2
    AND product_sku_id = 3;
-- 大米 50->38

-- 出库流水（负数），quantity_after 按扣减后结存
INSERT INTO
    inventory_transaction (
        product_sku_id,
        batch_no,
        warehouse_id,
        location_id,
        transaction_type,
        related_order_no,
        quantity_change,
        quantity_after,
        transaction_time,
        operator
    )
VALUES (
        1,
        'BATCH-HR-001',
        1,
        2,
        2,
        CONCAT(
            'OUT',
            DATE_FORMAT(@today, '%Y%m%d'),
            '001'
        ),
        -10,
        50,
        CONCAT(@today, ' 09:20:00'),
        1
    ),
    (
        2,
        'BATCH-EJ-001',
        1,
        3,
        2,
        CONCAT(
            'OUT',
            DATE_FORMAT(@today, '%Y%m%d'),
            '001'
        ),
        -8,
        42,
        CONCAT(@today, ' 09:20:00'),
        1
    ),
    (
        3,
        'BATCH-MI-001',
        1,
        2,
        2,
        CONCAT(
            'OUT',
            DATE_FORMAT(@today, '%Y%m%d'),
            '001'
        ),
        -12,
        38,
        CONCAT(@today, ' 09:20:00'),
        1
    );

-- 可选：将对应库位标记占用（示例）
UPDATE storage_location
SET
    status = 2,
    current_volume = 50.00
WHERE
    id = 2;

UPDATE storage_location
SET
    status = 2,
    current_volume = 42.00
WHERE
    id = 3;