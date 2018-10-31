package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class WarehourseOutOrderService extends BaseService {

    private static String[] columnNameArr = {"id","warehouse_id","order_id","store_color","num","out_date","order_num","create_id","create_date","state","type","order_item"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","",""};

    public WarehourseOutOrderService() {
        super("s_warehourse_out_order", new TableBean("s_warehourse_out_order", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
