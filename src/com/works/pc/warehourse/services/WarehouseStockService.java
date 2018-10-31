package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class WarehouseStockService extends BaseService {

    private static String[] columnNameArr = {"id","user_id","warehourse_id","state","quantity","batch_num","material_data","sort"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","",""};

    public WarehouseStockService() {
        super("s_warehouse_stock", new TableBean("s_warehouse_stock", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
