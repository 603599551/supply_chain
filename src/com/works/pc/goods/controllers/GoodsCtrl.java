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
