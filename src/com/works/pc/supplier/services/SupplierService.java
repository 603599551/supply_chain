package com.works.pc.supplier.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class SupplierService extends BaseService {

    private static String[] columnNameArr = {"id","address_id","num","pinyin","name","city","address","material_items","material_ids","state","update_date","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","DATETIME","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","",""};

    public SupplierService() {
        super("s_supplier", new TableBean("s_supplier", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
