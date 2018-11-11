package com.works.pc.warehourse.controllers;

import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.utils.JsonHashMap;
import com.works.pc.warehourse.services.WarehouseService;
import org.apache.commons.lang.StringUtils;

import static com.constants.DictionaryConstants.STATE;
import static com.constants.DictionaryConstants.WAREHOUSE_TYPE;

/**
 * 该类实现以下功能：
 * 1.单个仓库信息的增删改查
 * 2.分页查询仓库信息列表
 * @author CaryZ
 * @date 2018-11-06
 */
public class WarehouseCtrl extends BaseCtrl<WarehouseService> {

    private static final String FIELD_NUM="t.num";
    private static final String FIELD_PINYIN="t.pinyin";
    private static final String FIELD_NAME="t.name";
    private static final String FIELD_STATE="t.state";

    public WarehouseCtrl() {
        super(WarehouseService.class);
    }

    /**
     * 根据目标Record的state、type字段值(value)读取字典表得到state_text、type_text(name)
     * 根据city/province复制一份city_text/province_text
     * @author CaryZ
     * @date 2018-11-06
     * @param record 目标record
     */
    @Override
    public void handleRecord(Record record) {
        String state=record.getStr("state");
        if (StringUtils.isEmpty(state)){
            return;
        }
        Record r1= DictionaryConstants.DICT_RECORD_MAP.get(STATE).get(state);
        record.set("state_text",r1.getStr("name"));
        String type=record.getStr("type");
        if (StringUtils.isEmpty(state)){
            return;
        }
        Record r2= DictionaryConstants.DICT_RECORD_MAP.get(WAREHOUSE_TYPE).get(type);
        record.set("type_text",r2.getStr("name"));
        record.set("city_text",record.getStr("city"));
        record.set("province_text",record.getStr("province"));
    }

    @Override
    public void handleAddRecord(Record record) {
        record.set("state","1");
        record.set("update_date", DateUtil.GetDateTime());
        record.set("pinyin", HanyuPinyinHelper.getPinyinString(record.getStr("name")));
    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    /**
     * list/query方法支持如下查询：
     * 模糊查询：仓库编号、名称、拼音
     * 完全匹配查询：仓库状态、仓库类型、所在城市
     * list/query方法排序依据（有先后顺序）：
     * 仓库状态DESC、仓库编号ASC
     * @author CaryZ
     * @date 2018-11-06
     * @param record 查询条件
     */
    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword=record.getStr("keyword");
        if (StringUtils.isNotEmpty(keyword)){
            String []keywords=new String[]{keyword,keyword,keyword};
            record.set("$all$and#"+FIELD_NUM+"$like$or#"+FIELD_PINYIN+"$like$or#"+FIELD_NAME+"$like$or",keywords);
            record.remove("keyword");
        }
        record.set("$sort"," ORDER BY "+FIELD_STATE+" DESC,"+FIELD_NUM+" ASC");
    }
}
