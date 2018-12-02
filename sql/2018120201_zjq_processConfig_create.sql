-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.6.22 - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win32
-- HeidiSQL 版本:                  9.1.0.4886
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出  表 supply_chain.s_process_config 结构
CREATE TABLE IF NOT EXISTS `s_process_config` (
  `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID',
  `item` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '流程信息JSON',
  `process_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程名',
  `process_sequence` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '流程顺序详情',
  `create_time` varchar(100) DEFAULT NULL COMMENT '创建时间',
  `modify_time` varchar(100) DEFAULT NULL COMMENT '最新修改时间',
  `creator_id` varchar(100) DEFAULT NULL COMMENT '创建人id',
  `modifier_id` varchar(100) DEFAULT NULL COMMENT '修改人id',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程配置表';

-- 正在导出表  supply_chain.s_process_config 的数据：~2 rows (大约)
/*!40000 ALTER TABLE `s_process_config` DISABLE KEYS */;
INSERT INTO `s_process_config` (`id`, `item`, `process_name`, `process_sequence`, `create_time`, `modify_time`, `creator_id`, `modifier_id`, `remark`) VALUES
	('100', '{"process_name":"采购流程","process_value":"purchase_process_type","process_sequence":[{"stage_name":"物流","stage_value":"logistics","stage_sort":0},{"stage_name":"采购","stage_value":"purchase","stage_sort":1},{"stage_name":"财务","stage_value":"finance","stage_sort":2},{"stage_name":"老板","stage_value":"boss","stage_sort":3},{"stage_name":"仓库","stage_value":"warehouse","stage_sort":4}]}', 'purchase_process_type', 'logistics,purchase,finance,boss,warehouse', '2018-12-01 16:01:38', '2018-12-01 16:01:38', 'zjq', 'zjq', NULL),
	('200', '{"process_name":"采购退货流程","process_value":"purchase_return_type","process_sequence":[{"stage_name":"物流清点","stage_value":"logistics_clearance","stage_sort":0},{"stage_name":"采购审核","stage_value":"purchase_audit","stage_sort":1},{"stage_name":"财务确认","stage_value":"finance_confirm","stage_sort":2},{"stage_name":"物流发货","stage_value":"logistics_delivery","stage_sort":3},{"stage_name":"退货完成","stage_value":"return_finish","stage_sort":4}]}', 'purchase_return_type', 'logistics_clearance,purchase_audit,finance_confirm,logistics_delivery,return_finish', '2018-12-01 16:01:38', '2018-12-01 16:01:38', 'zjq', 'zjq', NULL);
/*!40000 ALTER TABLE `s_process_config` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
