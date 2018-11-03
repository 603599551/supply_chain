package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class WarehourseMovementOrderService extends BaseService {

    private static String[] columnNameArr = {"id","warehourse_to_id","warehourse_from_id","num","out_date","create_id","create_date","state","order_item","end_date"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","",""};

    public WarehourseMovementOrderService() {
        super("s_warehourse_movement_order", new TableBean("s_warehourse_movement_order", columnNameArr, columnTypeArr, columnCommentArr));
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