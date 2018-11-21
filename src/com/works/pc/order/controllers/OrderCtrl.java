package com.works.pc.order.controllers;

import com.alibaba.fastjson.JSONObject;
import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.utils.UserSessionUtil;
import com.works.pc.order.services.OrderService;

public class OrderCtrl extends BaseCtrl<OrderService> {

    public OrderCtrl() {
        super(OrderService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

    @Override
    public void handleAddRecord(Record record) {

    }

    @Override
    public void handleUpdateRecord(Record record) {

    }

    @Override
    public void createRecordBeforeSelect(Record record) {

    }

    public void createOrder() {
        JSONObject json = this.getJson(getRequest());
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        try {
            service.createOrder(json, usu);
        } catch (Exception e) {

        }
    }
}
