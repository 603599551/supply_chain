package com.works.common.services;

import com.common.service.BaseService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

import static com.constants.ProcessConstants.PROCESS_STRINGARRAY_MAP;

/**
 * Tomcat启动，加载所有流程信息
 * @author CaryZ
 * @date 2018-12-02
 */
public class ProcessConfigService extends BaseService {

    public static final String TABLENAME="s_process_config";

    public ProcessConfigService() {
        super(TABLENAME, null);
    }

    public static final void loadProcessConfig(){
        PROCESS_STRINGARRAY_MAP.clear();
        List<Record> processList= Db.find("SELECT * FROM s_process_config");
        if (processList!=null&&processList.size()>0){
            for (Record record:processList){
                PROCESS_STRINGARRAY_MAP.put(record.getStr("process_name"),record.getStr("process_sequence").split(","));
            }
        }
    }


    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return null;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return null;
    }
}
