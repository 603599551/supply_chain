ALTER TABLE `s_address`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '地址状态 0停用 1启用' AFTER `address`;
ALTER TABLE `s_purchase_order`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL AFTER `item`;
ALTER TABLE `s_purchase_purchasereturn_process`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL AFTER `to_user_id`;
ALTER TABLE `s_store`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '门店状态' AFTER `updatedate`;
ALTER TABLE `s_store_stock`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '门店状态' AFTER `store_id`;
ALTER TABLE `s_supplier`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '供应商状态' AFTER `material_ids`;
ALTER TABLE `s_sys_roles`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL AFTER `updatedate`;
ALTER TABLE `s_sys_user`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL AFTER `entry_ids`;
ALTER TABLE `s_warehouse`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '仓库状态' AFTER `city`;
ALTER TABLE `s_warehouse_stock`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL AFTER `warehourse_id`;
