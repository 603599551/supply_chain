package com.works.pc.store.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.store.services.StoreStockService;

public class StoreStockCtrl extends BaseCtrl<StoreStockService> {

    public StoreStockCtrl() {
        super(StoreStockService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
