package com.works.pc.sys.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.sys.services.SysMenuService;

public class SysMenuCtrl extends BaseCtrl<SysMenuService> {

    public SysMenuCtrl() {
        super(SysMenuService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
