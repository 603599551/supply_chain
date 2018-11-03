package com.works.pc.goods.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class MaterialService extends BaseService {

    private static String[] columnNameArr = {"id","name","num","pinyin","cost_price","purchase_price","min_unit","min2mid_num","mid_unit","mid2max_num","max_unit","out_unit","attribute","brand","storage_condition","shelf_life_num","shelf_life_unit","security_time","order_type","model","createdate","create_id","updatedate","update_id","state","remark","type","catalog_id"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","DECIMAL","DECIMAL","VARCHAR","INT","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","","","","","","","","","","","","","",""};

    public MaterialService() {
        super("s_material", new TableBean("s_material", columnNameArr, columnTypeArr, columnCommentArr));
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
