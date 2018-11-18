package com.works.pc.purchase.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.DateUtil;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.purchase.services.PurchaseReturnService;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 该类实现以下功能：
 * 新建采购退货单（1.引单新建 2.引仓库库存新建）
 * 通过采购退货单id显示采购退货项+总金额+流程日志
 * 关闭、完成采购退货单
 * @author CaryZ
 * @date 2018-11-16
 */
public class PurchaseReturnCtrl extends BaseCtrl<PurchaseReturnService> {
    UserSessionUtil usu=new UserSessionUtil(getRequest());

    private static final String FIELD_NUM="num";

    public PurchaseReturnCtrl() {
        super(PurchaseReturnService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {
        record.set("handle_id",usu.getSysUserId());
    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    /**
     * 模糊查询:采购退货单编号
     * 完全匹配查询:采购退货单状态、处理人id
     * 按照退货单编号倒序 排序
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
        String handleId=record.getStr("handle_id");
        if (StringUtils.isNotEmpty(handleId)){
            List<Record> list= Db.find("SELECT DISTINCT purchase_id FROM s_purchase_purchasereturn_process WHERE handle_id=?",usu.getSysUserId());
            String[]wildcard=new String[list.size()];
            int i=0;
            for (Record r:list){
                wildcard[i]=r.getStr("purchase_id");
                i++;
            }
            record.set("$in#and#id",wildcard);
        }
        record.set("$sort"," ORDER BY "+FIELD_NUM+" DESC");
    }

    /**
     * 批量新增采购退货单，退货流程记录
     * @author CaryZ
     * @date 2018-11-17
     */
    public void batchSave(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleAddRecord(record);
            List<Record> list= service.batchSave(record);
            if(list != null){
                jhm.putSuccess(list);
            }else{
                jhm.putFail("添加失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 关闭退货单
     * @author CaryZ
     * @date 2018-11-18
     */
    public void shutdown(){
        JsonHashMap jhm = new JsonHashMap();
        Record record=getParaRecord();
        try {
            boolean flag = service.shutdown(record);
            if(flag){
                jhm.putMessage("关闭成功！");
            }else{
                jhm.putFail("关闭失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }
}
