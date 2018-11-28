package com.works.pc.warehouse.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.goods.services.MaterialService;
import com.works.pc.warehouse.services.WarehouseStockService;
import org.apache.commons.lang.StringUtils;

import static com.utils.NumberUtils.getMoney;

/**
 * 该类操作仓库库存表，实现新增和修改功能
 * @author CaryZ
 * @date 2018-11-10
 */
public class WarehouseStockCtrl extends BaseCtrl<WarehouseStockService> {

    UserSessionUtil usu=new UserSessionUtil(getRequest());

    private static final String FIELD_NUM="batch_num";

    public WarehouseStockCtrl() {
        super(WarehouseStockService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {
        record.set("quantity",getMoney(record.get("quantity")));
        record.set("user_id",usu.getSysUserId());
        record.set("warehouse_id",usu.getUserBean().get("warehouse_id"));
        record.set("material_data", JSON.toJSONString(record.get("material_data")));
        record.set("sort", service.getCurrentSort()+1);
        record.set("state","1");
    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    /**
     * 完全匹配查询：原料id
     * 模糊查询：原料批号
     * 按照批号正序排
     * @param record 查询条件
     */
    @Override
    public void createRecordBeforeSelect(Record record) {
        String[] materialIds=record.get("material_ids");
        if (materialIds!=null&&materialIds.length>0){
            record.set("$in#and#material_id",materialIds);
        }
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword};
            record.set("$all$and#"+FIELD_NUM+"$like$or",keywords);
            record.remove("keyword");
        }
        record.set("warehouse_id",usu.getUserBean().get("warehouse_id"));
        record.set("$sort"," ORDER BY batch_num DESC");
    }

    /**
     * 将仓库库存以树的方式体现（在盘点时用到）
     */
    public void getStockTree(){
        MaterialService materialService=enhance(MaterialService.class);
        JsonHashMap jhm = new JsonHashMap();
        Record root = materialService.getStockTree("s_warehouse_stock","warehouse_id",(String)usu.getUserBean().get("warehouse_id"));
        jhm.putSuccess(root);
        renderJson(jhm);
    }

    /**
     * 查询仓库库存原料id和名称，使用HashSet<Map>去除重复
     */
    public void queryMaterialNameIds(){
        JsonHashMap jhm = new JsonHashMap();
        Record record=new Record();
        record.set("warehouse_id",usu.getUserBean().get("warehouse_id"));
        jhm.putSuccess(service.queryMaterialNameIds(record));
        renderJson(jhm);
    }

    /**
     * 针对直接入库的情况，新增仓库库存
     * @author CaryZ
     * @date 2018-11-10
     */
    @Override
    public void add(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleAddRecord(record);
            String id = service.add(record);
            if(id != null){
                jhm.putMessage("添加成功！");
            }else{
                jhm.putFail("添加失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 通过原料ids查询批号列表
     * @author CaryZ
     * @date 2018-11-13
     */
    public void queryBatchNumList(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            service.queryBatchNumList(record);
            jhm.putSuccess(record);
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }
}
