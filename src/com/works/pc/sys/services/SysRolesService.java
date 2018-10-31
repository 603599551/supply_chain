package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class SysRolesService extends BaseService {

    private static String[] columnNameArr = {"id","name","remark","updatedate","state"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","",""};

    public SysRolesService() {
        super("s_sys_roles", new TableBean("s_sys_roles", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
