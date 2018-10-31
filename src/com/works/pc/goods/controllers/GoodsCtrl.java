package com.works.pc.goods.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.goods.services.GoodsService;

public class GoodsCtrl extends BaseCtrl<GoodsService> {

    public GoodsCtrl() {
        super(GoodsService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
