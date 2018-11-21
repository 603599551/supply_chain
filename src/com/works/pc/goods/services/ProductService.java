package com.works.pc.goods.services;

import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.common.service.OrderNumberGenerator;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.*;
import com.works.pc.order.services.OrderService;
import com.works.pc.purchase.services.PurchaseOrderService;
import com.works.pc.store.services.StoreProductRelationService;
import com.works.pc.store.services.StoreStockService;
import com.works.pc.supplier.services.SupplierService;
import com.works.pc.sys.services.CatalogService;
import com.works.pc.warehourse.services.WarehouseStockService;

import java.util.*;

public class ProductService extends BaseService {

    private static String[] columnNameArr = {"id", "create_id", "catalog_id", "name", "num", "pinyin", "state", "bom", "sort", "type", "wm_type", "unit", "attribute", "parent_id", "parent_name", "parent_num", "cost_price", "purchase_price", "sell_price", "remark"};
    private static String[] columnTypeArr = {"VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "INT", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "DECIMAL", "DECIMAL", "DECIMAL", "VARCHAR"};
    private static String[] columnCommentArr = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

    public ProductService() {
        super("s_product", new TableBean("s_product", columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        if (list != null && list.size() > 0) {
            for (Record r : list) {
                r.set("bom", JSONObject.parseObject(r.getStr("bom")));
            }
        }
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        if(page != null && page.getList().size() > 0){
            for (Record r : page.getList()) {
                r.set("bom", JSONObject.parseObject(r.getStr("bom")));
            }
        }
        return page;
    }

    /**
     * 获取商品树，一级商品默认parent_id=0
     *
     * @return
     */
    public Record getProductTree(Record record) {
        String state = record.getStr("state");
        List<Record> productCatalogList;
        String sql = "select m.*,CONCAT(m.name,'-',m.num,'-',m.pinyin) search_text,m.catalog_id catalog_pid, '' catalog_cid from s_product m ";
        if (state != null && state.trim().length() > 0) {
            sql += " where m.state=?";
            productCatalogList = Db.find(sql, state);
        } else {
            productCatalogList = Db.find(sql);
        }
        for (Record r : productCatalogList) {
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
     *
     * @return
     */
    public Record getProductCatalogTree(Record record) {
        List<Record> allList = new ArrayList<>();
        CatalogService catalogService = enhance(CatalogService.class);
        Map<String, String> columnsMap = new HashMap<>();
        columnsMap.put("catalog_cid", "id");
        columnsMap.put("catalog_pid", "parent_id");
        List<Record> catalogList = catalogService.getRenameCatalogList("product", columnsMap);
        if (catalogList != null && catalogList.size() > 0) {
            for (Record r : catalogList) {
                r.set("showChild", false);
            }
        }
        allList.addAll(catalogList);
        Record productTree = getProductTree(record);
        if (productTree != null && productTree.get("children") != null) {
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
        if (parentId != null && parentId.trim().length() > 0) {
            Record parentProduct = this.findById(parentId);
            record.set("bom", parentProduct.getStr("bom"));
            return super.add(record);
        } else {
            record.set("parent_id", "0");
            return super.add(record);
        }
    }

    /**
     * 修改商品bom
     * 修改一级商品bom时会同步所有子商品bom
     * 如果修改的是子商品的bom，那么不会影响其他
     *
     * @param jsonObject
     * @return
     * @throws PcException
     */
    public boolean updateBom(JSONObject jsonObject) throws PcException {
        String id = jsonObject.getString("id");
        Record product = this.findById(id);
        if ("0".equals(product.get("parent_id"))) {
            List<Record> productChildren = Db.find("select * from s_product where parent_id=?", id);
            if (productChildren != null && productChildren.size() > 0) {
                for (Record r : productChildren) {
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

    /**
     * 获取商品单位
     * @return
     * @throws PcException
     */
    public List<Record> getProductUnit() throws PcException {
        List<Record> result = new ArrayList<>();
        List<Record> unitList = Db.find("SELECT DISTINCT M.UNIT UNIT FROM S_PRODUCT M");
        if (unitList != null && unitList.size() > 0) {
            Set<String> unitSet = new HashSet<>();
            for (Record r : unitList) {
                unitSet.add(r.getStr("UNIT"));
            }
            for (String s : unitSet) {
                if (s != null && s.trim().length() > 0) {
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
     *
     * @param id
     * @return 删除数量
     * @throws PcException
     */
    public String deleteById(String id) throws PcException {
        String tableName = getCannotRecordList(id);
        if (tableName == null) {
            int i = super.delete(id);
            if (i > 0) {
                return null;
            }
            return "删除失败";
        } else {
            return "该商品被" + tableName + "占用，不能删除！";
        }
    }

    /**
     * 获取不能删除的商品数据
     * 判断以下表中数据是否用到了当前商品，括号中是因为当前表而不需要去查询的表
     * 1、门店商品关联
     * 2、门店订单表（门店退货单、门店废弃单）
     * 3、商品表
     *
     * @param id
     * @return
     */
    private String getCannotRecordList(String id) {
        StoreProductRelationService storeProductRelationService = enhance(StoreProductRelationService.class);
        OrderService orderService = enhance(OrderService.class);
        Record storeProductRelation = new Record();
        storeProductRelation.set("product_id", id);
        try {
            List<Record> storeProductRelationList = storeProductRelationService.list(storeProductRelation);
            if (storeProductRelationList != null && storeProductRelationList.size() > 0) {
                return "门店商品关联";
            }
        } catch (PcException e) {
            e.printStackTrace();
            return "门店商品关联";
        }
        Record order = new Record();
        String key = "$like#order_item";
        String value = id;
        order.set(key, value);
        try {
            List<Record> orderList = orderService.list(order);
            if (orderList != null && orderList.size() > 0) {
                return "门店订货单";
            }
        } catch (PcException e) {
            e.printStackTrace();
            return "门店订货单";
        }
        Record product = new Record();
        product.set("parent_id", id);
        try {
            List<Record> productList = this.list(product);
            if (productList != null && productList.size() > 0) {
                return "子商品";
            }
        } catch (PcException e) {
            e.printStackTrace();
            return "子商品";
        }
        return null;
    }

    public Record getProductNoCatalogTree(Record record){
        //$all$and#name$like$or#pinyin$like$or#num$like$or
        Record root = new Record();
        root.set("id", "0");
        try {
            List<Record> childList = this.list(record);
            if(childList != null && childList.size() > 0){
                Set<String> childrenSet = new HashSet<>();
                for(int i = 0; i < childList.size(); i++){
                    childrenSet.add(childList.get(i).get("parent_id"));
                }
                Record parent = new Record();
                parent.setColumns(record);
                parent.set("$in#and#id", childrenSet.toArray(new String[childrenSet.size()]));

                parent.remove("$<>#and#parent_id");
                parent.set("parent_id", "0");

                List<Record> parentList = this.list(parent);
                List<Record> allList = new ArrayList<>(parentList);
                allList.addAll(childList);
                if(allList != null && allList.size() > 0){
                    for(Record r : allList){
                        r.set("showChild", false);
                    }
                }
                BeanUtils.createTree(root, allList);
            }else{
                record.remove("$<>#and#parent_id");
                record.set("parent_id", "0");
                List<Record> parentList = this.list(record);
                if(parentList != null && parentList.size() > 0){
                    for(Record r : parentList){
                        r.set("showChild", false);
                    }
                }
                BeanUtils.createTree(root, parentList);
            }
        } catch (PcException e) {
            e.printStackTrace();
        }
        return root;
    }

    /**
     * 获取订货时的商品树
     */
    public Record getOrderProductTree(Record record, UserSessionUtil usu){
        CatalogService catalogService = enhance(CatalogService.class);
        Map<String, String> columnsMap = new HashMap<>();
        columnsMap.put("catalog_cid", "id");
        columnsMap.put("catalog_pid", "parent_id");
        List<Record> catalogList = catalogService.getRenameCatalogList("product", columnsMap);
        String productSelect = "select p.*,p.catalog_id catalog_pid, '' catalog_cid from s_product p, s_store_product_relation spr where p.id=spr.product_id and p.state=? and spr.store_id=?";
        List<Record> productList = Db.find(productSelect, "1", usu.getUserBean().get("store_id"));
        if(productList != null && productList.size() > 0){
            for(Record r : productList){
                r.remove("bom");
                String searchText = r.getStr("parent_name") + "-" + r.getStr("num") + "-" + HanyuPinyinHelper.getFirstLettersLo(r.getStr("parent_name"));
                r.set("search_text", searchText);
            }
        }
        List<Record> allList = new ArrayList<>(catalogList);
        allList.addAll(productList);
        Record root = new Record();
        root.set("catalog_cid", "0");
        BeanUtils.createTree(root, allList, "catalog_pid", "catalog_cid");
        return root;
    }

}
