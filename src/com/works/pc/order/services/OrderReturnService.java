package com.works.pc.order.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bean.TableBean;
import com.common.service.BaseService;
import com.exception.PcException;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.DateUtil;
import com.works.pc.store.services.StoreStockService;
import com.works.pc.warehouse.services.WarehouseStockService;

import java.util.List;

/**
 * @author CaryZ
 * @date 2018-11-23
 * 实现门店退货流程
 * 1.小程序提交退货单
 * 2.pc端店长查看退货单（图片传全路径）
 * 3.PC端店长可以撤销退货单，前提在物流没有接收退货单基础上
 * 4.物流接收退货单，可以仿照原系统。修改订单状态，减少门店库存。
 * 5.物流退回退货单，修改订单状态，增加门店库存。
 * 6.物流完成退货单，修改订单状态、增加物流仓库库存。
 */
@Before(Tx.class)
public class OrderReturnService extends BaseService {

    private static final String TABLENAME="s_order_return";
    private static String[] orderReturnState={"unaccepted","accepted","finished","revoked","returned"};
    private static String[] columnNameArr = {"id","store_id","num","order_date","arrive_date","order_item","store_color","order_state","create_id","create_date","logistics_id","logistics_date","order_type","close_date","close_reason","close_id","city","remark","image","return_reason"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","","","","","",""};

    public OrderReturnService() {
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
            r.set("order_item", JSONObject.parseObject(r.getStr("order_item")));
        }
        return page;
     }

    /**
     * 1.order_item(JSON格式) 里存了item---JSON数组
     * 2.加上当前日期
     * @param record 要修改成的数据
     * @return
     */
     @Override
     public boolean updateById(Record record) throws PcException{
         record.set("order_item",JSON.toJSONString(record.get("order_item")));
         record.set("logistics_date", DateUtil.GetDateTime());
         return super.updateById(record);
     }

    /**
     * 物流接收退货单
     * 退货单状态：未接收->已接收
     * 接收后每个原料信息多了warehouse_stock_id，修改订单状态，减少门店库存。
     * @param record 退货单信息（order_item从门店库存记录里获取，每个元素组成：material_data+仓库库存记录warehouse_stock_id+门店库存记录store_stock_id+现有数量quantity（出库单位）+要退的数量current_quantity（出库单位））
     * @return
     */
    public boolean acceptReturnItems(Record record) throws PcException{
        StoreStockService storeStockService=enhance(StoreStockService.class);
        JSONObject jsonObject=record.get("order_item");
        JSONArray jsonArray=jsonObject.getJSONArray("item");
        record.set("order_item",JSON.toJSONString(jsonObject));
        record.set("order_state",orderReturnState[1]);
        record.set("logistics_date", DateUtil.GetDateTime());
        if (!super.updateById(record)){
            return false;
        }
        return storeStockService.batchUpdate(jsonArray,false);
    }

    /**
     * 物流完成退货单
     * 退货单状态：已接收->已完成
     * 修改订单状态、修改物流仓库库存。
     * @param record
     * @return
     */
    public boolean finishOrder(Record record) throws PcException{
        WarehouseStockService warehouseStockService=enhance(WarehouseStockService.class);
        record.set("arrive_date",DateUtil.GetDate());
        record.set("order_state",orderReturnState[2]);
        if (!this.updateById(record)){
            return false;
        }
        return warehouseStockService.updateStockAfterReturn(record.get("order_item"),"warehouse_stock_id",true);
    }

    /**
     * 店长撤销退货单
     * 退货单状态：已撤销
     * 修改订单状态
     * @param record
     * @return
     * @throws PcException
     */
    public boolean revokeOrder(Record record) throws PcException{
        record.set("order_state",orderReturnState[3]);
        return this.updateById(record);
    }

    /**
     * 物流退回退货单
     * 退货单状态：已退回
     * 修改订单状态，增加门店库存。
     * @param record
     * @return
     * @throws PcException
     */
    public boolean returnOrder(Record record) throws PcException{
        StoreStockService storeStockService=enhance(StoreStockService.class);
        JSONObject jsonObject=record.get("order_item");
        JSONArray jsonArray=jsonObject.getJSONArray("item");
        record.set("order_item",JSON.toJSONString(jsonObject));
        record.set("order_state",orderReturnState[4]);
        record.set("logistics_date", DateUtil.GetDateTime());
        if (!super.updateById(record)){
            return false;
        }
        return storeStockService.batchUpdate(jsonArray,true);
    }

}
