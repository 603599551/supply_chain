package com.works.pc.goods.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class ProductService extends BaseService {

    private static String[] columnNameArr = {"id","create_id","catalog_id","name","num","pinyin","state","bom","sort","type","wm_type","unit","attribute","parent_id","parent_name","parent_num","cost_price","purchase_price","sell_price","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","DECIMAL","DECIMAL","DECIMAL","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","","","","","",""};

    public ProductService() {
        super("s_product", new TableBean("s_product", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
