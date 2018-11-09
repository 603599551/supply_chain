package com.works.pc.sys.services;

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
import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 * 该类实现以下功能：
 * 1.单个地址信息的增改查
 * 2.查询地址表中所有城市/省份(不含重复)
 * @author CaryZ
 */

@Before(Tx.class)
public class AddressService extends BaseService {

    private static final String TABLENAME="s_address";
    private static String[] columnNameArr = {"id","city","province","address","state"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","",""};

    public AddressService() {
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
     * 该方法实现查询地址表中所有城市或者省份，去除重复功能
     * 可应用在所有门店、仓库所在城市或者省份的下拉列表
     * @author CaryZ
     * @date 2018-11-04
     * @param keyword 查询依据的字段名 city/province
     * @return 返回集合 所有城市或者省份 去除重复
     */
    public List<Record> queryCityOrProvince(String keyword){
            List<Record> list= Db.find("SELECT DISTINCT " +keyword+ " AS name,"+keyword+" AS value FROM "+TABLENAME+" ORDER BY "+keyword+" ASC");
            return list;
    }

    /**
     * 该方法逻辑如下：
     * 将供应商/仓库/门店信息的省,市,详细地址（不包含省市）合起来存到address中
     * 根据address详细地址（包含省市）在地址表查询是否已存在地址信息
     * 若存在，则不在地址表中添加，若不存在，则在地址表中添加
     * 将地址id填到供应商/仓库/门店的address_id
     * @author CaryZ
     * @date 2018-11-06
     * @param record 新增/修改的供应商/仓库/门店信息
     * @return 运行成功/失败 返true/false
     */
    public boolean isExist(Record record) throws PcException{
        String state=record.getStr("state");
        //包含省市
        String address=record.getStr("address");
        Record aRecord= Db.findFirst("SELECT id FROM "+TABLENAME+" WHERE address=?",address);
        String addressId;
        if (aRecord==null){
            Record newAddress=new Record();
            newAddress.set("city",record.getStr("city"));
            newAddress.set("province",record.getStr("province"));
            newAddress.set("address",address);
            newAddress.set("state",state);
            if (this.add(newAddress) == null){
                return false;
            }
            addressId=newAddress.getStr("id");
        }else {
            addressId=aRecord.getStr("id");
            aRecord.set("state",state);
            if (!this.updateById(aRecord)){
                return false;
            }
        }
        record.set("address_id",addressId);
        return true;
    }

    /**
     * 通过id判断地址是否有变动，若有变则更新地址表的记录信息
     * @author CaryZ
     * @date 2018-11-07
     * @param record 要修改成的数据
     * @param oldItem 原数据
     * @param isExist 表中是否存在address
     * @return 运行成功返回true，否则false
     * @throws PcException
     */
    public boolean updateMessage(Record record,Record oldItem,boolean isExist) throws PcException{
        String oldAddress=oldItem.getStr("address");
        String oldName=oldItem.getStr("name");
        String oldState=oldItem.getStr("state");
        String name=record.getStr("name");
        String state=record.getStr("state");
        String city=record.getStr("city");
        String province=record.getStr("province");
        //不含省市
        String rawAddress=record.getStr("address");
        //包含省市
        String address=province+city+rawAddress;
        //当地址有变动或状态改变时
        if (!StringUtils.equals(address,oldAddress)||!StringUtils.equals(state,oldState)){
            record.set("city",city);
            if (!this.isExist(record)){
                return false;
            }
        }
        if (!StringUtils.equals(name,oldName)){
            record.set("pinyin", HanyuPinyinHelper.getPinyinString(name));
        }
        if(isExist){
            record.set("address",address);
        }
        record.remove("province");
        return true;
    }

    /**
     * 通过id联合查询地址表和供应商/仓库/门店表得到一条信息
     * 联合查询的目的是将供应商/仓库/门店所在的省份province查询出来
     * 将包含省市的address中的省市信息去掉
     * @author CaryZ
     * @date 2018-11-06
     * @param id 记录id
     * @param tableName 供应商/仓库/门店表
     * @param isExist 供应商/仓库/门店表中是否存在address字段
     * @return 返回一条供应商/仓库/门店信息
     */
    public Record queryMessage(String id,String tableName,boolean isExist){
        String sql;
        if (isExist){
            sql="SELECT s.*,a.province FROM "+tableName+" s,"+TABLENAME+" a WHERE s.address_id=a.id AND s.id=?";
        }else {
            sql="SELECT s.*,a.province,a.address FROM "+tableName+" s,"+TABLENAME+" a WHERE s.address_id=a.id AND s.id=?";
        }
        Record record=Db.findFirst(sql,id);
        if (record==null){
            return null;
        }
        String rawAddress=record.getStr("address");
        String address= StringUtils.substringAfter(rawAddress,"市");
        record.set("address",address);
        return record;
    }
}
