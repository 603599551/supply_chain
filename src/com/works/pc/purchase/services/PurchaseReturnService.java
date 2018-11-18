package com.works.pc.purchase.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.DateUtil;
import com.utils.UUIDTool;
import com.utils.UserSessionUtil;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static com.common.service.OrderNumberGenerator.getWarehousePurchaseOrderNumber;
import static com.common.service.OrderNumberGenerator.getWarehouseReturnOrderNumber;

/**
 * 该类实现以下功能：
 * 新建采购退货单（1.引单新建 2.引仓库库存新建）
 * 通过采购退货单id显示采购退货项+总金额+流程日志
 * 关闭、完成采购退货单
 * @author CaryZ
 * @date 2018-11-16
 */
@Before(Tx.class)
public class PurchaseReturnService extends BaseService {

    private static final String TABLENAME="s_purchase_return";
    private static String[] purchaseReturnState={"logistics_clearance","purchase_audit","finance_confirm","logistics_delivery","return_shutdown","return_finish"};
    private static String[] columnNameArr = {"id","supplier_id","num","from_purchase_order_id","from_purchase_order_num","return_item","color","order_state","close_date","close_reason","close_id","city","remark","image","return_reason"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","",""};

    public PurchaseReturnService() {
        super(TABLENAME, new TableBean(TABLENAME, columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        List<Record> list=page.getList();
        for (Record r:list){
            r.set("return_item", JSONObject.parseObject(r.getStr("return_item")));
        }
        return page;
     }

    /**
     * 批量新增采购退货单，退货流程记录，逻辑如下：
     * 先接收退货项return_item，根据供应商id拆分成几个部分，生成多个退货单，状态：物流清点，在流程表里建多条记录
     * @author CaryZ
     * @date 2018-11-17
     * @param record 新增数据包括 from_purchase_order_id,from_purchase_order_num,return_item,current_quantity,city、handle_id(从controller传过来)
     * @return List 多个退货单
     */
    public List<Record> batchSave(Record record) throws PcException {
        PurchasePurchasereturnProcessService ppps=enhance(PurchasePurchasereturnProcessService.class);
        //引单id
        String fromPurchaseOrderId=record.getStr("from_purchase_order_id");
        //引单编号
        String fromPurchaseOrderNum=record.getStr("from_purchase_order_num");
        //订单发起城市
        String city=record.getStr("city");
        Map<String,String> stockMap=new HashMap<>();
        //引单新建时
        if (StringUtils.isNotEmpty(fromPurchaseOrderId)){
            List<Record> stockList=Db.find("SELECT id,material_id FROM s_warehouse_stock WHERE purchase_order_id=?",fromPurchaseOrderId);
            for (Record stockR:stockList){
                //key---material_id,value---库存记录id
                stockMap.put(stockR.getStr("material_id"),stockR.getStr("id"));
            }
        }
        JSONArray jsonArray=JSONArray.parseArray(record.getStr("return_item"));
        int jsonLen=jsonArray.size();
        //key---供应商id value---供应商id相同的退货项
        Map<String,String> returnItemMap=new HashMap<>();
        //遍历return_item，将供应商id相同的退货项整理到一起
        for(int i=0;i<jsonLen;i++){
            JSONObject jsob=jsonArray.getJSONObject(i);
            //引单新建时，将库存记录id存进item数组中的每个元素
            if (stockMap.get(jsob.getString("id"))!=null){
                jsob.put("stock_id",stockMap.get(jsob.getString("id")));
            }
            String supplierId=jsob.getString("supplier_id");
            if (returnItemMap.get(supplierId)==null){
                returnItemMap.put(supplierId,jsob.toJSONString());
            }else {
                returnItemMap.put(supplierId,returnItemMap.get(supplierId)+","+jsob.toJSONString());
            }
        }
        int mapLen=returnItemMap.size();
        //处理新增的退货单List
        List<Record> returnItemList=new ArrayList<>(mapLen);
        Iterator<Map.Entry<String, String>> entries = returnItemMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            Record record1=new Record();
            record1.set("id", UUIDTool.getUUID());
            record1.set("supplier_id",entry.getKey());
            record1.set("num",getWarehouseReturnOrderNumber());
            record1.set("from_purchase_order_id",fromPurchaseOrderId);
            record1.set("from_purchase_order_num",fromPurchaseOrderNum);
            record1.set("return_item",entry.getValue());
            record1.set("order_state",purchaseReturnState[0]);
            record1.set("city",city);
            returnItemList.add(record1);
        }
        //批量新增退货流程记录
        if (!ppps.batchSaveReturnProcess(record,returnItemList)){
            return null;
        }
        try{
            //批量新增的退货单List
            return Db.batchSave(TABLENAME,returnItemList,returnItemList.size())==null? null:returnItemList;
        }catch (Exception e){
            throw new PcException(ADD_EXCEPTION,e.getMessage());
        }
    }

    /**
     * 关闭退货单
     * 将退货单表的记录的状态改为“关闭”
     * 将流程表该单的所有记录改成“完成”
     * @author CaryZ
     * @date 2018-11-18
     * @param r 要关闭的采购单ID、关闭原因、关闭人ID
     * @return 运行成功/失败 true/false
     */
    public boolean shutdown(Record r) throws PcException {
        Record record = new Record();
        record.set("id", r.getStr("id"));
        record.set("state", purchaseReturnState[4]);
        record.set("close_date", DateUtil.GetDateTime());
        record.set("close_reason", r.getStr("close_reason"));
        record.set("close_id", r.getStr("close_id"));
        if (!super.updateById(record)) {
            return false;
        }
        return Db.update("UPDATE s_purchase_purchasereturn_process SET state='1' WHERE purchase_id=?",r.getStr("id"))==0? false:true;
    }
}
