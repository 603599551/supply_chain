package com.common.service;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

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

    public static String[] getProcessSequence(String processName){
        Record record= Db.findFirst("SELECT process_sequence FROM s_process_config WHERE process_name=?",processName);
        return record.getStr("process_sequence").split(",");
    }

    public static String[] getPurchaseProcess(){
        return getProcessSequence(PURCHASE_PROCESS_TYPE);
    }

    public static String[] getPurchaseReturnProcess(){
        return getProcessSequence(PURCHASE_RETURN_TYPE);
    }
}
