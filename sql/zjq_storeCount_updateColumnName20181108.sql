ALTER TABLE `s_store_count`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_store_count`
	COMMENT='门店盘点-单据',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `store_id` `store_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '门店ID' AFTER `id`,
	CHANGE COLUMN `num` `num` VARCHAR(50) NULL DEFAULT NULL COMMENT '盘点编号' AFTER `store_id`,
	CHANGE COLUMN `count_date` `count_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '盘点日期' AFTER `num`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '盘点备注' AFTER `count_date`,
	CHANGE COLUMN `count_item` `count_item` TEXT NULL COMMENT '盘点项' AFTER `remark`,
	CHANGE COLUMN `store_color` `store_color` VARCHAR(20) NULL DEFAULT NULL COMMENT '门店颜色' AFTER `count_item`,
	CHANGE COLUMN `count_id` `count_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '盘点人ID' AFTER `store_color`,
	CHANGE COLUMN `sort` `sort` INT(11) NULL DEFAULT NULL COMMENT '排序' AFTER `count_id`;
