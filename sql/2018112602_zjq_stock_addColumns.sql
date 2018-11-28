ALTER TABLE `s_store_stock`
	ADD COLUMN `available_quantity` INT(11) NULL DEFAULT NULL COMMENT '可用数量' AFTER `quantity`,
	ADD COLUMN `change_record` TEXT NULL DEFAULT NULL COMMENT '可用数量的变化记录' AFTER `available_quantity`;

ALTER TABLE `s_warehouse_stock`
	ADD COLUMN `available_quantity` INT(11) NULL DEFAULT NULL COMMENT '可用数量' AFTER `quantity`,
	ADD COLUMN `change_record` TEXT NULL DEFAULT NULL COMMENT '可用数量的变化记录' AFTER `available_quantity`;
