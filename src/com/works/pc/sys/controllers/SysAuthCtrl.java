package com.works.pc.sys.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.sys.services.SysAuthService;

public class SysAuthCtrl extends BaseCtrl<SysAuthService> {

    public SysAuthCtrl() {
        super(SysAuthService.class);
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
