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
            List reList=null;
            if ("admin".equals(usu.getUsername())) {
                List<Record> list = Db.find("select id,name as title,CONCAT('/',ifnull(url,'')) as link,parent_id,sort,icon as iconName,type from menu order by sort");
                reList = service.sort(list);
            } else {
                String jobId = usu.getUserBean().getRoleId();
                List<Record> list = Db.find("select m.id,m.name as title,CONCAT('/',ifnull(m.url,'')) as link,m.parent_id,m.sort,m.icon as iconName,m.type from menu m,author_job_menu ajm where m.ID=ajm.menu_id and ajm.job_id=? and ajm.access='1' order by sort", jobId);
                reList = service.sort(list);
            }

            if(reList!=null && !reList.isEmpty()){
                Map map=(Map)reList.get(0);
                String link=(String)map.get("link");
                jhm.put("defaultLink",link);
            }
            jhm.putCode(1).put("list", reList);
            renderJson(jhm);
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putCode(-1).putMessage(e.toString());
            renderJson(jhm);
        }
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
