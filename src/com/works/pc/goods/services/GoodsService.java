package com.works.pc.goods.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class GoodsService extends BaseService {

    private static String[] columnNameArr = {"id","name","price","num","create_date"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","DECIMAL","INT","VARCHAR"};
    private static String[] columnCommentArr = {"","","","",""};

    public GoodsService() {
        super("s_goods", new TableBean("s_goods", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
