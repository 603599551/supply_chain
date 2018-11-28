package com.constants;

import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryConstants {

    //启用停用状态
    public static final String STATE = "state";
    //原料商品类型
    public static final String TYPE = "type";
    //仓库类型
    public static final String WAREHOUSE_TYPE = "warehouse_type";
    //储存条件
    public static final String STORAGE_CONDITION = "storage_condition";
    //保质期单位
    public static final String SHELF_LIFE_UNIT = "shelf_life_unit";
    //订单类型
    public static final String ORDER_TYPE = "order_type";
    //采购类型
    public static final String PURCHASE_TYPE = "purchase_type";
    //采购单状态
    public static final String PURCHASE_ORDER_TYPE = "purchase_order_type";
    //采购流程状态
    public static final String PURCHASE_PROCESS_TYPE = "purchase_process_type";
    //商品类型
    public static final String PRODUCT_TYPE = "product_type";
    //门店退货单状态
    public static final String STORE_RETURN_TYPE="store_return_type";
    //采购退货流程状态
    public static final String PURCHASE_RETURN_TYPE = "purchase_return_type";
    //门店废弃单类型
    public static final String STORE_SCRAP_TYPE = "store_scrap_type";
    //门店废弃单状态
    public static final String STORE_SCRAP_STATE = "store_scrap_state";
    //流程类型
    public static final String PROCESS_TYPE = "process_type";



    /**
     * dictionary表中的常量，tomcat启动时将字典值读取到内存中
     * 格式：
     *      大分类dictionary表的value--》设为a
     *      具体字典值dictionary表的value--》设为b
     *      具体字典值dictionary表的name--》设为c
     *      Map<a, Map<b, c>>
     * 例如：
     *      性别
     *      id   parent_id  name   value     sort
     *      1	    0	    性别	   gender	 100
     *      2	    1	    男	     1	     120
     *      3	    1	    女	     0	     130
     *      Map<gender, Map<1, 男>>
     *      Map<gender, Map<0, 女>>
     *
     */
    public static final Map<String, Map<String, String>> DICT_STRING_MAP = new HashMap<>();

    /**
     * dictionary表中的常量，tomcat启动时将字典值读取到内存中
     * 格式：
     *      大分类dictionary表的value--》设为a
     *      具体字典值dictionary表的value--》设为b
     *      具体字典值dictionary表的Record--》设为c
     *      Map<a, Map<b, c>>
     * 例如：
     *      性别
     *      id   parent_id  name   value     sort
     *      1	    0	    性别	   gender	 100
     *      2	    1	    男	     1	     120
     *      3	    1	    女	     0	     130
     *      Map<gender, Map<1, Record>>
     *      Map<gender, Map<0, Record>>
     *
     */
    public static final Map<String, Map<String, Record>> DICT_RECORD_MAP = new HashMap<>();

    /**
     * dictionary表中的常量，tomcat启动时将字典值读取到内存中
     * 格式：
     *      大分类dictionary表的value--》设为a
     *      具体字典值dictionary表的Record--》设为b
     *      Map<a, List<b>>
     * 例如：
     *      性别
     *      id   parent_id  name   value     sort
     *      1	    0	    性别	   gender	 100
     *      2	    1	    男	     1	     120
     *      3	    1	    女	     0	     130
     *      Map<gender, List<Record>>
     *      List<Record> ：Record（男字典值的）、Record（女字典值的）
     */
    public static final Map<String, List<Record>> DICT_RECORD_LIST = new HashMap<>();

}
