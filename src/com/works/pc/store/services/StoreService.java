package com.works.pc.store.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.sys.services.AddressService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 该类实现以下功能：
 * 1.单个门店信息的增改查
 * 2.分页查询门店列表
 * @author CaryZ
 * @date 2018-11-07
 */
@Before(Tx.class)
public class StoreService extends BaseService {

    private static final String TABLENAME="s_store";
    private static String[] columnNameArr = {"id","create_id","address_id","name","pinyin","city","address","phone","sort","updatedate","state","color","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","",""};

    public StoreService() {
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
     * 给新增的门店分配颜色
     * @author CaryZ
     * @date 2018-11-08
     * @param record 新增的门店信息
     * @return 分配成功/失败 返回true/false
     * @throws PcException
     */
    public boolean allocateColor(Record record) throws PcException{
        Record cRecord= Db.findFirst("SELECT color FROM s_common_color WHERE state='0' ORDER BY sort ASC");
        String color=cRecord.getStr("color");
        record.set("color",color);
        try{
            int flag=Db.update("UPDATE s_common_color SET state='1' WHERE color=?",color);
            return flag==0? false:true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new PcException(UPDATE_EXCEPTION, e.getMessage());
        }
    }

    /**
     * 该方法逻辑如下：
     * 将门店信息的省,市,详细地址（不包含省市）合起来存到address中
     * 根据address详细地址（包含省市）在地址表查询是否已存在地址信息
     * 若存在，则不在地址表中添加，若不存在，则在地址表中添加
     * 将地址id填到门店的address_id,最后再新增门店信息
     * @author CaryZ
     * @date 2018-11-07
     * @param record 新增的门店信息
     * @return 新增成功/失败 返回record的id/null
     * @throws PcException
     */
    @Override
    public String add(Record record) throws PcException {
        AddressService addressService=super.enhance(AddressService.class);
        allocateColor(record);
        record.set("sort",getCurrentSort());
        if (!addressService.isExist(record)){
            return null;
        }
        record.remove("province");
        return super.add(record);
    }

    /**
     * 通过id联合查询s_address、s_store表得到门店信息
     * 联合查询的目的是将门店所在的省份province查询出来
     * 将包含省市的address中的省市信息去掉
     * @author CaryZ
     * @date 2018-11-07
     * @param id 记录id
     * @return 返回一条门店记录
     */
    @Override
    public Record findById(String id){
        AddressService addressService=super.enhance(AddressService.class);
        return addressService.queryMessage(id,TABLENAME,true);
    }


    /**
     * 通过id修改门店信息，并更新地址表的记录信息
     * @author CaryZ
     * @date 2018-11-07
     * @param record 要修改成的数据
     * @return 修改成功返回true，否则修改false
     * @throws PcException
     */
    @Override
    public boolean updateById(Record record) throws PcException{
        AddressService addressService=super.enhance(AddressService.class);
        String id=record.getStr("id");
        Record oldStore=super.findById(id);
        if (!addressService.updateMessage(record,oldStore,true)){
            return false;
        };
        record.set("updatedate",DateUtil.GetDateTime());
        return super.updateById(record);
    }

    /**
     * 分页查询门店列表，关联查询地址表得到省份
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
     * 该方法实现查询门店表中所有门店
     * @author CaryZ
     * @date 2018-11-09
     */
    public List<Record> queryStores(){
        List<Record> list= Db.find("SELECT name,id AS value FROM "+TABLENAME+" ORDER BY sort ASC");
        return list;
    }
}
