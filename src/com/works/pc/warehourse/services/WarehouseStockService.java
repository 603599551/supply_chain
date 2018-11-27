package com.works.pc.warehourse.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bean.TableBean;
import com.common.service.BaseService;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.BeanUtils;
import com.utils.UUIDTool;
import com.utils.UnitConversion;
import com.works.pc.goods.services.MaterialService;
import com.works.pc.order.services.OrderReturnService;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 该类操作仓库库存表，实现新增和修改功能
 * @author CaryZ
 * @date 2018-11-10
 */
@Before(Tx.class)
public class WarehouseStockService extends BaseService {

    private static final String TABLENAME="s_warehouse_stock";
    private static final String WAREHOUSE_ID="warehouse_id";
    private static String[] columnNameArr = {"id","user_id","warehouse_id","state","quantity","batch_num","material_data","sort","material_id","purchase_order_id","purchase_order_num"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","",""};

    public WarehouseStockService() {
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
            r.set("material_data", JSONObject.parseObject(r.getStr("material_data")));
        }
        return page;
     }

    /**
     * 根据盘点项JSON数组的仓库库存id更新库存信息
     *  "count_item":[
     *              {
     *                  "id":"原料id",
     *                  "stock_id":"仓库库存记录id",
     *                  "batch_num":"仓库原料批号",
     *                  "before_quantity":"盘点项之前的数量"(出库单位)
     *                  "current_quantity":"盘点项现在的数量"(出库单位)
     *                  "item_remark":"盘点项的备注"
     *              },
     *              {
     *                  "id":"原料id",
     *                  "stock_id":"仓库库存记录id",
     *                  "batch_num":"仓库原料批号",
     *                  "before_quantity":"盘点项之前的数量",
     *                  "current_quantity":"盘点项现在的数量",
     *                  "item_remark":"盘点项的备注"
     *              }
     *          ]
     * @author CaryZ
     * @date 2018-11-11
     * @param record 新增盘点信息
     * @param countItems 盘点项JSON数组
     * @param materialMap key存id，value存JSONObject
     * @return 更新库存信息成功/失败 true/false
     * @throws PcException
     */
    public boolean batchHandle(Record record,JSONArray countItems,Map<String,Record> materialMap) throws PcException{
        int countLen=countItems.size();
        //本次盘点项JSON转成的List<Record>
        List<Record> itemList=new ArrayList<>(countLen);
        for (int i=0;i<countLen;i++){
            Record countItem= BeanUtils.jsonToRecord(countItems.getJSONObject(i));
            Record materialR=materialMap.get(countItem.getStr("id"));
            //此步是为了便于调用outUnit2SmallUnit函数
            materialR.set("quantity",countItem.getStr("current_quantity"));
            Record newStockR=new Record();
            newStockR.set("id",countItem.getStr("stock_id"));
            newStockR.set("user_id",record.getStr("count_id"));
            newStockR.set("quantity",UnitConversion.outUnit2SmallUnitDecil(materialR));
            //由于仓库入库时，material_data存了提供本批次原料的供应商id，编号，姓名，因此在更新库存信息时不更新material_data
//            countItem.set("id",countItem.getStr("stock_id"));
//            countItem.set("user_id",record.getStr("count_id"));
//            countItem.set("warehouse_id",record.getStr("warehouse_id"));
//            countItem.set("quantity", UnitConversion.outUnit2SmallUnitDecil(materialR));
//            countItem.remove("stock_id","before_quantity","current_quantity","item_remark");
            itemList.add(newStockR);
        }
        return Db.batchUpdate(TABLENAME,"id",itemList,countLen)==null? false:true;
    }

    /**
     * 批量新增仓库库存信息
     * @author CaryZ
     * @date 2018-11-13
     * @param addStockList 新增库存信息
     * @return 更新成功/失败 返true/false
     */
    public boolean batchSave(List<Record> addStockList)throws PcException{
        int sort=getCurrentSort();
        for (Record record:addStockList){
            sort++;
            record.set("sort",sort);
        }
        try{
            return Db.batchSave(TABLENAME,addStockList,addStockList.size())==null? false:true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new PcException(ADD_EXCEPTION,e.getMessage());
        }
    }

    /**
     * 采购退货流程：在物流发货后根据item每个元素中的stock_id减少仓库库存
     * @author CaryZ
     * @date 2018-11-17
     * @param record 当前流程记录信息 current_quantity 退货数量（出库单位） quantity库存数量（最小单位）
     * @return 成功/失败 true/false
     */
    public boolean updateStockAfterDelivery(Record record){
        String item=record.getStr("item");
        return updateStockAfterReturn(item,"stock_id",false);
    }

