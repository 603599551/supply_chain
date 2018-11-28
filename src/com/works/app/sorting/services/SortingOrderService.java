package com.works.app.sorting.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.service.WxSmallProgramService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.UnitConversion;
import com.utils.UserSessionUtil;
import com.works.pc.warehouse.services.WarehouseOutOrderService;

import java.util.*;

public class SortingOrderService extends WxSmallProgramService{

    public List<Map<String, Object>> downOrder(UserSessionUtil usu){
        WarehouseOutOrderService warehouseOutOrderService = enhance(WarehouseOutOrderService.class);
        String warehouseId = (String)usu.getUserBean().get("warehouse_id");
        Record outOrder = new Record();
        outOrder.set("warehouse_id", warehouseId);
        outOrder.set("state", WarehouseOutOrderService.TO_BE_SORTING);
        String sql = "select woo.*, ss.name store_name, so.arrive_date arrive_date from s_warehouse_out_order woo, s_store ss, s_order so where woo.order_id=so.id and woo.store_id=ss.id and woo.warehouse_id=? and woo.state=?";
        List<Record> warehouseOutOrderList = Db.find(sql, warehouseId, WarehouseOutOrderService.TO_BE_SORTING);
        if(warehouseOutOrderList != null && warehouseOutOrderList.size() > 0){
            List<Map<String, Object>> storeList = new ArrayList<>();
            Map<String, List<Record>> storeMap = new HashMap<>();
            for(Record r : warehouseOutOrderList){
                List<Record> list = storeMap.computeIfAbsent(r.get("store_name"), k -> new ArrayList<>());
                list.add(r);
            }
            for(String store_name : storeMap.keySet()){
                Map<String, Object> store = new HashMap<>();
                storeList.add(store);
                List<Record> list = storeMap.get(store_name);
                store.put("name", store_name);
                store.put("store_id", list.get(0).getStr("store_id"));
                store.put("number", list.size());
                store.put("status", "未完成");
                List<Map<String, Object>> orderListR = new ArrayList<>();
                store.put("order", orderListR);
                if(list != null && list.size() > 0){
                    for(Record r : list){
                        JSONArray outList = JSON.parseObject(r.getStr("out_list")).getJSONArray("out_list");
                        Map<String, Object> orderMapR = new HashMap<>();
                        orderMapR.put("orderNum", r.get("order_num"));
                        orderMapR.put("orderTime", r.get("arrive_date"));
                        orderMapR.put("orderId", r.get("order_id"));
                        orderMapR.put("orderStatus", "待分拣");
                        orderMapR.put("orderUntreated", outList.size());
                        List<Map<String, Object>> materialList = new ArrayList<>();
                        orderMapR.put("orderList", materialList);
                        orderListR.add(orderMapR);
                        for(int i = 0; i < outList.size(); i++){
                            JSONObject material =outList.getJSONObject(i);
                            Map<String, Object> materialR = new HashMap<>();
                            materialR.put("name", material.getString("name"));
                            materialR.put("batch_code", material.getString("batch_num"));
                            materialR.put("number", material.getString("order_num"));
                            materialR.put("unit_text", material.getString("out_unit"));
                            materialR.put("code", material.getString("num"));
                            materialR.put("isOk", false);
                            materialR.put("id", material.getString("id"));
                            String attr = UnitConversion.getAttrByOutUnit(r);
                            materialR.put("attribute", attr);
                            materialList.add(materialR);
                        }
                    }
                }
            }
            return storeList;
        }
        return null;
    }

    public void submitOrder(String... ids){
        if(ids != null && ids.length > 0){
            List<Object> params = new ArrayList<>();
            params.add(WarehouseOutOrderService.OUT_STORAGE);
            String wenhao = "";
            for(String s : ids){
                wenhao += "?,";
                params.add(s);
            }
            wenhao = wenhao.substring(0, wenhao.length() - 1);
            String update = "update warehouse_out_order set status=? where id in({{wenhao}})".replace("{{wenhao}}", wenhao);

            Db.update(update, params.toArray(new String[params.size()]));
        }
    }

}
