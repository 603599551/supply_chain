ALTER TABLE `s_purchase_return`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_purchase_return`
	COMMENT='采购退货\r\n注：参照采购流程表\r\n\r\n和供应商关联\r\n\r\n两个入口，一个入库以后退货，一个采购后，没入库前退货\r\n\r\n退货时需要通过采购单退货\r\nfrom_purchase_order_num：退货时的采购单num\r\nfrom_purchase_order_id：退货时的采购单id',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `supplier_id` `supplier_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '供应商ID' AFTER `id`,
	CHANGE COLUMN `num` `num` VARCHAR(50) NULL DEFAULT NULL COMMENT '采购退货单编号' AFTER `supplier_id`,
	CHANGE COLUMN `from_purchase_order_id` `from_purchase_order_id` VARCHAR(50) NULL DEFAULT NULL COMMENT '退货时的采购单ID' AFTER `num`,
	CHANGE COLUMN `from_purchase_order_num` `from_purchase_order_num` VARCHAR(50) NULL DEFAULT NULL COMMENT '退货时的采购单编号' AFTER `from_purchase_order_id`,
	CHANGE COLUMN `return_item` `return_item` TEXT NULL COMMENT '退货项' AFTER `from_purchase_order_num`,
	CHANGE COLUMN `color` `color` VARCHAR(20) NULL DEFAULT NULL COMMENT '供应商颜色（考虑查询，存在冗余）' AFTER `return_item`,
	CHANGE COLUMN `order_state` `order_state` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单状态（字典值关联）' AFTER `color`,
	CHANGE COLUMN `close_date` `close_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '订单关闭日期' AFTER `order_state`,
	CHANGE COLUMN `close_reason` `close_reason` TEXT NULL COMMENT '订单关闭原因' AFTER `close_date`,
	CHANGE COLUMN `close_id` `close_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单关闭人ID' AFTER `close_reason`,
	CHANGE COLUMN `city` `city` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单发起城市（用于后期计划物流成本）' AFTER `close_id`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '退货单备注' AFTER `city`,
	CHANGE COLUMN `image` `image` TEXT NULL COMMENT '退货证据（图片）' AFTER `remark`,
	CHANGE COLUMN `return_reason` `return_reason` TEXT NULL COMMENT '退货原因' AFTER `image`;


ALTER TABLE `s_purchase_order`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_purchase_order`
	COMMENT='采购-单据\r\n\r\n\r\nfrom_purchase_order_num：引单采购时，被引单的采购单num\r\nfrom_purchase_order_id：引单采购时，被引单的采购单id',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `num` `num` VARCHAR(50) NULL DEFAULT NULL COMMENT '采购单编号' AFTER `id`,
	CHANGE COLUMN `from_purchase_order_id` `from_purchase_order_id` VARCHAR(50) NULL DEFAULT NULL COMMENT '引单采购的单据ID' AFTER `num`,
	CHANGE COLUMN `from_purchase_order_num` `from_purchase_order_num` VARCHAR(50) NULL DEFAULT NULL COMMENT '引单采购的单据编号' AFTER `from_purchase_order_id`,
	CHANGE COLUMN `create_id` `create_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '创建人ID' AFTER `from_purchase_order_num`,
	CHANGE COLUMN `create_date` `create_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '创建时间' AFTER `create_id`,
	CHANGE COLUMN `purchase_type` `purchase_type` VARCHAR(100) NULL DEFAULT NULL COMMENT '采购类型（直采，外采，字典值）' AFTER `create_date`,
	CHANGE COLUMN `close_date` `close_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '订单关闭日期' AFTER `purchase_type`,
	CHANGE COLUMN `close_reason` `close_reason` TEXT NULL COMMENT '订单关闭原因' AFTER `close_date`,
	CHANGE COLUMN `close_id` `close_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单关闭人ID' AFTER `close_reason`,
	CHANGE COLUMN `city` `city` VARCHAR(100) NULL DEFAULT NULL COMMENT '订单发起城市（用于后期计划物流成本）' AFTER `close_id`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '订单备注' AFTER `city`,
	CHANGE COLUMN `item` `item` TEXT NULL COMMENT '采购项' AFTER `remark`,
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '状态（物流、采购员、财务、老板、仓库）' AFTER `item`;

ALTER TABLE `s_purchase_purchasereturn_process`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_purchase_purchasereturn_process`
	COMMENT='采购-流程表\r\n注：利用PID,to_user_ID,控制流程',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `purchase_id` `purchase_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '采购单ID' AFTER `id`,
	CHANGE COLUMN `num` `num` VARCHAR(50) NULL DEFAULT NULL COMMENT '采购编号' AFTER `purchase_id`,
	CHANGE COLUMN `handle_id` `handle_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '处理人ID' AFTER `num`,
	CHANGE COLUMN `handle_date` `handle_date` VARCHAR(50) NULL DEFAULT NULL COMMENT '处理日期' AFTER `handle_id`,
	CHANGE COLUMN `purchase_type` `purchase_type` VARCHAR(100) NULL DEFAULT NULL COMMENT '采购类型（直采，外采，字典值）' AFTER `handle_date`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '备注' AFTER `purchase_type`,
	CHANGE COLUMN `parent_id` `parent_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '树形ID,多层处理' AFTER `remark`,
	CHANGE COLUMN `to_user_id` `to_user_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '下一级交给谁' AFTER `parent_id`,
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '完成状态（1，0）' AFTER `to_user_id`;
