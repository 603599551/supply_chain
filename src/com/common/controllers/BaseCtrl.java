package com.common.controllers;

import com.Exception.PcException;
import com.common.service.BaseService;
import com.constants.KEY;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.JsonHashMap;
import com.utils.NumberUtils;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.*;



public abstract class BaseCtrl<T extends BaseService> extends Controller implements KEY {

    protected T service;

    public BaseCtrl(Class<T> cla){
        service = enhance(cla);
    }

    /**
     * 将前台的参数整理到Record对象中
     * @return
     */
    protected Record getParaRecord(){
        Record result = null;
        Enumeration<String> namesList = this.getRequest().getParameterNames();
        if(namesList != null && namesList.hasMoreElements()){
            result = new Record();
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
    public void add(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = this.getParaRecord();
        try {
            boolean flag = service.add(record);
            if(flag){
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
    public void showById(){
        JsonHashMap jhm = new JsonHashMap();
        String id = getPara("id");
        try{
            Record record = service.findById(id);
            handleRecord(record);
            jhm.putSuccess(record);
        } catch (Exception e){
            jhm.putFail(e.getMessage());
        }
        renderJson(jhm);
    }

    public abstract void handleRecord(Record record);

    public void updateById(){
        JsonHashMap jhm = new JsonHashMap();
        Record record = this.getParaRecord();
        try {
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
    public void list(){
        JsonHashMap jhm = new JsonHashMap();
        try{
            Record record = getParaRecord();
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
}
