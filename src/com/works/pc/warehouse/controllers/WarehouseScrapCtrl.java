package com.works.pc.warehouse.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.warehouse.services.WarehouseScrapService;

public class WarehouseScrapCtrl extends BaseCtrl<WarehouseScrapService> {

    public WarehouseScrapCtrl() {
        super(WarehouseScrapService.class);
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
