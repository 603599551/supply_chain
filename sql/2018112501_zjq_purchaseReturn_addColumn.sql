ALTER TABLE `s_purchase_return`
	ADD COLUMN `create_date` VARCHAR(50) NULL COMMENT '创建时间' AFTER `return_reason`;
