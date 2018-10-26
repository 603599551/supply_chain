package com.utils;

import java.util.HashMap;

/**
 * @author szsw
 *
 * 后台响应通用JSON工具类
 * 请求成功，数据正确：<br>
 *     code=1<br>
 *     data：具体数据<br>
 * 请求成功，数据错误：<br>
 *     code=0<br>
 *     message：错误原因<br>
 * 请求服务器报错：<br>
 *     code=-1<br>
 *     message：“服务器异常！”<br>
 *
 *
 */
public class JsonHashMap extends HashMap<String, Object> {

    private static final int RESULT_FAIl = 0;
    private static final int RESULT_SUCCESS = 1;
    private static final int RESULT_ERROR = -1;


    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String DATE = "data";

    private static final String FAIL_MESSAGE = "服务器异常！";
    private static final String ERROR_MESSAGE = "数据加载失败！";

    /**
     * 构造方法默认设置code=1
     */
    public JsonHashMap() {
        putCode(RESULT_SUCCESS);
    }

    /**
     * 设置code值
     * @param code
     *  * 请求成功，数据正确：<br>
     *     code=1<br>
     * 请求成功，数据错误：<br>
     *     code=0<br>
     * 请求服务器报错：<br>
     *     code=-1<br>
     * @return this
     */
    public JsonHashMap putCode(int code) {
        return put(CODE, code);
    }

    /**
     * 设置信息值
     * @param message
     *  * 请求成功，数据正确：<br>
     *     data：具体数据<br>
     * 请求成功，数据错误：<br>
     *     message：错误原因<br>
     * 请求服务器报错：<br>
     *     message：“服务器异常！”<br>
     * @return this
     */
    public JsonHashMap putMessage(String message) {
        return put(MESSAGE, message);
    }

    /**
     * 设置键值对
     * @param key 键
     * @param value 值
     * @return 当前对象
     */
    public JsonHashMap put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 设置请求成功，数据正确
     * @param obj 请求成功的响应数据
     * @return this
     */
    public JsonHashMap putSuccess(Object obj){
        return put(DATE, obj);
    }

    /**
     * 设置请求失败的数据
     * @param obj 请求失败后的响应数据
     * @return this
     */
    public JsonHashMap putError(String obj){
        return putCode(RESULT_ERROR).putMessage(obj);
    }

    /**
     * 设置请求失败的数据
     * @return this
     */
    public JsonHashMap putError(){
        return putCode(RESULT_ERROR).putMessage(ERROR_MESSAGE);
    }

    /**
     * 设置请求成功，数据错误
     * @param obj 响应数据
     * @return this
     */
    public JsonHashMap putFail(String obj){
        return putCode(RESULT_FAIl).putMessage(obj);
    }
    /**
     * 设置请求成功，数据错误
     * @return this
     */
    public JsonHashMap putFail(){
        return putCode(RESULT_FAIl).putMessage(FAIL_MESSAGE);
    }

}
