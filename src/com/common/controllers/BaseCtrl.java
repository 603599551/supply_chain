package com.common.controllers;

import com.alibaba.fastjson.JSONObject;
import com.exception.PcException;
import com.common.service.BaseService;
import com.constants.KEY;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.sun.org.apache.regexp.internal.RE;
import com.utils.JsonHashMap;
import com.utils.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.text.ParseException;
import java.util.*;



public abstract class BaseCtrl<T extends BaseService> extends Controller implements KEY {

    protected T service;

    /**
     * 初始化service
     * @param cla service的class
     */
    public BaseCtrl(Class<T> cla){
        service = enhance(cla);
    }

    /**
     * 将前台的参数整理到Record对象中
     * @return
     */
    protected Record getParaRecord(){
        Record result = new Record();
        Enumeration<String> namesList = this.getRequest().getParameterNames();
        if(namesList != null && namesList.hasMoreElements()){
            while(namesList.hasMoreElements()){
                String name = namesList.nextElement();
                result.set(name, getPara(name));
            }
        }
        return result;
    }

    /**
     * 取Request中的数据对象
     * @return
     * @throws Exception
     */
    protected String getRequestObject() throws Exception {
        StringBuilder json = new StringBuilder();
        BufferedReader reader = this.getRequest().getReader();
        String line = null;
        while((line = reader.readLine()) != null){
            json.append(line);
        }
        reader.close();
        return json.toString();
    }

    /**
     * 将前台的参数整理到Record对象中
     * @return
     */
    protected Map<String, Object> getParaMaps(){
        Map<String, Object> result = null;
        Enumeration<String> namesList = this.getRequest().getParameterNames();
        if(namesList != null && namesList.hasMoreElements()){
            result = new HashMap<>();
            while(namesList.hasMoreElements()){
                String name = namesList.nextElement();
                result.put(name, getPara(name));
            }
        }
        return result;
    }

    public static Map getParameterMap(HttpServletRequest request){
        Map<String,Object> returnMap = new HashMap();
        Enumeration<String> names=request.getParameterNames();
        while(names.hasMoreElements()){
            String name=names.nextElement();
            String value=request.getParameter(name);
            String[] values=request.getParameterValues(name);
            if(values.length==1){
                returnMap.put(name, value);
            }else{
                returnMap.put(name, values);
            }
        }
        return returnMap;
    }

    /**
     * 接收JSON参数用这个
     * @param request
     * @return
     */
    public static JSONObject getJson(HttpServletRequest request){
        JSONObject paraJsonObject=null;
        Map paraMap=request.getParameterMap();
        Iterator it=paraMap.keySet().iterator();
        if(it.hasNext()){
            Object obj=it.next();
            paraJsonObject= JSONObject.parseObject(obj.toString());
        }
        return paraJsonObject;
    }

    /**
     * 几天之后
     * @param date 日期
     * @param nextDay 天数
     * @return date之后nextDay天后的日期
     */
    protected String nextDay(String date, int nextDay){
        String result = "";
        try {
            Date today = sdf_ymd.parse(date);
            today = new Date(today.getTime() + nextDay * ONE_DAY_TIME);
            result = sdf_ymd.format(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 基础新增
     */
    public void add(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = this.getParaRecord();
        try {
            handleAddRecord(record);
            String id = service.add(record);
            if(id != null){
                jhm.putMessage("添加成功！");
            }else{
                jhm.putFail("添加失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 基础删除
     */
    public void deleteByIds(){
        JsonHashMap jhm = new JsonHashMap();
        String[] ids = this.getParaValues("ids");
        try {
            int deleteNum = service.delete(ids);
            jhm.putMessage("删除了" + deleteNum + "条数据！");
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 通过id展示
     */
    public void showById(){
        JsonHashMap jhm = new JsonHashMap();
        String id = getPara("id");
        try{
            Record record = service.findById(id);
            handleRecord(record);
            jhm.putSuccess(record);
        } catch (Exception e){
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 处理record中字典值添加中文
     * @param record 目标record
     */
    public abstract void handleRecord(Record record);

    /**
     * 处理新增时的特殊处理
     * 比如创建时间穿件人等
     * @param record
     */
    public abstract void handleAddRecord(Record record);

    /**
     * 处理修改时的特殊处理
     * 比如修改时间、修改人等
     * @param record
     */
    public abstract void handleUpdateRecord(Record record);


    /**
     * 根据查询条件处理record
     * key
     *      $all$or#name$like$or#pinyin$like$or#state$eq$and
     *      $sort
     *      $in#and#column
     *      $<>#and#parent_id
     *      $fromTo#and#sql
     * value
     *      array
     *      sql string
     *      array
     *      array
     * key
     *      all代表条件作为一个整体查询，or代表这个整体条件对外的逻辑关系，只允许为or/and
     *      name代表字段名，like代表条件的方式，只允许为like/eq，or代表逻辑关系
     * value
     *      是一个数组，对应每个字段的具体值
     * 上例：
     * select * from tableName where 1=1 or( name like ? or pinyin like ? and state=?)
     * order by name desc ,pinyin asc,state desc
     * @param record 查询条件
     */
    public abstract void createRecordBeforeSelect(Record record);


    /**
     * 基础修改
     */
    public void updateById(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = this.getParaRecord();
        try {
            handleUpdateRecord(record);
            boolean flag = service.updateById(record);
            if(flag){
                jhm.putMessage("修改成功！");
            }else{
                jhm.putFail("修改失败！");
            }
        } catch (PcException e) {
            e.printStackTrace();
            jhm.putError(e.getMsg());
        }
        renderJson(jhm);
    }

    /**
     * 基础查询
     */
    public void list(){
        JsonHashMap jhm = new JsonHashMap();
        try{
            Record record = getParaRecord();
            createRecordBeforeSelect(record);
            List<Record> list = this.service.list(record);
            if(list != null && list.size() > 0){
                for(Record r : list){
                    handleRecord(r);
                }
            }
            jhm.putSuccess(list);
        }catch (Exception e){
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 基础分页查询
     */
    public void query(){
        JsonHashMap jhm = new JsonHashMap();
        try{
            Record record = getParaRecord();
            String pageNumStr = getPara("pageNum");
            String pageSizeStr = getPara("pageSize");
            //如果为空时赋给默认值
            int pageNum = NumberUtils.parseInt(pageNumStr, 1);
            int pageSize = NumberUtils.parseInt(pageSizeStr, 10);
            if(record != null){
                record.remove("pageNum", "pageSize");
            }
            createRecordBeforeSelect(record);
            Page<Record> page = this.service.query(record, pageNum, pageSize);
            if(page != null && page.getList() != null && page.getList().size() > 0){
                for(Record r : page.getList()){
                    handleRecord(r);
                }
            }
            jhm.putSuccess(page);
        }catch (Exception e){
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }

    /**
     * 修改启用停用接口
     */
    public void updateState(){
        JsonHashMap jhm = new JsonHashMap();
        String id = getPara("id");
        String state = getPara("state");
        Record record = new Record();
        record.set("id", id);
        record.set("state", state);
        try {
            boolean flag = Db.update(service.getTableName(), record);
            if(flag){
                jhm.putMessage("修改状态成功！");
            }else{
                jhm.putFail("修改状态失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }
}
