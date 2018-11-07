package com.works.pc.sys.controllers;

import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.JsonHashMap;
import com.works.pc.sys.services.SysRolesService;

import java.util.List;

public class SysRolesCtrl extends BaseCtrl<SysRolesService> {

    public SysRolesCtrl() {
        super(SysRolesService.class);
    }

    public void getRoleDict(){
        JsonHashMap jhm = new JsonHashMap();
        String selectRoleDict = "select name name, id `value` from s_sys_roles where state=?";
        List<Record> roleDictList = Db.find(selectRoleDict, "1");
        jhm.putSuccess(roleDictList);
        renderJson(jhm);
    }

    @Override
    public void handleRecord(Record record) {
        if(record != null){
            String state_text = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.STATE).get(record.getStr("state"));
            record.set("state_text", state_text);
        }
    }

    @Override
    public void handleAddRecord(Record record) {
        String time = DateUtil.GetDateTime();
        record.set("updatedate", time);
        record.set("state", 1);
        String[] menuIdArr = getParaValues("menu_ids");
        record.set("menu_ids", menuIdArr);
    }

    @Override
    public void handleUpdateRecord(Record record) {
        String time = DateUtil.GetDateTime();
        record.set("updatedate", time);
        String[] menuIdArr = getParaValues("menu_ids");
        record.set("menu_ids", menuIdArr);
    }

    @Override
    public void createRecordBeforeSelect(Record record) {
        if(record != null){
            record.set("$like_name", record.get("keyword"));
        }
    }
}
