package com.works.pc.purchase.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bean.TableBean;
import com.common.service.BaseService;
import com.constants.ProcessConstants;
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
import com.works.pc.supplier.services.SupplierService;
import com.works.pc.sys.services.SysUserService;
import com.works.pc.warehouse.services.WarehouseStockService;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.constants.DictionaryConstants.PROCESS_TYPE;
import static com.constants.ProcessConstants.PURCHASE_PROCESS_TYPE;
import static com.constants.ProcessConstants.PURCHASE_RETURN_TYPE;
import static com.utils.BeanUtils.jsonArrayToList;
import static com.utils.BeanUtils.recordListToMapList;
import static com.utils.NumberUtils.getMoney;

/**
 * 该类实现采购流程、采购退货流程
 * @author CaryZ
 * @date 2018-11-12
 */
@Before(Tx.class)
public class PurchasePurchasereturnProcessService extends BaseService {

    private static final String TABLENAME="s_purchase_purchasereturn_process";
    private static final String LOGISTICS="logistics";
    private static final String WAREHOUSE="warehouse";
    private static final String PURCHASE="purchase";
    private static final String BOSS="boss";
    private static String[] purchaseOrderState= ProcessConstants.PROCESS_STRINGARRAY_MAP.get(PURCHASE_PROCESS_TYPE);
    private static String[] purchaseReturnState= ProcessConstants.PROCESS_STRINGARRAY_MAP.get(PURCHASE_RETURN_TYPE);
    private static String[] columnNameArr = {"id","purchase_id","purchase_return_id","num","handle_id","handle_date","purchase_type","remark","parent_id","to_user_id","state","item","purchase_order_state"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","TEXT","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","",""};

    public PurchasePurchasereturnProcessService() {
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
                if (!StringUtils.equals(r.getStr("purchase_order_state"),"warehouse")){
                    JSONObject jsonObject=JSONObject.parseObject(r.getStr("item"));
                    r.set("item", jsonObject.getJSONArray("item"));
                    r.set("total_price",jsonObject.getDoubleValue("total_price"));
                }
            }
        }
        return page;
     }

    /**
     * 采购流程控制
     * @author CaryZ
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     * @throws PcException
     */
    public boolean updateByIdReturnMap(Record record) throws PcException{
        if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseOrderState[1])){
            return purchaseProcessStage1(record);
        }else if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseOrderState[2])){
            return purchaseProcessStage2(record);
        }else if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseOrderState[3])){
            return purchaseProcessStage3(record);
        } else if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseOrderState[4])){
            return purchaseProcessStage4(record);
        }
        return false;
    }

    /**
     * 采购流程阶段0-物流
     * 在提交采购单时已完成该阶段
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseProcessStage0(Record record){
        return false;
    }

    /**
     * 采购流程阶段1-采购
     * 更新当前阶段记录
     * 更新采购单信息
     * 新增下一阶段记录
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseProcessStage1(Record record) throws PcException{
        //在JSON数组中的每个元素加入总价total_price和供应商信息
        String item=insertSupplierMessage(record);
        record.set("item",item);
        if (!super.updateById(record)){
            return false;
        }
        Record purchaseOrder=new Record();
        purchaseOrder.set("item",item);
        if (!updatePurchaseOrder(record,purchaseOrder)){
            return false;
        }
        Record nextStageR=new Record();
        nextStageR.set("item",item);
        return addNextStage(record,nextStageR);
    }

    /**
     * 采购流程阶段2-财务
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseProcessStage2(Record record) throws PcException{
        //在JSON数组中的每个元素加入总价total_price和供应商信息
        String item=insertSupplierMessage(record);
        record.set("item",item);
        if (!super.updateById(record)){
            return false;
        }
        Record purchaseOrder=new Record();
        if (!updatePurchaseOrder(record,purchaseOrder)){
            return false;
        }
        Record nextStageR=new Record();
        nextStageR.set("item",record.getStr("item"));
        return addNextStage(record,nextStageR);
    }

    /**
     * 采购流程阶段3-老板
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseProcessStage3(Record record) throws PcException{
        record.set("to_user_id",getLogisticsId(record.getStr("purchase_id")));
        String item1=insertSupplierMessage(record);
        record.set("item",item1);
        if (!super.updateById(record)){
            return false;
        }
        Record purchaseOrder=new Record();
        JSONArray itemArray=BeanUtils.getJSONArrayFromJSONString(record.getStr("item"),"item");
        int itemLen=itemArray.size();
        //更新采购单表的item字段的原料接收状态都是未接收
        for (int i=0;i<itemLen;i++){
            itemArray.getJSONObject(i).put("is_accepted","false");
        }
        String item2=BeanUtils.jsonArrayToString(itemArray,"item");
        purchaseOrder.set("item",item2);
        if (!updatePurchaseOrder(record,purchaseOrder)){
            return false;
        }
        Record nextStageR=new Record();
        return addNextStage(record,nextStageR);
    }

    /**
     * 采购流程阶段4-仓库
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseProcessStage4(Record record) throws PcException{
        record.set("to_user_id",record.getStr("handle_id"));
        JSONArray itemArray=JSONArray.parseArray(record.getStr("item"));
        String item=BeanUtils.jsonArrayToString(itemArray,"item");
        record.set("item",item);
        if (!super.updateById(record)){
            return false;
        }
        String purchaseId=record.getStr("purchase_id");
        PurchaseOrderService purchaseOrderService=enhance(PurchaseOrderService.class);
        //此处将原本存在的total_price覆盖了，后期如果需要再做修正。
        List<Record> purchaseOrderItems=purchaseOrderService.queryFromPurchaseOrder(purchaseId).get("item");
        if (!addWarehouseStock(record,purchaseOrderItems)){
            return false;
        }
        Record purchaseOrder=new Record();
        List<Map<String,Object>>mapList=recordListToMapList(purchaseOrderItems);
        JSONArray jsonArray=JSONArray.parseArray(JSONArray.toJSONString(mapList));
        purchaseOrder.set("item",BeanUtils.jsonArrayToString(jsonArray,"item"));
        if (!updatePurchaseOrder(record,purchaseOrder)){
            return false;
        }
        //若仓库接收完全部货物，则不创建下一流程记录，将有关记录全部标记为完成
        if (isAll(purchaseId)){
            purchaseOrderService.finish(purchaseId);
            return true;
        }else {
            Record nextStageR=new Record();
            return addNextStage(record,nextStageR);
        }
    }

    /**
     * 新增下一阶段流程
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @param nextStageR 下一阶段记录
     * @return
     * @throws PcException
     */
    public boolean addNextStage(Record record,Record nextStageR) throws PcException{
        nextStageR.set("purchase_id",record.getStr("purchase_id"));
        nextStageR.set("num",record.getStr("num"));
        nextStageR.set("handle_id",record.getStr("to_user_id"));
        nextStageR.set("purchase_type",record.getStr("purchase_type"));
        nextStageR.set("purchase_order_state",getPurchaseOrderState(record.getStr("purchase_order_state")));
        nextStageR.set("parent_id",record.getStr("id"));
        nextStageR.set("state","0");
        return super.add(nextStageR)==null? false:true;
    }

    /**
     * 更新采购单信息
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @param purchaseOrder 采购单
     * @return
     * @throws PcException
     */
    public boolean updatePurchaseOrder(Record record,Record purchaseOrder) throws PcException{
        PurchaseOrderService purchaseOrderService=enhance(PurchaseOrderService.class);
        purchaseOrder.set("id",record.getStr("purchase_id"));
        purchaseOrder.set("state",getPurchaseOrderState(record.getStr("purchase_order_state")));
        return purchaseOrderService.updateById(purchaseOrder);
    }

    /**
     * 仓库入库，新增库存
     * @date 2018-12-01
     * @param record
     * @param purchaseOrderItems
     * @return
     * @throws PcException
     */
    public boolean addWarehouseStock(Record record,List<Record> purchaseOrderItems) throws PcException{
        WarehouseStockService warehouseStockService=enhance(WarehouseStockService.class);
        JSONArray itemArray=BeanUtils.getJSONArrayFromJSONString(record.getStr("item"),"item");
        int itemLen=itemArray.size();
        List<Record> addStockList=new ArrayList<>();
        for (int i=0;i<itemLen;i++){
            Record stockR=new Record();
            JSONObject jsob=itemArray.getJSONObject(i);
            stockR.set("id", UUIDTool.getUUID());
            stockR.set("user_id", record.getStr("handle_id"));
            stockR.set("warehouse_id", record.getStr("warehouse_id"));
            stockR.set("purchase_order_id", record.getStr("purchase_id"));
            stockR.set("purchase_order_num", record.getStr("num"));
            stockR.set("state", "1");
            //将JSONObject对象转为Record，再将提货单位数量->最小单位数量
            stockR.set("quantity", UnitConversion.outUnit2SmallUnitDecil(BeanUtils.jsonToRecord(jsob)));
            stockR.set("available_quantity", stockR.get("quantity"));
            stockR.set("change_record",JSON.toJSONString(addChangeRecord(record,stockR)));
            stockR.set("batch_num", jsob.get("batch_num"));
            stockR.set("material_data", JSON.toJSONString(jsob));
            stockR.set("material_id", jsob.get("id"));
            addStockList.add(stockR);
            for (Record record2:purchaseOrderItems){
                if (StringUtils.equals(stockR.getStr("material_id"),record2.getStr("id"))){
                    record2.set("is_accepted","true");
                    break;
                }
            }
        }
        return warehouseStockService.batchSave(addStockList);
    }

    /**
     * 采购入库时，用于处理change_record字段的数据
     * @param record 当前阶段的流程记录
     * @param stockR 新增库存记录
     * @return
     */
    public Map<String,List<Map>> addChangeRecord(Record record,Record stockR){
        PurchaseOrderService purchaseOrderService=enhance(PurchaseOrderService.class);
        List<Map> processList=new ArrayList<>();
        Map<String,Object> process=new HashMap<>();
        process.put("handle_type","purchase_inwarehouse");
        process.put("handle_type_text", DictionaryConstants.DICT_STRING_MAP.get(PROCESS_TYPE).get(process.get("handle_type")));
        process.put("handle_time",DateUtil.GetDateTime());
        process.put("handle_tablename",purchaseOrderService.getTableName());
        process.put("handle_record_id",record.getStr("purchase_id"));
        process.put("handle_id",record.getStr("handle_id"));
        SysUserService sysUserService=enhance(SysUserService.class);
        Map<String,Record> userMap=sysUserService.getUsers();
        process.put("handle_name",userMap.get(process.get("handle_id")).getStr("nickname"));
        process.put("before_quantity",0);
        process.put("after_quantity",stockR.get("quantity"));
        processList.add(process);
        Map<String,List<Map>> toJsonMap=new HashMap<>(1);
        toJsonMap.put("process",processList);
        return toJsonMap;
    }

    /**
     * 通过item里的supplier_id查询current_price、name、num
     * @param record 当前流程记录
     * @return
     */
    public String insertSupplierMessage(Record record){
        JSONArray itemArray=JSONArray.parseArray(record.getStr("item"));
        SupplierService supplierService=enhance(SupplierService.class);
        int itemLen=itemArray.size();
        String[]ids=new String[itemLen];
        for (int i=0;i<itemLen;i++){
            ids[i]=itemArray.getJSONObject(i).getString("supplier_id");
        }
        List<Record> supplierList=supplierService.selectByColumnIn("id",ids);
        Map<String,Record> supplierMap=new HashMap<>(itemLen);
        for(Record supplierR:supplierList){
            supplierMap.put(supplierR.getStr("id"),supplierR);
        }
        double totalPrice=0;
        for (int i=0;i<itemLen;i++){
            JSONObject jsonObject=itemArray.getJSONObject(i);
            Record record1=supplierMap.get(jsonObject.getString("supplier_id"));
            jsonObject.put("supplier_name",record1.getStr("name"));
            jsonObject.put("supplier_num",record1.getStr("num"));
            String materialItems=record1.getStr("material_items");
            JSONObject jsonObject1=JSONObject.parseObject(materialItems);
            JSONArray jsonArray1=jsonObject1.getJSONArray("items");
            int len1=jsonArray1.size();
            for (int j=0;j<len1;j++){
                if(StringUtils.equals(jsonObject.getString("id"),jsonArray1.getJSONObject(j).getString("id"))){
                    jsonObject.put("current_price",supplierService.getMaterialPrice(jsonArray1.getJSONObject(j)));
                    break;
                }
            }
            totalPrice+=jsonObject.getDouble("current_price")*(double)Integer.parseInt(jsonObject.getString("quantity"));
        }
        DecimalFormat df = new DecimalFormat("#.00");
        Map<String,Object> map=new HashMap(1);
        map.put("item",itemArray);
        map.put("total_price",df.format(totalPrice));
        String item= JSON.toJSONString(map);
        return item;
    }

    /**
     * 通过采购单ID得到物流人ID
     * @param purchaseId
     * @return 物流人ID
     */
    public String getLogisticsId(String purchaseId){
        Record record= Db.findFirst("SELECT handle_id FROM s_purchase_purchasereturn_process WHERE parent_id='0' AND purchase_id=?",purchaseId);
        return record==null? null:record.getStr("handle_id");
    }

    /**
     * 通过采购单当前的状态找到下一个流程记录的采购单状态（采购->财务->boss->仓库）
     * 特殊情况：当处于仓库阶段且未完成时，下一阶段仍为仓库
     * @param currentState
     * @return
     */
    public String getPurchaseOrderState(String currentState){
        int len=purchaseOrderState.length;
        for (int i=0;i<len;i++){
            if (StringUtils.equals(currentState,purchaseOrderState[i])){
                if (i==len-1){
                    return purchaseOrderState[i];
                }else {
                    return purchaseOrderState[i+1];
                }
            }
        }
        return null;
    }

    /**
     * 验证仓库是否接收完全部货品
     * 目前验证item数组的长度是否相等，仅能满足一种原料一次入库的情况，
     * 若日后想拓展，可以在相同原料信息的数量上累加
     * @param id 采购单id
     * @return
     */
    public boolean isAll(String id){
        //在流程表中找到该采购单在仓库阶段所有的记录的item
        String multipleItemSql="SELECT item FROM s_purchase_purchasereturn_process WHERE purchase_id=? AND purchase_order_state='warehouse' AND state='1'";
        //在采购单表找到采购单的item
        String totalItemSql="SELECT * FROM s_purchase_order WHERE id=?";
        Record totalItemR=Db.findFirst(totalItemSql,id);
        JSONArray totalItemsArray=BeanUtils.getJSONArrayFromJSONString(totalItemR.get("item"),"item");
        int totalLen=totalItemsArray.size();
        int multipleLen=0;
        List<Record> multipleItemR=Db.find(multipleItemSql,id);
        for (Record record:multipleItemR){
            String item=record.getStr("item");
            JSONObject singleJson=JSONObject.parseObject(item);
            JSONArray singleItemArray=singleJson.getJSONArray("item");
            multipleLen+=singleItemArray.size();
        }
        return totalLen==multipleLen? true:false;
    }

    /**
     * 通过用户id查询未处理的流程记录
     * @return
     */
    public List<Record> getUnfinishedProcess(String id){
        return Db.find("SELECT * FROM s_purchase_purchasereturn_process WHERE handle_id=? AND state='0' ORDER BY num ASC",id);
    }

    /**
     * 通过采购单id查询所有流程，按操作时间正序排
     * @param purchaseId
     * @return
     */
    public List<Record> getProcessForOneOrder(String purchaseId){
        return Db.find("SELECT p.*,u.nickname FROM s_purchase_purchasereturn_process p,s_sys_user u WHERE p.purchase_id=? AND p.handle_id=u.id ORDER BY IF(ISNULL(p.handle_date),1,0),p.handle_date",purchaseId);
    }

