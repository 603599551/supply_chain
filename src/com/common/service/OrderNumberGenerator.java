package com.common.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;


/**
 * 生成订单编号的公共类   仓库采购单、仓库退货单、仓库废弃单、仓库盘点单、仓库出库单、仓库移库单、门店订单、门店废弃订单、门店退货订单、门店盘点单
 * @author CaryZ
 * @date 2018-11-08
 */
public class OrderNumberGenerator {
    /**
     * 补零长度
     */
    private static final int LENGTH=4;

    /**
     * 门店盘点单类型
     */
    private static final String TYPE_MDPD="MDPD";
    /**
     * 仓库采购单类型
     */
    private static final String TYPE_CKPD="CKPD";
    /**
     * 仓库退货单类型
     */
    private static final String TYPE_CKTH="CKTH";
    /**
     * 仓库移库单类型
     */
    private static final String TYPE_CKYK="CKYK";
    /**
     * 门店订货单类型
     */
    private static final String TYPE_MDDH="MDDH";
    /**
     * 门店退货单类型
     */
    private static final String TYPE_MDTH="MDTH";
    /**
     * 门店废弃单类型
     */
    private static final String TYPE_MDFQ="MDFQ";
    /**
     * 仓库出库单类型
     */
    private static final String TYPE_CKCK="CKCK";

    public static synchronized String getOrderNumber(String name,String remark,String type){
        String reStr="";
        String date= DateUtil.GetDate();
        Record r= Db.findFirst("SELECT * FROM s_order_number WHERE type=?",type);
        if(r!=null){//如果有记录就继续判断
            String dateInR=r.getStr("date");
            if(date.equals(dateInR)){//如果日期相同
                int number=r.getInt("number");
                number++;
                reStr=getNumber(type,date,LENGTH,number);
                Db.update("UPDATE s_order_number SET number=? WHERE type=?",number,type);
            }else{//如果数据库中的日期不是当前系统日期
                Db.update("UPDATE s_order_number SET date=?,number=? WHERE type=?",date,1,type);
                reStr=getNumber(type,date,LENGTH,1);
            }
        }else{//如果没有记录就添加记录
            Record saveR=new Record();
            saveR.set("date",date);
            saveR.set("name",name);
            saveR.set("type",type);
            saveR.set("number",1);
            saveR.set("remark",remark);

            Db.save("s_order_number",saveR);

            reStr=getNumber(type,date,LENGTH,1);
        }
        return reStr.replace("-","");
    }

    /**
     * 生成门店盘点单编号
     * @return
     */
    public static synchronized String getStoreCountOrderNumber(){
        return getOrderNumber("门店盘点单","门店盘点编号",TYPE_MDPD);
    }

    /**
     * 生成仓库采购单编号
     * @return
     */
    public static synchronized String getWarehousePurchaseOrderNumber(){
        return getOrderNumber("仓库采购单","仓库采购编号",TYPE_CKPD);
    }

    /**
     * 生成仓库退货单编号
     * @return
     */
    public static synchronized String getWarehouseReturnOrderNumber(){
        return getOrderNumber("仓库退货单","仓库退货编号",TYPE_CKTH);
    }
    /**
     * 生成仓库移库单编号
     * @return
     */
    public static synchronized String getWarehouseMoveOrderNumber(){
        return getOrderNumber("仓库移库单","仓库移库编号",TYPE_CKYK);
    }
    /**
     * 生成门店订货单编号
     * @return
     */
    public static synchronized String getStoreOrderNumber(){
        return getOrderNumber("门店订货单","门店订货编号",TYPE_MDDH);
    }

    /**
     * 生成门店退货单编号
     * @return
     */
    public static synchronized String getStoreOrderReturnNumber(){
        return getOrderNumber("门店退货单","门店退货编号",TYPE_MDTH);
    }

    /**
     * 生成门店废弃单编号
     * @return
     */
    public static synchronized String getStoreScrapReturnNumber(){
        return getOrderNumber("门店废弃单","门店废弃编号",TYPE_MDFQ);
    }

