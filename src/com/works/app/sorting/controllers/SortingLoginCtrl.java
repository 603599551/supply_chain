package com.works.app.sorting.controllers;

import com.bean.UserBean;
import com.common.controllers.WxSmallProgramCtrl;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;

public class SortingLoginCtrl extends WxSmallProgramCtrl {

    public void login(){
        String username = getPara("username");
        String password = getPara("password");
        JsonHashMap jhm = new JsonHashMap();
        String sql = "select wu.*, w.name warehouse_name from s_sys_user wu, s_warehouse w where wu.username=? and wu.password=? and wu.entry_ids=w.id";
        Record user = Db.findFirst(sql, username, password);
        if(user != null){
            UserBean ub = new UserBean();
            ub.setId(user.get("id"));
            ub.setUsername(user.getStr("username"));
            ub.setNickname(user.getStr("warehouse_name"));
            ub.put("warehouse_id", user.getStr("warehouse_id"));
            ub.put("warehouse_name", user.getStr("warehouse_name"));
            jhm.put("data", ub);
            jhm.putMessage("登录成功！");
        }else{
            jhm.putCode(0).putMessage("用户名或密码错误！");
        }
        renderJson(jhm);
    }

    public void resetPwd(){
        String username = getPara("username");
        String historyPwd = getPara("history_pwd");
        String password = getPara("password");
        JsonHashMap jhm = new JsonHashMap();
        String sql = "select * from s_sys_user where username=? and password=?";
        Record user = Db.findFirst(sql, username, historyPwd);
        if(user != null){
            String update = "update s_sys_user set password=? where id=?";
            Object[] param = {password, user.get("id")};
            Db.update(update, param);
            jhm.putMessage("修改成功！");
        }else{
            jhm.putMessage("用户名不存在或原密码错误！").putCode(0);
        }
        renderJson(jhm);
    }

}
