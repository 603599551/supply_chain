package com.works.pc.purchase.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.purchase.services.PurchasePurchasereturnProcessService;

public class PurchasePurchasereturnProcessCtrl extends BaseCtrl<PurchasePurchasereturnProcessService> {

    public PurchasePurchasereturnProcessCtrl() {
        super(PurchasePurchasereturnProcessService.class);
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
