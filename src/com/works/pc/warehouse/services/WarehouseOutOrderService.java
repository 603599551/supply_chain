package com.works.pc.warehouse.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.*;
import com.works.pc.order.services.OrderService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseOutOrderService extends BaseService {

    private static String[] columnNameArr = {"id", "warehouse_id", "order_id", "store_id", "store_color", "num", "out_date", "order_num", "create_id", "create_date", "state", "type", "order_item"};
    private static String[] columnTypeArr = {"VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR"};
    private static String[] columnCommentArr = {"", "", "", "", "", "", "", "", "", "", "", "", ""};

    public static final String TO_BE_SORTING = "to_be_sorting";
    public static final String OUT_STORAGE = "out_storage";

    public WarehouseOutOrderService() {
        super("s_warehouse_out_order", new TableBean("s_warehourse_out_order", columnNameArr, columnTypeArr, columnCommentArr));
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
     * 生成原材料出库信息
     * @param orderRecord
     * @param usu
     * @return
     */
    public Map<String, Object> getOutOrderMaterialList(Record orderRecord, UserSessionUtil usu) {
        Map<String, Object> result = new HashMap<>();
        String orderItem = orderRecord.getStr("order_item");
        JSONObject orderItemObj = JSONObject.parseObject(orderItem);
        JSONArray materialArray = orderItemObj.getJSONArray("material_list");

//        List<Record> warehouseStockList = Db.find("select ws.* from s_warehouse_stock ws, s_warehouse w where ss.city=w.city and w.id=ws.warehouse_id and ss.id=? order by ws.batch_num", orderRecord.get("store_id"));
        List<Record> warehouseStockList = null;
        try {
            warehouseStockList = this.list(null);
        } catch (PcException e) {
            e.printStackTrace();
        }
        Map<String, List<Record>> warehouseStockMaterialIdListMap = new HashMap<>();
        if(warehouseStockList != null && warehouseStockList.size() > 0){
            for (Record r : warehouseStockList){
                List<Record> list = warehouseStockMaterialIdListMap.computeIfAbsent(r.getStr("material_id"), k-> new ArrayList<>());
                list.add(r);
            }
        }
        List<Record> all = new ArrayList<>();
        if(materialArray != null && materialArray.size() > 0){
            for(int i = 0; i < materialArray.size(); i++){
                Record material = BeanUtils.jsonToRecord(materialArray.getJSONObject(i));
                int material_number = UnitConversion.outUnit2SmallUnit(material.getInt("out_unit_quantity"), material);
                List<Record> warehouseStockMaterialList = warehouseStockMaterialIdListMap.get(material.getStr("id"));
                if(warehouseStockMaterialList != null && warehouseStockMaterialList.size() > 0){
                    for(int j = 0; j < warehouseStockMaterialList.size(); j++){
                        Record ws = warehouseStockMaterialList.get(j);
                        material.set("batch_num", ws.get("batch_num"));
                        if(ws.getInt("quantity") - material_number >= 0){
                            material.set("warehouse_stock_quantity", material_number);
                            all.add(material);
                            material_number = 0;
                            break;
                        }else{
                            material.set("warehouse_stock_quantity", ws.getInt("quantity"));
                            all.add(material);
                            material_number -= ws.getInt("quantity");
                        }
                    }
                    if(material_number > 0){
                        List<Record> list = (List<Record>) result.computeIfAbsent("problem_list", k->new ArrayList<>());
                        list.add(material);
                    }
                }
            }
            result.put("material_list", all);
        }
        return result;
    }

    public Map<String, String> toBeSorting(Record record, UserSessionUtil usu) {
        Map<String, String> result = new HashMap<>();
        OrderService orderService = enhance(OrderService.class);
        Record order = new Record();
        String sysUserId = usu.getSysUserId();
        order.set("id", record.getStr("id"));
        return result;
    }
}
