package com.works.pc.goods.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.goods.services.MaterialService;

public class MaterialCtrl extends BaseCtrl<MaterialService> {

    public MaterialCtrl() {
        super(MaterialService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
