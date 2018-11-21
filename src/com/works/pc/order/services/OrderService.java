package com.works.pc.order.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.service.BaseService;
import com.bean.TableBean;
import com.common.service.OrderNumberGenerator;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.UUIDTool;
import com.utils.UserSessionUtil;
import com.works.pc.goods.services.ProductService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService extends BaseService {

    private static String[] columnNameArr = {"id","store_id","num","order_date","arrive_date","order_item","store_color","order_state","create_id","create_date","logistics_id","logistics_date","accept_id","accept_date","order_type","sorting_id","sorting_date","close_date","close_reason","close_id","city","remark"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","","","","","","","","","","","",""};

    private static final String TO_BE_CONFIRMED = "to_be_confirmed";
    private static final String TO_BE_RECEIVED = "to_be_received";
    private static final String STORE_REVOCATION = "store_revocation";
    private static final String LOGISTICS_OVERRULE = "logistics_overrule";
    private static final String TO_BE_OUT_STORAGE = "to_be_out_storage";
    private static final String TO_BE_SORTING = "to_be_sorting";
    private static final String SORTING_FINISH = "sorting_finish";
    private static final String FINISH = "finish";

    public OrderService() {
        super("s_order", new TableBean("s_order", columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return page;
     }

    public void createOrder(JSONObject json, UserSessionUtil usu) {
        ProductService productService = enhance(ProductService.class);
        String id = UUIDTool.getUUID();
        String storeId = usu.getUserBean().get("store_id").toString();
        String store_color = usu.getUserBean().get("store_id").toString();
        String num = OrderNumberGenerator.getStoreOrderNumber();
        String orderDate = json.getString("order_date");
        String arriveDate = json.getString("arrive_date");
        String createId = usu.getSysUserId();
        String createDate = DateUtil.GetDateTime();
        String orderState = TO_BE_CONFIRMED;
        Map<String, Object> orderItemMap = new HashMap<>();
        JSONArray items = json.getJSONArray("items");
        try {
            if(items != null && json.size() > 0){
                List<String> productIdList = new ArrayList<>();
                for(int i = 0; i < items.size(); i++){
                    JSONObject obj = items.getJSONObject(i);
                    productIdList.add(obj.getString("id"));
                }
                Record productSelect = new Record();
                String key = "$in#and#id";
                productSelect.set(key, productIdList.toArray());
                List<Record> productList = productService.list(productSelect);

            }else{

            }
        } catch (PcException e) {
            e.printStackTrace();
        }
    }
}
