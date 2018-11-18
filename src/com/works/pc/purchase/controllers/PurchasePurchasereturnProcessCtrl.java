package com.works.pc.purchase.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.DateUtil;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.purchase.services.PurchasePurchasereturnProcessService;

import java.util.Map;

/**
 * 该类实现采购流程、采购退货流程
 * @author CaryZ
 * @date 2018-11-12
 */
public class PurchasePurchasereturnProcessCtrl extends BaseCtrl<PurchasePurchasereturnProcessService> {
    UserSessionUtil usu=new UserSessionUtil(getRequest());

    public PurchasePurchasereturnProcessCtrl() {
        super(PurchasePurchasereturnProcessService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {

    }

    @Override
    public void handleUpdateRecord(Record record) {
        record.set("handle_date", DateUtil.GetDateTime());
        record.set("state", "1");
        record.set("warehouse_id",usu.getUserBean().get("warehouse_id"));
    }

    @Override
    public void createRecordBeforeSelect(Record record) {

    }


    /**
     * 更新采购流程
     * @author CaryZ
     * @date 2018-11-12
     */
    @Override
    public void updateById(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleUpdateRecord(record);
            Map<String,Object> resultMap = service.updateByIdReturnMap(record);
            if((boolean)resultMap.get("flag")){
                jhm.putSuccess(resultMap);
            }else{
                jhm.putFail("修改失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 更新退货流程
     * @author CaryZ
     * @date 2018-11-18
     */
    public void updateReturnProcess(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleUpdateRecord(record);
            boolean flag = service.updateReturnProcess(record);
            if(flag){
                jhm.putMessage("修改成功！");
            }else{
                jhm.putFail("修改失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }
}
