package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class SysUserService extends BaseService {

    private static String[] columnNameArr = {"id","role_id","username","password","nickname","sex","phone","entry_ids","state","remark","updatedate","pyin"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","",""};

    public SysUserService() {
        super("s_sys_user", new TableBean("s_sys_user", columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return null;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return null;
     }

}
