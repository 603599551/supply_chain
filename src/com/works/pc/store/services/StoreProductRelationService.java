package com.works.pc.store.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class StoreProductRelationService extends BaseService {

    private static String[] columnNameArr = {"store_id","product_id"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"",""};

    public StoreProductRelationService() {
        super("s_store_product_relation", new TableBean("s_store_product_relation", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
