package com.works.pc.goods.controllers;

import com.common.controllers.BaseCtrl;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.goods.services.MaterialService;

import java.util.List;

public class MaterialCtrl extends BaseCtrl<MaterialService> {

    public MaterialCtrl() {
        super(MaterialService.class);
    }

    @Override
    public void handleRecord(Record record) {
        service.handleRecord(record);
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
        String[] catalogIds = getParaValues("catalog_ids");
        if(catalogIds != null && catalogIds.length > 0){
            String key = "$in#and#catalog_id";
            record.set(key, catalogIds);
        }
        if(keyword != null && keyword.length() > 0){
            String key = "$all$and#name$like$or#pinyin$like$or#num$like$or";
            String[] value = {keyword, keyword, keyword};
            record.set(key, value);
        }
    }

    /**
     * 通过id删除原料
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

    /**
     * 获取原材料单位
     */
    public void getMaterialUnit(){
        JsonHashMap jhm = new JsonHashMap();
        try {
            List<Record> unitList = service.getMaterialUnit();
            jhm.putSuccess(unitList);
        }catch (PcException e){
            e.printStackTrace();
            jhm.putFail(e.getMsg());
        }
        renderJson(jhm);
    }
}
