package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class SysAuthService extends BaseService {

    private static String[] columnNameArr = {"id","menu_id","role_id","udpatedate"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","",""};

    public SysAuthService() {
        super("s_sys_auth", new TableBean("s_sys_auth", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
