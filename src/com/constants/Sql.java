package com.constants;

public interface Sql {

    String _SELECT = "SELECT * ";
    String _FROM = " FROM `{{tableName}}` WHERE 1=1 ";
    String SELECT = _SELECT + _FROM;
    String DELETE = "DELETE FROM `{{tableName}}` WHERE id IN({{wildcard}})";

}
