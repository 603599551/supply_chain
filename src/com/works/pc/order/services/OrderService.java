package com.works.pc.order.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.common.service.OrderNumberGenerator;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.*;
import com.works.pc.goods.services.ProductService;
import com.works.pc.store.services.StoreStockService;
import com.works.pc.warehouse.services.WarehouseOutOrderService;
import com.works.pc.warehouse.services.WarehouseStockService;

import java.util.*;

public class OrderService extends BaseService {

    private static String[] columnNameArr = {"id", "store_id", "num", "order_date", "arrive_date", "order_item", "store_color", "order_state", "create_id", "create_date", "logistics_id", "logistics_date", "accept_id", "accept_date", "order_type", "sorting_id", "sorting_date", "close_date", "close_reason", "close_id", "city", "remark"};
    private static String[] columnTypeArr = {"VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR"};
    private static String[] columnCommentArr = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

    private static final String TO_BE_CONFIRMED = "to_be_confirmed";
    private static final String TO_BE_RECEIVED = "to_be_received";
    private static final String STORE_REVOCATION = "store_revocation";
    private static final String LOGISTICS_OVERRULE = "logistics_overrule";
    private static final String TO_BE_OUT_STORAGE = "to_be_out_storage";
    private static final String TO_BE_SORTING = "to_be_sorting";
    private static final String SORTING_FINISH = "sorting_finish";
    private static final String FINISH = "finish";

