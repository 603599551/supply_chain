package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SysUserService extends BaseService {

    private static String[] columnNameArr = {"id","role_id","username","password","nickname","sex","phone","entry_ids","state","remark","updatedate","pinyin"};
    private static String[] columnTypeArr = {"VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR","VARCHAR"};
    private static String[] columnCommentArr = {"","","","","","","","","","","",""};

    public SysUserService() {
        super("s_sys_user", new TableBean("s_sys_user", columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        SysRolesService sysRolesService = enhance(SysRolesService.class);
        try {
            List<Record> sysRoleList = sysRolesService.list(null);
            if(sysRoleList != null && sysRoleList.size() > 0){
                Map<String, String> sysRoleIdNameMap = new HashMap<>();
                for(Record r : sysRoleList){
                    sysRoleIdNameMap.put(r.getStr("id"), r.getStr("name"));
                }
                if(page != null && page.getList() != null && page.getList().size() > 0){
                    for(Record pageRecord : page.getList()){
                        pageRecord.set("role_text", sysRoleIdNameMap.get(pageRecord.getStr("role_id")));
                    }
                }
            }
        } catch (PcException e) {
            e.printStackTrace();
        }
        return page;
     }

    /**
     * 所有用户的id(key)和Record(value)  包括id和nickname
     * @return
     */
     public Map<String,Record> getUsers(){
        List<Record> list=Db.find("SELECT id,nickname FROM s_sys_user");
         Map<String,Record> map=new HashMap<>(list.size());
         for (Record record:list){
             map.put(record.getStr("id"),record);
         }
         return map;
     }

    /**
     * 流程的下一处理人列表
     * 根据角色名称得到属于该角色的所有可用用户
     * @param name
     * @return
     */
     public List<Record> getToUsers(String name){
         return Db.find("SELECT id value,nickname name FROM s_sys_user WHERE role_id=(SELECT id FROM s_sys_roles WHERE name=?) AND state='1'",name);
     }


     public Record login(Record record) throws PcException {
         return this.findOne(record);
     }
}
