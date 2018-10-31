package com.works.pc.purchase.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class PurchaseOrderService extends BaseService {

    private static String[] columnNameArr = {"id","num","create_id","create_date","purchase_type","close_date","close_reason","close_id","city","remark","item","state"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","","","","","",""};

    public PurchaseOrderService() {
        super("s_purchase_order", new TableBean("s_purchase_order", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
