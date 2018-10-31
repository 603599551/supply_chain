package com.works.pc.store.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.store.services.StoreProductRelationService;

public class StoreProductRelationCtrl extends BaseCtrl<StoreProductRelationService> {

    public StoreProductRelationCtrl() {
        super(StoreProductRelationService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
