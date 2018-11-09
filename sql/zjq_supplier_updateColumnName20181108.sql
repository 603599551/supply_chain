ALTER TABLE `s_supplier`
	CHANGE COLUMN `update_date` `updatedate` DATETIME NULL DEFAULT NULL COMMENT '记录更新时间' AFTER `state`;