package com.works.pc.order.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.order.services.OrderReturnService;

public class OrderReturnCtrl extends BaseCtrl<OrderReturnService> {

    public OrderReturnCtrl() {
        super(OrderReturnService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {

    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    @Override
    public void createRecordBeforeSelect(Record record) {

    }
}