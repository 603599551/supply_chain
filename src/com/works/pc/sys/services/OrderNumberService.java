package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class OrderNumberService extends BaseService {

    private static String[] columnNameArr = {"date","name","number","remark","type"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","",""};

    public OrderNumberService() {
        super("s_order_number", new TableBean("s_order_number", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