    public OrderService() {
        super("s_order", new TableBean("s_order", columnNameArr, columnTypeArr, columnCommentArr));
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
     * 订单，选择原材料后保存的方法
     * @param json 前台传的参数
     * @param usu 用户
     * @return
     */
    public Map<String,Record> createMaterialOrder(JSONObject json, UserSessionUtil usu) {
        String orderId = json.getString("id");
        try {
            Record order = this.findById(orderId);
            JSONArray materialArray = json.getJSONArray("material_list");
            JSONObject orderItemObj = JSONObject.parseObject(JSON.toJSONString(order.getStr("order_item")));
            List<Record> materialList = BeanUtils.jsonArrayToList(materialArray);
            orderItemObj.put("material_list", BeanUtils.recordListToMapList(materialList));
            Record updateOrder = new Record();
            updateOrder.set("id", orderId);
            updateOrder.set("order_item", orderItemObj.toJSONString());
            updateById(updateOrder);
        } catch (PcException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 门店撤销
     * @param record
     * @param usu
     * @return
     */
    public Map<String,String> storeRevocation(Record record, UserSessionUtil usu) {
        return close(record, usu, STORE_REVOCATION);
    }

    /**
     * 物流驳回
     * @param record
     * @param usu
     * @return
     */
    public Map<String,String> logisticsOverrule(Record record, UserSessionUtil usu) {
        return close(record, usu, LOGISTICS_OVERRULE);
    }

    /**
     * 关闭订单，包括门店撤销和物流驳回
     * @param record
     * @param usu
     * @param state
     * @return
     */
    private Map<String, String> close(Record record, UserSessionUtil usu, String state){
        Map<String, String> result = new HashMap<>();
        try {
            Record order = new Record();
            String sysUserId = usu.getSysUserId();
            order.set("order_state", state);
            order.set("close_date", DateUtil.GetDateTime());
            order.set("close_id", sysUserId);
            order.set("close_reason", record.getStr("close_reason"));
            order.set("id", record.getStr("id"));
            if(this.updateById(order)){
                return null;
            }else{
                result.put("msg", "撤销失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            result.put("msg", e.getMsg());
        }
        return result;
    }

    /**
     * 订单，选择商品保存时的方法
     * @param json
     * @param usu
     * @return
     */
    public List<Record> createProductOrder(JSONObject json, UserSessionUtil usu) {
        ProductService productService = enhance(ProductService.class);
        String id = UUIDTool.getUUID();
        String storeId = usu.getUserBean().get("store_id").toString();
        String storeColor = usu.getUserBean().get("store_color").toString();
        String num = OrderNumberGenerator.getStoreOrderNumber();
        String orderDate = json.getString("order_date");
        String arriveDate = json.getString("arrive_date");
        String orderType = json.getString("order_type");
        String createId = usu.getSysUserId();
        String createDate = DateUtil.GetDateTime();
        String orderState = TO_BE_CONFIRMED;
        Map<String, Object> orderItemMap = new HashMap<>();
        JSONArray orderItem = json.getJSONArray("order_item");//id和number
        try {
            List<Record> productList = null;
            if (orderItem != null && orderItem.size() > 0) {
                List<String> productIdList = new ArrayList<>();
                Map<String, JSONObject> productIdJsonMap = new HashMap<>();
                for (int i = 0; i < orderItem.size(); i++) {
                    JSONObject obj = orderItem.getJSONObject(i);
                    productIdList.add(obj.getString("id"));
                    productIdJsonMap.put(obj.getString("id"), obj);
                }
                Record productSelect = new Record();
                String key = "$in#and#id";
                productSelect.set(key, productIdList.toArray(new String[productIdList.size()]));
                productList = productService.list(productSelect);
                if (productList != null && productList.size() > 0) {
                    for (Record r : productList) {
                        JSONObject obj = productIdJsonMap.get(r.getStr("id"));
                        r.set("order_quantity", obj.getIntValue("number"));
                    }
                }
                orderItemMap.put("product_list", BeanUtils.recordListToMapList(productList));
            }
            Record order = new Record();
            order.set("id", id);
            order.set("store_id", storeId);
            order.set("store_color", storeColor);
            order.set("num", num);
            order.set("order_date", orderDate);
            order.set("arrive_date", arriveDate);
            order.set("create_id", createId);
            order.set("create_date", createDate);
            order.set("order_state", orderState);
            order.set("order_type", orderType);
            Map<String, Record> result = getBomMaterialMap(productList, null);
            Map<String, Record> oldMaterialMap = getBomMaterialFromDateToDateMap(orderDate, arriveDate, null, null);
            List<Record> materialList = new ArrayList<>();
            for(Map.Entry<String, Record> entry : result.entrySet()){
                Record material = entry.getValue();
                Record oldMaterial = oldMaterialMap.get(entry.getKey());
                int oldMaterialOrderQuantity = 0;
                int oldOutUnitQuantity = 0;
                int oldOutUnitQuantity2OrderQuantity = 0;
                if(oldMaterial != null){
                    oldMaterialOrderQuantity = oldMaterial.getInt("total_material_quantity");
                    oldOutUnitQuantity = oldMaterial.getInt("out_unit_quantity");
                    oldOutUnitQuantity2OrderQuantity = UnitConversion.outUnit2SmallUnit(oldOutUnitQuantity, oldMaterial);
                }
                int materialOrderQuantity = material.getInt("total_material_quantity");
                int materialOutUnitQuantity = UnitConversion.smallUnit2outUnit(materialOrderQuantity - (oldOutUnitQuantity2OrderQuantity - oldMaterialOrderQuantity), material);
                if(materialOutUnitQuantity > 0){
                    material.set("out_unit_quantity", materialOutUnitQuantity);
                }else{
                    material.set("out_unit_quantity", 0);
                }
                materialList.add(material);
            }
            orderItemMap.put("material_list", BeanUtils.recordListToMapList(materialList));
            order.set("order_item", JSON.toJSON(orderItemMap).toString());
            this.add(order);
            return materialList;
        } catch (PcException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过bom整理一个map，Map<原料id， 原料对象>
     * @param productList 商品集合
     * @param map 原料map集合
     * @return
     */
    public Map<String, Record> getBomMaterialMap(List<Record> productList, Map<String, Record> map) {
        Map<String, Record> result = map != null ? map : new HashMap<>();
        if (productList != null && productList.size() > 0) {
            for (int i = 0; i < productList.size(); i++) {
                Record record = productList.get(i);
                result = getOneProductdBomMaterialMap(record, result);
            }
        }
        return result;
    }

    /**
     * 一个商品的bom拆分
     * @param record
     * @param map
     * @return
     */
    public Map<String, Record> getOneProductdBomMaterialMap(Record record, Map<String, Record> map){
        Map<String, Record> result = map != null ? map : new HashMap<>();
        JSONObject json = JSON.parseObject(record.getStr("bom"));
        JSONArray bomArray = json.getJSONArray("bom");
        int number = record.getInt("order_quantity");
        for (int j = 0; j < bomArray.size(); j++) {
            JSONObject materialJson = bomArray.getJSONObject(j);
            Record material = result.computeIfAbsent(materialJson.getString("id"), k -> BeanUtils.jsonToRecord(materialJson));
            int totalMaterialQuantity = number * material.getInt("net_num") + (material.get("total_material_quantity") != null ? material.getInt("total_material_quantity") : 0);
            material.set("total_material_quantity", totalMaterialQuantity);
        }
        return result;
    }

    /**
     * 查询时间段之间的bom拆分order表中商品的原料拆分
     * @param startDate 起始时间
     * @param endDate 结束时间
     * @param map
     * @param productList
     * @return
     */
    public Map<String, Record> getBomMaterialFromDateToDateMap(String startDate, String endDate, Map<String, Record> map, List<Record> productList){
        Map<String, Record> result = map != null ? map : new HashMap<>();
        Record selectRecord = new Record();
        String selectKey = "$fromTo#and#arrive_date > ? and arrive_date < ?";
        String[] selectValue = {startDate, endDate};
        selectRecord.set(selectKey, selectValue);
        try {
            List<Record> orderList = this.list(selectRecord);
            if(productList != null && productList.size() > 0){
                orderList.addAll(productList);
            }
            if(orderList != null && orderList.size() > 0){
                for(Record record : orderList){
                    JSONObject orderItem = JSONObject.parseObject(record.getStr("order_item"));
                    JSONArray jsonArray = orderItem.getJSONArray("material_list");
                    List<Record> list = BeanUtils.jsonArrayToList(jsonArray);
                    for(Record r : list){
                        Record material = result.get(r.getStr("id"));
                        if(material != null){
                            material.set("total_material_quantity", material.getInt("total_material_quantity") + r.getInt("total_material_quantity"));
                            material.set("out_unit_quantity", material.getInt("out_unit_quantity") + r.getInt("out_unit_quantity"));
                        }else{
                            result.put(r.getStr("id"), r);
                        }
                    }
                }
            }
        } catch (PcException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成出库单
     * @param record
     * @param usu
     * @return
     */
    public Map<String,Object> getOutOrderMaterialList(Record record, UserSessionUtil usu) {
        Map<String, Object> result = new HashMap<>();
        try {
            WarehouseOutOrderService warehouseOutOrderService = enhance(WarehouseOutOrderService.class);
            Record order = new Record();
            String sysUserId = usu.getSysUserId();
            order.set("order_state", TO_BE_OUT_STORAGE);
            order.set("logistics_date", DateUtil.GetDateTime());
            order.set("logistics_id", sysUserId);
            order.set("id", record.getStr("id"));
            Record orderRecord = this.findById(record.getStr("id"));
            Map<String, Object> map = warehouseOutOrderService.getOutOrderMaterialList(orderRecord, usu);
            if(this.updateById(order)){
                result.putAll(map);
            }else{
                result.put("msg", "生成出库单失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            result.put("msg", e.getMsg());
        }
        return result;
    }

    public void createOutOrder(JSONObject json, UserSessionUtil usu) {
        WarehouseStockService warehouseStockService = enhance(WarehouseStockService.class);
        WarehouseOutOrderService warehouseOutOrderService = enhance(WarehouseOutOrderService.class);
        JSONArray jsonArray = json.getJSONArray("material_list");
        String orderId = json.getString("order_id");
        try {
            Record order = this.findById(orderId);
            Set<String> warehouseStockIdSet = new HashSet<>();
            Map<String, JSONObject> materialIdJsonMap = new HashMap<>();
            if(jsonArray != null && jsonArray.size() > 0){
                for(int i = 0; i < jsonArray.size(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    materialIdJsonMap.put(jsonObject.getString("id"), jsonObject);
                    warehouseStockIdSet.add(jsonObject.getString("id"));
                }
            }
            Record warehouseStockSelect = new Record();
            warehouseStockSelect.set("$in#and#id", warehouseStockIdSet.toArray(new String[warehouseStockIdSet.size()]));
            List<Record> warehouseStockList = warehouseStockService.list(warehouseStockSelect);
            Map<String, List<Record>> warehouseIdWarehouseStockListMap = new HashMap<>();
            if(warehouseStockList != null && warehouseStockList.size() > 0){
                for(Record r : warehouseStockList){
                    List<Record> list = warehouseIdWarehouseStockListMap.computeIfAbsent(r.getStr("warehouse_id"), k->new ArrayList<>());
                    list.add(r);
                }
            }
            List<Record> warehouseOutOrderList = new ArrayList<>();
            for(Map.Entry<String, List<Record>> entry : warehouseIdWarehouseStockListMap.entrySet()){
                String warehouseId = entry.getKey();
                List<Record> list = entry.getValue();
                if(list != null && list.size() > 0){
                    for(Record r : list){
                        JSONObject materialJson = materialIdJsonMap.get(r.getStr("material_id"));
                        r.set("out_number", materialJson.getIntValue("out_number"));
                    }
                }
                Map<String, Object> orderItemMap = new HashMap<>();
                orderItemMap.put("out_list", BeanUtils.recordListToMapList(list));
                String orderItem = JSON.toJSONString(orderItemMap);
                Record warehouseOutOrder = new Record();
                warehouseOutOrder.set("id", UUIDTool.getUUID());
                warehouseOutOrder.set("warehouse_id", warehouseId);
                warehouseOutOrder.set("order_id", order.get("id"));
                warehouseOutOrder.set("store_id", order.get("store_id"));
                warehouseOutOrder.set("store_color", order.get("store_color"));
                warehouseOutOrder.set("num", OrderNumberGenerator.getWarehouseOutOrderNumber());
                warehouseOutOrder.set("out_date", order.get("arrive_date"));
                warehouseOutOrder.set("order_num", order.get("num"));
                warehouseOutOrder.set("create_id", usu.getSysUserId());
                warehouseOutOrder.set("create_date", DateUtil.GetDateTime());
                warehouseOutOrder.set("state", WarehouseOutOrderService.TO_BE_SORTING);
                warehouseOutOrder.set("type", order.get("order_type"));
                warehouseOutOrder.set("order_item", orderItem);
                warehouseOutOrderList.add(warehouseOutOrder);
            }
            JSONObject orderItemObj = JSON.parseObject(order.getStr("order_item"));
            if(warehouseOutOrderList != null && warehouseOutOrderList.size() > 0){
                Db.batchSave(warehouseOutOrderService.getTableName(), warehouseOutOrderList, warehouseOutOrderList.size());
                JSONArray orderItemArray = JSONArray.parseArray(JSON.toJSONString(BeanUtils.recordListToMapList(warehouseOutOrderList)));
                orderItemObj.put("warehouseOutOrderList", orderItemArray);
            }else{
                orderItemObj.put("warehouseOutOrderList", new String[0]);
            }
            Record orderUpdate = new Record();
            orderUpdate.set("id", order.get("id"));
            order.set("order_state", TO_BE_SORTING);
            order.set("order_item", orderItemObj.toJSONString());
            this.updateById(order);
        } catch (PcException e) {
            e.printStackTrace();
        }
    }

    public void storeReceived(String id, UserSessionUtil usu) {
        try {
            Record order = this.findById(id);
            JSONObject orderItemObj = JSON.parseObject(order.getStr("order_item"));
            JSONArray warehouseOutOrderArray = orderItemObj.getJSONArray("warehouseOutOrderList");
            if(warehouseOutOrderArray != null && warehouseOutOrderArray.size() > 0){
                List<Record> storeStockMaterialList = new ArrayList<>();
                for(int i = 0; i < warehouseOutOrderArray.size(); i++){
                    JSONObject warehouseOutOrder = warehouseOutOrderArray.getJSONObject(i);
                    JSONArray orderItem = warehouseOutOrder.getJSONArray("order_item");
                    for(int j = 0; j < orderItem.size(); j++){
                        JSONObject item = orderItem.getJSONObject(j);
                        Record storeStock = new Record();
                        storeStock.set("material_id", item.getString("material_id"));
                        storeStock.set("out_number", item.getIntValue("out_number"));
                        storeStockMaterialList.add(storeStock);
                    }
                }
            }
        } catch (PcException e) {
            e.printStackTrace();
        }
    }

    public void addStoreStock(List<Record> storeStockMaterialList, String storeId){
        StoreStockService storeStockService = enhance(StoreStockService.class);
        Record record = new Record();
        record.set("store_id", storeId);
        try {
            List<Record> storeStockList = storeStockService.list(record);
            Map<String, Record> storeStockMap = new HashMap<>();
            if(storeStockList != null && storeStockList.size() > 0){
                for(Record r : storeStockList){
                    storeStockMap.put(r.getStr("material_id"), r);
                }
            }
            List<Record> updateList = new ArrayList<>();
            List<Record> addList = new ArrayList<>();
            if(storeStockMaterialList != null && storeStockMaterialList.size() > 0){
                for(Record r : storeStockMaterialList){
                    Record storeStock = storeStockMap.get(r.getStr("material_id"));
                    if(storeStock != null){

                    }else{
                        storeStock = new Record();
                    }
                }
            }
        } catch (PcException e) {
            e.printStackTrace();
        }
    }

}
