package com.works.pc.store.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class StoreProductRelationService extends BaseService {

    private static String[] columnNameArr = {"store_id","product_id"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"",""};

    public StoreProductRelationService() {
        super("s_store_product_relation", new TableBean("s_store_product_relation", columnNameArr, columnTypeArr, columnCommentArr));
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
