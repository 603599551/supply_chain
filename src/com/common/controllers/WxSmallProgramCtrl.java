package com.common.controllers;

import com.jfinal.core.Controller;
import com.utils.UserSessionUtil;

public abstract class WxSmallProgramCtrl extends Controller {

    protected UserSessionUtil getUserSessionUnit(){
        String userId = getHeader("userId");
        UserSessionUtil result = new UserSessionUtil(userId);
        return result;
    }

}
