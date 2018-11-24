ALTER TABLE `s_purchase_purchasereturn_process`
	ADD COLUMN `purchase_return_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '采购退货单ID' AFTER `purchase_id`,
	ADD INDEX `Index 3` (`purchase_return_id`),
	ADD CONSTRAINT `FK_s_purchase_purchasereturn_process_s_purchase_return` FOREIGN KEY (`purchase_return_id`) REFERENCES `s_purchase_return` (`id`);
