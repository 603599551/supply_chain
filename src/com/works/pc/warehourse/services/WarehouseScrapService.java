package com.works.pc.warehourse.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class WarehouseScrapService extends BaseService {

    private static String[] columnNameArr = {"id","warehouse_id","num","scrap_item","scrap_date","scrap_reason","scrap_user_id","remark","image"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","",""};

    public WarehouseScrapService() {
        super("s_warehouse_scrap", new TableBean("s_warehouse_scrap", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
