package com.works.pc.goods.services;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.BeanUtils;
import com.utils.HanyuPinyinHelper;
import com.utils.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.works.pc.purchase.services.PurchaseOrderService;
import com.works.pc.store.services.StoreStockService;
import com.works.pc.supplier.services.SupplierService;
import com.works.pc.warehourse.services.WarehouseStockService;

import java.util.*;

@Before(Tx.class)
public class MaterialService extends BaseService {

    private static String[] columnNameArr = {"id","name","num","pinyin","cost_price","purchase_price","min_unit","min2mid_num","mid_unit","mid2max_num","max_unit","out_unit","attribute","brand","storage_condition","shelf_life_num","shelf_life_unit","security_time","order_type","model","createdate","create_id","updatedate","update_id","state","remark","type","catalog_id"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","DECIMAL","DECIMAL","VARCHAR","INT","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","","","","","","","","","","","","","",""};

    public MaterialService() {
        super("s_material", new TableBean("s_material", columnNameArr, columnTypeArr, columnCommentArr));
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
     * 根据盘点项JSON数组查询material数据
     * @author CaryZ
     * @date 2018-11-08
     * @param countItems 盘点项JSON数组
     * @return materialList
     */
    public List<Record> queryMaterials(JSONArray countItems){
        int countLen=countItems.size();
        String[] ids=new String[countLen];
        for (int i=0;i<countLen;i++){
            ids[i]=countItems.getJSONObject(i).getString("id");
        }
        return queryMaterials(ids);
    }

    /**
     * 根据ids查询material数据
     * @author CaryZ
     * @date 2018-11-08
     * @param ids 盘点项ids
     * @return materialList
     */
    public List<Record> queryMaterials(String... ids){
        return super.selectByColumnIn("id",ids);
    }

    /**
     * 通过id删除material
     * @param id
     * @return 删除数量
     * @throws PcException
     */
    public int deleteById(String id) throws PcException {
        String tableName = getCannotRecordList(id);
        if(tableName == null){
            return super.delete(id);
        }else{
            throw new PcException(APP_DELETE_EXCEPTION, "该原料被" + tableName + "占用，不能删除！");
        }
    }

    /**
     * 获取不能删除的原料数据
     * 判断以下表中数据是否用到了当前原料，括号中是因为当前表而不需要去查询的表
     * 1、仓库库存（仓库盘点、仓库废弃、出库单、移库单）
     * 2、门店库存（门店盘点、门店废弃、门店退货）
     * 3、采购订单（采购退货）
     * 4、供应商
     * 5、商品（订单）
     * @param id
     * @return
     */
    private String getCannotRecordList(String id){
        WarehouseStockService warehouseStockService = enhance(WarehouseStockService.class);
        StoreStockService storeStockService = enhance(StoreStockService.class);
        PurchaseOrderService purchaseOrderService = enhance(PurchaseOrderService.class);
        SupplierService supplierService = enhance(SupplierService.class);
        ProductService productService = enhance(ProductService.class);
        List<Record> warehouseStockList = warehouseStockService.selectByColumnIn("material_id", id);
        if(warehouseStockList != null && warehouseStockList.size() > 0){
            return "仓库库存";
        }
        List<Record> storeStockList = storeStockService.selectByColumnIn("material_id", id);
        if(storeStockList != null && storeStockList.size() > 0){
            return "门店库存";
        }
        try {
            Record purchaseOrderSelect = new Record();
            String likeKey = "$like#item";
            purchaseOrderSelect.set(likeKey, id);
            List<Record> purchaseOrderList = purchaseOrderService.list(purchaseOrderSelect);
            if(purchaseOrderList != null && purchaseOrderList.size() > 0){
                return "采购订单";
            }
        } catch (PcException e) {
            e.printStackTrace();
            return "采购订单";
        }
        try {
            Record supplierSelect = new Record();
            String likeKey = "$like#material_ids";
            supplierSelect.set(likeKey, id);
            List<Record> supplierList = supplierService.list(supplierSelect);
            if(supplierList != null && supplierList.size() > 0){
                return "供应商";
            }
        } catch (PcException e) {
            e.printStackTrace();
            return "供应商";
        }
        try {
            Record productSelect = new Record();
            String likeKey = "$like#bom";
            productSelect.set(likeKey, id);
            List<Record> productList = productService.list(productSelect);
            if(productList != null && productList.size() > 0){
                return "商品";
            }
        } catch (PcException e) {
            e.printStackTrace();
            return "商品";
        }
        return null;
    }

    /**
     * 获取带有分类的原料树
     * @return
     */
    public Record getMaterialTree(){
        List<Record> materialList = Db.find("select m.*,CONCAT(m.name,'-',m.num,'-',m.pinyin) search_text,m.catalog_id catalog_pid, '' catalog_cid from s_material m where m.state=?", 1);
        List<Record> catalogList = Db.find("select c.*,c.name search_text,c.id catalog_cid, c.parent_id catalog_pid from s_catalog c where c.type=?", "material");
        if(catalogList != null && catalogList.size() > 0){
            for(Record r : catalogList){
                String searchText = r.get("search_text") + "-" + HanyuPinyinHelper.getFirstLettersLo(r.getStr("search_text"));
                r.set("search_text", searchText);
            }
        }
        if(materialList != null && materialList.size() > 0){
            for(Record r : materialList){
                handleRecord(r);
            }
        }
        Record root = new Record();
        root.set("catalog_cid", "0");
        List<Record> allList = new ArrayList<>(materialList);
        allList.addAll(catalogList);
        BeanUtils.createTree(root, allList, "catalog_pid", "catalog_cid");
        return root;
    }

    public void handleRecord(Record record) {
        Map<String, String> storageCondition = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.STORAGE_CONDITION);
        Map<String, String> shelfLifeUnit = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.SHELF_LIFE_UNIT);
        Map<String, String> orderType = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.ORDER_TYPE);
        Map<String, String> purchaseType = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.PURCHASE_TYPE);
        record.set("storage_condition_text", storageCondition.get(record.getStr("storage_condition")));
        record.set("shelf_life_unit_text", shelfLifeUnit.get(record.getStr("shelf_life_unit")));
        record.set("order_type_text", orderType.get(record.getStr("order_type")));
        record.set("type_text", purchaseType.get(record.getStr("type")));
    }

    /**
     * 将门店/仓库 库存以树的方式体现（在盘点时用到）
     * @param tableName 库存表名
     * @param columnName 字段名
     * @param columnValue 字段值
     * @return
     */
    public Record getStockTree(String tableName,String columnName,String columnValue){
        List<Record> catalogList = Db.find("select c.*,c.name search_text,c.id catalog_cid, c.parent_id catalog_pid from s_catalog c where c.type=?", "material");
        List<Record> stockList=Db.find("SELECT *,id catalog_cid FROM "+tableName+" WHERE "+columnName+"=?",columnValue);
        for (Record record:stockList){
            JSONObject jsonObject=JSONObject.parseObject(record.getStr("material_data"));
            record.set("catalog_pid",jsonObject.getString("catalog_id"));
            record.set("material_data",jsonObject);
        }
        Record root = new Record();
        root.set("catalog_cid", "0");
        List<Record> allList = new ArrayList<>(stockList);
        allList.addAll(catalogList);
        BeanUtils.createTree(root, allList, "catalog_pid", "catalog_cid");
        return root;
    }

    /**
     * 获取带有原料批号的分类原料树
     * @return
     */
    public Record getBatchNumTree(){
        Record root=getMaterialTree();
        List<Record> materialList=Db.find("select m.*, m.catalog_id catalog_pid,m.id catalog_cid from s_material m where m.state=?", 1);
        if(materialList != null && materialList.size() > 0){
            for(Record r : materialList){
                handleRecord(r);
            }
        }
        List<Record> stockList=Db.find("SELECT *,material_id catalog_pid,'' catalog_cid FROM s_warehouse_stock WHERE warehouse_id=?","999a561766b142349ca73f1cac54a18a");
        List<Record> allList = new ArrayList<>(materialList);
        allList.addAll(stockList);
        BeanUtils.createTree(root, allList, "catalog_pid", "catalog_cid");
        return root;
    }

    /**
     * 获取原料表中的所有单位
     * @return
     * @throws PcException
     */
    public List<Record> getMaterialUnit() throws PcException {
        List<Record> result = new ArrayList<>();
        List<Record> unitList = Db.find("SELECT DISTINCT M.MAX_UNIT MAX,M.MID_UNIT MID,M.MIN_UNIT MIN FROM S_MATERIAL M");
        if(unitList != null && unitList.size() > 0){
            Set<String> unitSet = new HashSet<>();
            for(Record r : unitList){
                unitSet.add(r.getStr("MAX"));
                unitSet.add(r.getStr("MID"));
                unitSet.add(r.getStr("MIN"));
            }
            for(String s : unitSet){
                if(s != null && s.trim().length() > 0){
                    Record record = new Record();
                    record.set("name", s);
                    record.set("value", s);
                    result.add(record);
                }
            }
        }
        return result;
    }
}
