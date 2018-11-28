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
import com.works.pc.warehouse.services.WarehouseStockService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.common.service.OrderNumberGenerator.getWarehousePurchaseOrderNumber;
import static com.constants.DictionaryConstants.PURCHASE_ORDER_TYPE;
import static com.constants.DictionaryConstants.PURCHASE_TYPE;

/**
 * 该类实现以下功能：
 * 新建采购单（1.完全新建。2.引单新建）
 * 通过采购单id显示采购项+总金额+流程日志
 * 关闭、完成采购单
 * @author CaryZ
 * @date 2018-11-11
 */
@Before(Tx.class)
public class PurchaseOrderService extends BaseService {

    private static final String TABLENAME="s_purchase_order";
    private static String[] purchaseOrderState={"logistics","purchase","finance","boss","warehouse","shutdown","finish"};
    private static String[] columnNameArr = {"id","num","from_purchase_order_id","from_purchase_order_num","create_id","create_date","purchase_type","close_date","close_reason","close_id","city","remark","item","state"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","",""};

    public PurchaseOrderService() {
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
            r.set("item", JSONObject.parseObject(r.getStr("item")));
        }
        return page;
     }

    /**
     * 引单新建时，通过采购单id查询信息，这里主要是把item的数据格式变成list
     * @param id
     * @return
     * @throws PcException
     */
     public Record queryFromPurchaseOrder(String id) throws PcException{
        Record record=super.findById(id);
        String item=record.getStr("item");
        JSONObject jsonObject=JSONObject.parseObject(item);
        JSONArray jsonArray=jsonObject.getJSONArray("item");
        int len=jsonArray.size();
        List<Record> list=new ArrayList<>(len);
        List<Record> notAcceptedList=new ArrayList<>();
        for (int i=0;i<len;i++){
            Record record1=BeanUtils.jsonToRecord(jsonArray.getJSONObject(i));
            if (StringUtils.equals(record1.getStr("is_accepted"),"false")) {
                notAcceptedList.add(record1);
            }else{
                list.add(record1);
            }
        }
        int acceptedLen=list.size();
        for(int i=0;i<acceptedLen;i++){
            notAcceptedList.add(list.get(i));
        }
        record.set("item",notAcceptedList);
        return record;
     }

    /**
     * 新增采购单
     * 同时在采购流程表中增加一条记录,指定下一处理人
     * @param record 新增数据
     * @return
     */
    @Override
    public String add(Record record) throws PcException{
        PurchasePurchasereturnProcessService ppps=enhance(PurchasePurchasereturnProcessService.class);
        //采购项
        String item="";
        //采购类型
        String purchaseType="";
        //采购单备注
        String remark="";
        JSONArray jsonArray=JSONArray.parseArray(record.getStr("item"));
        Map<String,JSONArray> map=new HashMap(1);
        map.put("item",jsonArray);
        item=JSON.toJSONString(map);
        record.set("item",item);
        purchaseType=record.getStr("purchase_type");
        remark=record.getStr("remark");
        //采购单编号
        String num=getWarehousePurchaseOrderNumber();
        record.set("num",num);
        //采购单ID
        String purchaseId=super.add(record);
        if (purchaseId==null){
            return null;
        }
        //采购流程-物流完成单
        Record logisticsR=new Record();
        logisticsR.set("purchase_id",purchaseId);
        logisticsR.set("num",num);
        logisticsR.set("item",item);
        logisticsR.set("purchase_type",purchaseType);
        logisticsR.set("purchase_order_state",purchaseOrderState[0]);
        logisticsR.set("remark",remark);
        logisticsR.set("parent_id","0");
        logisticsR.set("state","1");
        logisticsR.set("handle_id",record.getStr("create_id"));
        logisticsR.set("handle_date", DateUtil.GetDateTime());
        logisticsR.set("to_user_id",record.getStr("to_user_id"));
        String logisticsId=ppps.add(logisticsR);
        if (logisticsId==null){
            return null;
        }
        //采购段未完成单
        Record purchaseR=new Record();
        purchaseR.set("purchase_id",purchaseId);
        purchaseR.set("num",num);
        purchaseR.set("item",item);
        purchaseR.set("handle_id",record.getStr("to_user_id"));
        purchaseR.set("purchase_type",purchaseType);
        purchaseR.set("purchase_order_state",purchaseOrderState[1]);
        purchaseR.set("parent_id",logisticsId);
        purchaseR.set("state","0");
        return ppps.add(purchaseR);
    }

