package com.works.pc.warehourse.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.warehourse.services.WarehouseStockService;

public class WarehouseStockCtrl extends BaseCtrl<WarehouseStockService> {

    public WarehouseStockCtrl() {
        super(WarehouseStockService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
