ALTER TABLE `s_sys_menu`
ADD COLUMN `level`  int(11) NULL AFTER `icon`;
update s_sys_menu set level=3 where url is not null;
update s_sys_menu set level=2 where url is null;
update s_sys_menu set level=1 where parent_id='0';
