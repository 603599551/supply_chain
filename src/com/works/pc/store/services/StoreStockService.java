package com.works.pc.store.services;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.BeanUtils;
import com.utils.UUIDTool;
import com.works.pc.goods.services.MaterialService;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类操作门店库存表，实现新增和修改功能
 * @author CaryZ
 * @date 2018-11-08
 */
@Before(Tx.class)
public class StoreStockService extends BaseService {

    private static final String TABLENAME="s_store_stock";
    private static final String STORE_ID="store_id";
    private static String[] columnNameArr = {"id","store_id","state","quantity","batch_num","material_data","store_color","sort","material_id"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","INT","INT","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","",""};

    public StoreStockService() {
        super(TABLENAME, new TableBean(TABLENAME, columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return page;
     }

    /**
     * 根据盘点项JSON数组的原料ids查询库存信息
     * 若不存在，则批量新增该项的库存记录
     * 若存在，则批量更新库存数量和原料信息
     * 先更新已存在的库存信息，再新增信息
     * @author CaryZ
     * @date 2018-11-09
     * @param record 新增盘点信息
     * @return 更新库存信息成功/失败 true/false
     * @throws PcException
     */
    public boolean batchHandle(Record record,JSONArray countItems) throws PcException{
        MaterialService materialService=super.enhance(MaterialService.class);
        int countLen=countItems.size();
        //本次盘点项JSON转成的List<Record>
        List<Record> itemList=new ArrayList<>(countLen);
        //盘点项中需要新增的库存信息
        List<Record> itemAddList=new ArrayList<>(countLen);

        StringBuffer sql=new StringBuffer("SELECT id,material_id FROM "+TABLENAME+" WHERE material_id IN(");
        for (int i=0;i<countLen;i++){
            Record countItem= BeanUtils.jsonToRecord(countItems.getJSONObject(i));
            itemList.add(countItem);
            itemAddList.add(countItem);
            if (i==countLen-1){
                sql.append("?)");
            }else {
                sql.append("?,");
            }
        }
        sql.append(" AND "+STORE_ID+"=?");
        int itemLen=itemList.size();
        //将itemList中的id单独提出来
        String[]ids=new String[itemLen+1];
        for (int i=0;i<itemLen;i++){
            ids[i]=itemList.get(i).getStr("id");
        }
        ids[itemLen]=record.getStr(STORE_ID);
        //该店已存在的库存信息
        List<Record> stockList= Db.find(sql.toString(),ids);
        //根据盘点项查询出来的原料信息List
        List<Record> materialList=materialService.queryMaterials(countItems);
        //要新增的库存信息
        List<Record> addList=new ArrayList<>();
        //要更新的库存信息
        List<Record> updateList=new ArrayList<>();

        if (!batchUpdate(stockList,itemList,itemAddList,updateList,materialList)){
            return false;
        }

        return batchSave(record,addList,itemAddList,materialList);
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
                updatedStock.set("material_id",materialId);
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
            addedStock.set(STORE_ID, record.getStr(STORE_ID));
            addedStock.set("state", record.getStr("state"));
            addedStock.set("quantity", currentQuantity);
            addedStock.set("sort", sort);  sort++;
            addedStock.set("material_id", materialId);
            addedStock.set("store_color", record.getStr("store_color"));
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
