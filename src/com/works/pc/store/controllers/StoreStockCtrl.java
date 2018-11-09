package com.works.pc.store.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.store.services.StoreStockService;

public class StoreStockCtrl extends BaseCtrl<StoreStockService> {

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
}
