package com.works.pc.store.controllers;

import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.store.services.StoreService;
import org.apache.commons.lang.StringUtils;

import static com.constants.DictionaryConstants.STATE;

/**
 * 该类实现以下功能：
 * 1.单个门店信息的增改查
 * 2.分页查询门店列表
 * @author CaryZ
 * @date 2018-11-07
 */
public class StoreCtrl extends BaseCtrl<StoreService> {

    UserSessionUtil usu=new UserSessionUtil(getRequest());

    private static final String FIELD_PHONE="t.phone";
    private static final String FIELD_PINYIN="t.pinyin";
    private static final String FIELD_NAME="t.name";
    private static final String FIELD_ADDRESS="t.address";
    private static final String FIELD_STATE="t.state";
    private static final String FIELD_SORT="t.sort";

    public StoreCtrl() {
        super(StoreService.class);
    }

    /**
     * 根据目标Record的state字段值(value)读取字典表得到state_text(name)
     * 根据city/province复制一份city_text/province_text
     * @author CaryZ
     * @date 2018-11-07
     * @param record 目标record
     */
    @Override
    public void handleRecord(Record record) {
        String state=record.getStr("state");
        if (StringUtils.isEmpty(state)){
            return;
        }
        Record r= DictionaryConstants.DICT_RECORD_MAP.get(STATE).get(state);
        record.set("state_text",r.getStr("name"));
        record.set("city_text",record.getStr("city"));
        record.set("province_text",record.getStr("province"));
    }

    @Override
    public void handleAddRecord(Record record) {
        record.set("state","1");
        record.set("create_id",usu.getSysUserId());
        record.set("updatedate", DateUtil.GetDateTime());
        record.set("pinyin", HanyuPinyinHelper.getPinyinString(record.getStr("name")));
        record.set("address",record.getStr("province")+record.getStr("city")+record.getStr("address"));
    }

    @Override
    public void handleUpdateRecord(Record record) {
        record.set("create_id",usu.getSysUserId());
    }

    /**
     * list/query方法支持如下查询：
     * 模糊查询：门店名称、名称拼音、联系电话、详细地址（包含省市）
     * 完全匹配查询：门店状态、所在城市
     * list/query方法排序依据（有先后顺序）：
     * 门店状态DESC、排序ASC
     * @author CaryZ
     * @date 2018-11-07
     * @param record 查询条件
     */
    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword,keyword,keyword,keyword};
            record.set("$all$and#"+FIELD_PHONE+"$like$or#"+FIELD_NAME+"$like$or#"+FIELD_PINYIN+"$like$or#"+FIELD_ADDRESS+"$like$or",keywords);
            record.remove("keyword");
        }
        record.set("$sort"," ORDER BY "+FIELD_STATE+" DESC,"+FIELD_SORT+" ASC");
    }

    /**
     * 该方法实现查询门店表中所有门店
     * @author CaryZ
     * @date 2018-11-09
     */
    public void queryStores(){
        JsonHashMap jhm=new JsonHashMap();
        jhm.putSuccess(service.queryStores());
        renderJson(jhm);
    }
}
