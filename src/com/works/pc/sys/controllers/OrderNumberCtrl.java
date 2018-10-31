package com.works.pc.sys.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.sys.services.OrderNumberService;

public class OrderNumberCtrl extends BaseCtrl<OrderNumberService> {

    public OrderNumberCtrl() {
        super(OrderNumberService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
