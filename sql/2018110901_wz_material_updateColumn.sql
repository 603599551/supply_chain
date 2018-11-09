ALTER TABLE `s_material`
MODIFY COLUMN `order_type`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单类型（字典值读）' AFTER `security_time`;

