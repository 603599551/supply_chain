package com.works.pc.store.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.store.services.StoreCountService;

public class StoreCountCtrl extends BaseCtrl<StoreCountService> {

    public StoreCountCtrl() {
        super(StoreCountService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
