package com.works.pc.purchase.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class PurchaseReturnService extends BaseService {

    private static String[] columnNameArr = {"id","supplier_id","num","return_item","color","order_state","close_date","close_reason","close_id","city","remark","image","return_reason"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","",""};

    public PurchaseReturnService() {
        super("s_purchase_return", new TableBean("s_purchase_return", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
