package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.UUIDTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SysAuthService extends BaseService {

    private static String[] columnNameArr = {"id","menu_id","role_id","updatedate"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","",""};

    public SysAuthService() {
        super("s_sys_auth", new TableBean("s_sys_auth", columnNameArr, columnTypeArr, columnCommentArr));
    }

    public boolean addAssignRoleAuth(String roleId, String[] menuIdArr){
        SysMenuService sysMenuService = enhance(SysMenuService.class);
        boolean result = false;
        Record sysMenu = new Record();
        sysMenu.set("$in#and#id", menuIdArr);
        Set<String> menuIdSet = new HashSet<>();
        getMenuIds(sysMenuService, menuIdSet, menuIdArr);
        getMenuIds(sysMenuService, menuIdSet, menuIdSet.toArray(new String[menuIdSet.size()]));
        menuIdSet.remove("0");
        String deleteAuthSql = "delete from s_sys_auth where role_id=?";
        Db.delete(deleteAuthSql, roleId);
        if (menuIdSet != null && menuIdSet.size() > 0) {
            List<Record> sysAuthList = new ArrayList<>();
            for (String s : menuIdSet) {
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

    public void getMenuIds(SysMenuService sysMenuService, Set<String> menuIdSet, String[] menuIdArr){
        Record sysMenu = new Record();
        sysMenu.set("$in#and#id", menuIdArr);
        try {
            List<Record> menuList = sysMenuService.list(sysMenu);
            if(menuList != null && menuList.size() > 0){
                for(Record r : menuList){
                    menuIdSet.add(r.getStr("id"));
                    menuIdSet.add(r.getStr("parent_id"));
                }
            }
        } catch (PcException e) {
            e.printStackTrace();
        }
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
