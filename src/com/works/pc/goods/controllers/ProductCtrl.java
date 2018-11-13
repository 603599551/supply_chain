package com.works.pc.goods.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.*;
import com.works.pc.goods.services.ProductService;

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
        record.set("state", 1);
        record.set("pinyin", HanyuPinyinHelper.getFirstLettersLo(record.getStr("name")));
    }

    @Override
    public void handleUpdateRecord(Record record) {
        record.set("pinyin", HanyuPinyinHelper.getFirstLettersLo(record.getStr("name")));
    }

    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword = getPara("keyword");
        String key = "$all$and#name$like$or#pinyin$like$or#num$like$or";
        String[] value = {keyword, keyword, keyword};
        record.set(key, value);
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
    public void getMaterialTree(){
        JsonHashMap jhm = new JsonHashMap();
        Record root = service.getProductCatalogTree();
        jhm.putSuccess(root);
        renderJson(jhm);
    }

    public void bom(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        try {
            boolean flag = service.bom(json);
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
}
