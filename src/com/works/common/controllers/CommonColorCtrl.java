package com.works.common.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.common.services.CommonColorService;

public class CommonColorCtrl extends BaseCtrl<CommonColorService> {

    public CommonColorCtrl() {
        super(CommonColorService.class);
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
