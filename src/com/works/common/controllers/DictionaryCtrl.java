package com.works.common.controllers;

import com.common.controllers.BaseCtrl;
import com.common.service.BaseService;
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
        JsonHashMap jrd = new JsonHashMap();
        try {
            List<Record> list = Db.find("select name, value from s_dictionary where parent_id=(select id from s_dictionary where value=?) order by sort", dict);
            Record all = new Record();
            all.set("value", "-1");
            all.set("name", "全部");
            list.add(0, all);
            jrd.putSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            jrd.putError(e.getMessage());
        }
        renderJson(jrd);
    }

    /**
     * 第一项为“请选择”
     */
    public void getDictIncludeChoose() {
        String dict = getPara("dict");
        JsonHashMap jrd = new JsonHashMap();
        try {
            List<Record> list;
            //如果是消息管理查看，只显示部分数据
            if (!StringUtils.equals(dict, "notice_type")) {
                list = Db.find("select name, value from s_dictionary where parent_id=(select id from s_dictionary where value=?) order by sort", dict);
                //绩效考核的时候去掉”请选择“选项
                if (!StringUtils.equals(dict, "performance_type")) {
                    Record all = new Record();
                    all.set("value", "-1");
                    all.set("name", "请选择");
                    list.add(0, all);
                }
                //添加员工页面的在职状态只显示请选择和在职
                if(StringUtils.equals(dict,"job_type")){
                    for(int i = 1 ; i < list.size() ; ++i){
                        if(!StringUtils.equals(list.get(i).getStr("value"),"on")){
                            list.get(i).set("disabled",true);
                        }
                    }
                }
            } else {
                list = Db.find("select name, value from s_dictionary where (value = 'apply_movein' or value = 'movein_notice') and parent_id=(select id from s_dictionary where value=? )",dict);
                Record all = new Record();
                all.set("value", "-1");
                all.set("name", "请选择");
                list.add(0, all);
            }
            jrd.putSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            jrd.putError(e.getMessage());
        }
        renderJson(jrd);
    }

    /**
     * 只返回数据库中的字典值
     */
    public void getDict() {
        String dict = getPara("dict");
        JsonHashMap jrd = new JsonHashMap();
        try {
            List<Record> list = Db.find("select name, value from s_dictionary where parent_id=(select id from s_dictionary where value=?) order by sort", dict);
            jrd.putSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            jrd.putError(e.getMessage());
        }
        renderJson(jrd);
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