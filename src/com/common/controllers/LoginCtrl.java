package com.common.controllers;

import com.bean.UserBean;
import com.constants.KEY;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.works.pc.sys.services.SysUserService;

public class LoginCtrl extends Controller {

    private SysUserService service = enhance(SysUserService.class);

    public void index() {
        JsonHashMap jhm = new JsonHashMap();
        try {
            String username = getPara("username");
            String password = getPara("password");

            Record record = new Record();
            record.set("username", username);
            record.set("password", password);
            Record r = service.findOne(record);
            if (r != null) {
                String state = r.getStr("state");
                if ("0".equals(state)) {
                    jhm.putFail("离职员工不能登录！");
                    renderJson(jhm);
                    return;
                }
                UserBean ub = new UserBean();
                ub.setId(r.get("id"));
                ub.setName(r.getStr("username"));
                setSessionAttr(KEY.SESSION_USER, ub);
                setCookie("userId", r.get("id"), 60 * 60 * 24 * 3);

                jhm.putCode(1);
                jhm.put("userId", r.get("id"));
                jhm.put("sessionId", getSession().getId());
                jhm.putMessage("登录成功！");
            } else {
                jhm.putCode(-1);
                jhm.putMessage("用户名或密码错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putCode(-1).putMessage(e.toString());
        }
        renderJson(jhm);
    }

}
