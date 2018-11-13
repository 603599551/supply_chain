package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.BeanUtils;

import java.util.List;

/**
 * 该类实现以下功能：
 * 1.分类的增删改查
 * 2.生成商品/原料分类树
 * @author CaryZ
 */
@Before(Tx.class)
public class CatalogService extends BaseService {

    private static final String TABLENAME="s_catalog";
    private static String[] columnNameArr = {"id","num","sort","remark","name","parent_id","type"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","INT","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","区分原料和商品类型"};

    public CatalogService() {
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
     * 通过type查询s_catelog表生成一个分类树
     * @author CaryZ
     * @date 2018-11-06
     * @param type 区别原料和商品类型 material:原料 product:商品
     * @return record 分类树
     */
    public Record queryCategoryTree(String type) throws PcException{
        Record typeRecord=new Record();
        typeRecord.set("type",type);
        Record root=new Record();
        root.set("id","0");
        //前端创建树需要showChildren字段，用于判断是否展开显示子节点
        List<Record> catalogList = super.list(typeRecord);
        if(catalogList != null && catalogList.size() > 0){
            for(Record r : catalogList){
                r.set("showChildren", false);
            }
        }
        BeanUtils.createTree(root,catalogList);
        return root;
    }

    @Override
    public String add(Record record) throws PcException{
        record.set("sort",getCurrentSort("WHERE parent_id='"+record.getStr("parent_id")+"'")+10);
        return super.add(record);
    }

}
