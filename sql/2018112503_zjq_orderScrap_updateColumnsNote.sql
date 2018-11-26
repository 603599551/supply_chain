ALTER TABLE `s_order_scrap`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_order_scrap`
	COMMENT='门店废弃单表',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `store_id` `store_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '门店id' AFTER `id`,
	CHANGE COLUMN `num` `num` VARCHAR(50) NULL DEFAULT NULL COMMENT '订单id' AFTER `store_id`,
	CHANGE COLUMN `order_date` `order_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '订单创建日期' AFTER `num`,
	CHANGE COLUMN `arrive_date` `arrive_date` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单完成日期' AFTER `order_date`,
	CHANGE COLUMN `order_item` `order_item` TEXT NULL COMMENT '订单项' AFTER `arrive_date`,
	CHANGE COLUMN `store_color` `store_color` VARCHAR(20) NULL DEFAULT NULL COMMENT '门店颜色' AFTER `order_item`,
	CHANGE COLUMN `order_state` `order_state` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单状态' AFTER `store_color`,
	CHANGE COLUMN `create_id` `create_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '创建人id' AFTER `order_state`,
	CHANGE COLUMN `create_date` `create_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建日期' AFTER `create_id`,
	CHANGE COLUMN `logistics_id` `logistics_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '物流处理人id' AFTER `create_date`,
	CHANGE COLUMN `logistics_date` `logistics_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '物流处理日期' AFTER `logistics_id`,
	CHANGE COLUMN `order_type` `order_type` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单类型' AFTER `logistics_date`,
	CHANGE COLUMN `close_date` `close_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '订单关闭日期' AFTER `order_type`,
	CHANGE COLUMN `close_reason` `close_reason` TEXT NULL COMMENT '订单关闭原因' AFTER `close_date`,
	CHANGE COLUMN `close_id` `close_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '关闭人id' AFTER `close_reason`,
	CHANGE COLUMN `city` `city` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单发起城市' AFTER `close_id`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '备注' AFTER `city`,
	CHANGE COLUMN `image` `image` TEXT NULL COMMENT '废弃证据，图片' AFTER `remark`,
	CHANGE COLUMN `scrap_reason` `scrap_reason` TEXT NULL COMMENT '废弃原因' AFTER `image`;

ALTER TABLE `s_warehouse_movement_order`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_warehouse_movement_order`
	COMMENT='移库表',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `warehourse_to_id` `warehourse_to_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '目标仓库id' AFTER `id`,
	CHANGE COLUMN `warehourse_from_id` `warehourse_from_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '原仓库id' AFTER `warehourse_to_id`,
	CHANGE COLUMN `num` `num` VARCHAR(100) NULL DEFAULT NULL COMMENT '移库单号' AFTER `warehourse_from_id`,
	CHANGE COLUMN `out_date` `out_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '移库时间' AFTER `num`,
	CHANGE COLUMN `create_id` `create_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '创建人id' AFTER `out_date`,
	CHANGE COLUMN `create_date` `create_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建时间' AFTER `create_id`,
	CHANGE COLUMN `state` `state` VARCHAR(100) NULL DEFAULT NULL COMMENT '状态' AFTER `create_date`,
	CHANGE COLUMN `order_item` `order_item` TEXT NULL COMMENT '原材料项' AFTER `state`,
	CHANGE COLUMN `end_date` `end_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '结束时间' AFTER `order_item`;
	UPDATE `supply_chain`.`s_dictionary` SET `name`='已撤销' WHERE  `id`='9040';
UPDATE `supply_chain`.`s_dictionary` SET `value`='revoked' WHERE  `id`='9040';

