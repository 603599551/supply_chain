package com.works.pc.purchase.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class PurchaseReturnService extends BaseService {

    private static String[] columnNameArr = {"id","supplier_id","num","from_purchase_order_id","from_purchase_order_num","return_item","color","order_state","close_date","close_reason","close_id","city","remark","image","return_reason"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","",""};

    public PurchaseReturnService() {
        super("s_purchase_return", new TableBean("s_purchase_return", columnNameArr, columnTypeArr, columnCommentArr));
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
