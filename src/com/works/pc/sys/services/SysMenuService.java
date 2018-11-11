package com.works.pc.sys.services;

import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.UserSessionUtil;

import java.util.ArrayList;
import java.util.List;

public class SysMenuService extends BaseService {

    private static String[] columnNameArr = {"id", "name", "url", "parent_id", "sort", "icon", "remark"};
    private static String[] columnTypeArr = {"VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "INT", "VARCHAR", "VARCHAR"};
    private static String[] columnCommentArr = {"", "", "", "", "", "", ""};

    public SysMenuService() {
        super("s_sys_menu", new TableBean("s_sys_menu", columnNameArr, columnTypeArr, columnCommentArr));
    }

    public List<Record> getTree(UserSessionUtil usu) {
        List<Record> list;
        if ("admin".equals(usu.getUsername())) {
            list = Db.find("select id,name as title, name label, ifnull(url,'') as link,parent_id,sort,icon as iconName from s_sys_menu order by sort");
        } else {
            String jobId = usu.getUserBean().getRoleId();
            list = Db.find("select m.id,m.name as title,name label, ifnull(url,'') as link,m.parent_id,m.sort,m.icon as iconName,m.type from menu m,author_job_menu ajm where m.ID=ajm.menu_id and ajm.job_id=? and ajm.access='1' order by sort", jobId);
        }
        Record root = new Record();
        root.set("id", "0");
        BeanUtils.createTree(root, list);
        return root.get("children");
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