    /**
     * 生成仓库出库单编号
     * @return
     */
    public static synchronized String getWarehouseOutOrderNumber(){
        return getOrderNumber("仓库出库单","仓库出库编号",TYPE_CKCK);
    }

//    /**
//     * 仓库出库订单类型
//     */
//    private static final String TYPE_CK="CK";
//    /**
//     * 移库类型
//     */
//    private static final String TYPE_YK = "YK";
//
//    /**
//     * 门店采购订单类型
//     */
//    private static final String TYPE_CD="CD";
//    /**
//     * 门店退货单类型
//     */
//    private static final String TYPE_TD = "TD";
//    /**
//     * 门店引单退货单类型
//     */
//    private static final String TYPE_CH = "CH";
//
//    /**
//     * 送货单类型
//     */
//    private static final String TYPE_SH = "SH";
//
//
//
//    /**
//     * 生成出库单号
//     * @return
//     */
//    public synchronized String getOutWarehouseOrderNumber(){
//        String reStr="";
//        String date= DateUtil.GetDate();
//        Record r= Db.findFirst("select * from order_number where type=?",TYPE_CK);
//        if(r!=null){//如果有记录就继续判断
//            String dateInR=r.getStr("date");
//            if(date.equals(dateInR)){//如果日期相同
//                int number=r.getInt("number");
//                number++;
//                reStr=getNumber(TYPE_CK,date,LENGTH,number);
//                Db.update("update order_number set number=? where type=?",number,TYPE_CK);
//            }else{//如果数据库中的日期不是当前系统日期
//                Db.update("update order_number set date=?,number=? where type=?",date,1,TYPE_CK);
//                reStr=getNumber(TYPE_CK,date,LENGTH,1);
//            }
//        }else{//如果没有记录就添加记录
//            Record saveR=new Record();
//            saveR.set("date",date);
//            saveR.set("type",TYPE_CK);
//            saveR.set("number",1);
//            saveR.set("remark","出库订单号");
//
//            Db.save("order_number",saveR);
//
//            reStr=getNumber(TYPE_CK,date,LENGTH,1);
//        }
//        return reStr.replace("-","");
//    }
//
//    /**
//     * 生成门店订单号
//     * @return
//     */
//    public synchronized String getStoreOrderNumber(){
//        String reStr="";
//        String date= DateUtil.GetDate();
//        Record r= Db.findFirst("select * from order_number where type=?",TYPE_CD);
//        if(r!=null){//如果有记录就继续判断
//            String dateInR=r.getStr("date");
//            if(date.equals(dateInR)){//如果日期相同
//                int number=r.getInt("number");
//                number++;
//                reStr=getNumber(TYPE_CD,date,LENGTH,number);
//                Db.update("update order_number set number=? where type=?",number,TYPE_CD);
//            }else{//如果数据库中的日期不是当前系统日期
//                Db.update("update order_number set date=?,number=? where type=?",date,1,TYPE_CD);
//                reStr=getNumber(TYPE_CD,date,LENGTH,1);
//            }
//        }else{//如果没有记录就添加记录
//            Record saveR=new Record();
//            saveR.set("date",date);
//            saveR.set("type",TYPE_CD);
//            saveR.set("number",1);
//            saveR.set("remark","门店采购订单号");
//
//            Db.save("order_number",saveR);
//
//            reStr=getNumber(TYPE_CD,date,LENGTH,1);
//        }
//        return reStr.replace("-","");
//    }
//
//    /**
//     * 生成门店退货单号
//     * @return
//     */
//    public synchronized String getReturnOrderNumber(){
//        String reStr="";
//        String date= DateUtil.GetDate();
//        Record r= Db.findFirst("select * from order_number where type=?",TYPE_TD);
//        if(r!=null){//如果有记录就继续判断
//            String dateInR=r.getStr("date");
//            if(date.equals(dateInR)){//如果日期相同
//                int number=r.getInt("number");
//                number++;
//                reStr=getNumber(TYPE_TD,date,LENGTH,number);
//                Db.update("update order_number set number=? where type=?",number,TYPE_TD);
//            }else{//如果数据库中的日期不是当前系统日期
//                Db.update("update order_number set date=?,number=? where type=?",date,1,TYPE_TD);
//                reStr=getNumber(TYPE_TD,date,LENGTH,1);
//            }
//        }else{//如果没有记录就添加记录
//            Record saveR=new Record();
//            saveR.set("date",date);
//            saveR.set("type",TYPE_TD);
//            saveR.set("number",1);
//            saveR.set("remark","门店采购订单号");
//
//            Db.save("order_number",saveR);
//
//            reStr=getNumber(TYPE_TD,date,LENGTH,1);
//        }
//        return reStr.replace("-","");
//    }
//
//    /**
//     * 生成门店引单退货单号
//     * @return
//     */
//    public synchronized String getStoreScrapNumber(){
//        String reStr="";
//        String date= DateUtil.GetDate();
//        Record r= Db.findFirst("select * from order_number where type=?",TYPE_CH);
//        if(r!=null){//如果有记录就继续判断
//            String dateInR=r.getStr("date");
//            if(date.equals(dateInR)){//如果日期相同
//                int number=r.getInt("number");
//                number++;
//                reStr=getNumber(TYPE_CH,date,LENGTH,number);
//                Db.update("update order_number set number=? where type=?",number,TYPE_CH);
//            }else{//如果数据库中的日期不是当前系统日期
//                Db.update("update order_number set date=?,number=? where type=?",date,1,TYPE_CH);
//                reStr=getNumber(TYPE_CH,date,LENGTH,1);
//            }
//        }else{//如果没有记录就添加记录
//            Record saveR=new Record();
//            saveR.set("date",date);
//            saveR.set("type",TYPE_CH);
//            saveR.set("number",1);
//            saveR.set("remark","门店引单退货单号");
//
//            Db.save("order_number",saveR);
//
//            reStr=getNumber(TYPE_CH,date,LENGTH,1);
//        }
//        return reStr.replace("-","");
//    }
//    /**
//     * 生成仓库移库订单号
//     * @return
//     */
//    public synchronized String getWarehouseMovementOrderNumber(){
//        String reStr="";
//        String date= DateUtil.GetDate();
//        Record r= Db.findFirst("select * from order_number where type=?",TYPE_YK);
//        if(r!=null){//如果有记录就继续判断
//            String dateInR=r.getStr("date");
//            if(date.equals(dateInR)){//如果日期相同
//                int number=r.getInt("number");
//                number++;
//                reStr=getNumber(TYPE_YK,date,LENGTH,number);
//                Db.update("update order_number set number=? where type=?",number,TYPE_YK);
//            }else{//如果数据库中的日期不是当前系统日期
//                Db.update("update order_number set date=?,number=? where type=?",date,1,TYPE_YK);
//                reStr=getNumber(TYPE_YK,date,LENGTH,1);
//            }
//        }else{//如果没有记录就添加记录
//            Record saveR=new Record();
//            saveR.set("date",date);
//            saveR.set("type",TYPE_YK);
//            saveR.set("number",1);
//            saveR.set("remark","仓库移库订单号");
//
//            Db.save("order_number",saveR);
//
//            reStr=getNumber(TYPE_YK,date,LENGTH,1);
//        }
//        return reStr.replace("-","");
//    }
//
//    /**
//     * 生成门店订单号
//     * @return
//     */
//    public synchronized String getSendGoodsOrderNumber(){
//        String reStr="";
//        String date= DateUtil.GetDate();
//        Record r= Db.findFirst("select * from order_number where type=?",TYPE_SH);
//        if(r!=null){//如果有记录就继续判断
//            String dateInR=r.getStr("date");
//            if(date.equals(dateInR)){//如果日期相同
//                int number=r.getInt("number");
//                number++;
//                reStr=getNumber(TYPE_SH,date,LENGTH,number);
//                Db.update("update order_number set number=? where type=?",number,TYPE_SH);
//            }else{//如果数据库中的日期不是当前系统日期
//                Db.update("update order_number set date=?,number=? where type=?",date,1,TYPE_SH);
//                reStr=getNumber(TYPE_SH,date,LENGTH,1);
//            }
//        }else{//如果没有记录就添加记录
//            Record saveR=new Record();
//            saveR.set("date",date);
//            saveR.set("type",TYPE_SH);
//            saveR.set("number",1);
//            saveR.set("remark","门店采购订单号");
//
//            Db.save("order_number",saveR);
//
//            reStr=getNumber(TYPE_SH,date,LENGTH,1);
//        }
//        return reStr.replace("-","");
//    }

    private static String getNumber(String type,String date,int length,int number){
        return type+date+String.format("%0"+LENGTH+"d", number);
    }
    public static void main(String[] args) {
        for(int i=1;i<=15000;i++) {
            String s = String.format("%04d", i);
            System.out.println(s);
        }
    }
}
