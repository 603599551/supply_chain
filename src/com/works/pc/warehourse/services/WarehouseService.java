package com.works.pc.warehourse.services;

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
@Before(Tx.class)
public class WarehouseService extends BaseService {
    AddressService addressService=enhance(AddressService.class);

    private static final String TABLENAME="s_warehouse";
    private static String[] columnNameArr = {"id","address_id","num","name","city","state","remark","pinyin","type"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","",""};

    public WarehouseService() {
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
        return addressService.queryMessage(id,TABLENAME,false);
    }

    /**
     * 分页查询仓库列表，关联查询地址表得到省份
     * @param record 查询条件，key是字段名，value不为空将被作为条件查询
     * @param pageNum 页码数
     * @param pageSize 每页都少条数据
     * @return
     * @throws PcException
     */
    @Override
    public Page<Record> query(Record record, int pageNum, int pageSize) throws PcException{
        return addressService.query(record,TABLENAME,pageNum,pageSize);
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
        if (!addressService.updateMessage(record,oldWarehouse,false)){
            return false;
        };
        return super.updateById(record);
    }
}
