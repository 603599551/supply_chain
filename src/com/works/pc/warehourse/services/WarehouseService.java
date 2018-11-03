package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class WarehouseService extends BaseService {

    private static String[] columnNameArr = {"id","address_id","num","name","city","state","remark","pinyin","type"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","",""};

    public WarehouseService() {
        super("s_warehouse", new TableBean("s_warehouse", columnNameArr, columnTypeArr, columnCommentArr));
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
