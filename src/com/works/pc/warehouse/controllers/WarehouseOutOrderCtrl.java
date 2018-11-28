package com.works.pc.warehouse.controllers;

import com.common.controllers.BaseCtrl;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.utils.UUIDTool;
import com.utils.UserSessionUtil;
import com.works.pc.goods.services.MaterialService;
import com.works.pc.warehouse.services.WarehouseOutOrderService;
import com.works.pc.warehouse.services.WarehouseService;
import com.works.pc.warehouse.services.WarehouseStockService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseOutOrderCtrl extends BaseCtrl<WarehouseOutOrderService> {

    public WarehouseOutOrderCtrl() {
        super(WarehouseOutOrderService.class);
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

    }

    public void cs() throws PcException {
        MaterialService materialService = enhance(MaterialService.class);
        List<Record> materialList = materialService.list(new Record());
        Map<String, List<Record>> materialMap = new HashMap<>();
        for(Record r : materialList){
            List<Record> list = materialMap.computeIfAbsent(r.getStr("storage_condition"), k->new ArrayList<>());
            list.add(r);
        }
        WarehouseService warehouseService = enhance(WarehouseService.class);
        List<Record> warehouseList = warehouseService.list(null);
        Map<String, Record> warehouseMap = new HashMap<>();
        for(Record r : warehouseList){
            warehouseMap.put(r.getStr("type"), r);
        }
        List<Record> all = new ArrayList<>();
        for(Map.Entry<String, List<Record>> e : materialMap.entrySet()){
            List<Record> mList = e.getValue();
            String type = e.getKey();
            Record warehouse = warehouseMap.get(type);
            for(Record r : mList){
                Record warehouseStock = new Record();
                warehouseStock.set("id", UUIDTool.getUUID());
                warehouseStock.set("user_id", "2a1c85dc392644a381dd6464c6c93af3");
                warehouseStock.set("warehouse_id", warehouse.get("id"));
                warehouseStock.set("state", "1");
                warehouseStock.set("quantity", "1000000");
                warehouseStock.set("batch_num", "20181123");
                warehouseStock.set("material_data", r.toJson());
                warehouseStock.set("sort", "1");
                warehouseStock.set("material_id", r.get("id"));
                warehouseStock.set("purchase_order_id", "");
                warehouseStock.set("purchase_order_num", "");
                all.add(warehouseStock);
            }
        }
        WarehouseStockService warehouseStockService = enhance(WarehouseStockService.class);
        //warehouseStockService.batchSave(all);
    }

    /**
     * 待分拣，处理出库单，例如选择批号
     */
    public void toBeSorting(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = getParaRecord();
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            Map<String, String> map = service.toBeSorting(record, usu);
            if(map != null){
                jhm.putFail(map.get("msg"));
            }else{
                jhm.putMessage("生成出库单成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putError(e.getMessage());
        }
        renderJson(jhm);
    }
}
