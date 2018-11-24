package com.works.pc.order.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.order.services.OrderReturnService;
import org.apache.commons.lang.StringUtils;

/**
 * @author CaryZ
 * @date 2018-11-23
 * 实现门店退货流程
 * 1.小程序提交退货单
 * 2.pc端店长查看退货单（图片传全路径）
 * 3.PC端店长可以撤销退货单，前提在物流没有接收退货单基础上
 * 4.物流接收退货单，可以仿照原系统。修改订单状态，修改门店库存。
 * 5.物流退回退货单，修改订单状态，修改门店库存。
 * 6.物流完成退货单，修改订单状态、修改物流仓库库存。
 */
public class OrderReturnCtrl extends BaseCtrl<OrderReturnService> {

    UserSessionUtil usu=new UserSessionUtil(getRequest());

    private static final String FIELD_NUM="num";
    private static final String FIELD_CREATEDATE="create_date";

    public OrderReturnCtrl() {
        super(OrderReturnService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    /**
     * 新增退货单，放到小程序里，这里不做
     * @param record
     */
    @Override
    public void handleAddRecord(Record record) {

    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    /**
     * 完全匹配查询：退货单状态
     * 模糊查询：退货单号
     * @param record 查询条件
     */
    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword};
            record.set("$all$and#"+FIELD_NUM+"$like$or",keywords);
            record.remove("keyword");
        }
        record.set("$sort"," ORDER BY "+FIELD_CREATEDATE+" DESC");
    }

    /**
     * 物流接收退货单
     * 退货单状态：未接收->已接收
     * 每个原料信息多了warehouse_stock_id，修改订单状态，减少门店库存。
     */
    public void acceptReturnItems(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            boolean flag = service.acceptReturnItems(record);
            if(flag){
                jhm.putMessage("接收成功！");
            }else{
                jhm.putFail("接收失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 物流完成退货单
     * 退货单状态：已接收->已完成
     * 修改订单状态、修改物流仓库库存。
     */
    public void finishOrder(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            boolean flag = service.finishOrder(record);
            if(flag){
                jhm.putMessage("完成成功！");
            }else{
                jhm.putFail("完成失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 店长撤销退货单
     * 退货单状态：已撤销
     * 修改订单状态
     */
    public void revokeOrder(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            boolean flag = service.revokeOrder(record);
            if(flag){
                jhm.putMessage("撤销成功！");
            }else{
                jhm.putFail("撤销失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 物流退回退货单
     * 退货单状态：已退回
     * 修改订单状态，增加门店库存。
     */
    public void returnOrder(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            boolean flag = service.returnOrder(record);
            if(flag){
                jhm.putMessage("退回成功！");
            }else{
                jhm.putFail("退回失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }
}