    /**
     * 1.采购退货，在物流发货后根据item每个元素中的stock_id减少仓库库存
     * 2.物流完成退货单，根据order_item里的warehouse_stock_id、要退的数量current_quantity（出库单位）增加仓库库存的quantity
     * @param item JSON数组
     * @param isAdd 增加/减少 true/false
     * @param colomnName 仓库库存id字段名
     * @return
     */
    public boolean updateStockAfterReturn(String item,String colomnName,boolean isAdd){
        MaterialService ms=enhance(MaterialService.class);
        JSONObject itemJson=JSONObject.parseObject(item);
        JSONArray itemArray=itemJson.getJSONArray("item");
        //根据itemArray查询原料的详细信息
        List<Record> materialList=ms.queryMaterials(itemArray);
        int len=materialList.size();
        //materialList转map key存id，value存JSONObject
        Map<String,Record> materialMap=new HashMap(len);
        for (int j=0;j<len;j++){
            materialMap.put(materialList.get(j).getStr("id"),materialList.get(j));
        }
        int itemLen=itemArray.size();
        //item每个元素中的stock_id提出来
        String[] ids=new String[itemLen];
        for (int i=0;i<itemLen;i++){
            ids[i]=itemArray.getJSONObject(i).getString(colomnName);
        }
        //通过id找到历史库存
        List<Record> stockList=super.selectByColumnIn("id",ids);
        //退货项转成的List<Record>
        List<Record> itemList=new ArrayList<>(itemLen);
        String beforeQuantity="";
        for (int i=0;i<itemLen;i++){
            Record itemR= BeanUtils.jsonToRecord(itemArray.getJSONObject(i));
            Record materialR=materialMap.get(itemR.getStr("id"));
            for (Record stockR:stockList){
                if (StringUtils.equals(itemR.getStr(colomnName),stockR.getStr("id"))){
                    beforeQuantity=stockR.getStr("quantity");
                    break;
                }
            }
            Record updateStockR=new Record();
            updateStockR.set("id",itemR.getStr(colomnName));
            //此步是为了便于调用outUnit2SmallUnit函数
            materialR.set("quantity",itemR.getStr("current_quantity"));
            if (isAdd){
                //历史库存+退货量(最小单位)
                updateStockR.set("quantity", Double.parseDouble(beforeQuantity)+UnitConversion.outUnit2SmallUnitDecil(materialR));
            }else {
                //历史库存-退货量(最小单位)
                updateStockR.set("quantity", Double.parseDouble(beforeQuantity)-UnitConversion.outUnit2SmallUnitDecil(materialR));
            }
            itemList.add(updateStockR);
        }
        return Db.batchUpdate(TABLENAME,"id",itemList,itemLen)==null? false:true;
    }

    /**
     * 查询仓库库存原料id和名称，使用HashSet<Map>去除重复
     * @param record
     * @return
     */
    public HashSet<Record> queryMaterialNameIds(Record record){
        List<Record> list=Db.find("SELECT material_id value,material_data name FROM s_warehouse_stock WHERE state='1' AND quantity>'0' AND warehouse_id=?",record.getStr("warehouse_id"));
        for (Record r:list){
            JSONObject jsonObject=JSONObject.parseObject(r.getStr("name"));
            r.set("name",jsonObject.getString("name"));
        }
        HashSet<Record> set=new HashSet<>();
        set.addAll(list);
        return set;
    }

    /**
     * 通过门店退货单的原料ids查询仓库库存表得到库存记录id和批号
     * 遍历totalList，采用map方式进行归类
     * key---原料id value ---list<Record> 批号-库存记录id
     * @param record 门店退货单信息
     * @return
     */
    public void queryBatchNumList(Record record) throws PcException{
        if (record.get("is_accepted")){
            OrderReturnService orderReturnService=enhance(OrderReturnService.class);
            Record returnOrderR=orderReturnService.findById(record.getStr("id"));
            record.set("order_item",JSONObject.parseObject(returnOrderR.getStr("order_item")));
        }else {
            JSONObject jsonObject=record.get("order_item");
            JSONArray itemArray=jsonObject.getJSONArray("item");
            int itemLen=itemArray.size();
            String[] materialIds=new String[itemLen];
            for (int i=0;i<itemLen;i++){
                materialIds[i]=itemArray.getJSONObject(i).getString("id");
            }
            List<Record> totalList= super.selectByColumnIn("material_id",materialIds);
            Map<String,List<Record>> map=new HashMap<>();
            List<Record> list=new ArrayList<>();
            String materialId="";
            for (Record record1:totalList){
                Record record2=new Record();
                record2.set("name",record1.getStr("batch_num"));
                record2.set("value",record1.getStr("id"));
                materialId=record1.getStr("material_id");
//                //首次新建batchNumList
//                if (map.get(materialId)==null){
//                    List<Record> batchNumList=new ArrayList<>();
//                    batchNumList.add(record2);
//                    map.put(materialId,batchNumList);
//                }else {
//                    //取目标list，往里加Record，再put回去
//                    list=map.get(materialId);
//                    list.add(record2);
//                    map.put(materialId,list);
//                }
                //java8新特性，效果和上面的一样，待前端测试
                List<Record> batchNumList= map.computeIfAbsent(materialId,k->new ArrayList<>());
                batchNumList.add(record2);
            }
            for (int i=0;i<itemLen;i++){
                itemArray.getJSONObject(i).put("batch_nums",map.get(itemArray.getJSONObject(i).getString("id")));
                itemArray.getJSONObject(i).put("warehouse_stock_id","");
            }
        }
    }

