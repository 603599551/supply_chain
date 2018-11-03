package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class SysMenuService extends BaseService {

    private static String[] columnNameArr = {"id","name","url","parent_id","sort","icon","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","",""};

    public SysMenuService() {
        super("s_sys_menu", new TableBean("s_sys_menu", columnNameArr, columnTypeArr, columnCommentArr));
    }

    public List<Record> sort(List<Record> recordList){
        return null;
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return page;
     }

}
