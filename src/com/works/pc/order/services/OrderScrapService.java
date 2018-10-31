package com.works.pc.order.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class OrderScrapService extends BaseService {

    private static String[] columnNameArr = {"id","store_id","num","order_date","arrive_date","order_item","store_color","order_state","create_id","create_date","logistics_id","logistics_date","order_type","close_date","close_reason","close_id","city","remark","image","scrap_reason"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","","","","","",""};

    public OrderScrapService() {
        super("s_order_scrap", new TableBean("s_order_scrap", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
