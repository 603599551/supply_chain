package com.works.pc.warehourse.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.warehourse.services.WarehouseScrapService;

public class WarehouseScrapCtrl extends BaseCtrl<WarehouseScrapService> {

    public WarehouseScrapCtrl() {
        super(WarehouseScrapService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
