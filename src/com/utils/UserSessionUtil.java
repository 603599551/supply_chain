package com.utils;

import com.bean.UserBean;
import com.constants.KEY;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserSessionUtil {

    private UserBean userBean;
    private HttpSession session;

    public UserSessionUtil(HttpServletRequest request){
        //处理跨域问题
        if(true){

        }else{

        }
        session=request.getSession();
        userBean=(UserBean)session.getAttribute(KEY.SESSION_USER);
    }

    public UserBean getUser() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
        session.setAttribute(KEY.SESSION_USER, userBean);
    }
}
