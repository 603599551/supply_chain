package com.works.pc.order.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.order.services.OrderScrapService;

public class OrderScrapCtrl extends BaseCtrl<OrderScrapService> {

    public OrderScrapCtrl() {
        super(OrderScrapService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
