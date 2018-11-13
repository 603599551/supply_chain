package com.works.pc.goods.services;

import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;

import java.util.ArrayList;
import java.util.List;

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
    public Record getProductTree() {
        List<Record> productCatalogList = Db.find("select m.*,CONCAT(m.name,'-',m.num,'-',m.pinyin) search_text,m.catalog_id catalog_pid, '' catalog_cid from s_product m where m.state=?", 1);
        Record root = new Record();
        root.set("parent_id", "0");
        BeanUtils.createTree(root, productCatalogList);
        return root;
    }

    /**
     * 获取商品和分类组合的树
     * @return
     */
    public Record getProductCatalogTree(){
        Record productTree = getProductTree();
        List<Record> catalogList = Db.find("select c.*,c.name search_text,c.id catalog_cid, c.parent_id catalog_pid from s_catalog c where c.type=?", "product");
        List<Record> allList = new ArrayList<>(productTree.get("children"));
        allList.addAll(catalogList);
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
            return super.add(record);
        }
    }

    public boolean bom(JSONObject jsonObject) throws PcException {
        String id = jsonObject.getString("id");
        Record product = this.findById(id);
        if(product.get("parent_id") == null || product.getStr("parent_id").trim().length() == 0){
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
}
