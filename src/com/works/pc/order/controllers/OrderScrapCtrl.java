package com.works.pc.order.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.order.services.OrderScrapService;
import org.apache.commons.lang.StringUtils;

import static com.constants.DictionaryConstants.STORE_SCRAP_STATE;

/**
 * @author CaryZ
 * @date 2018-11-25
 * 实现门店废弃流程
 * 1.小程序提交废弃单
 * 2.pc端店长查看废弃单（图片传全路径）
 * 4.物流接收废弃单，可以仿照原系统。修改订单状态，减少门店库存。
 * 5.物流退回废弃单，修改订单状态，增加门店库存。
 * 6.物流完成废弃单，修改订单状态。
 */
public class OrderScrapCtrl extends BaseCtrl<OrderScrapService> {

    UserSessionUtil usu=new UserSessionUtil(getRequest());

    private static final String FIELD_NUM="num";
    private static final String FIELD_CREATEDATE="create_date";

    public OrderScrapCtrl() {
        super(OrderScrapService.class);
    }

    @Override
    public void handleRecord(Record record) {
        record.set("logistics_id",usu.getSysUserId());
        record.set("order_state_text", DictionaryConstants.DICT_STRING_MAP.get(STORE_SCRAP_STATE).get(record.getStr("order_state")));
    }

    /**
     * 新增废弃单，放到小程序里，这里不做
     * @param record
     */
    @Override
    public void handleAddRecord(Record record) {

    }

    /**
     * 完全匹配查询：废弃单状态、门店id、创建开始时间到结束时间
     * 模糊查询：废弃单号
     * @param record 查询条件
     */
    @Override
    public void handleUpdateRecord(Record record) {

    }

    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword};
            record.set("$all$and#"+FIELD_NUM+"$like$or",keywords);
            record.remove("keyword");
        }
        String fromDate=record.getStr("from_date");
        String toDate=record.getStr("to_date");
        if (StringUtils.isNotEmpty(fromDate)&&StringUtils.isNotEmpty(toDate)){
            record.set("$fromto"," AND Date(create_date) BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
        }
        record.set("$sort"," ORDER BY "+FIELD_CREATEDATE+" DESC");
    }

    /**
     * 物流接收废弃单
     * 废弃单状态：未接收->已接收
     * 修改订单状态，减少门店库存。
     */
    public void acceptReturnItems(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleRecord(record);
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
     * 物流完成废弃单
     * 退货单状态：已接收->已完成
     * 修改订单状态
     */
    public void finishOrder(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleRecord(record);
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
     * 店长撤销废弃单
     * 废弃单状态：已撤销
     * 修改订单状态
     */
    public void revokeOrder(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleRecord(record);
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
     * 物流退回废弃单
     * 退货单状态：已退回
     * 修改订单状态，增加门店库存。
     */
//    public void returnOrder(){
//        JsonHashMap jhm = new JsonHashMap();
//        JSONObject json = getJson(getRequest());
//        Record record = BeanUtils.jsonToRecord(json);
//        try {
//            handleRecord(record);
//            boolean flag = service.returnOrder(record);
//            if(flag){
//                jhm.putMessage("退回成功！");
//            }else{
//                jhm.putFail("退回失败！");
//            }
//        } catch (PcException e) {
//            e.printStackTrace();
//            jhm.putError(e.getMsg());
//        }
//        renderJson(jhm);
//    }
}
