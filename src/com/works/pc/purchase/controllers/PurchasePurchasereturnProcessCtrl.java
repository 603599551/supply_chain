package com.works.pc.purchase.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.DateUtil;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.purchase.services.PurchasePurchasereturnProcessService;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static com.constants.DictionaryConstants.PURCHASE_ORDER_TYPE;
import static com.constants.DictionaryConstants.PURCHASE_TYPE;

/**
 * 该类实现采购流程、采购退货流程
 * @author CaryZ
 * @date 2018-11-12
 */
public class PurchasePurchasereturnProcessCtrl extends BaseCtrl<PurchasePurchasereturnProcessService> {
    UserSessionUtil usu=new UserSessionUtil(getRequest());

    private static final String FIELD_NUM="num";

    public PurchasePurchasereturnProcessCtrl() {
        super(PurchasePurchasereturnProcessService.class);
    }

    @Override
    public void handleRecord(Record record) {
        record.set("purchase_type_text", DictionaryConstants.DICT_STRING_MAP.get(PURCHASE_TYPE).get(record.getStr("purchase_type")));
        record.set("state_text", DictionaryConstants.DICT_STRING_MAP.get(PURCHASE_ORDER_TYPE).get(record.getStr("state")));
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
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword};
            record.set("$all$and#"+FIELD_NUM+"$like$or",keywords);
            record.remove("keyword");
        }
        //要改
        if (StringUtils.equals(record.getStr("purchase_order_state"),"purchase")){
            record.set("handle_id","caigou_id");
        }else if (StringUtils.equals(record.getStr("purchase_order_state"),"finance")){
            record.set("handle_id","caiwu_id");
        }else if (StringUtils.equals(record.getStr("purchase_order_state"),"boss")){
            record.set("handle_id","boss_id");
        }else if (StringUtils.equals(record.getStr("purchase_order_state"),"warehouse")){
            record.set("handle_id","1");
        };
        record.set("state","0");
        record.set("$sort"," ORDER BY num ASC");
        //        record.set("handle_id",usu.getSysUserId())
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
