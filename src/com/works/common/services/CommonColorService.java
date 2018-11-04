package com.works.common.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class CommonColorService extends BaseService {

    private static String[] columnNameArr = {"color","state","sort"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","",""};

    public CommonColorService() {
        super("s_common_color", new TableBean("s_common_color", columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return page;
     }

}
