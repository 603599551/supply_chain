ALTER TABLE `s_material`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '原料状态' AFTER `update_id`;
ALTER TABLE `s_product`
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '商品状态' AFTER `pinyin`;
