ALTER TABLE `s_sys_auth`
CHANGE COLUMN `udpatedate` `updatedate`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `role_id`;
