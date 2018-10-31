package com.works.pc.store.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class StoreStockService extends BaseService {

    private static String[] columnNameArr = {"id","store_id","state","quantity","batch_num","material_data","store_color","sort"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","INT","INT","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","",""};

    public StoreStockService() {
        super("s_store_stock", new TableBean("s_store_stock", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
