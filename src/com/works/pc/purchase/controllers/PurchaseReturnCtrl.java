package com.works.pc.purchase.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.purchase.services.PurchaseReturnService;

public class PurchaseReturnCtrl extends BaseCtrl<PurchaseReturnService> {

    public PurchaseReturnCtrl() {
        super(PurchaseReturnService.class);
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
