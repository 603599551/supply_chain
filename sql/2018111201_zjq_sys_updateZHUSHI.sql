ALTER TABLE `s_sys_roles`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_sys_roles`
	COMMENT='角色表',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `name` `name` VARCHAR(50) NULL DEFAULT NULL COMMENT '角色名称' AFTER `id`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '角色备注' AFTER `name`,
	CHANGE COLUMN `updatedate` `updatedate` VARCHAR(50) NULL DEFAULT NULL COMMENT '修改时间' AFTER `remark`,
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '角色状态' AFTER `updatedate`;

ALTER TABLE `s_sys_user`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_sys_user`
	COMMENT='1.用于系统登陆\r\n2.注意门店登陆相关需求\r\n3.注意库管门店登陆相关需求\r\n4.entry_ids关联实体的id，每个id之间用逗号分隔，例如库管员：\r\n库管员1管理的仓库是1号（id=1）、2号（id=2），entry_ids字段1,2',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `role_id` `role_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '角色ID' AFTER `id`,
	CHANGE COLUMN `username` `username` VARCHAR(50) NULL DEFAULT NULL COMMENT '用户名' AFTER `role_id`,
	CHANGE COLUMN `password` `password` VARCHAR(50) NULL DEFAULT NULL COMMENT '密码' AFTER `username`,
	CHANGE COLUMN `nickname` `nickname` VARCHAR(50) NULL DEFAULT NULL COMMENT '昵称' AFTER `password`,
	CHANGE COLUMN `sex` `sex` VARCHAR(10) NULL DEFAULT NULL COMMENT '性别' AFTER `nickname`,
	CHANGE COLUMN `phone` `phone` VARCHAR(20) NULL DEFAULT NULL COMMENT '电话' AFTER `sex`,
	CHANGE COLUMN `entry_ids` `entry_ids` TEXT NULL COMMENT '关联的实体ID' AFTER `phone`,
	CHANGE COLUMN `state` `state` VARCHAR(50) NULL DEFAULT NULL COMMENT '用户状态' AFTER `entry_ids`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '备注(留用字段)' AFTER `state`,
	CHANGE COLUMN `updatedate` `updatedate` VARCHAR(50) NULL DEFAULT NULL COMMENT '更新时间(系统时间)' AFTER `remark`,
	CHANGE COLUMN `pinyin` `pinyin` VARCHAR(50) NULL DEFAULT NULL COMMENT '用户昵称拼音' AFTER `updatedate`;

ALTER TABLE `s_sys_menu`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_sys_menu`
	COMMENT='菜单表',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `name` `name` VARCHAR(100) NULL DEFAULT NULL COMMENT '菜单名称' AFTER `id`,
	CHANGE COLUMN `url` `url` VARCHAR(100) NULL DEFAULT NULL COMMENT '菜单地址' AFTER `name`,
	CHANGE COLUMN `parent_id` `parent_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '父级UUID' AFTER `url`,
	CHANGE COLUMN `sort` `sort` INT(11) NULL DEFAULT NULL COMMENT '排序' AFTER `parent_id`,
	CHANGE COLUMN `icon` `icon` VARCHAR(100) NULL DEFAULT NULL COMMENT '图标' AFTER `sort`,
	CHANGE COLUMN `remark` `remark` TEXT NULL COMMENT '备注' AFTER `icon`;

ALTER TABLE `s_sys_auth`
	ALTER `id` DROP DEFAULT;
ALTER TABLE `s_sys_auth`
	COMMENT='权限表',
	CHANGE COLUMN `id` `id` VARCHAR(100) NOT NULL COMMENT 'UUID' FIRST,
	CHANGE COLUMN `menu_id` `menu_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '菜单ID' AFTER `id`,
	CHANGE COLUMN `role_id` `role_id` VARCHAR(100) NULL DEFAULT NULL COMMENT '角色ID' AFTER `menu_id`,
	CHANGE COLUMN `updatedate` `updatedate` VARCHAR(50) NULL DEFAULT NULL COMMENT '修改时间' AFTER `role_id`;
