package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.UUIDTool;

import java.util.ArrayList;
import java.util.List;

public class SysAuthService extends BaseService {

    private static String[] columnNameArr = {"id","menu_id","role_id","updatedate"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","",""};

    public SysAuthService() {
        super("s_sys_auth", new TableBean("s_sys_auth", columnNameArr, columnTypeArr, columnCommentArr));
    }

    public boolean addAssignRoleAuth(String roleId, String[] menuIdArr){
        boolean result = false;
        String deleteAuthSql = "delete from s_sys_auth where role_id=?";
        Db.delete(deleteAuthSql, roleId);
        if (menuIdArr != null && menuIdArr.length > 0) {
            List<Record> sysAuthList = new ArrayList<>();
            for (String s : menuIdArr) {
                Record r = new Record();
                r.set("id", UUIDTool.getUUID());
                r.set("menu_id", s);
                r.set("role_id", roleId);
                r.set("updatedate", DateUtil.GetDateTime());
                sysAuthList.add(r);
            }
            result = Db.batchSave("s_sys_auth", sysAuthList, sysAuthList.size()) != null;
        }
        return result;
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return page;
     }

}