    /**
     * 仓库角色完成整个采购单
     * 调用此方法，说明货物未接收完，则前端提示“货物未接收完，是否确认完成？”
     * 本方法将流程表中仓库记录的采购单状态该为“完成”，以及采购单表的记录为“完成”
     * @param id 采购单id
     * @return 运行成功/失败 true/false
     */
    public boolean finish(String id)throws PcException {
        Record record = new Record();
        record.set("id", id);
        record.set("state", purchaseOrderState[6]);
        if (!super.updateById(record)) {
            return false;
        }
        return Db.update("UPDATE s_purchase_purchasereturn_process SET purchase_order_state='finish' WHERE purchase_id=? AND purchase_order_state='warehouse'",id)==0? false:true;
    }


    /**
     * 关闭采购单
     * 将采购单表的记录的状态改为“关闭”
     * 将流程表该单的所有记录改成“完成”
     * @param r 要关闭的采购单ID、关闭原因、关闭人ID
     * @return 运行成功/失败 true/false
     */
    public boolean shutdown(Record r) throws PcException {
        Record record = new Record();
        record.set("id", r.getStr("id"));
        record.set("state", purchaseOrderState[5]);
        record.set("close_date", DateUtil.GetDateTime());
        record.set("close_reason", r.getStr("close_reason"));
        record.set("close_id", r.getStr("close_id"));
        if (!super.updateById(record)) {
            return false;
        }
        return Db.update("UPDATE s_purchase_purchasereturn_process SET state='1' WHERE purchase_id=?",r.getStr("id"))==0? false:true;
    }


