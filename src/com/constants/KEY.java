package com.constants;

import java.text.SimpleDateFormat;

public interface KEY {

    String SESSION_USER = "session_user";
    SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
    long ONE_DAY_TIME = 1000 * 60 * 60 * 24;
    long ONE_YEAR_TIME = ONE_DAY_TIME * 366;
}
