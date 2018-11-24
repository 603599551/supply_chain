package com.utils;

import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang.StringUtils;

/**
 * 单位换算
 *
 * @author mym
 */
public class UnitConversion {


    /**
     * 将最小单位的数量，换算成提货单位
     * 如果提货单位是大单位，那么门店想要的数量（最小单位）换算成提货单位的公式是：wantNumOutUnit=门店想要数量/(大单位数量*大规格数量)，不能整除的+1
     * 如果提货单位是大单位，那么门店想要的数量（最小单位）换算成提货单位的公式是：wantNumOutUnit=门店想要数量/大单位数量，不能整除的+1
     *
     * @param num        要换算的数量（最小单位）
     * @param mid_unit    中间单位
     * @param min2mid_num    小单位换算成中间单位的数值
     * @param max_unit    大单位
     * @param mid2max_num 中间单位换算成大单位的数值
     * @param out_unit    提货单位
     * @return
     */
    public static int smallUnit2outUnit(int num, String min_unit, String mid_unit, int min2mid_num, String max_unit, int mid2max_num, String out_unit) {
        if (StringUtils.isBlank(min_unit)) {
            throw new NullPointerException("unit不能为空！");
        }
        if (StringUtils.isBlank(out_unit)) {
            throw new NullPointerException("outUnit不能为空！");
        }
        int reNum = 0;
        if (StringUtils.isNotBlank(max_unit) && out_unit.equals(max_unit)) {//如果“提货单位”与“大规格单位”相同
            reNum = (int) Math.ceil((double) num / (double) (min2mid_num * mid2max_num));
        } else if (StringUtils.isNotBlank(mid_unit) && out_unit.equals(mid_unit)) {
            reNum = (int) Math.ceil((double) num / (double) min2mid_num);
        } else if (StringUtils.isNotBlank(min_unit) && out_unit.equals(min_unit)) {
            reNum = num;
        } else {
            throw new RuntimeException("没有与提货单位相匹配的转换单位！");
        }
        return reNum;
    }

    /**
     * 将提货单位的数量，换算成最小单位的数量
     * 从record中取出如下数据：num,min_unit,mid_unit,min2mid_num,max_unit,mid2max_num,out_unit
     * @param r
     * @return
     */
    public static int outUnit2SmallUnit(Record r) {
        String out_unit = r.getStr("out_unit");//出库单位
        String max_unit = r.getStr("max_unit");//大单位
        Object boxAttrNumObj = r.get("mid2max_num");//大单位换算成箱的数值
        String mid_unit = r.getStr("mid_unit");//大单位
        String min_unit = r.getStr("min_unit");//最小单位
        Object unitNumObj = r.getStr("min2mid_num");//小单位换算成大单位的数值
        Object numObj = r.getStr("quantity");//要换算的数量（提货单位）

        int mid2max_num = NumberUtils.parseInt(boxAttrNumObj, 0);
        int min2mid_num = NumberUtils.parseInt(unitNumObj, 0);
        int num=NumberUtils.parseInt(numObj, 0);

        return outUnit2SmallUnit(num,min_unit,mid_unit, min2mid_num, max_unit,mid2max_num, out_unit);
    }

    /**
     * 将提货单位的数量，换算成最小单位的数量
     * 如果提货单位是大单位，那么提货单位换算成最小单位的公式是：num*min2mid_num*mid2max_num
     * 如果提货单位是大单位，那么提货单位换算成最小单位的公式是：num*min2mid_num
     *
     * @param num        要换算的数量（提货单位）
     * @param min_unit       小单位
     * @param mid_unit    大单位
     * @param min2mid_num    小单位换算成大单位的数值
     * @param max_unit    大单位（有可能为空）
     * @param mid2max_num 中间单位换算成大单位的数值
     * @param out_unit    提货单位
     * @return
     */
    public static int outUnit2SmallUnit(int num, String min_unit, String mid_unit, int min2mid_num, String max_unit, int mid2max_num, String out_unit) {
        if (StringUtils.isBlank(min_unit)) {
            throw new NullPointerException("unit不能为空！");
        }
        if (StringUtils.isBlank(out_unit)) {
            throw new NullPointerException("outUnit不能为空！");
        }
        int reNum = 0;
        if (StringUtils.isNotBlank(max_unit) && out_unit.equals(max_unit)) {//如果“提货单位”与“大规格单位”相同
            reNum = num * min2mid_num * mid2max_num;
        } else if (out_unit.equals(mid_unit)) {
            reNum = num * min2mid_num;
        } else if (out_unit.equals(min_unit)) {
            reNum = num;
        } else {
            throw new RuntimeException("没有与提货单位相匹配的转换单位！");
        }
        return reNum;
    }


