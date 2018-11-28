package com.works.pc.warehouse.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.warehouse.services.WarehouseMovementOrderService;

public class WarehouseMovementOrderCtrl extends BaseCtrl<WarehouseMovementOrderService> {

    public WarehouseMovementOrderCtrl() {
        super(WarehouseMovementOrderService.class);
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
