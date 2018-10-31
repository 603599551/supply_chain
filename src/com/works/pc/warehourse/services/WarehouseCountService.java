package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class WarehouseCountService extends BaseService {

    private static String[] columnNameArr = {"id","warehouse_id","num","count_date","remark","count_item","count_id","sort"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","",""};

    public WarehouseCountService() {
        super("s_warehouse_count", new TableBean("s_warehouse_count", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
