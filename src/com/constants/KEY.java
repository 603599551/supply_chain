package com.constants;

import java.text.SimpleDateFormat;

public interface KEY {

    String SESSION_USER = "session_user";

    SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
    long ONE_DAY_TIME = 1000 * 60 * 60 * 24;
    long ONE_YEAR_TIME = ONE_DAY_TIME * 366;

    /**
     * 1开头
     * PcException code
     */

    String ADD_EXCEPTION = "10001";
    String DELETE_EXCEPTION = "10002";
    String UPDATE_EXCEPTION = "10003";


    /**
     * 2开头
     * AppException code
     */


}
