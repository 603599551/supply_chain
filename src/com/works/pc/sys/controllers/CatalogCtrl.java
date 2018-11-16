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

    /**
     * 通过type(material:原料 product:商品)查询s_catelog表生成一个仿真树
     * @author CaryZ
     * @date 2018-11-15
     */
    public void createEmulationalTree(){
        JsonHashMap jhm=new JsonHashMap();
        String type=getPara("type");
        try{
            List<Record> list=service.createEmulationalTree(type);
            if (list==null){
                jhm.putFail("查询失败！");
            }else {
                jhm.putSuccess(list);
            }
        }catch (PcException e){
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 获取分类的所有二级记录
     * @author CaryZ
     * @date 2018-11-15
     */
    public void getLevel2CatalogList(){
        JsonHashMap jhm=new JsonHashMap();
        String type = getPara("type");
        try{
            List<Record> catalogList = service.getLevel2CatalogList(type);
            jhm.putSuccess(catalogList);
        }catch (PcException e){
            e.printStackTrace();
            jhm.putError(e.getMsg());
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
     * 通过id删除分类
     * @author CaryZ
     * @date 2018-11-15
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
}
