package com.works.app.sorting.controllers;

import com.common.controllers.WxSmallProgramCtrl;
import com.jfinal.plugin.activerecord.Db;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.app.sorting.services.SortingOrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SortingOrderCtrl extends WxSmallProgramCtrl {

    private SortingOrderService service = enhance(SortingOrderService.class);

    public void downOrder(){
        JsonHashMap jhm = new JsonHashMap();
        List<Map<String, Object>> orderList = service.downOrder(getUserSessionUnit());
        if(orderList != null && orderList.size() > 0){
            jhm.put("data", orderList);
        }else{
            jhm.put("data", new ArrayList<>());
        }
        renderJson(jhm);
    }

    public void submitOrder(){
        JsonHashMap jhm = new JsonHashMap();
        try{
            String[] ids = getParaValues("ids");
            service.submitOrder(ids);
            jhm.putMessage("提交成功！");
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putCode(-1).putMessage("数据加载失败！");
        }
        renderJson(jhm);
    }

}
