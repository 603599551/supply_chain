ALTER TABLE `s_warehouse_stock`
	ADD COLUMN `purchase_order_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '采购单ID' AFTER `warehouse_id`,
	ADD COLUMN `purchase_order_num` VARCHAR(50) NULL DEFAULT NULL COMMENT '采购单编号' AFTER `purchase_order_id`;
