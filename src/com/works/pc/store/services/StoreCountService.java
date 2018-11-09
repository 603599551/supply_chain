package com.works.pc.store.services;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.exception.PcException;
import com.utils.BeanUtils;
import com.works.pc.goods.services.MaterialService;
//import net.sf.json.JSONArray;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类实现新增门店盘点信息功能
 * 支持按照门店ID、盘点日期完全匹配查询
 * @author CaryZ
 * @date 2018-11-08
 */
@Before(Tx.class)
public class StoreCountService extends BaseService {
    MaterialService materialService=super.enhance(MaterialService.class);
    StoreStockService storeStockService=super.enhance(StoreStockService.class);

    private static final String TABLENAME="s_store_count";
    private static String[] columnNameArr = {"id","store_id","num","count_date","remark","count_item","store_color","count_id","sort"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","","","","","",""};

    public StoreCountService() {
        super(TABLENAME, new TableBean(TABLENAME, columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return page;
     }

    /**
     * 实现新增门店盘点单据功能，根据盘点项更新门店库存表
     * 前端传盘点备注remark，盘点项JSON数组（原料id，2数量，1remark）,sort排序（最后要删掉该备注）
     * 根据原料id对盘点项JSON数组里的元素补充原料信息
     * 盘点项JSON格式如下：
     * {
     *     "count_item":[
     *              {
     *                  "id":"原料id",
     *                  "beforeQuantity":"盘点项之前的数量",
     *                  "currentQuantity":"盘点项现在的数量",
     *                  "itemRemark":"盘点项的备注"
     *              },
     *              {
     *                  "id":"原料id",
     *                  "beforeQuantity":"盘点项之前的数量",
     *                  "currentQuantity":"盘点项现在的数量",
     *                  "itemRemark":"盘点项的备注"
     *              }
     *          ]
     * }
     * @param record 新增数据
     * @return 新增成功返回record的id，否则返回null
     * @throws PcException
     */
    public String add(Record record,JSONArray countItems)throws PcException{
        storeStockService.batchHandle(record,countItems);
        //将原料信息插入到相应的JSON元素中
        int countLen=countItems.size();
        List<Record> materialList=materialService.queryMaterials(countItems);
        int materialLen=materialList.size();
        Map materialMap=new HashMap(materialLen);
        //materialList转map key存id，value存JSONObject
        for (int j=0;j<materialLen;j++){
            materialMap.put(materialList.get(j).getStr("id"),countItems.getJSONObject(j));
        }
        JSONObject job;
        for (int i=0;i<countLen;i++){
            job=countItems.getJSONObject(i);
            if (materialMap.get(job.getString("id"))!=null){
                materialList.get(i).setColumns(job);
                break;
            }
        }
        JSONArray jsonArray=JSONArray.parseArray(JSONArray.toJSONString(materialList));
        Map map=new HashMap(1);
        map.put("items",jsonArray);
        record.set("sort",super.getCurrentSort()+1);
        record.set("count_item",JSON.toJSONString(map));
        record.remove("state");
        return super.add(record);
    }
}
