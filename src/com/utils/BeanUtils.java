package com.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class BeanUtils {

    public static Record jsonToRecord(JSONObject jsonObject){
        Record result = new Record();
        if(jsonObject != null){
            result.setColumns(jsonObject);
        }else{
            return null;
        }
        return result;
    }

    /**
     * 创建树形结构
     * @param root 根节点对象
     * @param list 要生成tree的集合
     * @param parentName 父节点关联名字，例如parent_id
     * @param name 子节点名字，例如id
     */
    public static void createTree(Record root, List<Record> list, String parentName, String name){
        if(list == null || list.size() == 0){
            return;
        }
        for(Record r : list){
            if(r.get(parentName).equals(root.get(name))){
                List<Record> children = root.get("children");
                if(children == null){
                    children = new ArrayList<>();
                    root.set("children", children);
                }
                children.add(r);
            }
        }
        List<Record> children = root.get("children");
        if(children != null && children.size() > 0){
            for(Record r : children){
                createTree(r, list, parentName, name);
            }
        }
    }

    /**
     * 创建树形结构
     * @param root 根节点对象
     * @param list 要生成tree的集合
     */
    public static void createTree(Record root, List<Record> list){
        createTree(root, list, "parent_id", "id");
    }

}
