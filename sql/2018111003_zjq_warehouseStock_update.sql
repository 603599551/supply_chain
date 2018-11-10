ALTER TABLE `s_warehouse_stock`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_warehouse_stock`
	COMMENT='仓库原料库存表',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `user_id` `user_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '用户ID' AFTER `id`,
	CHANGE COLUMN `warehourse_id` `warehourse_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '仓库ID' AFTER `user_id`,
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '仓库状态' AFTER `warehourse_id`,
	CHANGE COLUMN `quantity` `quantity` VARCHAR(100) NULL DEFAULT NULL COMMENT '原料数量' AFTER `state`,
	CHANGE COLUMN `batch_num` `batch_num` VARCHAR(100) NULL DEFAULT NULL COMMENT '原料批号' AFTER `quantity`,
	CHANGE COLUMN `material_data` `material_data` TEXT NULL COMMENT '原料数据' AFTER `batch_num`,
	CHANGE COLUMN `sort` `sort` INT(11) NULL DEFAULT NULL COMMENT '原料排序' AFTER `material_data`,
	CHANGE COLUMN `material_id` `material_id` VARCHAR(50) NULL DEFAULT NULL COMMENT '原料ID' AFTER `sort`;
ALTER TABLE `s_warehouse_stock`
	CHANGE COLUMN `warehourse_id` `warehouse_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '仓库ID' AFTER `user_id`;
