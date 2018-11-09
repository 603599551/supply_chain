ALTER TABLE `s_material`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_material`
	COMMENT='原料表',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `name` `name` VARCHAR(100) NULL DEFAULT NULL COMMENT '原料名称' AFTER `id`,
	CHANGE COLUMN `num` `num` VARCHAR(20) NULL DEFAULT NULL COMMENT '原料编号' AFTER `name`,
	CHANGE COLUMN `pinyin` `pinyin` VARCHAR(50) NULL DEFAULT NULL COMMENT '原料名称拼音' AFTER `num`,
	CHANGE COLUMN `cost_price` `cost_price` DECIMAL(10,3) NULL DEFAULT NULL COMMENT '原料成本价' AFTER `pinyin`,
	CHANGE COLUMN `purchase_price` `purchase_price` DECIMAL(10,3) NULL DEFAULT NULL COMMENT '门店采购价' AFTER `cost_price`,
	CHANGE COLUMN `min_unit` `min_unit` VARCHAR(20) NULL DEFAULT NULL COMMENT '原料最小单位' AFTER `purchase_price`,
	CHANGE COLUMN `min2mid_num` `min2mid_num` INT(11) NULL DEFAULT NULL COMMENT '最小单位转中间' AFTER `min_unit`,
	CHANGE COLUMN `mid_unit` `mid_unit` VARCHAR(20) NULL DEFAULT NULL COMMENT '原料中间单位' AFTER `min2mid_num`,
	CHANGE COLUMN `mid2max_num` `mid2max_num` INT(11) NULL DEFAULT NULL COMMENT '中间单位转最大' AFTER `mid_unit`,
	CHANGE COLUMN `max_unit` `max_unit` VARCHAR(20) NULL DEFAULT NULL COMMENT '原料最大单位' AFTER `mid2max_num`,
	CHANGE COLUMN `out_unit` `out_unit` VARCHAR(20) NULL DEFAULT NULL COMMENT '原料出库单位' AFTER `max_unit`,
	CHANGE COLUMN `attribute` `attribute` VARCHAR(200) NULL DEFAULT NULL COMMENT '原料规格' AFTER `out_unit`,
	CHANGE COLUMN `brand` `brand` VARCHAR(100) NULL DEFAULT NULL COMMENT '原料品牌' AFTER `attribute`,
	CHANGE COLUMN `storage_condition` `storage_condition` VARCHAR(100) NULL DEFAULT NULL COMMENT '原料存储条件' AFTER `brand`,
	CHANGE COLUMN `shelf_life_num` `shelf_life_num` VARCHAR(100) NULL DEFAULT NULL COMMENT '原料保质期' AFTER `storage_condition`,
	CHANGE COLUMN `shelf_life_unit` `shelf_life_unit` VARCHAR(100) NULL DEFAULT NULL COMMENT '保质期单位（天）' AFTER `shelf_life_num`,
	CHANGE COLUMN `security_time` `security_time` VARCHAR(50) NULL DEFAULT NULL COMMENT '原料到货周期' AFTER `shelf_life_unit`
