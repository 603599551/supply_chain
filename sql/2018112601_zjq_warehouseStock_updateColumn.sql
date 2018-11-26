ALTER TABLE `s_warehouse_stock`
	CHANGE COLUMN `quantity` `quantity` INT NULL DEFAULT NULL COMMENT '原料数量' AFTER `state`;
