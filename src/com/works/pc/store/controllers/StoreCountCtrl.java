package com.works.pc.store.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.common.service.BaseService;
import com.common.service.OrderNumberGenerator;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.DateUtil;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.store.services.StoreCountService;
import org.apache.commons.lang.StringUtils;

import static com.common.service.OrderNumberGenerator.getStoreCountOrderNumber;

/**
 * 该类实现新增门店盘点信息功能
 * 分页查询盘点记录列表
 * @author CaryZ
 * @date 2018-11-08
 */
public class StoreCountCtrl extends BaseCtrl<StoreCountService> {

    UserSessionUtil usu=new UserSessionUtil(getRequest());

    public StoreCountCtrl() {
        super(StoreCountService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {
        record.set("num",getStoreCountOrderNumber());
        record.set("state", "1");
        record.set("count_date", DateUtil.GetDateTime());
        record.set("store_id",usu.getUserBean().get("store_id"));
        record.set("store_color", usu.getUserBean().get("store_color"));
        record.set("count_id", usu.getSysUserId());
    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    /**
     * 门店盘点记录列表
     * 模糊查询：盘点编号
     * 完全匹配查询：盘点开始时间到结束时间、门店id
     * 按照盘点日期倒序排
     * @param record 查询条件
     */
    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword};
            record.set("$all$and#num$like$or",keywords);
            record.remove("keyword");
        }
        String fromDate=record.getStr("from_date");
        String toDate=record.getStr("to_date");
        if (StringUtils.isNotEmpty(fromDate)&&StringUtils.isNotEmpty(toDate)){
            record.set("$fromto"," AND Date(count_date) BETWEEN '"+fromDate+"' AND '"+toDate+"' ");
        }
        record.set("store_id",usu.getUserBean().get("store_id"));
        record.set("$sort"," ORDER BY count_date DESC");
    }

    /**
     * 新增门店盘点
     * @author CaryZ
     * @date 2018-11-09
     */
    @Override
    public void add(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        JSONArray list=json.getJSONArray("count_item");
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleAddRecord(record);
            String id = service.add(record,list);
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
}
