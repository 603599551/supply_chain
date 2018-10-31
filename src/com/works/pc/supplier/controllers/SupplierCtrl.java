package com.works.pc.supplier.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.supplier.services.SupplierService;

public class SupplierCtrl extends BaseCtrl<SupplierService> {

    public SupplierCtrl() {
        super(SupplierService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