    /**
     * 根据出库单位返回规格（该规格的最大单位是出库单位）<br/>
     * 从record中取出如下数据：out_unit,max_unit,box_attr_num,mid_unit,min_unit,unit_num
     * 如：提货单位是箱，返回规格是：100袋/箱
     *
     * @return
     */
    public static String getAttrByOutUnit(Record r) {
        String out_unit = r.getStr("out_unit");//出库单位
        String max_unit = r.getStr("max_unit");//大单位
        Object boxAttrNumObj = r.get("mid2max_num");//大单位换算成箱的数值
        String mid_unit = r.getStr("mid_unit");//大单位
        String min_unit = r.getStr("min_unit");//最小单位
        Object unitNumObj = r.getStr("min2mid_num");//小单位换算成大单位的数值

        int mid2max_num = NumberUtils.parseInt(boxAttrNumObj, 0);
        int min2mid_num = NumberUtils.parseInt(unitNumObj, 0);

        return getAttrByOutUnit(min_unit, min2mid_num, mid_unit, mid2max_num, max_unit, out_unit);
    }

    /**
     * 根据提货单位返回规格（该规格的最大单位是出库单位）<br/>
     * 如：提货单位是箱，返回规格是：100袋/箱
     *
     * @return
     */
    public static String getAttrByOutUnit(String min_unit, int min2mid_num, String mid_unit, int mid2max_num, String max_unit, String out_unit) {
        if (StringUtils.isBlank(min_unit)) {
            throw new NullPointerException("unit不能为空！");
        }
        if (StringUtils.isBlank(out_unit)) {
            throw new NullPointerException("outUnit不能为空！");
        }

        String attribute2 = "";
        if (out_unit.equals(max_unit)) {
            attribute2 = mid2max_num + mid_unit + "/" + max_unit;
        } else if (out_unit.equals(mid_unit)) {
            attribute2 = min2mid_num + min_unit + "/" + mid_unit;
        } else if (out_unit.equals(min_unit)) {
            attribute2 = min_unit;
        } else {
            throw new RuntimeException("没有与提货单位相匹配的转换单位！");
        }
        return attribute2;
    }


    /**
     * 将最小单位的数量，换算成提货单位，保留5位小数点
     * 如果提货单位是大单位，那么门店想要的数量（最小单位）换算成提货单位的公式是：wantNumOutUnit=门店想要数量/(大单位数量*大规格数量)，不能整除的+1
     * 如果提货单位是大单位，那么门店想要的数量（最小单位）换算成提货单位的公式是：wantNumOutUnit=门店想要数量/大单位数量，不能整除的+1
     *
     * @param num        要换算的数量（最小单位）
     * @param mid_unit    大单位
     * @param min2mid_num    小单位换算成大单位的数值
     * @param max_unit    大单位
     * @param mid2max_num 中间单位换算成大单位的数值
     * @param out_unit    提货单位
     * @return
     */
    public static double smallUnit2outUnitDecil(double num, String min_unit, String mid_unit, int min2mid_num, String max_unit, int mid2max_num, String out_unit) {
        if (StringUtils.isBlank(min_unit)) {
            throw new NullPointerException("unit不能为空！");
        }
        if (StringUtils.isBlank(out_unit)) {
            throw new NullPointerException("outUnit不能为空！");
        }
        if (num <= 0) {
            return 0;
        }
        double reNum = 0;
        if (StringUtils.isNotBlank(max_unit) && out_unit.equals(max_unit)) {//如果“提货单位”与“大规格单位”相同
            reNum = (double) num / (double) (min2mid_num * mid2max_num);
        } else if (StringUtils.isNotBlank(mid_unit) && out_unit.equals(mid_unit)) {
            reNum = (double) num / (double) min2mid_num;
        } else if (StringUtils.isNotBlank(min_unit) && out_unit.equals(min_unit)) {
            reNum = num;
        } else {
            throw new RuntimeException("没有与提货单位相匹配的转换单位！");
        }
        reNum = new Double(String.format("%.5f", reNum));
        return reNum;
    }

