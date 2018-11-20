package com.works.pc.supplier.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bean.TableBean;
import com.common.service.BaseService;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.DateUtil;
import com.works.pc.sys.services.AddressService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类实现以下功能：
 * 1.单个供应商信息的增改查
 * 2.分页查询供应商列表
 * @author CaryZ
 */
@Before(Tx.class)
public class SupplierService extends BaseService {

    private static final String TABLENAME="s_supplier";
    private static String[] columnNameArr = {"id","address_id","num","pinyin","name","city","address","material_items","material_ids","state","updatedate","remark","linkman","phone"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","DATETIME","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","",""};

    public SupplierService() {
        super(TABLENAME, new TableBean(TABLENAME, columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        List<Record> list=page.getList();
        for (Record r:list){
            r.set("material_items", JSONObject.parseObject(r.getStr("material_items")));
        }
        return page;
     }

    /**
     * 该方法逻辑如下：
     * 将供应商信息的省,市,详细地址（不包含省市）合起来存到address中
     * 根据address详细地址（包含省市）在地址表查询是否已存在地址信息
     * 若存在，则不在地址表中添加，若不存在，则在地址表中添加
     * 将地址id填到供应商的address_id,最后再新增供应商信息
     * @author CaryZ
     * @date 2018-11-04
     * @param record 新增的供应商信息
     * @return 新增成功/失败 返回record的id/null
     * @throws PcException
     */
    @Override
    public String add(Record record) throws PcException {
        AddressService addressService=super.enhance(AddressService.class);
        if (!addressService.isExist(record)){
            return null;
        }
        System.out.println(record.getStr("material_items"));
        JSONArray jsonArray=JSONArray.parseArray(record.getStr("material_items"));
        Map<String,JSONArray> map=new HashMap(1);
        map.put("items",jsonArray);
        record.set("material_items", JSON.toJSONString(map));
        record.remove("province");
        return super.add(record);
    }

    /**
     * 通过id联合查询s_address、s_supplier表得到供应商信息
     * 联合查询的目的是将供应商所在的省份province查询出来
     * 将包含省市的address中的省市信息去掉
     * @author CaryZ
     * @date 2018-11-05
     * @param id 记录id
     * @return 返回一条供应商记录
     */
    @Override
    public Record findById(String id){
        AddressService addressService=super.enhance(AddressService.class);
        return addressService.queryMessage(id,TABLENAME,true);
    }


    /**
     * 分页查询供应商列表，关联查询地址表得到省份
     * @param record 查询条件，key是字段名，value不为空将被作为条件查询
     * @param pageNum 页码数
     * @param pageSize 每页都少条数据
     * @return
     * @throws PcException
     */
    @Override
    public Page<Record> query(Record record, int pageNum, int pageSize) throws PcException{
        AddressService addressService=super.enhance(AddressService.class);
        return addressService.query(record,TABLENAME,pageNum,pageSize);
    }

    /**
     * 通过id修改供应商信息，并更新地址表的记录信息
     * @author CaryZ
     * @date 2018-11-05
     * @param record 要修改成的数据
     * @return 修改成功返回true，否则修改false
     * @throws PcException
     */
    @Override
    public boolean updateById(Record record) throws PcException{
        AddressService addressService=super.enhance(AddressService.class);
        String id=record.getStr("id");
        Record oldSupplier=super.findById(id);
        if (!addressService.updateMessage(record,oldSupplier,true)){
            return false;
        };
        JSONArray jsonArray=JSONArray.parseArray(record.getStr("material_items"));
        Map<String,JSONArray> map=new HashMap(1);
        map.put("items",jsonArray);
        record.set("material_items", JSON.toJSONString(map));
        record.set("updatedate",DateUtil.GetDateTime());
        return super.updateById(record);
    }

    /**
     * 通过原料ids查询供应商的material_ids是否包含该id
     * @param record 包含原料JSON数组
     * @return 能提供该原料的供应商信息，包括供应商id、名称
     */
    public void querySupplierForMaterial(Record record){
        JSONArray jsonArray=JSONArray.parseArray(record.getStr("item"));
        int len=jsonArray.size();
        String[] ids=new String[len];
        ids[0]=jsonArray.getJSONObject(0).getString("id");
        StringBuffer sql=new StringBuffer("SELECT * FROM s_supplier WHERE material_ids LIKE CONCAT('%',?,'%') ");
        for (int i=1;i<len;i++){
            ids[i]=jsonArray.getJSONObject(i).getString("id");
            sql.append(" OR material_ids LIKE CONCAT('%',?,'%')");
        }
        //获取该单所有原料的供应商，会有重复使用的情况
        List<Record> supplierList= Db.find(sql.toString(),ids);
        //遍历原料
        for (int i=0;i<len;i++){
            String materialId=jsonArray.getJSONObject(i).getString("id");
            List<Record> finalList=new ArrayList<>();
            for(Record r:supplierList){
                //存在包含关系，即该供应商能提供该原料
                if (r.getStr("material_ids").indexOf(materialId)!=-1){
                    Record supplierR=new Record();
                    supplierR.set("name",r.getStr("name"));
                    supplierR.set("value",r.getStr("id"));
                    finalList.add(supplierR);
                }
            }
            jsonArray.getJSONObject(i).put("suppliers",finalList);
        }
        record.set("item",jsonArray);
    }

    /**
     * 获取原料的价格
     * @return
     */
    public double getMaterialPrice(JSONObject jsonObject){
        return jsonObject.getDouble("current_price");
    }
}
