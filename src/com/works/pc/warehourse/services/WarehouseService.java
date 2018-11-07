package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.works.pc.sys.services.AddressService;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 该类实现以下功能：
 * 1.单个仓库信息的增删改查
 * 2.分页查询仓库信息列表
 * @author CaryZ
 * @date 2018-11-06
 */
public class WarehouseService extends BaseService {
    AddressService addressService=enhance(AddressService.class);

    private static String[] columnNameArr = {"id","address_id","num","name","city","state","remark","pinyin","type"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","",""};

    public WarehouseService() {
        super("s_warehouse", new TableBean("s_warehouse", columnNameArr, columnTypeArr, columnCommentArr));
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
     * 该方法实现新增仓库信息，逻辑如下：
     * 将仓库所在省,市,详细地址（不包含省市）合起来存到address中
     * 根据address详细地址（包含省市）在地址表查询是否已存在地址信息
     * 若存在，则不在地址表中添加，若不存在，则在地址表中添加
     * 将地址id填到仓库的address_id,最后再新增仓库信息
     * @author CaryZ
     * @date 2018-11-06
     * @param record 新增数据
     * @return 新增成功/失败 返回record的id/null
     * @throws PcException
     */
    @Override
    public String add(Record record) throws PcException{
        record.set("state",1);
        record.set("update_date", DateUtil.GetDateTime());
        record.set("pinyin", HanyuPinyinHelper.getPinyinString(record.getStr("name")));
        if (!addressService.isExist(record)){
            return null;
        }
        record.remove("province");
        return super.add(record);
    }

    /**
     * 通过id联合查询s_address、s_warehouse表得到康库信息
     * 联合查询的目的是将仓库所在的省份,城市查询出来
     * 将包含省市的address中的省市信息去掉
     * @author CaryZ
     * @date 2018-11-06
     * @param id 记录id
     * @return 返回一条仓库信息记录
     */
    @Override
    public Record findById(String id){
        return addressService.queryMessage(id,"s_warehouse",false);
    }

    /**
     * 通过id修改仓库信息，并更新地址表的记录信息
     * @author CaryZ
     * @date 2018-11-06
     * @param record 要修改成的数据
     * @return 修改成功返回true，否则修改false
     * @throws PcException
     */
    @Override
    public boolean updateById(Record record) throws PcException{
        String id=record.getStr("id");
        Record oldWarehouse=this.findById(id);
        String oldAddress=oldWarehouse.getStr("address");
        String oldName=oldWarehouse.getStr("name");
        String oldState=oldWarehouse.getStr("state");
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
            if (!addressService.isExist(record)){
                return false;
            }
        }
        if (!StringUtils.equals(name,oldName)){
            record.set("pinyin",HanyuPinyinHelper.getPinyinString(name));
        }
        record.remove("province");
        return super.updateById(record);
    }


}
