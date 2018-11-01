package com.works.pc.warehourse.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.warehourse.services.WarehourseMovementOrderService;

public class WarehourseMovementOrderCtrl extends BaseCtrl<WarehourseMovementOrderService> {

    public WarehourseMovementOrderCtrl() {
        super(WarehourseMovementOrderService.class);
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

}
