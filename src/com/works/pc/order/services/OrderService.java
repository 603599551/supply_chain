package com.works.pc.order.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class OrderService extends BaseService {

    private static String[] columnNameArr = {"id","store_id","num","order_date","arrive_date","order_item","store_color","order_state","create_id","create_date","logistics_id","logistics_date","accept_id","accept_date","order_type","sorting_id","sorting_date","close_date","close_reason","close_id","city","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","","","","","","","",""};

    public OrderService() {
        super("s_order", new TableBean("s_order", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
