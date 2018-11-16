package com.works.pc.store.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.goods.services.MaterialService;
import com.works.pc.store.services.StoreStockService;

public class StoreStockCtrl extends BaseCtrl<StoreStockService> {

    UserSessionUtil usu=new UserSessionUtil(getRequest());

    public StoreStockCtrl() {
        super(StoreStockService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {

    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    /**
     * 门店库存列表
     * 按照sort ASC排序
     * @param record 查询条件
     */
    @Override
    public void createRecordBeforeSelect(Record record) {
        record.set("$sort"," ORDER BY sort ASC");
    }

    /**
     * 将门店库存以树的方式体现（在盘点时用到）
     */
    public void getStockTree(){
        MaterialService materialService=enhance(MaterialService.class);
        JsonHashMap jhm = new JsonHashMap();
        Record root = materialService.getStockTree("s_store_stock","store_id",(String)usu.getUserBean().get("store_id"));
        jhm.putSuccess(root);
        renderJson(jhm);
    }
}
