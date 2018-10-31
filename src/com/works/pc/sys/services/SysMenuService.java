package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class SysMenuService extends BaseService {

    private static String[] columnNameArr = {"id","name","url","parent_id","sort","icon","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","",""};

    public SysMenuService() {
        super("s_sys_menu", new TableBean("s_sys_menu", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
