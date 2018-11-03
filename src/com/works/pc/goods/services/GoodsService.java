package com.works.pc.goods.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class GoodsService extends BaseService {

    private static String[] columnNameArr = {"id","name","price","num","create_date"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","DECIMAL","INT","VARCHAR"};
    private static String[] columnCommentArr = {"","","","",""};

    public GoodsService() {
        super("s_goods", new TableBean("s_goods", columnNameArr, columnTypeArr, columnCommentArr));
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
