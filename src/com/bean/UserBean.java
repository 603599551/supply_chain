package com.bean;

import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class UserBean extends HashMap<String, Object> {

    private String id;
    private String username;
    private String nickname;
    private String password;
    private String remark;
    private String roleId;

    private Map<String, Object> user = new HashMap<>();

    public UserBean(){
        super();
    }

    public UserBean(Record record){
        super();
        this.id = record.getStr("id");
        this.username = record.getStr("username");
        this.nickname = record.getStr("nickname");
        this.password = record.getStr("password");
        this.remark = record.getStr("remark");
        this.roleId = record.getStr("role_id");
        user = record.getColumns();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        user.put("username", username);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        user.put("nickname", nickname);
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

    public String getRoleId() {
        return roleId;
    }
}
