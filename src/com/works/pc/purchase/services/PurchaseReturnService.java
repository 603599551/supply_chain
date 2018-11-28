package com.works.pc.purchase.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bean.TableBean;
import com.common.service.BaseService;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.BeanUtils;
import com.utils.DateUtil;
import com.utils.UUIDTool;
import com.utils.UnitConversion;
import com.works.pc.warehouse.services.WarehouseStockService;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static com.common.service.OrderNumberGenerator.getWarehouseReturnOrderNumber;
import static com.constants.DictionaryConstants.PURCHASE_RETURN_TYPE;

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
    private static String[] columnNameArr = {"id","supplier_id","num","from_purchase_order_id","from_purchase_order_num","return_item","color","order_state","close_date","close_reason","close_id","city","remark","image","return_reason","create_date"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","TEXT","VARCHAR","VARCHAR","VARCHAR","TEXT","VARCHAR","VARCHAR","TEXT","TEXT","TEXT","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","",""};

    public PurchaseReturnService() {
        super(TABLENAME, new TableBean(TABLENAME, columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        if (page!=null&&page.getList()!=null&&page.getList().size()>0){
            List<Record> list=page.getList();
            for (Record r:list){
                r.set("return_item", JSONObject.parseObject(r.getStr("return_item")));
            }
        }
        return page;
     }

    /**
     * 批量新增采购退货单，退货流程记录，逻辑如下：
     * 先接收退货项return_item，根据供应商id拆分成几个部分，生成多个退货单，状态：物流清点，在流程表里建多条记录，批量更新可用库存
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
        Map<String,Record> stockMap=new HashMap<>();
        //引单新建时
        if (StringUtils.isNotEmpty(fromPurchaseOrderId)){
            List<Record> stockList=Db.find("SELECT * FROM s_warehouse_stock WHERE purchase_order_id=?",fromPurchaseOrderId);
            for (Record stockR:stockList){
                //key---material_id,value---库存记录
                stockMap.put(stockR.getStr("material_id"),stockR);
            }
        }
        JSONArray jsonArray=JSONArray.parseArray(record.getStr("return_item"));
        int jsonLen=jsonArray.size();
        //key---供应商id value---供应商id相同的退货项
        Map<String,JSONArray> returnItemMap=new HashMap<>();
        //可用库存list
        List<Record> updateStockList=new ArrayList<>(jsonLen);
        double aq=0.0;
        String changeRecord="";
        Record baseR;
        //遍历return_item，将供应商id相同的退货项整理到一起
        for(int i=0;i<jsonLen;i++){
            JSONObject jsob=jsonArray.getJSONObject(i);
            Record stockMapR=stockMap.get(jsob.getString("id"));
            //引单新建时，将库存记录id存进item数组中的每个元素，并从stockMap中拿change_record和available_quantity（最小单位）
            if (stockMapR!=null){
                jsob.put("stock_id",stockMapR.getStr("id"));
                aq= stockMapR.getDouble("available_quantity");
                baseR=stockMapR;
            }else {
                aq=jsob.getDouble("available_quantity");
                baseR=BeanUtils.jsonToRecord(jsob);
            }
            Record aStockR=new Record();
            aStockR.set("id",jsob.getString("stock_id"));
            //可用库存=可用库存-退货量
            aStockR.set("available_quantity",aq-UnitConversion.outUnit2SmallUnitDecil(BeanUtils.jsonToRecord(jsob),"current_quantity"));
            Record changeR=new Record();
            changeR.set("handle_type","purchase_return");
            changeR.set("handle_tablename",TABLENAME);
            changeR.set("handle_id",record.getStr("handle_id"));
            changeR.set("after_quantity",aStockR.getDouble("available_quantity"));
            //这里handle_record_id还没生成，后期重构时要修正
            aStockR.set("change_record",super.updateChangeRecord(changeR,baseR,record));
            updateStockList.add(aStockR);
            String supplierId=jsob.getString("supplier_id");
            JSONArray ilkArray=returnItemMap.computeIfAbsent(supplierId,k->new JSONArray());
            ilkArray.add(jsob);
        }
        //批量更新可用库存
        WarehouseStockService warehouseStockService=enhance(WarehouseStockService.class);
        if (!warehouseStockService.batchUpdate(updateStockList)){
            return null;
        }
        int mapLen=returnItemMap.size();
        //处理新增的退货单List
        List<Record> returnItemList=new ArrayList<>(mapLen);
        Iterator<Map.Entry<String, JSONArray>> entries = returnItemMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, JSONArray> entry = entries.next();
            Record record1=new Record();
            record1.set("id", UUIDTool.getUUID());
            record1.set("supplier_id",entry.getKey());
            record1.set("num",getWarehouseReturnOrderNumber());
            record1.set("from_purchase_order_id",fromPurchaseOrderId);
            record1.set("from_purchase_order_num",fromPurchaseOrderNum);
            Map<String,JSONArray> RIP=new HashMap<>(1);
            RIP.put("item",entry.getValue());
            record1.set("return_item", JSON.toJSONString(RIP));
            record1.set("order_state",purchaseReturnState[0]);
            record1.set("city",city);
            record1.set("create_date",DateUtil.GetDateTime());
            returnItemList.add(record1);
        }
        try{
            //批量新增的退货单List
            if (Db.batchSave(TABLENAME,returnItemList,returnItemList.size())==null){
                return null;
            }
        }catch (Exception e){
            throw new PcException(ADD_EXCEPTION,e.getMessage());
        }
        //批量新增退货流程记录
        List<Record> returnProcessList=ppps.batchSaveReturnProcess(record,returnItemList);
        for (Record r:returnProcessList){
            r.set("item",JSONObject.parseObject(r.getStr("item")).getJSONArray("item"));
        }
        return returnProcessList;
    }

    /**
     * 关闭退货单
     * 将退货单表的记录的状态改为“关闭”
     * 将流程表该单的所有记录改成“完成”
     * 增加涉及的退货项的可用库存aq,change_record
     * @author CaryZ
     * @date 2018-11-18
     * @param r 要关闭的采购单ID、关闭原因、关闭人ID
     * @return 运行成功/失败 true/false
     */
    public boolean shutdown(Record r) throws PcException {
        Record record = this.findById(r.getStr("id"));
        record.set("state", purchaseReturnState[4]);
        record.set("close_date", DateUtil.GetDateTime());
        record.set("close_reason", r.getStr("close_reason"));
        record.set("close_id", r.getStr("close_id"));
        if (!super.updateById(record)) {
            return false;
        }
        String returnItem=record.getStr("return_item");
        JSONObject jsonObject=JSONObject.parseObject(returnItem);
        JSONArray itemArray=jsonObject.getJSONArray("item");
        int itemLen=itemArray.size();
        String[]ids=new String[itemLen];
        //key---库存记录id，value---退货项信息
        Map<String,JSONObject>itemMap=new HashMap<>(itemLen);
        for(int i=0;i<itemLen;i++){
            ids[i]=itemArray.getJSONObject(i).getString("stock_id");
            itemMap.put(ids[i],itemArray.getJSONObject(i));
        }
        List<Record> updateStockList=super.selectByColumnIn("id",ids);
        for(Record stockR:updateStockList){
            //退货量（出库单位）
            double returnNum=itemMap.get(stockR.getStr("id")).getDouble("current_quantity");
            JSONObject jsob=JSONObject.parseObject(stockR.getStr("material_data"));
            jsob.put("return_quantity",returnNum);
            //退货量（最小单位）
            returnNum=UnitConversion.outUnit2SmallUnitDecil(BeanUtils.jsonToRecord(jsob),"return_quantity");
            Record changeR=new Record();
            changeR.set("handle_type","purchase_return");
            changeR.set("handle_tablename",TABLENAME);
            changeR.set("handle_id",record.getStr("close_id"));
            changeR.set("after_quantity",stockR.getDouble("available_quantity"));
            stockR.set("change_record",super.updateChangeRecord(changeR,stockR,record));
            stockR.set("available_quantity",stockR.getDouble("available_quantity")+returnNum);
        }
        //批量更新可用库存
        WarehouseStockService warehouseStockService=enhance(WarehouseStockService.class);
        if (!warehouseStockService.batchUpdate(updateStockList)){
            return false;
        }
        return Db.update("UPDATE s_purchase_purchasereturn_process SET state='1' WHERE purchase_return_id=?",r.getStr("id"))==0? false:true;
    }

    /**
     * 通过采购退货单id显示退货项+总金额+流程日志
     * 数据格式见接口文档
     * @param purchaseId 采购退货单id
     * @return
     */
    public Record showPurchaseReturnOrderById(String purchaseId) throws PcException{
        PurchasePurchasereturnProcessService ppps=enhance(PurchasePurchasereturnProcessService.class);
        Record orderR=Db.findFirst("SELECT * FROM s_purchase_return WHERE id=?",purchaseId);
        JSONArray itemArray=JSONObject.parseObject(orderR.getStr("return_item")).getJSONArray("item");
        Record record=new Record();
        record.setColumns(orderR);
        record.set("order_state_text",DictionaryConstants.DICT_STRING_MAP.get(PURCHASE_RETURN_TYPE).get(record.getStr("order_state")));
        //前端要求判断第几个
        int stateLen=purchaseReturnState.length;
        int count=0;
        for (int i=0;i<stateLen;i++){
            if (StringUtils.equals(record.getStr("order_state"),purchaseReturnState[i])){
                if (i==5){
                    count=5;
                }else if (i==4){
                    count=-1;
                }else {
                    count=i+1;
                }
                break;
            }
        }
        record.set("state_count",count);
        //采购项
        record.set("item",itemArray);
        int len=itemArray.size();
        double singlePrice=0;
        double totalPrice=0;
        //采购单总金额
        for (int i=0;i<len;i++){
            singlePrice=itemArray.getJSONObject(i).getDoubleValue("current_price")*itemArray.getJSONObject(i).getDoubleValue("current_quantity");
            totalPrice+=singlePrice;
        }
        record.set("total_price",totalPrice);
        //流程日志
        List<Record> processList=ppps.getReturnProcessForOneOrder(purchaseId);
        record.set("process",handleProcessInfo(processList));
        return record;
    }

    /**
     * 对流程记录数据的处理，整理成接口文档规定的格式
     * @param processList 流程记录list
     * @return 流程日志list
     * @throws PcException
     */
    public List<Record> handleProcessInfo(List<Record> processList)throws PcException{
        for (Record record:processList){
            record.remove("item");
            Record messageR=new Record();
            if (StringUtils.isEmpty(record.getStr("handle_date"))){
                record.set("is_handle",0);
                record.set("message","未处理");
            }else {
                if (StringUtils.equals(record.getStr("state"),"1")){
                    messageR.set("is_agree_text","同意");
                }else {
                    messageR.set("is_agree_text","不同意");
                }
                record.set("is_handle",1);
                record.set("message",messageR);
            }
        }
        return processList;
    }

}
