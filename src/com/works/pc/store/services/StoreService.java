package com.works.pc.store.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class StoreService extends BaseService {

    private static String[] columnNameArr = {"id","create_id","address_id","name","city","address","phone","sort","updatedate","state","color","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","INT","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","",""};

    public StoreService() {
        super("s_store", new TableBean("s_store", columnNameArr, columnTypeArr, columnCommentArr));
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
