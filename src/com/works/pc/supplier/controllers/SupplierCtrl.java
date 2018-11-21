package com.works.pc.supplier.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.BeanUtils;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.utils.JsonHashMap;
import com.works.pc.supplier.services.SupplierService;
import org.apache.commons.lang.StringUtils;

import static com.constants.DictionaryConstants.STATE;

/**
 * 该类实现以下功能：
 * 1.单个供应商信息的增改查
 * 2.分页查询供应商列表
 * @author CaryZ
 */

public class SupplierCtrl extends BaseCtrl<SupplierService> {

    private static final String FIELD_NUM="t.num";
    private static final String FIELD_PINYIN="t.pinyin";
    private static final String FIELD_NAME="t.name";
    private static final String FIELD_ADDRESS="t.address";
    private static final String FIELD_STATE="t.state";

    public SupplierCtrl() {
        super(SupplierService.class);
    }

    /**
     * 根据目标Record的state字段值(value)读取字典表得到state_text(name)
     * 根据city/province复制一份city_text/province_text
     * @author CaryZ
     * @date 2018-11-05
     * @param record 目标record
     */
    @Override
    public void handleRecord(Record record) {
        String state=record.getStr("state");
        if (StringUtils.isEmpty(state)){
            return;
        }
        Record r=DictionaryConstants.DICT_RECORD_MAP.get(STATE).get(state);
        record.set("state_text",r.getStr("name"));
        record.set("city_text",record.getStr("city"));
        record.set("province_text",record.getStr("province"));
    }

    @Override
    public void handleAddRecord(Record record) {
        record.set("state","1");
        record.set("updatedate", DateUtil.GetDateTime());
        record.set("pinyin", HanyuPinyinHelper.getPinyinString(record.getStr("name")));
        record.set("address",record.getStr("province")+record.getStr("city")+record.getStr("address"));
    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    /**
     * list/query方法支持如下查询：
     * 模糊查询：供应商编号、姓名、姓名拼音、详细地址（包含省市）
     * 完全匹配查询：供应商状态、所在城市
     * list/query方法排序依据（有先后顺序）：
     * 供应商状态DESC、供应商编号ASC
     * @author CaryZ
     * @date 2018-11-05
     * @param record 查询条件
     */
    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword,keyword,keyword,keyword};
            record.set("$all$and#"+FIELD_NUM+"$like$or#"+FIELD_PINYIN+"$like$or#"+FIELD_NAME+"$like$or#"+FIELD_ADDRESS+"$like$or",keywords);
            record.remove("keyword");
        }
        record.set("$sort"," ORDER BY "+FIELD_STATE+" DESC,"+FIELD_NUM+" ASC");
    }

    /**
     * 新增供应商信息
     * @author CaryZ
     * @date 2018-11-09
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
     * 修改供应商信息
     * @author CaryZ
     * @date 2018-11-11
     */
    @Override
    public void updateById(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        try {
            handleUpdateRecord(record);
            boolean flag = service.updateById(record);
            if(flag){
                jhm.putMessage("修改成功！");
            }else{
                jhm.putFail("修改失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 查询能提供该原料的供应商信息
     * @author CaryZ
     * @date 2018-11-13
     */
    public void querySupplierForMaterial(){
        JsonHashMap jhm = new JsonHashMap();
        JSONObject json = getJson(getRequest());
        Record record = BeanUtils.jsonToRecord(json);
        service.querySupplierForMaterial(record);
        jhm.putSuccess(record);
        renderJson(jhm);
    }
}
