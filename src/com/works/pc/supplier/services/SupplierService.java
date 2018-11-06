package com.works.pc.supplier.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.utils.StringUtil;
import com.utils.UUIDTool;
import com.works.pc.sys.services.AddressService;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 该类实现以下功能：
 * 1.单个供应商信息的增改查
 * 2.分页查询供应商列表
 * @author CaryZ
 */
@Before(Tx.class)
public class SupplierService extends BaseService {
    AddressService addressService=super.enhance(AddressService.class);

    private static String[] columnNameArr = {"id","address_id","num","pinyin","name","city","address","material_items","material_ids","state","update_date","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","DATETIME","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","",""};

    public SupplierService() {
        super("s_supplier", new TableBean("s_supplier", columnNameArr, columnTypeArr, columnCommentArr));
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
     * 该方法逻辑如下：
     * 将供应商信息的省,市,详细地址（不包含省市）合起来存到address中
     * 根据address详细地址（包含省市）在地址表查询是否已存在地址信息
     * 若存在，则不在地址表中添加，若不存在，则在地址表中添加
     * 将地址id填到供应商的address_id,最后再新增供应商信息
     * @author CaryZ
     * @date 2018-11-04
     * @param record 新增的供应商信息
     * @return 新增成功返回true，否则返回false
     * @throws PcException
     */
    @Override
    public boolean add(Record record) throws PcException {
        String city=record.getStr("city");
        String province=record.getStr("province");
        //不含省市
        String rawAddress=record.getStr("address");

        //包含省市
        String address=province+city+rawAddress;
        record.set("address",address);
        Record aRecord= Db.findFirst("SELECT id FROM s_address WHERE address=?",address);
        String addressId;
        if (aRecord==null){
            Record newAddress=new Record();
            newAddress.set("city",city);
            newAddress.set("province",province);
            newAddress.set("address",address);
            newAddress.set("state",1);
            if (!addressService.add(newAddress)){
                return false;
            }
            addressId=newAddress.getStr("id");
        }else {
            addressId=aRecord.getStr("id");
        }


        record.set("address_id",addressId);
        record.set("pinyin", HanyuPinyinHelper.getPinyinString(record.getStr("name")));
        record.set("state",1);
        record.set("update_date", DateUtil.GetDateTime());
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
        Record record=Db.findFirst("SELECT s.*,a.province FROM s_supplier s,s_address a WHERE s.address_id=a.id AND s.id=?",id);
        if (record==null){
            return null;
        }
        String rawAddress=record.getStr("address");
        String address=StringUtils.substringAfter(rawAddress,"市");
        record.set("address",address);
        return record;
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
        String id=record.getStr("id");
        Record oldSupplier=super.findById(id);
        String oldAddress=oldSupplier.getStr("address");
        String oldName=oldSupplier.getStr("name");

        String addressId;
        String state=record.getStr("state");
        String city=record.getStr("city");
        String province=record.getStr("province");
        String name=record.getStr("name");
        //不含省市
        String rawAddress=record.getStr("address");
        //包含省市
        String address=province+city+rawAddress;
        //当地址有变动时
        if (!StringUtils.equals(address,oldAddress)){
            record.set("city",city);
            //查询地址表中有无该地址信息
            Record aRecord= Db.findFirst("SELECT id FROM s_address WHERE address=?",address);
            //地址表中无该地址
            if (aRecord==null){
                Record newAddress=new Record();
                newAddress.set("city",city);
                newAddress.set("province",province);
                newAddress.set("address",address);
                newAddress.set("state",state);
                if (!addressService.add(newAddress)){
                    return false;
                }
                addressId=newAddress.getStr("id");
            }else {
                addressId=aRecord.getStr("id");
            }
            record.set("address_id",addressId);
        }
        if (!StringUtils.equals(name,oldName)){
            record.set("pinyin",HanyuPinyinHelper.getPinyinString(name));
        }
        record.set("address",address);
        record.set("update_date",DateUtil.GetDateTime());
        record.remove("province");
        return super.updateById(record);
    }
}
