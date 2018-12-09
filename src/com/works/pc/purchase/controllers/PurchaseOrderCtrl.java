package com.works.pc.purchase.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.DateUtil;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.purchase.services.PurchaseOrderService;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import static com.constants.DictionaryConstants.PURCHASE_ORDER_TYPE;
import static com.constants.DictionaryConstants.PURCHASE_TYPE;

/**
 * 该类实现以下功能：
 * 新建采购单（1.完全新建。2.引单新建）
 * 修改采购单状态（物流、采购员、财务、老板、仓库、关闭）
 * 根据id查询采购单的采购项
 * 采购单回显：采购项+总金额+日志
 * @author CaryZ
 * @date 2018-11-11
 */
public class PurchaseOrderCtrl extends BaseCtrl<PurchaseOrderService> {
    UserSessionUtil usu=new UserSessionUtil(getRequest());

    private static final String FIELD_NUM="num";
    private static final String FIELD_CREATEDATE="create_date";

    public PurchaseOrderCtrl() {
        super(PurchaseOrderService.class);
    }

    @Override
    public void handleRecord(Record record) {
        record.set("purchase_type_text",DictionaryConstants.DICT_STRING_MAP.get(PURCHASE_TYPE).get(record.getStr("purchase_type")));
        record.set("state_text",DictionaryConstants.DICT_STRING_MAP.get(PURCHASE_ORDER_TYPE).get(record.getStr("state")));

    }

    /**
     * 因为新建采购单已经完成了物流这一步骤的操作，所以state直接设置为采购
     * @param record
     */
    @Override
    public void handleAddRecord(Record record) {
        record.set("state","purchase");
        record.set("create_id",usu.getSysUserId());
        record.set("create_date", DateUtil.GetDateTime());
    }

    @Override
    public void handleUpdateRecord(Record record) {
        record.set("create_id",usu.getSysUserId());
        record.set("create_date", DateUtil.GetDateTime());
    }

    /**
     * 模糊查询:采购单编号
     * 完全匹配查询:采购单状态、处理人id
     * 按照创建时间倒序 排序
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
        record.set("$sort"," ORDER BY "+FIELD_CREATEDATE+" DESC");
    }

    /**
     * 新增采购单信息
     * @author CaryZ
     * @date 2018-11-11
     */
    @Override
    public void add(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleAddRecord(record);
            String id = service.add(record);
            if(id != null){
                jhm.putMessage("添加成功！");
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
     * 关闭采购单
     * @author CaryZ
     * @date 2018-11-13
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

    /**
     * 完成采购单
     * @author CaryZ
     * @date 2018-11-13
     */
    public void finish(){
        JsonHashMap jhm = new JsonHashMap();
        String id=getPara("id");
        try {
            boolean flag = service.finish(id);
            if(flag){
                jhm.putMessage("完成！");
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
     * 通过采购单id显示采购项+总金额+流程日志
     * @author CaryZ
     * @date 2018-11-14
     */
    public void showPurchaseOrderById(){
        JsonHashMap jhm = new JsonHashMap();
        String id=getPara("id");
        try {
            jhm.putSuccess(service.showPurchaseOrderById(id));
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 引单新建时，通过采购单id查询信息，这里主要是把item的数据格式变成list
     * @author CaryZ
     * @date 2018-11-14
     */
    public void queryFromPurchaseOrder(){
        JsonHashMap jhm = new JsonHashMap();
        String id=getPara("id");
        try {
            jhm.putSuccess(service.queryFromPurchaseOrder(id));
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }
}
