package com.works.pc.purchase.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class PurchasePurchasereturnProcessService extends BaseService {

    private static String[] columnNameArr = {"id","purchase_id","num","handle_id","handle_date","purchase_type","remark","parent_id","to_user_id","state"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","","","",""};

    public PurchasePurchasereturnProcessService() {
        super("s_purchase_purchasereturn_process", new TableBean("s_purchase_purchasereturn_process", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
