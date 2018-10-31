package com.common.service;

import com.Exception.PcException;
import com.bean.TableBean;
import com.constants.KEY;
import com.constants.Sql;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.UUIDTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseService implements KEY, Sql{

    protected String tableName;
    protected TableBean tableBean;

    public BaseService(String tableName, TableBean tableBean){
        this.tableName = tableName;
        this.tableBean = tableBean;
    }

    public boolean add(Record record) throws PcException {
        try{
            record.set("id", UUIDTool.getUUID());
            return Db.save(tableName, getRecord(record));
        }catch (Exception e){
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println(e.getMessage());
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            throw new PcException(ADD_EXCEPTION, e.getMessage());
        }
    }

    public int delete(String... ids) throws PcException{
        StringBuilder wildcard = new StringBuilder("");
        if(ids != null && ids.length > 0){
            for(String s : ids){
                wildcard.append("?,");
            }
        }else{
            throw new PcException(DELETE_EXCEPTION, "删除记录的id没有传递！");
        }
        String deleteSql = DELETE.replace("{{tableName}}", tableName).replace("{{wildcard}}", wildcard.substring(0, wildcard.length() - 1));
        try{
            return Db.delete(deleteSql, ids);
        }catch (Exception e){
            throw new PcException(DELETE_EXCEPTION, e.getMessage());
        }
    }
    public Record findById(String id) {
            return Db.findById(tableName, id);
    }
    public Record findOne(Record record) {
        List<Record> list = this.list(record);
        if(list != null){
            return list.get(0);
        }
        return null;
    }
    public boolean updateById(Record record) throws PcException{
        try{
            return Db.update(tableName, getRecord(record));
        }catch (Exception e){
            throw new PcException(UPDATE_EXCEPTION, e.getMessage());
        }
    }
    public List<Record> list(Record record) {
        StringBuilder select = new StringBuilder(SELECT.replace("{{tableName}}", tableName));
        List<Object> params = new ArrayList<>();
        select.append(createWhereSql(getRecord(record), params));
        return Db.find(select.toString(), params.toArray());
    }
    public Page<Record> query(Record record, int pageNum, int pageSize) throws PcException{
        StringBuilder select = new StringBuilder(_SELECT);
        StringBuilder from = new StringBuilder(_FROM.replace("{{tableName}}", tableName));
        List<Object> params = new ArrayList<>();
        from.append(createWhereSql(getRecord(record), params));
        if(params != null && params.size() > 0){
            return Db.paginate(pageNum, pageSize, select.toString(), from.toString(), params.toArray());
        }else{
            return Db.paginate(pageNum, pageSize, select.toString(), from.toString());
        }
    }

    private StringBuilder createWhereSql(Record record, List<Object> params){
        StringBuilder result = new StringBuilder("");
        if(record != null){
            Map<String, Object> columns = record.getColumns();
            for(Map.Entry<String, Object> entry : columns.entrySet()){
                if(entry.getValue() != null){
                    result.append(" and `" + entry.getKey() + "`=? ");
                    params.add(entry.getValue());
                }
            }
        }
        return result;
    }

    private Record getRecord(Record record){
        Record result = new Record();
        String[] columnNameArr = tableBean.getColumnNameArr();
        if(columnNameArr != null && columnNameArr.length > 0){
            for(String c : columnNameArr){
                Object obj = record.get(c);
                if(obj != null){
                    result.set(c, record.get(c));
                }
            }
        }
        return result;
    }

}