    public static double smallUnit2outUnitDecil(Record r) {
        return smallUnit2outUnitDecil(r,"quantity");
    }

    public static double smallUnit2outUnitDecil(Record r,String numName) {
        String out_unit = r.getStr("out_unit");//出库单位
        String max_unit = r.getStr("max_unit");//大单位
        String min_unit = r.getStr("min_unit");//最小单位
        Object boxAttrNumObj = r.getStr("mid2max_num");//中间单位换算成大单位的数值
        String mid_unit = r.getStr("mid_unit");//中间单位
        Object unitNumObj = r.getStr("min2mid_num");//小单位换算成大单位的数值
        Object numObj = r.getStr(numName);//要换算的数量（提货单位）

        int mid2max_num = NumberUtils.parseInt(boxAttrNumObj, 0);
        int min2mid_num = NumberUtils.parseInt(unitNumObj, 0);
        double num=NumberUtils.parseDouble(numObj, 0);

        return smallUnit2outUnitDecil(num,min_unit,mid_unit, min2mid_num, max_unit,mid2max_num, out_unit);
    }

    /**
     * 将提货单位的数量，换算成最小单位的数量
     * 如果提货单位是大单位，那么提货单位换算成最小单位的公式是：num*min2mid_num*mid2max_num
     * 如果提货单位是大单位，那么提货单位换算成最小单位的公式是：num*min2mid_num
     *
     * @param num           要换算的数量（提货单位）
     * @param min_unit          小单位
     * @param mid_unit       大单位
     * @param min2mid_num       小单位换算成大单位的数值
     * @param max_unit       大单位（有可能为空）
     * @param boxAttrNumStr 中间单位换算成大单位的数值
     * @param out_unit       提货单位
     * @return
     */
    public static double outUnit2SmallUnitDecil(double num, String min_unit, String mid_unit, int min2mid_num, String max_unit, String boxAttrNumStr, String out_unit) {
        if (StringUtils.isBlank(min_unit)) {
            throw new NullPointerException("unit不能为空！");
        }
        if (StringUtils.isBlank(out_unit)) {
            throw new NullPointerException("outUnit不能为空！");
        }
        int mid2max_num = boxAttrNumStr != null ? new Integer(boxAttrNumStr) : 0;
        double reNum = 0;
        if (StringUtils.isNotBlank(max_unit) && out_unit.equals(max_unit)) {//如果“提货单位”与“大规格单位”相同
            reNum = num * min2mid_num * mid2max_num;
        } else if (out_unit.equals(mid_unit)) {
            reNum = num * min2mid_num;
        } else if (out_unit.equals(min_unit)) {
            reNum = num;
        } else {
            throw new RuntimeException("没有与提货单位相匹配的转换单位！");
        }
        return reNum;
    }

    /**
     * 将提货单位的数量，换算成最小单位的数量
     * 从record中取出如下数据：num,min_unit,mid_unit,min2mid_num,max_unit,mid2max_num,out_unit
     * @param r 默认字段名为quantity
     * @return
     */
    public static double outUnit2SmallUnitDecil(Record r) {
        return outUnit2SmallUnitDecil(r,"quantity");
    }

    public static double outUnit2SmallUnitDecil(Record r,String numName) {
        String out_unit = r.getStr("out_unit");//出库单位
        String max_unit = r.getStr("max_unit");//大单位
        String boxAttrNumObj = r.getStr("mid2max_num");//中间单位换算成大单位的数值
        String mid_unit = r.getStr("mid_unit");//大单位
        String min_unit = r.getStr("min_unit");//最小单位
        Object unitNumObj = r.getStr("min2mid_num");//小单位换算成大单位的数值
        Object numObj = r.getStr(numName);//要换算的数量（提货单位）

        int min2mid_num = NumberUtils.parseInt(unitNumObj, 0);
        double num=NumberUtils.parseDouble(numObj, 0);

        return outUnit2SmallUnitDecil(num,min_unit,mid_unit, min2mid_num, max_unit,boxAttrNumObj, out_unit);
    }
}
