package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.BeanUtils;
import com.utils.HanyuPinyinHelper;
import com.works.pc.goods.services.MaterialService;
import com.works.pc.goods.services.ProductService;
import com.works.pc.purchase.services.PurchaseOrderService;
import com.works.pc.store.services.StoreStockService;
import com.works.pc.supplier.services.SupplierService;
import com.works.pc.warehourse.services.WarehouseStockService;

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
        root.set("showChildren","false");
        List<Record> list=super.list(typeRecord);
        if (list!=null&&list.size()>0){
            for (Record record:list){
                record.set("showChildren","false");
                String search_text = record.getStr("name") + "-" + HanyuPinyinHelper.getFirstLettersLo(record.getStr("name"));
                record.set("serach_text", search_text);
            }
        }
        BeanUtils.createTree(root,list);
        return root;
    }

    @Override
    public String add(Record record) throws PcException{
        record.set("sort",getCurrentSort("WHERE parent_id='"+record.getStr("parent_id")+"'")+10);
        return super.add(record);
    }

    /**
     * 获取分类的所有二级记录
     * @param type
     * @return
     */
    public List<Record> getLevel2CatalogList(String type) throws PcException{
        String sql = "select * from s_catalog where parent_id<>? and type=? order by sort";
        List<Record> result = Db.find(sql, "0", type);
        return result;
    }

    /**
     * 通过id删除catalog
     * @param id
     * @return 删除数量
     * @throws PcException
     */
    public int deleteById(String id) throws PcException {
        String tableName = getCannotRecordList(id);
        if(tableName == null){
            return super.delete(id);
        }else{
            throw new PcException(APP_DELETE_EXCEPTION, "该分类被" + tableName + "占用，不能删除！");
        }
    }

    /**
     * 获取不能删除的分类数据
     * 判断以下表中数据是否用到了当前分类或者当前分类下是否仍有子类/商品原料
     * 1.分类表
     * 2.原料表
     * 3.商品表
     * @param id 分类id
     * @return
     */
    private String getCannotRecordList(String id){
        CatalogService catalogService=enhance(CatalogService.class);
        MaterialService materialService=enhance(MaterialService.class);
        ProductService productService=enhance(ProductService.class);
        List<Record> catalogList=catalogService.selectByColumnIn("parent_id",id);
        if(catalogList != null && catalogList.size() > 0){
            return "分类";
        }
        List<Record> materialList=materialService.selectByColumnIn("catalog_id",id);
        if(materialList != null && materialList.size() > 0){
            return "原料";
        }
        List<Record> productList=productService.selectByColumnIn("catalog_id",id);
        if(productList != null && productList.size() > 0){
            return "商品";
        }
        return null;
    }
}