/***********************************************************以下为采购退货流程部分*************************************************************************/

    /**
     * 通过采购退货单id查询所有流程，按操作时间正序排
     * @param purchaseReturnId
     * @return
     */
    public List<Record> getReturnProcessForOneOrder(String purchaseReturnId){
        return Db.find("SELECT p.*,u.nickname FROM s_purchase_purchasereturn_process p,s_sys_user u WHERE p.purchase_return_id=? AND p.handle_id=u.id ORDER BY IF(ISNULL(p.handle_date),1,0),p.handle_date",purchaseReturnId);
    }

    /**
     * 采购退货流程控制
     * @author CaryZ
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     * @throws PcException
     */
    public boolean updateReturnProcess(Record record) throws PcException{
        if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseReturnState[0])){
            return purchaseReturnStage0(record);
        }else if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseReturnState[1])){
            return purchaseReturnStage1(record);
        }else if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseReturnState[2])){
            return purchaseReturnStage2(record);
        } else if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseReturnState[3])){
            return purchaseReturnStage3(record);
        } else if (StringUtils.equals(record.getStr("purchase_order_state"),purchaseReturnState[4])){
            return purchaseReturnStage4(record);
        }
        return false;
    }

    /**
     * 采购退货流程阶段0-物流清点
     * 1.更新退货单信息
     * 2.更新当前阶段记录
     * 3.新增下一阶段记录
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseReturnStage0(Record record) throws PcException{
        Record purchaseReturnOrder=new Record();
        purchaseReturnOrder.set("remark",record.getStr("remark"));
        purchaseReturnOrder.set("image",record.getStr("image"));
        purchaseReturnOrder.set("return_reason",record.getStr("return_reason"));
        if (!updatePurchaseReturnOrder(record,purchaseReturnOrder)){
            return false;
        }
        record.remove("image","return_reason");
        if (!updateCurrentReturnStage(record)){
            return false;
        }
        return addNextReturnStage(record);
    }

    /**
     * 采购退货流程阶段1-采购审核
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseReturnStage1(Record record) throws PcException{
        Record purchaseReturnOrder=new Record();
        if (!updatePurchaseReturnOrder(record,purchaseReturnOrder)){
            return false;
        }
        if (!updateCurrentReturnStage(record)){
            return false;
        }
        return addNextReturnStage(record);
    }

    /**
     * 采购退货流程阶段2-财务审批
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseReturnStage2(Record record) throws PcException{
        record.set("to_user_id",getReturnLogisticsId(record.getStr("purchase_return_id")));
        Record purchaseReturnOrder=new Record();
        if (!updatePurchaseReturnOrder(record,purchaseReturnOrder)){
            return false;
        }
        if (!updateCurrentReturnStage(record)){
            return false;
        }
        return addNextReturnStage(record);
    }

    /**
     * 采购退货流程阶段3-物流发货
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseReturnStage3(Record record) throws PcException{
        record.set("to_user_id",getFinanceId(record.getStr("purchase_return_id")));
        Record purchaseReturnOrder=new Record();
        if (!updatePurchaseReturnOrder(record,purchaseReturnOrder)){
            return false;
        }
        if (!updateCurrentReturnStage(record)){
            return false;
        }
        WarehouseStockService warehouseStockService=enhance(WarehouseStockService.class);
        if (!warehouseStockService.updateStockAfterDelivery(record)){
            return false;
        }
        return addNextReturnStage(record);
    }

    /**
     * 采购退货流程阶段4-财务收款（退货完成）
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     */
    public boolean purchaseReturnStage4(Record record) throws PcException{
        Record purchaseReturnOrder=new Record();
        if (!updatePurchaseReturnOrder(record,purchaseReturnOrder)){
            return false;
        }
        return updateCurrentReturnStage(record);
    }

    /**
     * 完成当前流程记录
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     * @throws PcException
     */
    public boolean updateCurrentReturnStage(Record record) throws PcException{
        record.set("item",insertTotalPrice(record.get("item")));
        return super.updateById(record);
    }

    /**
     * 更新退货单信息
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @param purchaseReturnOrder 退货单
     * @return
     * @throws PcException
     */
    public boolean updatePurchaseReturnOrder(Record record,Record purchaseReturnOrder) throws PcException{
        PurchaseReturnService purchaseReturnService=enhance(PurchaseReturnService.class);
        purchaseReturnOrder.set("id",record.getStr("purchase_return_id"));
        purchaseReturnOrder.set("order_state",getPurchaseReturnState(record.getStr("purchase_order_state")));
        return purchaseReturnService.updateById(purchaseReturnOrder);
    }

    /**
     * 新增下一阶段退货流程
     * @date 2018-12-01
     * @param record 当前阶段记录
     * @return
     * @throws PcException
     */
    public boolean addNextReturnStage(Record record) throws PcException{
        Record nextStageR=new Record();
        nextStageR.set("purchase_return_id",record.getStr("purchase_return_id"));
        nextStageR.set("num",record.getStr("num"));
        nextStageR.set("item",record.getStr("item"));
        nextStageR.set("handle_id",record.getStr("to_user_id"));
        nextStageR.set("purchase_order_state",getPurchaseReturnState(record.getStr("purchase_order_state")));
        nextStageR.set("parent_id",record.getStr("id"));
        nextStageR.set("state","0");
        return super.add(nextStageR)==null? false:true;
    }



    /**
     * 新建采购退货单时，在流程表批量新增记录
     * @author CaryZ
     * @date 2018-11-17
     * @param record 退货数据
     * @param returnItemList 批量新增的退货单
     * @return 新增成功/失败 true/false
     */
    public List<Record> batchSaveReturnProcess(Record record,List<Record> returnItemList)throws PcException{
        List<Record> returnProcessList=new ArrayList<>();
        for (Record record1:returnItemList){
            Record record2=new Record();
            record2.set("id",UUIDTool.getUUID());
            record2.set("purchase_return_id",record1.getStr("id"));
            record2.set("num",record1.getStr("num"));
            record2.set("item",record1.getStr("return_item"));
            record2.set("handle_id",record.getStr("handle_id"));
            record2.set("handle_date", DateUtil.GetDateTime());
            record2.set("purchase_order_state",purchaseReturnState[0]);
            record2.set("parent_id","0");
            returnProcessList.add(record2);
            //update的时候更新remark,to_user_id,state
        }
        try{
            return Db.batchSave(TABLENAME,returnProcessList,returnProcessList.size())==null? null:returnProcessList;
        }catch (Exception e){
            throw new PcException(ADD_EXCEPTION,e.getMessage());
        }
    }

    /**
     * 通过退货单当前的状态找到下一个流程记录的退货单状态（关闭、完成状态除外）
     * @param currentState
     * @return
     */
    public String getPurchaseReturnState(String currentState){
        int len=purchaseReturnState.length;
        for (int i=0;i<len;i++){
            if (StringUtils.equals(currentState,purchaseReturnState[i])){
                if (i==len-1){
                    return purchaseReturnState[i];
                }else {
                    return purchaseReturnState[i+1];
                }
            }
        }
        return null;
    }

    /**
     * 通过采购退货单ID得到物流人ID
     * @param purchaseId
     * @return 物流人ID
     */
    public String getReturnLogisticsId(String purchaseId){
        Record record= Db.findFirst("SELECT handle_id FROM s_purchase_purchasereturn_process WHERE parent_id='0' AND purchase_return_id=?",purchaseId);
        return record==null? null:record.getStr("handle_id");
    }

    /**
     * 通过退货单ID得到财务人ID
     * @param purchaseId
     * @return 财务人ID
     */
    public String getFinanceId(String purchaseId){
        Record record= Db.findFirst("SELECT handle_id FROM s_purchase_purchasereturn_process WHERE purchase_return_id=? AND purchase_order_state='finance_confirm'",purchaseId);
        return record==null? null:record.getStr("handle_id");
    }

    /**
     * 通过item里的current_price和current_quantity得到total_price
     * @param itemArray
     * @return
     */
    public String insertTotalPrice(JSONArray itemArray){
        double totalPrice=0;
        int itemLen=itemArray.size();
        for (int i=0;i<itemLen;i++){
            JSONObject jsonObject=itemArray.getJSONObject(i);
            totalPrice+=jsonObject.getDouble("current_price")*(double)Integer.parseInt(jsonObject.getString("current_quantity"));
        }
        Map<String,Object> map=new HashMap(1);
        map.put("item",itemArray);
        map.put("total_price",getMoney(totalPrice));
        String item= JSON.toJSONString(map);
        return item;
    }


