package com.works.common.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.common.services.OrderNumberService;

public class OrderNumberCtrl extends BaseCtrl<OrderNumberService> {

    public OrderNumberCtrl() {
        super(OrderNumberService.class);
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
