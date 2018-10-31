package com.works.pc.store.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class StoreCountService extends BaseService {

    private static String[] columnNameArr = {"id","store_id","num","count_date","remark","count_item","store_color","count_id","sort"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","","",""};

    public StoreCountService() {
        super("s_store_count", new TableBean("s_store_count", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
