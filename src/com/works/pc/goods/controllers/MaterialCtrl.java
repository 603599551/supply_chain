package com.works.pc.goods.controllers;

import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.goods.services.MaterialService;

import java.util.Map;

public class MaterialCtrl extends BaseCtrl<MaterialService> {

    public MaterialCtrl() {
        super(MaterialService.class);
    }

    @Override
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

    @Override
    public void handleAddRecord(Record record) {
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        String time = DateUtil.GetDateTime();
        record.set("create_id", usu.getSysUserId());
        record.set("update_id", usu.getSysUserId());
        record.set("createdate", time);
        record.set("updatedate", time);
        record.set("state", 1);
        record.set("pinyin", HanyuPinyinHelper.getFirstLettersLo(record.getStr("name")));
    }

    @Override
    public void handleUpdateRecord(Record record) {
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        String time = DateUtil.GetDateTime();
        record.set("update_id", usu.getSysUserId());
        record.set("updatedate", time);
        record.set("pinyin", HanyuPinyinHelper.getFirstLettersLo(record.getStr("name")));
    }

    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword = getPara("keyword");
        String key = "$all$and#name$like$or#pinyin$like$or";
        String[] value = {keyword, keyword};
        record.set(key, value);
    }

    /**
     * 通过id删除原料
     */
    public void deleteById(){
        JsonHashMap jhm = new JsonHashMap();
        String id = getPara("id");
        try {
            service.deleteById(id);
            jhm.putMessage("删除成功！");
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putMessage(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 取消批量删除接口
     */
    @Override
    public void deleteByIds() {
        return;
    }

    /**
     * 获取带有分类的原料树
     */
    public void getMaterialTree(){
        JsonHashMap jhm = new JsonHashMap();
        Record root = service.getMaterialTree();
        jhm.putSuccess(root);
        renderJson(jhm);
    }

    /**
     * 获取带有原料批号的分类原料树
     */
    public void getBatchNumTree(){
        JsonHashMap jhm = new JsonHashMap();
        Record root = service.getBatchNumTree();
        jhm.putSuccess(root);
        renderJson(jhm);
    }
}