    /**
     * 批量更新门店原料库存信息
     * @author CaryZ
     * @date 2018-11-09
     * @return 更新成功/失败 返true/false
     * @Param stockList 该店已存在的库存信息
     * @Param itemList  本次盘点项JSON转成的List<Record>
     * @Param itemAddList 盘点项中需要新增的库存信息
     * @Param updateList  要更新的库存信息
     * @Param materialList 盘点项涉及的原料信息
     * @deprecated 业务逻辑发生变化 不再使用该方法
     */
    public boolean batchUpdate(List<Record> stockList,List<Record> itemList,List<Record> itemAddList,List<Record> updateList,List<Record> materialList) throws PcException{
        Record stock;
        String stockId="";
        String materialId="";
        int stockLen=stockList.size();
        int itemLen=itemList.size();
        int materialLen=materialList.size();
        List<Record> removeList=new ArrayList<>();
        Map itemMap=new HashMap(itemLen);
        //itemList转map key存id，value存JSONObject
        for (int j=0;j<itemLen;j++){
            itemMap.put(itemList.get(j).getStr("id"),itemList.get(j));
        }
        for (int i=0;i<stockLen;i++){
            stock=stockList.get(i);
            stockId=stock.getStr("id");
            materialId=stock.getStr("material_id");
            Record item=(Record) itemMap.get(materialId);
            //若存在库存记录
            if (item!=null){
                Record updatedStock=new Record();
                updatedStock.set("id",stockId);
                updatedStock.set("quantity",item.getStr("current_quantity"));
                updateList.add(updatedStock);
                removeList.add(item);
            }
        }
        itemAddList.removeAll(removeList);


        //往已存在的库存记录更新原料信息
        int updateAddLen=updateList.size();
        if (updateAddLen>0){
            Map materialMap=new HashMap(materialLen);
            for (int j=0;j<materialLen;j++){
                materialMap.put(materialList.get(j).getStr("id"),materialList.get(j));
            }
            for (int i=0;i<updateAddLen;i++){
                Record updatedStock=updateList.get(i);
                materialId=updatedStock.getStr("material_id");
                if (materialMap.get(materialId)!=null){
                    updatedStock.set("material_data",materialMap.get(materialId).toString());
                }
            }
            try{
                return Db.batchUpdate(TABLENAME,"id",updateList,updateAddLen)==null? false:true;
            }catch (Exception e){
                throw new PcException(UPDATE_EXCEPTION,e.getMessage());
            }
        }
        return true;
    }



    /**
     * 批量新增门店原料库存信息
     * @author CaryZ
     * @date 2018-11-09
     * @return 更新成功/失败 返true/false
     * @Param record    盘点表的新增记录
     * @Param addList  要新增的库存信息
     * @Param itemAddList 盘点项涉及要新增的库存信息
     * @Param materialList 盘点项涉及的原料信息
     * @deprecated 业务逻辑发生变化 不再使用该方法
     */
    public boolean batchSave(Record record,List<Record> addList,List<Record> itemAddList,List<Record> materialList)throws PcException{
        String materialId="",currentQuantity="";
        int materialLen=materialList.size();
        Map materialMap=new HashMap(materialLen);
        for (int j=0;j<materialLen;j++){
            materialMap.put(materialList.get(j).getStr("id"),materialList.get(j));
        }
        int sort=super.getCurrentSort()+1;
        //新增库存记录
        int itemAddLen=itemAddList.size();
        for (int j=0;j<itemAddLen;j++){
            materialId=itemAddList.get(j).getStr("id");
            currentQuantity=itemAddList.get(j).getStr("current_quantity");
            Record addedStock=new Record();
            addedStock.set("id", UUIDTool.getUUID());
            addedStock.set(WAREHOUSE_ID, record.getStr(WAREHOUSE_ID));
            addedStock.set("state", record.getStr("state"));
            addedStock.set("quantity", currentQuantity);
            addedStock.set("sort", sort);  sort++;
            addedStock.set("material_id", materialId);
            addedStock.set("user_id", record.getStr("count_id"));
            if (materialMap.get(materialId)!=null){
                addedStock.set("material_data",materialMap.get(materialId).toString());
            }
            addList.add(addedStock);
        }
        try{
            return Db.batchSave(TABLENAME,addList,addList.size())==null? false:true;
        }catch (Exception e){
            throw new PcException(ADD_EXCEPTION,e.getMessage());
        }
    }
}
