package com.bean;

import com.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TableBean {

    private String tableName;
    private String name = "";
    private List<Column> columnList = new ArrayList<>();
    private String[] columnNameArr;

    public TableBean(String tableName, String[] columnNameArr, String[] columnTypeArr, String[] columnCommentArr){
        this.columnNameArr = columnNameArr;
        this.tableName = tableName;
        String[] tableNameArr = tableName.split("_");
        for(int i = 1; i < tableNameArr.length; i++){
            name += StringUtil.toUpperCaseFirstOne(tableNameArr[i]);
        }
        for(int i = 0; i < columnNameArr.length; i++){
            Column c = new Column(columnNameArr[i], columnTypeArr[i], columnCommentArr[i]);
            columnList.add(c);
        }

    }

    public String[] getColumnNameArr() {
        return columnNameArr;
    }

    public void setColumnNameArr(String[] columnNameArr) {
        this.columnNameArr = columnNameArr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

}
