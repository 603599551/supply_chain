package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class WarehouseService extends BaseService {

    private static String[] columnNameArr = {"id","address_id","num","name","city","state","remark","pinyin","type"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","",""};

    public WarehouseService() {
        super("s_warehouse", new TableBean("s_warehouse", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
