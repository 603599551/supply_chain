package com.works.pc.store.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.store.services.StoreService;

public class StoreCtrl extends BaseCtrl<StoreService> {

    public StoreCtrl() {
        super(StoreService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
