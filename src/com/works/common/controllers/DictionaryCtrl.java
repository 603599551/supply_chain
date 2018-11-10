package com.works.common.controllers;

import com.common.controllers.BaseCtrl;
import com.common.service.BaseService;
import com.constants.DictionaryConstants;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.works.common.services.DictionaryService;
import org.apache.commons.lang.StringUtils;
import java.util.List;

/**
 * 显示数据字典
 */
public class DictionaryCtrl extends BaseCtrl<DictionaryService> {

    public DictionaryCtrl() {
        super(DictionaryService.class);
    }

    /**
     * 第一项为“全部”
     * 传输参数返回list
     * 参数是字典值
     */
    public void getDictIncludeAll() {
        String dict = getPara("dict");
        JsonHashMap jhm = new JsonHashMap();
        try {
            List<Record> list= DictionaryConstants.DICT_RECORD_LIST.get(dict);
            for (Record r:list){
                r.remove("id","parent_id","sort","state_color");
            }
            Record all = new Record();
            all.set("value", "");
            all.set("name", "全部");
            list.add(0, all);
            jhm.putSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putError(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 第一项为“请选择”
     */
    public void getDictIncludeChoose() {
        String dict = getPara("dict");
        JsonHashMap jhm = new JsonHashMap();
        try {
            List<Record> list= DictionaryConstants.DICT_RECORD_LIST.get(dict);
            for (Record r:list){
                r.remove("id","parent_id","sort","state_color");
            }
            Record all = new Record();
            all.set("value", "");
            all.set("name", "请选择");
            list.add(0, all);
            jhm.putSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putError(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 只返回数据库中的字典值
     */
    public void getDict() {
        String dict = getPara("dict");
        JsonHashMap jhm = new JsonHashMap();
        try {
            List<Record> list= DictionaryConstants.DICT_RECORD_LIST.get(dict);
            for (Record r:list){
                r.remove("id","parent_id","sort","state_color");
            }
            jhm.putSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putError(e.getMessage());
        }
        renderJson(jhm);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {

    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    @Override
    public void createRecordBeforeSelect(Record record) {

    }
}
