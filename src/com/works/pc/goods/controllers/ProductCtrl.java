package com.works.pc.goods.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.*;
import com.works.pc.goods.services.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductCtrl extends BaseCtrl<ProductService> {

    public ProductCtrl() {
        super(ProductService.class);
    }

    @Override
    public void handleRecord(Record record) {
        Map<String, String> productType = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.PRODUCT_TYPE);
        Map<String, String> warehouseType = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.WAREHOUSE_TYPE);
        Map<String, String> state = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.STATE);
        record.set("type_text", productType.get(record.get("type")));
        record.set("wm_type_text", warehouseType.get(record.get("wm_type")));
        record.set("state_text", state.get(record.get("state")));
    }

    @Override
    public void handleAddRecord(Record record) {
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        record.set("state", 1);
        record.set("pinyin", HanyuPinyinHelper.getFirstLettersLo(record.getStr("name")));
        record.set("create_id", usu.getSysUserId());
        record.set("sort", service.getCurrentSort() + 10);
    }

    @Override
    public void handleUpdateRecord(Record record) {
        record.set("pinyin", HanyuPinyinHelper.getFirstLettersLo(record.getStr("name")));
    }

    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword = getPara("keyword");
        if(keyword != null && keyword.length() > 0){
            String key = "$all$and#name$like$or#pinyin$like$or#num$like$or";
            String[] value = {keyword, keyword, keyword};
            record.set(key, value);
        }
        String[] catalogIds = getParaValues("catalog_ids");
        if(catalogIds != null && catalogIds.length > 0){
            String key = "$in#and#catalog_id";
            record.set(key, catalogIds);
        }
    }

    /**
     * 取消批量删除接口
     */
    @Override
    public void deleteByIds() {
        return;
    }

    /**
     * 获取带有分类的商品树
     */
    public void getProductTree(){
        Record record = getParaRecord();
        JsonHashMap jhm = new JsonHashMap();
        Record root = service.getProductCatalogTree(record);
        jhm.putSuccess(root);
        renderJson(jhm);
    }

    /**
     * 修改商品bom接口
     */
    public void updateBom(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        try {
            boolean flag = service.updateBom(json);
            if(flag){
                jhm.putMessage("更新bom成功！");
            }else{
                jhm.putFail("更新bom失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    public void getProductUnit(){
        JsonHashMap jhm = new JsonHashMap();
        try {
            List<Record> unitList = service.getProductUnit();
            jhm.putSuccess(unitList);
        }catch (PcException e){
            e.printStackTrace();
            jhm.putFail(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 通过id删除商品
     */
    public void deleteById(){
        JsonHashMap jhm = new JsonHashMap();
        String id = getPara("id");
        try {
            String msg = service.deleteById(id);
            if(msg != null){
                jhm.putMessage(msg);
            }else{
                jhm.putMessage("删除成功！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putMessage(e.getMsg());
        }
        renderJson(jhm);
    }

    public void getProductNoCatalogTree(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = getParaRecord();
        String keyword = record.getStr("keyword");
        if(keyword != null && keyword.trim().length() > 0){
            String key = "$all$and#name$like$or#pinyin$like$or#num$like$or";
            String[] value = {keyword, keyword, keyword};
            record.set(key, value);
        }
        String[] parentIdArr = {"0"};
        record.set("$<>#and#parent_id", parentIdArr);
        Record root = service.getProductNoCatalogTree(record);
        List<Record> result = root.get("children") != null ? root.get("children") : new ArrayList<>();
        jhm.putSuccess(result);
        renderJson(jhm);
    }

}
