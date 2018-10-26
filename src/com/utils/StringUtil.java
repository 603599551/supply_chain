package com.utils;

public class StringUtil {

    /**
     * 判断字符串是否为空
     * @param string 目标字符串
     * @return 空返回true，否则返回false
     */
    public static boolean isBlank(String string) {
        return string == null || string.length() <= 0;
    }

    /**
     * 字符串去掉前后空格判断是否为空
     * @param string 目标字符串
     * @return 空返回true，否则返回false
     */
    public static boolean isBlankIncludeSpace(String string) {
        return string == null || string.trim().length() <= 0;
    }

    /**
     * 首字母小写
     * @param s 模板字符串
     * @return 首字母小写字符串
     */
    public static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    /**
     * 首字母大写
     * @param s 模板字符串
     * @return 首字母大写字符串
     */
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

}
