package com.works.pc.purchase.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class PurchaseOrderService extends BaseService {

    private static String[] columnNameArr = {"id","num","from_purchase_order_id","from_purchase_order_num","create_id","create_date","purchase_type","close_date","close_reason","close_id","city","remark","item","state"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","",""};

    public PurchaseOrderService() {
        super("s_purchase_order", new TableBean("s_purchase_order", columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return page;
     }

}