/****************************************************以下为废弃的方法********************************************************************************/
    /**
     * @deprecated 采购退货流程已在上方重构
     * 更新退货流程，具体如下：
     * 先更新退货单状态，再更新流程记录状态和指定下一处理人（关闭另外做)，增加下一流程未完成记录
     * 1.物流清点->采购审核
     * 2.采购审核->财务确认
     * 3.财务确认->物流发货
     * 4.物流发货->财务收款完成
     * @param record 当前流程记录信息 包括id,purchase_return_id,remark,to_user_id,(image,return_reason),purchase_order_state等等
     * @return 成功/失败 true/false
     */
    public boolean updateReturnProcess1(Record record) throws PcException{
        PurchaseReturnService prs=enhance(PurchaseReturnService.class);
        WarehouseStockService wss=enhance(WarehouseStockService.class);
        String purchaseOrderState=record.getStr("purchase_order_state");
        String nextOrderState=getPurchaseReturnState(purchaseOrderState);
        String purchaseId=record.getStr("purchase_return_id");
        if (StringUtils.equals(purchaseOrderState,"finance_confirm")){
            //财务确认的下一处理人设置为物流
            record.set("to_user_id",getReturnLogisticsId(purchaseId));
        }else if (StringUtils.equals(purchaseOrderState,"logistics_delivery")){
            //物流发货的下一处理人设置为财务
            record.set("to_user_id",getFinanceId(purchaseId));
        }
        //更新退货单状态
        Record purchaseReturnOrder=new Record();
        purchaseReturnOrder.set("id",purchaseId);
        if (!StringUtils.equals(purchaseOrderState,"return_finish")){
            purchaseReturnOrder.set("order_state",nextOrderState);
        }else {
            purchaseReturnOrder.set("order_state",purchaseOrderState);
        }
        if (StringUtils.equals(purchaseOrderState,"logistics_clearance")){
            purchaseReturnOrder.set("remark",record.getStr("remark"));
            purchaseReturnOrder.set("image",record.getStr("image"));
            purchaseReturnOrder.set("return_reason",record.getStr("return_reason"));
        }
        if (!prs.updateById(purchaseReturnOrder)){
            return false;
        }
        //更新流程表的本次记录“完成”
        if (StringUtils.equals(purchaseOrderState,"logistics_clearance")){
            record.remove("image","return_reason");
        }
        String item=insertTotalPrice(record.get("item"));
        record.set("item",item);
        if (!super.updateById(record)){
            return false;
        }
        //物流发货，更新仓库库存
        if (StringUtils.equals(purchaseOrderState,"logistics_delivery")){
            if (!wss.updateStockAfterDelivery(record)){
                return false;
            }
        }
        //财务收钱时，已经是最后一步，不需要再新建流程记录
        if (!StringUtils.equals(purchaseOrderState,"return_finish")){
            //下一步骤的未完成单
            Record nextR=new Record();
            nextR.set("purchase_return_id",purchaseId);
            nextR.set("num",record.getStr("num"));
            nextR.set("item",item);
            nextR.set("handle_id",record.getStr("to_user_id"));
            nextR.set("purchase_order_state",nextOrderState);
            nextR.set("parent_id",record.getStr("id"));
            nextR.set("state","0");
            return super.add(nextR)==null? false:true;
        }
        return true;
    }

    /**
     * @deprecated 新的采购流程已在上方重构
     * 采购流程：
     * 每完成一个步骤，更新item、remark、to_user_id、 state、handle_date（这俩通过handleUpdateRecord实现）
     * 同时往表中插入一条记录(下一处理步骤的)
     * ps：在仓库这个步骤中一般会插入多条记录
     * 插入记录的同时更新采购单的state和item
     *
     * 采购：前端传该记录的全部信息，更新item（多了current_price）、remark（可空）、to_user_id
     * 财务：remark,to_user_id
     * 老板：remark, to_user_id自动设置为物流的userId
     * 仓库：每接收一次货品，往表里插入一条记录
     *
     * @param record 当前阶段的流程记录
     * @return Map存俩key-value 1.flag-true/false 2.list-null/list
     * @throws PcException
     */
    public Map<String,Object> updateByIdReturnMap1(Record record) throws PcException{
        PurchaseOrderService pos=enhance(PurchaseOrderService.class);
        WarehouseStockService wss=enhance(WarehouseStockService.class);
        Map<String,Object> resultMap=new HashMap<>(2);
        String purchaseOrderState=record.getStr("purchase_order_state");
        String nextOrderState=getPurchaseOrderState(purchaseOrderState);
        String purchaseId=record.getStr("purchase_id");
        if (StringUtils.equals(purchaseOrderState,"boss")){
            //老板通过后，下一处理人自动设置为物流
            record.set("to_user_id",getLogisticsId(purchaseId));
        }else if(StringUtils.equals(purchaseOrderState,"warehouse")){
            //仓库阶段，下一处理人仍是本人，直至完成
            record.set("to_user_id",record.getStr("handle_id"));
        }
        JSONArray itemArray=JSONArray.parseArray(record.getStr("item"));
        int itemLen=itemArray.size();
        Map<String,JSONArray> map=new HashMap(1);
        map.put("item",itemArray);
        String item= JSON.toJSONString(map);
        record.set("item",item);
        //非物流、仓库阶段 在item字段中加入总价total_price和供应商信息
        if (!StringUtils.equals(purchaseOrderState,LOGISTICS)||!StringUtils.equals(purchaseOrderState,WAREHOUSE)){
            item=insertSupplierMessage(record);
        }
        //更新流程表的本次记录“完成”
        if (!super.updateById(record)){
            resultMap.put("flag",false);
            resultMap.put("list",null);
            return resultMap;
        }
        //即将更新的采购单
        Record purchaseOrder=new Record();
        //入库阶段
        if (StringUtils.equals(purchaseOrderState,WAREHOUSE)){
            List<Record> purchaseOrderItems=pos.queryFromPurchaseOrder(purchaseId).get("item");
            //每入一次，要批量新增一次库存
            List<Record> addStockList=new ArrayList<>();
            for (int i=0;i<itemLen;i++){
                Record stockR=new Record();
                JSONObject jsob=itemArray.getJSONObject(i);
                stockR.set("id", UUIDTool.getUUID());
                stockR.set("user_id", record.getStr("handle_id"));
                stockR.set("warehouse_id", record.getStr("warehouse_id"));
                stockR.set("purchase_order_id", purchaseId);
                stockR.set("purchase_order_num", record.getStr("num"));
                stockR.set("state", "1");
                //将JSONObject对象转为Record，再将提货单位数量->最小单位数量
                stockR.set("quantity", UnitConversion.outUnit2SmallUnitDecil(BeanUtils.jsonToRecord(jsob)));
                stockR.set("available_quantity", stockR.get("quantity"));
                stockR.set("change_record",JSON.toJSONString(addChangeRecord(record,stockR)));
                stockR.set("batch_num", jsob.get("batch_num"));
                stockR.set("material_data", JSON.toJSONString(jsob));
                stockR.set("material_id", jsob.get("id"));
                addStockList.add(stockR);
                for (Record record2:purchaseOrderItems){
                    if (StringUtils.equals(stockR.getStr("material_id"),record2.getStr("id"))){
                        record2.set("is_accepted","true");
                        break;
                    }
                }
            }
            List<Map<String,Object>>mapList2=recordListToMapList(purchaseOrderItems);
            JSONArray jsonArray2=JSONArray.parseArray(JSONArray.toJSONString(mapList2));
            Map<String,JSONArray> map2=new HashMap();
            map2.put("item",jsonArray2);
            purchaseOrder.set("item",JSON.toJSONString(map2));
            if (!wss.batchSave(addStockList)){
                resultMap.put("flag",false);
                return resultMap;
            }
            //若仓库接收完全部货物，则不创建下一流程记录，将有关记录全部标记为完成
            if (isAll(purchaseId)){
                pos.finish(purchaseId);
                resultMap.put("flag",true);
                return resultMap;
            }
        }
        purchaseOrder.set("id",purchaseId);
        //若当前阶段为采购，则更新采购单表的item字段的供应商id，编号，姓名，价格
        if (StringUtils.equals(purchaseOrderState,PURCHASE)){
            item=insertSupplierMessage(record);
            purchaseOrder.set("item",item);
            //若当前阶段为老板，则更新采购单表的item字段的原料接收状态都是未接收
        }else if (StringUtils.equals(purchaseOrderState,BOSS)){
            for (int i=0;i<itemLen;i++){
                itemArray.getJSONObject(i).put("is_accepted","false");
            }
            map.put("item",itemArray);
            purchaseOrder.set("item",JSON.toJSONString(map));
        }
        purchaseOrder.set("state",nextOrderState);
        if (!pos.updateById(purchaseOrder)){
            resultMap.put("flag",false);
            resultMap.put("list",null);
            return resultMap;
        }
        //下一步骤的未完成单
        Record nextR=new Record();
        nextR.set("purchase_id",purchaseId);
        nextR.set("num",record.getStr("num"));
        //仓库阶段，接收入库时再填写item，其余阶段都填写
        if (!StringUtils.equals(nextOrderState,"warehouse")){
            nextR.set("item",item);
        }
        nextR.set("handle_id",record.getStr("to_user_id"));
        nextR.set("purchase_type",record.getStr("purchase_type"));
        nextR.set("purchase_order_state",nextOrderState);
        nextR.set("parent_id",record.getStr("id"));
        nextR.set("state","0");
        if (super.add(nextR)==null){
            resultMap.put("flag",false);
        }else {
            resultMap.put("flag",true);
        }
        return resultMap;
    }

    /**
     * @deprecated
     * 区分item里的每种原料是否被接收
     * @param id 采购单id
     * @return
     */
    public Record checkMaterialIsAccepted(String id){
        //在流程表中找到该采购单在仓库阶段所有的记录的item
        String multipleItemSql="SELECT item FROM s_purchase_purchasereturn_process WHERE purchase_id=? AND purchase_order_state='warehouse' AND state='1'";
        //在采购单表找到采购单的item
        String totalItemSql="SELECT item FROM s_purchase_order WHERE id=?";
        Record totalItemR=Db.findFirst(totalItemSql,id);
        String totalItem=totalItemR.getStr("item");
        JSONObject totalJson=JSONObject.parseObject(totalItem);
        JSONArray totalItemsArray=totalJson.getJSONArray("item");
        int totalLen=totalItemsArray.size();
        //JSONArray转List<Record>
        List<Record> totalItemList=jsonArrayToList(totalItemsArray);
        List<Record> multipleItemR=Db.find(multipleItemSql,id);
        //key为原料的id，value为原料信息
        Map<String,JSONObject> multipleItemsMap=new HashMap<>();
        //将流程表中该采购单仓库阶段的多条记录的item字段中的原料json 转化成map格式
        for (Record record:multipleItemR){
            String item=record.getStr("item");
            JSONObject singleJson=JSONObject.parseObject(item);
            JSONArray singleItemArray=singleJson.getJSONArray("item");
            int singleLen=singleItemArray.size();
            for (int i=0;i<singleLen;i++){
                multipleItemsMap.put(singleItemArray.getJSONObject(i).getString("id"),singleItemArray.getJSONObject(i));
            }
        }
        int count1=0;
        int count2=0;
        //遍历totalItemList，若在multipleItemsMap中有该原料，则color为green，否则color为red
        for(Record item:totalItemList){
            if (multipleItemsMap.get(item.getStr("id"))==null){
                item.set("is_accepted",false);
                count1++;
            }else {
                item.set("is_accepted",true);
                count2++;
            }
        }
        Record record=new Record();
        record.set("list",totalItemList);
        record.set("not_accepted_count",count1);
        record.set("is_accepted_count",count2);
        return record;
    }
}
