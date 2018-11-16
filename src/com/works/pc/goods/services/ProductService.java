package com.works.pc.goods.services;

import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.works.pc.purchase.services.PurchaseOrderService;
import com.works.pc.store.services.StoreStockService;
import com.works.pc.supplier.services.SupplierService;
import com.works.pc.warehourse.services.WarehouseStockService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductService extends BaseService {

    private static String[] columnNameArr = {"id","create_id","catalog_id","name","num","pinyin","state","bom","sort","type","wm_type","unit","attribute","parent_id","parent_name","parent_num","cost_price","purchase_price","sell_price","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","DECIMAL","DECIMAL","DECIMAL","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","","","","","",""};

    public ProductService() {
        super("s_product", new TableBean("s_product", columnNameArr, columnTypeArr, columnCommentArr));
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
     * 获取商品树，一级商品默认parent_id=0
     * @return
     */
    public Record getProductTree(Record record) {
        String state = record.getStr("state");
        List<Record> productCatalogList;
        String sql = "select m.*,CONCAT(m.name,'-',m.num,'-',m.pinyin) search_text,m.catalog_id catalog_pid, '' catalog_cid from s_product m ";
        if(state != null && state.trim().length() > 0){
            sql += " where m.state=?";
            productCatalogList = Db.find(sql, state);
        }else{
            productCatalogList = Db.find(sql);
        }
        for(Record r : productCatalogList){
            r.remove("bom");
            r.set("showChild", false);
        }
        Record root = new Record();
        root.set("id", "0");
        BeanUtils.createTree(root, productCatalogList);
        return root;
    }

    /**
     * 获取商品和分类组合的树
     * @return
     */
    public Record getProductCatalogTree(Record record){
        List<Record> allList = new ArrayList<>();

        List<Record> catalogList = Db.find("select c.*,c.name search_text,c.id catalog_cid, c.parent_id catalog_pid from s_catalog c where c.type=?", "product");
        if(catalogList != null && catalogList.size() > 0){
            for(Record r : catalogList){
                r.set("showChild", false);
            }
        }
        allList.addAll(catalogList);
        Record productTree = getProductTree(record);
        if(productTree != null && productTree.get("children") != null){
            allList.addAll(productTree.get("children"));
        }
        Record root = new Record();
        root.set("catalog_cid", "0");
        BeanUtils.createTree(root, allList, "catalog_pid", "catalog_cid");
        return root;
    }

    @Override
    public String add(Record record) throws PcException {
        String parentId = record.getStr("parent_id");
        if(parentId != null && parentId.trim().length() > 0){
            Record parentProduct = this.findById(parentId);
            record.set("bom", parentProduct.getStr("bom"));
            return super.add(record);
        }else{
            record.set("parent_id", "0");
            return super.add(record);
        }
    }

    /**
     * 修改商品bom
     * 修改一级商品bom时会同步所有子商品bom
     * 如果修改的是子商品的bom，那么不会影响其他
     * @param jsonObject
     * @return
     * @throws PcException
     */
    public boolean updateBom(JSONObject jsonObject) throws PcException {
        String id = jsonObject.getString("id");
        Record product = this.findById(id);
        if("0".equals(product.get("parent_id"))){
            List<Record> productChildren = Db.find("select * from s_product where parent_id=?", id);
            if(productChildren != null && productChildren.size() > 0){
                for(Record r : productChildren){
                    r.set("bom", jsonObject.getJSONObject("bom"));
                }
                Db.batchUpdate(this.tableName, productChildren, productChildren.size());
            }
        }
        Record update = new Record();
        update.set("id", id);
        update.set("bom", jsonObject.getJSONObject("bom"));
        return Db.update(this.tableName, update);
    }

    public List<Record> getProductUnit() throws PcException {
        List<Record> result = new ArrayList<>();
        List<Record> unitList = Db.find("SELECT DISTINCT M.UNIT UNIT FROM S_PRODUCT M");
        if(unitList != null && unitList.size() > 0){
            Set<String> unitSet = new HashSet<>();
            for(Record r : unitList){
                unitSet.add(r.getStr("UNIT"));
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

    /**
     * 通过id删除product
     * @param id
     * @return 删除数量
     * @throws PcException
     */
    public int deleteById(String id) throws PcException {
        String tableName = getCannotRecordList(id);
        if(tableName == null){
            return super.delete(id);
        }else{
            throw new PcException(APP_DELETE_EXCEPTION, "该商品被" + tableName + "占用，不能删除！");
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
}
