package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class CatalogService extends BaseService {

    private static String[] columnNameArr = {"id","num","sort","remark","name","parent_id","type"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","区分原料和商品类型"};

    public CatalogService() {
        super("s_catalog", new TableBean("s_catalog", columnNameArr, columnTypeArr, columnCommentArr));
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
