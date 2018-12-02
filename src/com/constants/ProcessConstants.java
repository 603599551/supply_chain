package com.constants;


import java.util.HashMap;
import java.util.Map;

/**
 * 获取流程详情，主要包括流程中各个阶段的先后顺序
 * @author CaryZ
 * @date 2018-12-01
 */
public class ProcessConstants {

    //采购流程状态
    public static final String PURCHASE_PROCESS_TYPE = "purchase_process_type";
    //采购退货流程状态
    public static final String PURCHASE_RETURN_TYPE = "purchase_return_type";

    /**
     * process_config表
     * key---process_name
     * value---process_sequence
     * tomcat启动时将值读取到内存中
     */
    public static final Map<String,String[]> PROCESS_STRINGARRAY_MAP= new HashMap<>();
}
