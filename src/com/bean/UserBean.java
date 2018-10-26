package com.bean;

import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class UserBean extends HashMap<String, Object> {

    private String id;
    private String name;
    private String loginName;
    private String password;
    private String remark;

    private Map<String, Object> user = new HashMap<>();

    public UserBean(){
        super();
    }

    public UserBean(Record record){
        super();
        this.id = record.getStr("id");
        this.name = record.getStr("name");
        this.loginName = record.getStr("loginName");
        this.password = record.getStr("password");
        this.remark = record.getStr("remark");
        user = record.getColumns();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        user.put("name", name);
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
        user.put("login_name", loginName);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        user.put("password", password);
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
        user.put("remark", remark);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        user.put("id", id);
    }
}
