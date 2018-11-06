package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;


/**
 * 该类实现以下功能：
 * 1.单个地址信息的增改查
 * 2.查询地址表中所有城市/省份(不含重复)
 * @author CaryZ
 */

@Before(Tx.class)
public class AddressService extends BaseService {

    private static String[] columnNameArr = {"id","city","province","address","state"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","INT"};
    private static String[] columnCommentArr = {"","","","",""};

    public AddressService() {
        super("s_address", new TableBean("s_address", columnNameArr, columnTypeArr, columnCommentArr));
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
            List<Record> list= Db.find("SELECT DISTINCT " +keyword+ " AS name,"+keyword+" AS value FROM s_address ORDER BY "+keyword+" ASC");
            return list;
    }

}
