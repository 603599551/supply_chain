package com.works.pc.purchase.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class PurchasePurchasereturnProcessService extends BaseService {

    private static String[] columnNameArr = {"id","purchase_id","num","handle_id","handle_date","purchase_type","remark","parent_id","to_user_id","state","item"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","TEXT"};
    private static String[] columnCommentArr = {"","","","","","","","","","",""};

    public PurchasePurchasereturnProcessService() {
        super("s_purchase_purchasereturn_process", new TableBean("s_purchase_purchasereturn_process", columnNameArr, columnTypeArr, columnCommentArr));
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
