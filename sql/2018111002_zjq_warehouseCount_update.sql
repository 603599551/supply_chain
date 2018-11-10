ALTER TABLE `s_warehouse_count`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_warehouse_count`
	COMMENT='仓库盘点-单据',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `warehouse_id` `warehouse_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '仓库ID' AFTER `id`,
	CHANGE COLUMN `num` `num` VARCHAR(50) NULL DEFAULT NULL COMMENT '仓库盘点编号' AFTER `warehouse_id`,
	CHANGE COLUMN `count_date` `count_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '仓库盘点日期' AFTER `num`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '仓库盘点备注' AFTER `count_date`,
	CHANGE COLUMN `count_item` `count_item` TEXT NULL COMMENT '仓库盘点项' AFTER `remark`,
	CHANGE COLUMN `count_id` `count_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '仓库盘点人ID' AFTER `count_item`,
	CHANGE COLUMN `sort` `sort` INT(11) NULL DEFAULT NULL COMMENT '排序' AFTER `count_id`;
