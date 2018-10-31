package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;

public class AddressService extends BaseService {

    private static String[] columnNameArr = {"id","city","province","address"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","",""};

    public AddressService() {
        super("s_address", new TableBean("s_address", columnNameArr, columnTypeArr, columnCommentArr));
    }
}
