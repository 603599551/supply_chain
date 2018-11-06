package com.works.pc.sys.controllers;

import com.common.controllers.BaseCtrl;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.works.pc.sys.services.CatalogService;

import java.util.List;

/**
 * 该类实现以下功能：
 * 1.分类的增删改查
 * 2.生成商品/原料分类树
 * @author CaryZ
 */
public class CatalogCtrl extends BaseCtrl<CatalogService> {

    public CatalogCtrl() {
        super(CatalogService.class);
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

    /**
     * 通过type(material:原料 product:商品)查询s_catelog表生成一个分类树
     * @author CaryZ
     * @date 2018-11-06
     */
    public void queryCategoryTree(){
        JsonHashMap jhm=new JsonHashMap();
        String type=getPara("type");
        try{
            Record tree=service.queryCategoryTree(type);
            if (tree==null){
                jhm.putFail("查询失败！");
            }else {
                jhm.putSuccess(tree);
            }
        }catch (PcException e){
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }
}
