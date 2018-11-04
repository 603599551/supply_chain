package com.constants;

import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryConstants {

    //岗位常量
    public static final String STATE = "state";



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
