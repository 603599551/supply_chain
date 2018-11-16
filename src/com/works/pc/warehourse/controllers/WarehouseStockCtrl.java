package com.works.pc.warehourse.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.goods.services.MaterialService;
import com.works.pc.warehourse.services.WarehouseStockService;
import org.apache.commons.lang.StringUtils;

public class WarehouseStockCtrl extends BaseCtrl<WarehouseStockService> {

    UserSessionUtil usu=new UserSessionUtil(getRequest());

    public WarehouseStockCtrl() {
        super(WarehouseStockService.class);
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

    @Override
    public void createRecordBeforeSelect(Record record) {
        String[] materialIds=record.get("material_ids");
        if (materialIds!=null&&materialIds.length>0){
            record.set("$in#and#material_id",materialIds);
        }
        record.set("$sort"," ORDER BY batch_num DESC");
    }

    /**
     * 将仓库库存以树的方式体现（在盘点时用到）
     */
    public void getStockTree(){
        MaterialService materialService=enhance(MaterialService.class);
        JsonHashMap jhm = new JsonHashMap();
        Record root = materialService.getStockTree("s_warehouse_stock","warehouse_id",(String)usu.getUserBean().get("warehouse_id"));
        jhm.putSuccess(root);
        renderJson(jhm);
    }
}
