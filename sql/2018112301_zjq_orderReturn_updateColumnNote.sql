ALTER TABLE `s_order_return`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_order_return`
	COMMENT='订单表-退货单',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `store_id` `store_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '门店id' AFTER `id`,
	CHANGE COLUMN `num` `num` VARCHAR(50) NULL DEFAULT NULL COMMENT '订单编号' AFTER `store_id`,
	CHANGE COLUMN `order_date` `order_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '订单日期' AFTER `num`,
	CHANGE COLUMN `arrive_date` `arrive_date` VARCHAR(100) NULL DEFAULT NULL COMMENT '到货日期' AFTER `order_date`,
	CHANGE COLUMN `order_item` `order_item` TEXT NULL COMMENT '订单项' AFTER `arrive_date`,
	CHANGE COLUMN `store_color` `store_color` VARCHAR(20) NULL DEFAULT NULL COMMENT '门店颜色' AFTER `order_item`,
	CHANGE COLUMN `order_state` `order_state` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单状态' AFTER `store_color`,
	CHANGE COLUMN `create_id` `create_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '创建人id' AFTER `order_state`,
	CHANGE COLUMN `create_date` `create_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建时间' AFTER `create_id`,
	CHANGE COLUMN `logistics_id` `logistics_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '物流处理人id' AFTER `create_date`,
	CHANGE COLUMN `logistics_date` `logistics_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '物流处理日期' AFTER `logistics_id`,
	CHANGE COLUMN `order_type` `order_type` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单类型（门店废弃，或是送到物流再废弃）' AFTER `logistics_date`,
	CHANGE COLUMN `close_date` `close_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '订单关闭日期' AFTER `order_type`,
	CHANGE COLUMN `close_reason` `close_reason` TEXT NULL COMMENT '关闭原因' AFTER `close_date`,
	CHANGE COLUMN `close_id` `close_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单关闭人id' AFTER `close_reason`,
	CHANGE COLUMN `city` `city` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单发起城市（用于后期计划物流成本）' AFTER `close_id`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '备注' AFTER `city`,
	CHANGE COLUMN `image` `image` TEXT NULL COMMENT '退货证据（图片）' AFTER `remark`,
	CHANGE COLUMN `return_reason` `return_reason` TEXT NULL COMMENT '退货原因 ' AFTER `image`;
