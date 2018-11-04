package com.works.pc.sys.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.sys.services.SysMenuService;

import java.util.List;
import java.util.Map;

public class SysMenuCtrl extends BaseCtrl<SysMenuService> {

    public SysMenuCtrl() {
        super(SysMenuService.class);
    }

    public void index() {
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        JsonHashMap jhm = new JsonHashMap();
        try {
            List reList = service.getTree(usu);
            jhm.putSuccess(reList);
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
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
