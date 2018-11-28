package com.works.pc.order.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.order.services.OrderService;

import java.util.List;
import java.util.Map;

public class OrderCtrl extends BaseCtrl<OrderService> {

    public OrderCtrl() {
        super(OrderService.class);
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

    /**
     * 创建门店订单，选择商品。返回拆分bom后的原材料集合，materialNumber是原料的提货单位数量
     */
    public void createProductOrder() {
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = this.getJson(getRequest());
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            List<Record> materialList = service.createProductOrder(json, usu);
            jhm.putSuccess(materialList);
            jhm.putMessage("创建成功！");
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 提交原材料创建门店订单
     */
    public void createMaterialOrder() {
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = this.getJson(getRequest());
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            Map<String, Record> map = service.createMaterialOrder(json, usu);
            jhm.putSuccess(map);
            jhm.putMessage("创建成功！");
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 物流接收
     */
    public void logisticsReceived(){

    }

    /**
     * 去生成出库单
     *  获取订单数据，物流选择批号，然后生成出库单（调用createOutOrder接口）
     */
    public void getOutOrderMaterialList(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = getParaRecord();
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            Map<String,Object> map = service.getOutOrderMaterialList(record, usu);
            if(map.get("msg") != null){
                jhm.putFail(map.get("msg").toString());
            }else{
                jhm.putSuccess(map.get("material_list"));
                List<Record> problemList = (List<Record>) map.get("problem_list");
                if(problemList != null && problemList.size() > 0){
                    String message = "";
                    for(Record r : problemList){
                        message += r.getStr("name") + ",";
                    }
                    jhm.putMessage(message.substring(0, message.length() - 1) + "库存不足！");
                }else{
                    jhm.putMessage("接收成功！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putError(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 生成出库单
     */
    public void createOutOrder(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = this.getJson(getRequest());
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            service.createOutOrder(json, usu);
            jhm.putMessage("创建成功！");
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 仓库分拣
     *  小程序处理，设置订单状态为：分拣完成
     */
    public void storageSorting(){

    }

    /**
     * 门店接收入库，订单完成
     */
    public void storeReceived(){
        JsonHashMap jhm = new JsonHashMap();
        String id = getPara("id");
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            service.storeReceived(id, usu);
            jhm.putMessage("接收入库成功！");
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 门店撤销
     */
    public void storeRevocation(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = getParaRecord();
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            Map<String, String> map = service.storeRevocation(record, usu);
            if(map != null){
                jhm.putFail(map.get("msg"));
            }else{
                jhm.putMessage("撤销成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putError(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 物流驳回
     */
    public void logisticsOverrule(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = getParaRecord();
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            Map<String, String> map = service.logisticsOverrule(record, usu);
            if(map != null){
                jhm.putFail(map.get("msg"));
            }else{
                jhm.putMessage("撤销成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putError(e.getMessage());
        }
        renderJson(jhm);
    }
}
