UPDATE `supply_chain`.`s_sys_menu` SET `sort`=54 WHERE  `id`='53';
SELECT `id`, `name`, `url`, `parent_id`, `sort`, `icon`, `level`, `remark` FROM `supply_chain`.`s_sys_menu` WHERE  `id`='53';
UPDATE `supply_chain`.`s_sys_menu` SET `sort`=53 WHERE  `id`='52';
SELECT `id`, `name`, `url`, `parent_id`, `sort`, `icon`, `level`, `remark` FROM `supply_chain`.`s_sys_menu` WHERE  `id`='52';
UPDATE `supply_chain`.`s_sys_menu` SET `sort`=52 WHERE  `id`='51';
SELECT `id`, `name`, `url`, `parent_id`, `sort`, `icon`, `level`, `remark` FROM `supply_chain`.`s_sys_menu` WHERE  `id`='51';
INSERT INTO `supply_chain`.`s_sys_menu` (`id`, `name`, `url`, `parent_id`, `sort`, `level`, `remark`) VALUES ('54', '退货单列表', '/purchase/return_order_list', '50', 51, 3, NULL);
INSERT INTO `s_sys_menu` VALUES ('55', '入库', '/warehouse/stock_in', '39', 55, NULL, 3, NULL);
UPDATE `supply_chain`.`s_sys_menu` SET `name`='采购审核' WHERE  `id`='52';
UPDATE `supply_chain`.`s_sys_menu` SET `name`='财务审批' WHERE  `id`='53';
INSERT INTO `supply_chain`.`s_sys_menu` (`id`, `name`, `url`, `parent_id`, `sort`, `level`, `remark`) VALUES ('56', '物流发货', '/purchase/logistics_delivery', '50', 56, 3, NULL);
INSERT INTO `supply_chain`.`s_sys_menu` (`id`, `name`, `url`, `parent_id`, `sort`, `level`, `remark`) VALUES ('57', '财务收款', '/purchase/finance_getmoney', '50', 57, 3, NULL);
UPDATE `supply_chain`.`s_sys_menu` SET `url`='/purchase/p_logistics_delivery' WHERE  `id`='56';