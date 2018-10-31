package com.works.pc.goods.controllers;

import com.common.controllers.BaseCtrl;
import com.jfinal.plugin.activerecord.Record;
import com.works.pc.goods.services.ProductService;

public class ProductCtrl extends BaseCtrl<ProductService> {

    public ProductCtrl() {
        super(ProductService.class);
    }

    @Override
    public void handleRecord(Record record) {

    }

}
