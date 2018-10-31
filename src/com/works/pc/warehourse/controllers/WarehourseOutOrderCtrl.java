package com.works.pc.warehourse.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.warehourse.services.WarehourseOutOrderService;

public class WarehourseOutOrderCtrl extends BaseCtrl<WarehourseOutOrderService> {

    public WarehourseOutOrderCtrl() {
        super(WarehourseOutOrderService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
