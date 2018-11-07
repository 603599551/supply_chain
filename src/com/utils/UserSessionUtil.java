package com.utils;

import com.bean.UserBean;
import com.constants.KEY;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserSessionUtil {

    private UserBean userBean;
    private HttpSession session;
    private boolean login;

    public UserSessionUtil(HttpServletRequest request){
        //处理跨域问题
        if(true){
            userBean = new UserBean();
            userBean.setUsername("admin");
            userBean.setId("1");
            userBean.setNickname("管理员");
            userBean.setRemark("备注");
        }else{

        }
//        session=request.getSession();
//        userBean=(UserBean)session.getAttribute(KEY.SESSION_USER);
    }

    public boolean isLogin(){
        return login;
    }
    public String getSysUserId(){
        return userBean.getId();
    }
    public String getNickname(){
        return userBean.getNickname();
    }
    public String getUsername(){
        return userBean.getUsername();
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
        session.setAttribute(KEY.SESSION_USER, userBean);
    }
}
