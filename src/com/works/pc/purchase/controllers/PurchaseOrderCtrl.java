package com.works.pc.purchase.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.purchase.services.PurchaseOrderService;

public class PurchaseOrderCtrl extends BaseCtrl<PurchaseOrderService> {

    public PurchaseOrderCtrl() {
        super(PurchaseOrderService.class);
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

}
