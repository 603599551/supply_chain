package com.common.service;

import com.alibaba.druid.util.StringUtils;
import com.exception.PcException;
import com.bean.TableBean;
import com.constants.KEY;
import com.constants.Sql;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.UUIDTool;
import com.utils.UserSessionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Before(Tx.class)
public abstract class BaseService implements KEY, Sql{

    protected String tableName;
    protected TableBean tableBean;

    public <T> T enhance(Class<T> targetClass) {
        return Enhancer.enhance(targetClass);
    }

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
     * @return 新增成功返回record的id，否则返回null
     * @throws PcException
     */
    public String add(Record record) throws PcException {
        try{
            if(record.get("id") == null || record.getStr("id").length() == 0){
                record.set("id", UUIDTool.getUUID());
            }
            boolean flag = Db.save(tableName, getRecord(record));
            if(!flag){
                return null;
            }
            return record.getStr("id");
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
    public Record findById(String id) throws PcException {
        try{
            return Db.findById(tableName, id);
        }catch (Exception e){
            e.printStackTrace();
            throw new PcException(SELECT_EXCEPTION, "查询异常！");
        }
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
        select.append(createWhereSql(record, params, true));
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
        from.append(createWhereSql(record, params, true));
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
     * @param flag 是否执行getRecord方法，过滤掉表中不存在的字段
     * @return
     */
    protected StringBuilder createWhereSql(Record record, List<Object> params, boolean flag) throws PcException{
        StringBuilder result = new StringBuilder("");
        if(record != null){
            Record recordEntry = flag ? getRecord(record) : record;
            Map<String, Object> entryColumns = recordEntry.getColumns();
            for(Map.Entry<String, Object> entry : entryColumns.entrySet()){
                if(entry.getValue() != null){
                    if (!flag&&entry.getKey().startsWith("$")){
                        continue;
                    }
                    if(entry.getValue() instanceof String){
                        String value = (String)entry.getValue();
                        if(value.length() > 0){
                            if (!flag){
                                result.append(" and " + entry.getKey() + "=? ");
                            }else {
                                result.append(" and `" + entry.getKey() + "`=? ");
                            }
                            params.add(entry.getValue());
                        }
                    }
                }
            }
            Map<String, Object> columns = record.getColumns();
            for(Map.Entry<String, Object> entry : columns.entrySet()){
                if(entry.getValue() != null){
                    if(entry.getKey().startsWith("$like#")){
                        result.append(createLike(flag, entry));
                        params.add("%" + entry.getValue() + "%");
                    }else if (entry.getKey().startsWith("$all")){
                        result.append(createAll(flag, entry, params));
                    }else if(entry.getKey().startsWith("$in")){
                        String sql = createIn(entry, params);
                        if(sql != null){
                            result.append(sql);
                        }
                    }else if(entry.getKey().startsWith("$<>")){
                        String sql = createNotIn(entry, params);
                        if(sql != null){
                            result.append(sql);
                        }
                    }else if(entry.getKey().startsWith("$fromTo")){
                        result.append(createFromTo(flag, entry, params));
                    }
                }
            }
            Object fromto=columns.get("$fromto");
            if (fromto!=null){
                result.append(fromto);
            }
            Object sort=columns.get("$sort");
            if (sort!=null){
                result.append(sort);
            }
        }
        return result;
    }

    /**
     * 创建like子句
     * @param flag 是否用默认的字段名
     * @param entry record的每项
     * @return
     */
    private StringBuilder createLike(boolean flag, Map.Entry<String, Object> entry){
        StringBuilder result = new StringBuilder();
        if (!flag){
            result.append(" and " + entry.getKey().replace("$like#", "") + " like ? ");
        }else {
            result.append(" and `" + entry.getKey().replace("$like#", "") + "` like ? ");
        }
        return result;
    }

    /**
     * 创建多条件查询子句
     * @param flag 是否使用默认字段
     * @param entry record子项
     * @param params 替换问号的参数集合
     * @return
     * @throws PcException
     */
    private StringBuilder createAll(boolean flag, Map.Entry<String, Object> entry, List<Object> params) throws PcException{
        String start = entry.getKey().split("#")[0];
        String andOr = start.split("\\$")[2];
        if(!"and".equals(andOr) && !"or".equals(andOr)){
            throw new PcException(SQL_WHERE_CREATE_EXCEPTION, "参数：" + entry.getKey() + "错误！");
        }
        StringBuilder sql = new StringBuilder(" " + andOr + " ( ");
        String key = entry.getKey().replace(start + "#", "");
        String[] termArr = key.split("#");
        Object[] paramsArr = (Object[])entry.getValue();
        if(termArr.length != paramsArr.length){
            throw new PcException(SQL_WHERE_CREATE_EXCEPTION, "参数：" + entry.getKey() + "错误！参数匹配长度是" + paramsArr.length);
        }
        for(String term : termArr){
            String[] oneTerm = term.split("\\$");
            if(oneTerm.length != 3){
                throw new PcException(SQL_WHERE_CREATE_EXCEPTION, "参数：" + entry.getKey() + "错误！");
            }else{
                if (StringUtils.equals(term,termArr[0])){
                    oneTerm[2]="";
                }
                if("like".equals(oneTerm[1])){
                    if (!flag){
                        sql.append(" " + oneTerm[2] + " " + oneTerm[0] + " like CONCAT('%',?,'%') ");
                    }else {
                        sql.append(" " + oneTerm[2] + " `" + oneTerm[0] + "` like CONCAT('%',?,'%') ");
                    }

                }else if("eq".equals(oneTerm[1])){
                    if (!flag){
                        sql.append(" " + oneTerm[2] + " " + oneTerm[0] + "=? ");
                    }else {
                        sql.append(" " + oneTerm[2] + " `" + oneTerm[0] + "`=? ");
                    }
                }else{
                    throw new PcException(SQL_WHERE_CREATE_EXCEPTION, "参数：" + entry.getKey() + "错误！");
                }
            }
        }
        for(Object obj : paramsArr){
            params.add(obj);
        }
        sql.append(")");
        return sql;
    }

    /**
     * 创建in子句
     * @param entry record子项
     * @param params 替换问号的集合
     * @return
     */
    private String createIn(Map.Entry<String, Object> entry, List<Object> params){
        String[] valueArr = (String[]) entry.getValue();
        if(valueArr != null && valueArr.length > 0){
            //$in#and#column
            String[] keyArr = entry.getKey().split("#");
            String sql = keyArr[1] + " " + keyArr[2] + " in({{wildcard}})";
            String wildcard = "";
            for(String s : valueArr){
                wildcard += "?,";
                params.add(s);
            }
            wildcard = wildcard.substring(0, wildcard.length() - 1);
            return sql.replace("{{wildcard}}", wildcard);
        }
        return null;
    }

    /**
     * 创建不等于SQL子句
     * $<>and#column
     * @param entry record子项
     *              value必须是一个数组，该方法用not in实现，不是<>实现的
     * @param params 替换问号的参数集合
     * @return
     */
    private String createNotIn(Map.Entry<String, Object> entry, List<Object> params){
        String[] valueArr = (String[]) entry.getValue();
        if(valueArr != null && valueArr.length > 0){
            String[] keyArr = entry.getKey().split("#");
            String sql = keyArr[1] + " " + keyArr[2] + " not in({{wildcard}})";
            String wildcard = "";
            for(String s : valueArr){
                wildcard += "?,";
                params.add(s);
            }
            wildcard = wildcard.substring(0, wildcard.length() - 1);
            return sql.replace("{{wildcard}}", wildcard);
        }
        return null;
    }

    /**
     * 创建范围查询子句
     * @param flag 是否使用默认字段
     * @param entry record子项
     * @param params 替换问号的参数集合
     * @return
     * @throws PcException
     */
    private StringBuilder createFromTo(boolean flag, Map.Entry<String, Object> entry, List<Object> params) {
        String andOr = entry.getKey().split("#")[1];
        String sql = entry.getKey().split("#")[2];
        StringBuilder result = new StringBuilder(" " + andOr + " " + sql);
        Object[] paramsArr = (Object[]) entry.getValue();
        for(Object obj : paramsArr){
            params.add(obj);
        }
        return result;
    }

    /**
     * 根据表的基本字段处理record，防止新增或者修改时找不到字段问题
     * @param record
     * @return
     */
    protected Record getRecord(Record record){
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
     * 获取表中sort的最大数
     * @author CaryZ
     * @date 2018-11-09
     * @return
     */
    public synchronized int getCurrentSort(){
        return getCurrentSort("");
    }

    /**
     * 按照条件获取表中sort的最大数
     * @author szsw
     * @date 2018-11-11
     * @param where 查询条件
     * @return
     */
    public synchronized int getCurrentSort(String where){
        Record record=Db.findFirst("SELECT sort FROM "+this.tableName+" " + where + " ORDER BY sort DESC LIMIT 1");
        return record==null? 0:record.getInt("sort");
    }

    /**
     * 通过指定字段，对应参数值查询数据
     * @param columnName 字段名
     * @param columnValue 参数值
     * @return 所有数据
     */
    public List<Record> selectByColumnIn(String columnName, String... columnValue){
        if(columnValue == null || columnValue.length == 0){
            return null;
        }
        String selectSql = "select * from " + tableName + " where " + columnName + " in({{wildcard}})";
        StringBuilder wildcard = new StringBuilder("");
        for(String s : columnValue){
            wildcard.append("?,");
        }
        selectSql = selectSql.replace("{{wildcard}}", wildcard.substring(0, wildcard.length() - 1));
        return Db.find(selectSql, columnValue);
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

    public String getTableName() {
        return tableName;
    }

}