    /**
     * 通过采购单id显示采购项+总金额+流程日志
     * 数据格式见接口文档
     * @param purchaseId 采购单id
     * @return
     */
    public Record showPurchaseOrderById(String purchaseId)throws PcException{
        PurchasePurchasereturnProcessService ppps=enhance(PurchasePurchasereturnProcessService.class);
        Record orderR=Db.findFirst("SELECT * FROM s_purchase_order WHERE id=?",purchaseId);
        JSONArray itemArray=JSONObject.parseObject(orderR.getStr("item")).getJSONArray("item");
        Record record=new Record();
        record.setColumns(orderR);
        record.set("purchase_type_text", DictionaryConstants.DICT_STRING_MAP.get(PURCHASE_TYPE).get(record.getStr("purchase_type")));
        record.set("state_text",DictionaryConstants.DICT_STRING_MAP.get(PURCHASE_ORDER_TYPE).get(record.getStr("state")));
        //前端要求判断第几个
        int stateLen=purchaseOrderState.length;
        int count=0;
        for (int i=0;i<stateLen;i++){
            if (StringUtils.equals(record.getStr("state"),purchaseOrderState[i])){
                if (i==6){
                    count=4+1;
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
            singlePrice=itemArray.getJSONObject(i).getDoubleValue("current_price")*itemArray.getJSONObject(i).getDoubleValue("quantity");
            totalPrice+=singlePrice;
        }
        record.set("total_price",totalPrice);
        //流程日志
        List<Record> processList=ppps.getProcessForOneOrder(purchaseId);
        record.set("process",handleProcessInfo(processList,totalPrice,purchaseId));
        return record;
    }

    /**
     * 对流程记录数据的处理，整理成接口文档规定的格式
     * @param processList 流程记录list
     * @param totalPrice  采购单总金额
     * @param purchaseId  采购单id
     * @return 流程日志list
     * @throws PcException
     */
    public List<Object> handleProcessInfo(List<Record> processList,double totalPrice,String purchaseId)throws PcException{
        List<Object> finalList=new ArrayList<>(processList.size());
        int count=0;
        List<Record> enterWarehouseList=new ArrayList<>();
        for (Record processR:processList){
            String orderState=processR.getStr("purchase_order_state");
            String state=processR.getStr("state");
            JSONObject stageItemObject=JSONObject.parseObject(processR.getStr("item"));
            JSONArray stageItemArray=new JSONArray();
            if (stageItemObject!=null){
                stageItemArray=stageItemObject.getJSONArray("item");
            }
            int stageLen=stageItemArray.size();
            Record stageR=new Record();
            if (StringUtils.isEmpty(processR.getStr("handle_date"))){
                stageR.set("is_handle",0);
            }else {
                stageR.set("is_handle",1);
            }
            stageR.set("type",orderState);
            stageR.set("name",processR.getStr("nickname"));
            stageR.set("time",processR.getStr("handle_date"));
            stageR.set("remark",processR.getStr("remark"));
            List<Record> messageList=new ArrayList<>();
            //物流、采购
            if (StringUtils.equals(orderState,"logistics")||StringUtils.equals(orderState,"purchase")){
                if (StringUtils.equals(state,"1")){
                    Record messageR=new Record();
                    messageR.set("length",stageLen);
                    if (StringUtils.equals(orderState,"purchase")){
                        if(count!=processList.size()-1){
                            messageR.set("is_agree",true);
                            messageR.set("is_agree_text","同意");
                        }else {
                            messageR.set("is_agree",false);
                            messageR.set("is_agree_text","不同意");
                        }
                    }
                    stageR.set("message",messageR);
                }else {
                    stageR.set("message","未处理");
                }
                finalList.add(stageR);
                //财务、老板
            }else if (StringUtils.equals(orderState,"finance")||StringUtils.equals(orderState,"boss")){
                if (StringUtils.equals(state,"1")){
                    Record messageR=new Record();
                    if (StringUtils.equals(orderState,"finance")){
                        messageR.set("total_price",totalPrice);
                    }
                    if (count!=processList.size()-1){
                        messageR.set("is_agree",true);
                        messageR.set("is_agree_text","同意");
                    }else {
                        messageR.set("is_agree",false);
                        messageR.set("is_agree_text","不同意");
                    }
                    stageR.set("message",messageR);
                }else {
                    stageR.set("message","未处理");
                }
                finalList.add(stageR);
            }else {//仓库入库
                if (stageItemArray!=null&&stageLen>0){
                    Map<String,String> batchNumMap=getbatchNumMap(stageLen,purchaseId,stageItemArray);
                    for (int i=0;i<stageLen;i++){
                        Record messageR=new Record();
                        JSONObject jsob=stageItemArray.getJSONObject(i);
                        messageR.set("num",jsob.getString("num"));
                        messageR.set("name",jsob.getString("name"));
                        messageR.set("quantity",jsob.getString("quantity"));
                        messageR.set("batch_num",batchNumMap.get(jsob.getString("id")));
                        messageList.add(messageR);
                    }
                    stageR.set("message",messageList);
                }else {
                    stageR.set("message","未处理");
                }
                enterWarehouseList.add(stageR);
            }
            count++;
        }
        finalList.add(enterWarehouseList);
        return finalList;
    }

    /**
     * 通过仓库阶段每次接收的原料ids查询批号
     * @param stageLen item数组的长度
     * @param purchaseId 采购单id
     * @param stageItemArray item数组
     * @return batchNumMap key--原料id value--原料批号
     * @throws PcException
     */
    public Map<String,String> getbatchNumMap(int stageLen,String purchaseId,JSONArray stageItemArray)throws PcException{
        WarehouseStockService wss=enhance(WarehouseStockService.class);
        //将本次接收的原料id整理成数组
        String[]materialIds=new String[stageLen];
        for (int i=0;i<stageLen;i++){
            materialIds[i]=stageItemArray.getJSONObject(i).getString("id");
        }
        Record materialIdsR=new Record();
        materialIdsR.set("material_ids",materialIds);
        materialIdsR.set("purchase_order_id",purchaseId);
        //通过原料ids和采购单id查询仓库库存表得到原料批号
        List<Record> batchNumList=wss.list(materialIdsR);
        //将list整理成map，key--原料id，value--批号batch_num
        Map<String,String> batchNumMap=new HashMap<>(batchNumList.size());
        for (Record batchNumR:batchNumList){
            batchNumMap.put(batchNumR.getStr("material_id"),batchNumR.getStr("batch_num"));
        }
        return batchNumMap;
    }
}
