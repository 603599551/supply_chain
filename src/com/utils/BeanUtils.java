package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 创建树形结构，在显示仓库库存时用到，当某个原料没有库存时，加上disabled，禁选
     * @param root 带有分类的原料树
     * @param list 库存记录
     * @param parentName
     * @param name
     */
    public static void createTreeWithDisabled(Record root, List<Record> list, String parentName, String name){
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
                createTreeWithDisabled(r, list, parentName, name);
            }
        }else if (!list.contains(root)){
            root.set("disabled",true);
        }
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


    /**
     * 通过在分类名称前面加全角空格造出一个仿真树
     * @param root 根节点对象
     * @param list 要生成tree的集合
     * @param parentName 父节点关联名字，例如parent_id
     * @param name 子节点名字，例如id
     */
    public static void createEmulationalTree(Record root, List<Record> list, String parentName, String name,String str){
        //这里并没有return，最后结束是因为全部运行完了。
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
        if (!StringUtils.equals(root.getStr("id"),"0")){
            str+="　";
        }
        List<Record> children = root.get("children");
        if(children != null && children.size() > 0){
            for(Record r : children){
                r.set("name",str+r.getStr("name"));
                if (list.contains(r)){
                    list.get(list.indexOf(r)).set("name",r.getStr("name"));
                }
                createEmulationalTree(r, list, parentName, name,str);
            }
        }
    }

    /**
     * 通过在分类名称前面加全角空格造出一个仿真树
     * @param root 根节点对象
     * @param list 要生成tree的集合
     */
    public static void createEmulationalTree(Record root, List<Record> list){
        createEmulationalTree(root, list, "parent_id", "id","");
    }

    /**
     * List<Record>转List<Map>
     * @param recordList
     * @return
     */
    public static List<Map<String,Object>> recordListToMapList(List<Record> recordList){
        List<Map<String,Object>> mapList=new ArrayList<>();
        for (Record record:recordList){
            Map<String,Object> map=record.getColumns();
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 将record对象转化成json对象
     * @param record
     * @return
     */
    public static JSONObject recordToJson(Record record){
        JSONObject result = JSONObject.parseObject(JSON.toJSONString(record.getColumns()));
        return result;
    }

    /**
     * 将JSONArray转化成List
     * @param jsonArray
     * @return
     */
    public static List<Record> jsonArrayToList(JSONArray jsonArray){
        List<Record> result = new ArrayList<>();
        if(jsonArray != null && jsonArray.size() > 0){
            for(int i = 0; i < jsonArray.size(); i++){
                Record record = jsonToRecord(jsonArray.getJSONObject(i));
                result.add(record);
            }
        }
        return result;
    }

    /**
     * 将单个JSONArray转化成JSONString
     * @param jsonArray
     * @param columnName
     * @return
     */
    public static String jsonArrayToString(JSONArray jsonArray,String columnName){
        Map<String,JSONArray> map=new HashMap<>(1);
        map.put(columnName,jsonArray);
        return JSON.toJSONString(map);
    }
}
