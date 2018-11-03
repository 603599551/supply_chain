package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class WarehouseCountService extends BaseService {

    private static String[] columnNameArr = {"id","warehouse_id","num","count_date","remark","count_item","count_id","sort"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","",""};

    public WarehouseCountService() {
        super("s_warehouse_count", new TableBean("s_warehouse_count", columnNameArr, columnTypeArr, columnCommentArr));
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
