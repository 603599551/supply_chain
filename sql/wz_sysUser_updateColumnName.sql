ALTER TABLE `s_sys_user`
CHANGE COLUMN `pyin` `pinyin`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `updatedate`;