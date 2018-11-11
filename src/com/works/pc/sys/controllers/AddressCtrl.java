package com.works.pc.sys.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.utils.StringUtil;
import com.works.pc.sys.services.AddressService;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 该类实现以下功能：
 * 1.地址表信息的增删改查
 * 2.查询地址表中所有城市/省份(不含重复)
 * @author CaryZ
 */
public class AddressCtrl extends BaseCtrl<AddressService> {

    public AddressCtrl() {
        super(AddressService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {
        record.set("address",record.getStr("province")+record.getStr("city")+record.getStr("address"));
        record.set("state","1");
    }

    @Override
    public void handleUpdateRecord(Record record) {
        record.set("address",record.getStr("province")+record.getStr("city")+record.getStr("address"));
    }

    /**
     * list/query方法支持如下查询：
     * 模糊查询：详细地址（包含省市）
     * 完全匹配查询：省份、城市、状态
     * list/query方法排序依据（有先后顺序）：
     * 地址状态DESC、详细地址ASC
     * @author CaryZ
     * @date 2018-11-11
     * @param record 查询条件
     */
    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword};
            record.set("$all$and#address$like$or#",keywords);
        }
        record.set("$sort"," ORDER BY state DESC,address ASC");
    }

    /**
     * 该方法实现查询地址表中所有城市或者省份，去除重复功能
     * 可应用在所有门店、仓库所在城市或者省份的下拉列表
     * @author CaryZ
     * @date 2018-11-04
     */
    public void queryCityOrProvince(){
        JsonHashMap jhm=new JsonHashMap();
        String keyword=getPara("keyword");
        jhm.putSuccess(service.queryCityOrProvince(keyword));
        renderJson(jhm);
    }
}
