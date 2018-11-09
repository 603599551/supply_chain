ALTER TABLE `s_store_stock`
	ADD COLUMN `material_id` VARCHAR(100) NULL AFTER `material_data`;

ALTER TABLE `s_store_stock`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_store_stock`
	COMMENT='门店库存表',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `store_id` `store_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '门店ID' AFTER `id`,
	CHANGE COLUMN `state` `state` INT(11) NULL DEFAULT NULL COMMENT '门店状态' AFTER `store_id`,
	CHANGE COLUMN `quantity` `quantity` INT(11) NULL DEFAULT NULL COMMENT '原料数量' AFTER `state`,
	CHANGE COLUMN `batch_num` `batch_num` VARCHAR(100) NULL DEFAULT NULL COMMENT '原料批号' AFTER `quantity`,
	CHANGE COLUMN `material_data` `material_data` TEXT NULL COMMENT '存原料的JSON' AFTER `batch_num`,
	CHANGE COLUMN `material_id` `material_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '原料ID' AFTER `material_data`,
	CHANGE COLUMN `store_color` `store_color` VARCHAR(10) NULL DEFAULT NULL COMMENT '门店颜色' AFTER `material_id`,
	CHANGE COLUMN `sort` `sort` INT(11) NULL DEFAULT NULL COMMENT '原料排序' AFTER `store_color`;
