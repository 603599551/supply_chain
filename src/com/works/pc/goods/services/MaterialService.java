package com.works.pc.goods.services;

import com.alibaba.druid.util.StringUtils;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.StringUtil;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

@Before(Tx.class)
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

    /**
     * 根据盘点项JSON数组查询material数据
     * @author CaryZ
     * @date 2018-11-08
     * @param countItems 盘点项JSON数组
     * @return materialList
     */
    public List<Record> queryMaterials(JSONArray countItems){
        int countLen=countItems.size();
        String[] ids=new String[countLen];
        for (int i=0;i<countLen;i++){
            ids[i]=countItems.getJSONObject(i).getString("id");
        }
        return queryMaterials(ids);
    }

    /**
     * 根据ids查询material数据
     * @author CaryZ
     * @date 2018-11-08
     * @param ids 盘点项ids
     * @return materialList
     */
    public List<Record> queryMaterials(String... ids){
        return super.selectByColumnIn("id",ids);
    }
}
