/*
Navicat MySQL Data Transfer

Source Server         : 3308
Source Server Version : 50622
Source Host           : 127.0.0.1:3308
Source Database       : supply_chain

Target Server Type    : MYSQL
Target Server Version : 50622
File Encoding         : 65001

Date: 2018-11-02 14:08:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `s_address`
-- ----------------------------
DROP TABLE IF EXISTS `s_address`;
CREATE TABLE `s_address` (
  `id` varchar(100) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `province` varchar(100) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='��ַ��';

-- ----------------------------
-- Records of s_address
-- ----------------------------

-- ----------------------------
-- Table structure for `s_catalog`
-- ----------------------------
DROP TABLE IF EXISTS `s_catalog`;
CREATE TABLE `s_catalog` (
  `id` varchar(100) NOT NULL,
  `num` varchar(50) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `remark` text,
  `name` varchar(50) DEFAULT NULL,
  `parent_id` varchar(100) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL COMMENT '区分原料和商品类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='��Ʒ����\r\n���ڴ�����Ʒ�������';

-- ----------------------------
-- Records of s_catalog
-- ----------------------------

-- ----------------------------
-- Table structure for `s_common_color`
-- ----------------------------
DROP TABLE IF EXISTS `s_common_color`;
CREATE TABLE `s_common_color` (
  `color` varchar(20) DEFAULT NULL,
  `state` varchar(20) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_common_color
-- ----------------------------

-- ----------------------------
-- Table structure for `s_dictionary`
-- ----------------------------
DROP TABLE IF EXISTS `s_dictionary`;
CREATE TABLE `s_dictionary` (
  `id` varchar(100) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `parent_id` varchar(50) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  `state_color` varchar(100) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ֵ����������ڡ�����һЩ���õ�����';

-- ----------------------------
-- Records of s_dictionary
-- ----------------------------

-- ----------------------------
-- Table structure for `s_goods`
-- ----------------------------
DROP TABLE IF EXISTS `s_goods`;
CREATE TABLE `s_goods` (
  `id` varchar(32) NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `create_date` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_goods
-- ----------------------------
INSERT INTO `s_goods` VALUES ('02844dc8662a48d0b15772fd0b7dbeef', 'wer', '234.00', '234', null);
INSERT INTO `s_goods` VALUES ('c357d5db1a064e429f37dc6c2d7ae53d', '897', '987.00', '978', null);
INSERT INTO `s_goods` VALUES ('d05fa81073f4400a806d3eda1ab79a7f', 'wer', '234.00', '234', null);
INSERT INTO `s_goods` VALUES ('db09a0dd9f904238a38980b7ccb2f582', 'wer', '234.00', '234', null);

-- ----------------------------
-- Table structure for `s_material`
-- ----------------------------
DROP TABLE IF EXISTS `s_material`;
CREATE TABLE `s_material` (
  `id` varchar(100) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `num` varchar(20) DEFAULT NULL,
  `pinyin` varchar(50) DEFAULT NULL,
  `cost_price` decimal(10,3) DEFAULT NULL,
  `purchase_price` decimal(10,3) DEFAULT NULL,
  `min_unit` varchar(20) DEFAULT NULL,
  `min2mid_num` int(11) DEFAULT NULL,
  `mid_unit` varchar(20) DEFAULT NULL,
  `mid2max_num` int(11) DEFAULT NULL,
  `max_unit` varchar(20) DEFAULT NULL,
  `out_unit` varchar(20) DEFAULT NULL,
  `attribute` varchar(200) DEFAULT NULL,
  `brand` varchar(100) DEFAULT NULL,
  `storage_condition` varchar(100) DEFAULT NULL,
  `shelf_life_num` varchar(100) DEFAULT NULL,
  `shelf_life_unit` varchar(100) DEFAULT NULL,
  `security_time` varchar(50) DEFAULT NULL,
  `order_type` varchar(100) DEFAULT NULL,
  `model` varchar(100) DEFAULT NULL,
  `createdate` varchar(50) DEFAULT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `updatedate` varchar(50) DEFAULT NULL,
  `update_id` varchar(100) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `remark` text,
  `type` varchar(100) DEFAULT NULL,
  `catalog_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `catalog_Material_for_KEY` (`catalog_id`),
  CONSTRAINT `catalog_Material_for_KEY` FOREIGN KEY (`catalog_id`) REFERENCES `s_catalog` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ԭ���ϱ��û�мӹ����ġ���ƹϣ����ˡ�';

-- ----------------------------
-- Records of s_material
-- ----------------------------

-- ----------------------------
-- Table structure for `s_order`
-- ----------------------------
DROP TABLE IF EXISTS `s_order`;
CREATE TABLE `s_order` (
  `id` varchar(100) NOT NULL,
  `store_id` varchar(100) DEFAULT NULL,
  `num` varchar(50) DEFAULT NULL,
  `order_date` varchar(50) DEFAULT NULL,
  `arrive_date` varchar(100) DEFAULT NULL,
  `order_item` text,
  `store_color` varchar(20) DEFAULT NULL,
  `order_state` varchar(100) DEFAULT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `create_date` varchar(50) DEFAULT NULL,
  `logistics_id` varchar(100) DEFAULT NULL,
  `logistics_date` varchar(50) DEFAULT NULL,
  `accept_id` varchar(100) DEFAULT NULL,
  `accept_date` varchar(50) DEFAULT NULL,
  `order_type` varchar(100) DEFAULT NULL,
  `sorting_id` varchar(100) DEFAULT NULL,
  `sorting_date` varchar(50) DEFAULT NULL,
  `close_date` varchar(50) DEFAULT NULL,
  `close_reason` text,
  `close_id` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `remark` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_11` (`store_id`),
  CONSTRAINT `FK_Relationship_11` FOREIGN KEY (`store_id`) REFERENCES `s_store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='��������';

-- ----------------------------
-- Records of s_order
-- ----------------------------

-- ----------------------------
-- Table structure for `s_order_number`
-- ----------------------------
DROP TABLE IF EXISTS `s_order_number`;
CREATE TABLE `s_order_number` (
  `date` varchar(50) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `remark` varchar(100) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_order_number
-- ----------------------------

-- ----------------------------
-- Table structure for `s_order_return`
-- ----------------------------
DROP TABLE IF EXISTS `s_order_return`;
CREATE TABLE `s_order_return` (
  `id` varchar(100) NOT NULL,
  `store_id` varchar(100) DEFAULT NULL,
  `num` varchar(50) DEFAULT NULL,
  `order_date` varchar(50) DEFAULT NULL,
  `arrive_date` varchar(100) DEFAULT NULL,
  `order_item` text,
  `store_color` varchar(20) DEFAULT NULL,
  `order_state` varchar(100) DEFAULT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `create_date` varchar(50) DEFAULT NULL,
  `logistics_id` varchar(100) DEFAULT NULL,
  `logistics_date` varchar(50) DEFAULT NULL,
  `order_type` varchar(100) DEFAULT NULL,
  `close_date` varchar(50) DEFAULT NULL,
  `close_reason` text,
  `close_id` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `remark` text,
  `image` text,
  `return_reason` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_13` (`store_id`),
  CONSTRAINT `FK_Relationship_13` FOREIGN KEY (`store_id`) REFERENCES `s_store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='������-�˻���';

-- ----------------------------
-- Records of s_order_return
-- ----------------------------

-- ----------------------------
-- Table structure for `s_order_scrap`
-- ----------------------------
DROP TABLE IF EXISTS `s_order_scrap`;
CREATE TABLE `s_order_scrap` (
  `id` varchar(100) NOT NULL,
  `store_id` varchar(100) DEFAULT NULL,
  `num` varchar(50) DEFAULT NULL,
  `order_date` varchar(50) DEFAULT NULL,
  `arrive_date` varchar(100) DEFAULT NULL,
  `order_item` text,
  `store_color` varchar(20) DEFAULT NULL,
  `order_state` varchar(100) DEFAULT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `create_date` varchar(50) DEFAULT NULL,
  `logistics_id` varchar(100) DEFAULT NULL,
  `logistics_date` varchar(50) DEFAULT NULL,
  `order_type` varchar(100) DEFAULT NULL,
  `close_date` varchar(50) DEFAULT NULL,
  `close_reason` text,
  `close_id` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `remark` text,
  `image` text,
  `scrap_reason` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_12` (`store_id`),
  CONSTRAINT `FK_Relationship_12` FOREIGN KEY (`store_id`) REFERENCES `s_store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='������-��������';

-- ----------------------------
-- Records of s_order_scrap
-- ----------------------------

-- ----------------------------
-- Table structure for `s_product`
-- ----------------------------
DROP TABLE IF EXISTS `s_product`;
CREATE TABLE `s_product` (
  `id` varchar(100) NOT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `catalog_id` varchar(100) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `num` varchar(100) DEFAULT NULL,
  `pinyin` varchar(100) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `bom` text,
  `sort` int(11) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `wm_type` varchar(100) DEFAULT NULL,
  `unit` varchar(100) DEFAULT NULL,
  `attribute` varchar(200) DEFAULT NULL,
  `parent_id` varchar(200) DEFAULT NULL,
  `parent_name` varchar(200) DEFAULT NULL,
  `parent_num` varchar(100) DEFAULT NULL,
  `cost_price` decimal(10,3) DEFAULT NULL,
  `purchase_price` decimal(10,3) DEFAULT NULL,
  `sell_price` decimal(10,3) DEFAULT NULL,
  `remark` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_4` (`catalog_id`),
  KEY `FK_Relationship_7` (`create_id`),
  CONSTRAINT `FK_Relationship_4` FOREIGN KEY (`catalog_id`) REFERENCES `s_catalog` (`id`),
  CONSTRAINT `FK_Relationship_7` FOREIGN KEY (`create_id`) REFERENCES `s_sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='��Ʒ��Ϣ��\r\n����Ʒ���࣬�ŵ궼�й���\r\nע��������Ϣ���ӣ��漰';

-- ----------------------------
-- Records of s_product
-- ----------------------------

-- ----------------------------
-- Table structure for `s_purchase_order`
-- ----------------------------
DROP TABLE IF EXISTS `s_purchase_order`;
CREATE TABLE `s_purchase_order` (
  `id` varchar(100) NOT NULL,
  `num` varchar(50) DEFAULT NULL,
  `from_purchase_order_id` varchar(50) DEFAULT NULL,
  `from_purchase_order_num` varchar(50) DEFAULT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `create_date` varchar(50) DEFAULT NULL,
  `purchase_type` varchar(100) DEFAULT NULL,
  `close_date` varchar(50) DEFAULT NULL,
  `close_reason` text,
  `close_id` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `remark` text,
  `item` text,
  `state` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ɹ�-����';

-- ----------------------------
-- Records of s_purchase_order
-- ----------------------------

-- ----------------------------
-- Table structure for `s_purchase_purchasereturn_process`
-- ----------------------------
DROP TABLE IF EXISTS `s_purchase_purchasereturn_process`;
CREATE TABLE `s_purchase_purchasereturn_process` (
  `id` varchar(100) NOT NULL,
  `purchase_id` varchar(100) DEFAULT NULL,
  `num` varchar(50) DEFAULT NULL,
  `handle_id` varchar(100) DEFAULT NULL,
  `handle_date` varchar(50) DEFAULT NULL,
  `purchase_type` varchar(100) DEFAULT NULL,
  `remark` text,
  `parent_id` varchar(100) DEFAULT NULL,
  `to_user_id` varchar(100) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_10` (`purchase_id`),
  CONSTRAINT `FK_Relationship_10` FOREIGN KEY (`purchase_id`) REFERENCES `s_purchase_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ɹ�-���̱�\r\nע������PID,to_user_ID,��������';

-- ----------------------------
-- Records of s_purchase_purchasereturn_process
-- ----------------------------

-- ----------------------------
-- Table structure for `s_purchase_return`
-- ----------------------------
DROP TABLE IF EXISTS `s_purchase_return`;
CREATE TABLE `s_purchase_return` (
  `id` varchar(100) NOT NULL,
  `supplier_id` varchar(100) DEFAULT NULL,
  `num` varchar(50) DEFAULT NULL,
  `from_purchase_order_id` varchar(50) DEFAULT NULL,
  `from_purchase_order_num` varchar(50) DEFAULT NULL,
  `return_item` text,
  `color` varchar(20) DEFAULT NULL,
  `order_state` varchar(100) DEFAULT NULL,
  `close_date` varchar(50) DEFAULT NULL,
  `close_reason` text,
  `close_id` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `remark` text,
  `image` text,
  `return_reason` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_24` (`supplier_id`),
  CONSTRAINT `FK_Relationship_24` FOREIGN KEY (`supplier_id`) REFERENCES `s_supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ɹ��˻�\r\nע�����ղɹ����̱�\r\n\r\n�͹�Ӧ�̹���\r\n\r\n������ڣ�һ';

-- ----------------------------
-- Records of s_purchase_return
-- ----------------------------

-- ----------------------------
-- Table structure for `s_store`
-- ----------------------------
DROP TABLE IF EXISTS `s_store`;
CREATE TABLE `s_store` (
  `id` varchar(100) NOT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `address_id` varchar(100) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `city` varchar(20) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `updatedate` varchar(50) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `color` varchar(10) DEFAULT NULL,
  `remark` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_15` (`address_id`),
  KEY `FK_Relationship_6` (`create_id`),
  CONSTRAINT `FK_Relationship_15` FOREIGN KEY (`address_id`) REFERENCES `s_address` (`id`),
  CONSTRAINT `FK_Relationship_6` FOREIGN KEY (`create_id`) REFERENCES `s_sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ŵ������Ϣ��';

-- ----------------------------
-- Records of s_store
-- ----------------------------

-- ----------------------------
-- Table structure for `s_store_count`
-- ----------------------------
DROP TABLE IF EXISTS `s_store_count`;
CREATE TABLE `s_store_count` (
  `id` varchar(100) NOT NULL,
  `store_id` varchar(100) DEFAULT NULL,
  `num` varchar(50) DEFAULT NULL,
  `count_date` varchar(50) DEFAULT NULL,
  `remark` text,
  `count_item` text,
  `store_color` varchar(20) DEFAULT NULL,
  `count_id` varchar(100) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_14` (`store_id`),
  CONSTRAINT `FK_Relationship_14` FOREIGN KEY (`store_id`) REFERENCES `s_store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ŵ��̵�-����';

-- ----------------------------
-- Records of s_store_count
-- ----------------------------

-- ----------------------------
-- Table structure for `s_store_product_relation`
-- ----------------------------
DROP TABLE IF EXISTS `s_store_product_relation`;
CREATE TABLE `s_store_product_relation` (
  `store_id` varchar(100) NOT NULL,
  `product_id` varchar(200) NOT NULL,
  PRIMARY KEY (`store_id`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_store_product_relation
-- ----------------------------

-- ----------------------------
-- Table structure for `s_store_stock`
-- ----------------------------
DROP TABLE IF EXISTS `s_store_stock`;
CREATE TABLE `s_store_stock` (
  `id` varchar(100) NOT NULL,
  `store_id` varchar(100) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `batch_num` varchar(100) DEFAULT NULL,
  `material_data` text,
  `store_color` varchar(10) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_9` (`store_id`),
  CONSTRAINT `FK_Relationship_9` FOREIGN KEY (`store_id`) REFERENCES `s_store` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_store_stock
-- ----------------------------

-- ----------------------------
-- Table structure for `s_supplier`
-- ----------------------------
DROP TABLE IF EXISTS `s_supplier`;
CREATE TABLE `s_supplier` (
  `id` varchar(100) NOT NULL,
  `address_id` varchar(100) DEFAULT NULL,
  `num` varchar(20) DEFAULT NULL,
  `pinyin` varchar(100) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `material_items` text,
  `material_ids` text,
  `state` int(11) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `remark` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_17` (`address_id`),
  CONSTRAINT `FK_Relationship_17` FOREIGN KEY (`address_id`) REFERENCES `s_address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='��Ӧ�̻�����Ϣ��';

-- ----------------------------
-- Records of s_supplier
-- ----------------------------

-- ----------------------------
-- Table structure for `s_sys_auth`
-- ----------------------------
DROP TABLE IF EXISTS `s_sys_auth`;
CREATE TABLE `s_sys_auth` (
  `id` varchar(100) NOT NULL,
  `menu_id` varchar(100) DEFAULT NULL,
  `role_id` varchar(100) DEFAULT NULL,
  `udpatedate` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_2` (`role_id`),
  KEY `FK_Relationship_3` (`menu_id`),
  CONSTRAINT `FK_Relationship_2` FOREIGN KEY (`role_id`) REFERENCES `s_sys_roles` (`id`),
  CONSTRAINT `FK_Relationship_3` FOREIGN KEY (`menu_id`) REFERENCES `s_sys_menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_sys_auth
-- ----------------------------

-- ----------------------------
-- Table structure for `s_sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `s_sys_menu`;
CREATE TABLE `s_sys_menu` (
  `id` varchar(100) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `parent_id` varchar(100) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `icon` varchar(100) DEFAULT NULL,
  `remark` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_sys_menu
-- ----------------------------

-- ----------------------------
-- Table structure for `s_sys_roles`
-- ----------------------------
DROP TABLE IF EXISTS `s_sys_roles`;
CREATE TABLE `s_sys_roles` (
  `id` varchar(100) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `remark` text,
  `updatedate` varchar(50) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_sys_roles
-- ----------------------------

-- ----------------------------
-- Table structure for `s_sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `s_sys_user`;
CREATE TABLE `s_sys_user` (
  `id` varchar(100) NOT NULL,
  `role_id` varchar(100) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `sex` varchar(10) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `entry_ids` text,
  `state` int(11) DEFAULT NULL,
  `remark` text,
  `updatedate` varchar(50) DEFAULT NULL,
  `pyin` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_1` (`role_id`),
  CONSTRAINT `FK_Relationship_1` FOREIGN KEY (`role_id`) REFERENCES `s_sys_roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='1.����ϵͳ��½\r\n2.ע���ŵ��½�������\r\n3.ע�����ŵ��½��';

-- ----------------------------
-- Records of s_sys_user
-- ----------------------------

-- ----------------------------
-- Table structure for `s_warehourse_movement_order`
-- ----------------------------
DROP TABLE IF EXISTS `s_warehourse_movement_order`;
CREATE TABLE `s_warehourse_movement_order` (
  `id` varchar(100) NOT NULL,
  `warehourse_to_id` varchar(100) DEFAULT NULL,
  `warehourse_from_id` varchar(100) DEFAULT NULL,
  `num` varchar(100) DEFAULT NULL,
  `out_date` varchar(50) DEFAULT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `create_date` varchar(50) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `order_item` text,
  `end_date` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_19` (`warehourse_from_id`),
  KEY `FK_Relationship_20` (`warehourse_to_id`),
  CONSTRAINT `FK_Relationship_19` FOREIGN KEY (`warehourse_from_id`) REFERENCES `s_warehouse` (`id`),
  CONSTRAINT `FK_Relationship_20` FOREIGN KEY (`warehourse_to_id`) REFERENCES `s_warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_warehourse_movement_order
-- ----------------------------

-- ----------------------------
-- Table structure for `s_warehourse_out_order`
-- ----------------------------
DROP TABLE IF EXISTS `s_warehourse_out_order`;
CREATE TABLE `s_warehourse_out_order` (
  `id` varchar(100) NOT NULL,
  `warehouse_id` varchar(100) DEFAULT NULL,
  `order_id` varchar(100) DEFAULT NULL,
  `store_color` varchar(10) DEFAULT NULL,
  `num` varchar(100) DEFAULT NULL,
  `out_date` varchar(50) DEFAULT NULL,
  `order_num` varchar(50) DEFAULT NULL,
  `create_id` varchar(100) DEFAULT NULL,
  `create_date` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `order_item` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_25` (`warehouse_id`),
  KEY `FK_Relationship_26` (`order_id`),
  CONSTRAINT `FK_Relationship_25` FOREIGN KEY (`warehouse_id`) REFERENCES `s_warehouse` (`id`),
  CONSTRAINT `FK_Relationship_26` FOREIGN KEY (`order_id`) REFERENCES `s_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_warehourse_out_order
-- ----------------------------

-- ----------------------------
-- Table structure for `s_warehouse`
-- ----------------------------
DROP TABLE IF EXISTS `s_warehouse`;
CREATE TABLE `s_warehouse` (
  `id` varchar(100) NOT NULL,
  `address_id` varchar(100) DEFAULT NULL,
  `num` varchar(100) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `remark` text,
  `pinyin` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_16` (`address_id`),
  CONSTRAINT `FK_Relationship_16` FOREIGN KEY (`address_id`) REFERENCES `s_address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ֿ������Ϣ';

-- ----------------------------
-- Records of s_warehouse
-- ----------------------------

-- ----------------------------
-- Table structure for `s_warehouse_count`
-- ----------------------------
DROP TABLE IF EXISTS `s_warehouse_count`;
CREATE TABLE `s_warehouse_count` (
  `id` varchar(100) NOT NULL,
  `warehouse_id` varchar(100) DEFAULT NULL,
  `num` varchar(50) DEFAULT NULL,
  `count_date` varchar(50) DEFAULT NULL,
  `remark` text,
  `count_item` text,
  `count_id` varchar(100) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_23` (`warehouse_id`),
  CONSTRAINT `FK_Relationship_23` FOREIGN KEY (`warehouse_id`) REFERENCES `s_warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ֿ��̵�-����';

-- ----------------------------
-- Records of s_warehouse_count
-- ----------------------------

-- ----------------------------
-- Table structure for `s_warehouse_scrap`
-- ----------------------------
DROP TABLE IF EXISTS `s_warehouse_scrap`;
CREATE TABLE `s_warehouse_scrap` (
  `id` varchar(100) NOT NULL,
  `warehouse_id` varchar(100) DEFAULT NULL,
  `num` varchar(50) DEFAULT NULL,
  `scrap_item` text,
  `scrap_date` varchar(50) DEFAULT NULL,
  `scrap_reason` text,
  `scrap_user_id` varchar(100) DEFAULT NULL,
  `remark` text,
  `image` text,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_21` (`warehouse_id`),
  CONSTRAINT `FK_Relationship_21` FOREIGN KEY (`warehouse_id`) REFERENCES `s_warehouse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�ֿ�������ָ�ֿ�ֱ���ӵ�Ķ���\r\n\r\n�����ֿ�';

-- ----------------------------
-- Records of s_warehouse_scrap
-- ----------------------------

-- ----------------------------
-- Table structure for `s_warehouse_stock`
-- ----------------------------
DROP TABLE IF EXISTS `s_warehouse_stock`;
CREATE TABLE `s_warehouse_stock` (
  `id` varchar(100) NOT NULL,
  `user_id` varchar(100) DEFAULT NULL,
  `warehourse_id` varchar(100) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `quantity` varchar(100) DEFAULT NULL,
  `batch_num` varchar(100) DEFAULT NULL,
  `material_data` text,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Relationship_18` (`warehourse_id`),
  KEY `FK_Relationship_22` (`user_id`),
  CONSTRAINT `FK_Relationship_18` FOREIGN KEY (`warehourse_id`) REFERENCES `s_warehouse` (`id`),
  CONSTRAINT `FK_Relationship_22` FOREIGN KEY (`user_id`) REFERENCES `s_sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ԭ�Ͽ����Ϣ����ԭ�ϱ����';

-- ----------------------------
-- Records of s_warehouse_stock
-- ----------------------------
