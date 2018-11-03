package com.common.service;

import com.exception.PcException;
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

    /**
     * 构造方法 初始化表名和表信息
     * @param tableName 表名
     * @param tableBean 表信息
     */
    public BaseService(String tableName, TableBean tableBean){
        this.tableName = tableName;
        this.tableBean = tableBean;
    }

    /**
     * 通用新增方法
     * @param record 新增数据
     * @return 新增成功返回true，否则返回false
     * @throws PcException
     */
    public boolean add(Record record) throws PcException {
        try{
            record.set("id", UUIDTool.getUUID());
            return Db.save(tableName, getRecord(record));
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new PcException(ADD_EXCEPTION, e.getMessage());
        }
    }

    /**
     * 批量删除
     * @param ids 删除数据的id
     * @return 删除了多少条记录
     * @throws PcException
     */
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

    /**
     * 通过id查询数据
     * @param id 目标id
     * @return 具体数据
     */
    public Record findById(String id) {
            return Db.findById(tableName, id);
    }

    /**
     * 通过条件查询数据
     * @param record 查询条件，key是字段名，value不为空将被作为条件查询
     * @return 查询到的数据，如果没有查到，返回null
     */
    public Record findOne(Record record) throws PcException {
        List<Record> list = this.list(record);
        if(list != null){
            return list.get(0);
        }
        return null;
    }

    /**
     * 通过id修改
     * @param record 要修改成的数据
     * @return 修改成功返回true，否则修改false
     * @throws PcException
     */
    public boolean updateById(Record record) throws PcException{
        try{
            return Db.update(tableName, getRecord(record));
        }catch (Exception e){
            throw new PcException(UPDATE_EXCEPTION, e.getMessage());
        }
    }

    /**
     * 通过record查询数据
     * @param record 查询条件，key是字段名，value不为空将被作为条件查询
     * @return 查询到的集合
     */
    public List<Record> list(Record record) throws PcException {
        StringBuilder select = new StringBuilder(SELECT.replace("{{tableName}}", tableName));
        List<Object> params = new ArrayList<>();
        select.append(createWhereSql(record, params));
        return listBeforeReturn(Db.find(select.toString(), params.toArray()));
    }

    /**
     * 通过record分页查询
     * @param record 查询条件，key是字段名，value不为空将被作为条件查询
     * @param pageNum 页码数
     * @param pageSize 每页都少条数据
     * @return 分页后的查询结果
     * @throws PcException
     */
    public Page<Record> query(Record record, int pageNum, int pageSize) throws PcException{
        StringBuilder select = new StringBuilder(_SELECT);
        StringBuilder from = new StringBuilder(_FROM.replace("{{tableName}}", tableName));
        List<Object> params = new ArrayList<>();
        from.append(createWhereSql(record, params));
        if(params != null && params.size() > 0){
            return queryBeforeReturn(Db.paginate(pageNum, pageSize, select.toString(), from.toString(), params.toArray()));
        }else{
            return queryBeforeReturn(Db.paginate(pageNum, pageSize, select.toString(), from.toString()));
        }
    }

    /**
     * 创建查询条件SQL，将参数存放在params中
     * @param record 条件
     * @param params 参数
     * @return
     */
    private StringBuilder createWhereSql(Record record, List<Object> params) throws PcException{
        StringBuilder result = new StringBuilder("");
        if(record != null){
            Record recordEntry = getRecord(record);
            Map<String, Object> entryColumns = recordEntry.getColumns();
            for(Map.Entry<String, Object> entry : entryColumns.entrySet()){
                if(entry.getValue() != null){
                    result.append(" and `" + entry.getKey() + "`=? ");
                    params.add(entry.getValue());
                }
            }
            Map<String, Object> columns = record.getColumns();
            for(Map.Entry<String, Object> entry : columns.entrySet()){
                if(entry.getValue() != null){
                    if(entry.getKey().startsWith("$like_")){
                        result.append(" and `" + entry.getKey().replace("$like_", "") + "` like ? ");
                        params.add("%" + entry.getValue() + "%");
                    }else if (entry.getKey().startsWith("$all")){
                        String start = entry.getKey().split("_")[0];
                        String andOr = start.split("\\$")[2];
                        if(!"and".equals(andOr) && !"or".equals(andOr)){
                            throw new PcException(SQL_WHERE_CREATE_EXCEPTION, "参数：" + entry.getKey() + "错误！");
                        }
                        StringBuilder sql = new StringBuilder(" " + andOr + " (1=1 ");
                        String key = entry.getKey().replace(start + "_", "");
                        String[] termArr = key.split("_");
                        Object[] paramsArr = (Object[])entry.getValue();
                        if(termArr.length != paramsArr.length){
                            throw new PcException(SQL_WHERE_CREATE_EXCEPTION, "参数：" + entry.getKey() + "错误！参数匹配长度是" + paramsArr.length);
                        }
                        for(String term : termArr){
                            String[] oneTerm = term.split("\\$");
                            if(oneTerm.length != 3){
                                throw new PcException(SQL_WHERE_CREATE_EXCEPTION, "参数：" + entry.getKey() + "错误！");
                            }else{
                                if("like".equals(oneTerm[1])){
                                    sql.append(" " + oneTerm[2] + " `" + oneTerm[0] + "` like ? ");
                                }else if("eq".equals(oneTerm[1])){
                                    sql.append(" " + oneTerm[2] + " `" + oneTerm[0] + "`=? ");
                                }else{
                                    throw new PcException(SQL_WHERE_CREATE_EXCEPTION, "参数：" + entry.getKey() + "错误！");
                                }
                            }
                        }
                        for(Object obj : paramsArr){
                            params.add(obj);
                        }
                        sql.append(")");
                        result.append(sql);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 根据表的基本字段处理record，防止新增或者修改时找不到字段问题
     * @param record
     * @return
     */
    private Record getRecord(Record record){
        if (record==null){
            return null;
        }
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

    /**
     * list方法返回之前执行的方法
     * @param list 处理集合
     * @return 返回集合
     */
    public abstract List<Record> listBeforeReturn(List<Record> list);

    /**
     * page方法返回之前执行的方法
     * @param page 处理集合
     * @return 返回集合
     */
    public abstract Page<Record> queryBeforeReturn(Page<Record> page);

}