package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class CommonColorService extends BaseService {

    private static String[] columnNameArr = {"color","state","sort"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","",""};

    public CommonColorService() {
        super("s_common_color", new TableBean("s_common_color", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
