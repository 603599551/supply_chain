package com.works.pc.order.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.order.services.OrderService;

public class OrderCtrl extends BaseCtrl<OrderService> {

    public OrderCtrl() {
        super(OrderService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
