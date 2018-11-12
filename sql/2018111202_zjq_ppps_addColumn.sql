ALTER TABLE `s_purchase_purchasereturn_process`
	ADD COLUMN `purchase_order_state` VARCHAR(100) NULL DEFAULT NULL COMMENT '采购单状态（物流、采购、财务、老板、仓库、关闭、完成）' AFTER `purchase_type`;

INSERT INTO `s_dictionary` VALUES ('5070', '完成', '5000', 'finish', NULL, 5070);
