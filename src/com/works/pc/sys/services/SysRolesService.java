package com.works.pc.sys.services;

import com.common.service.BaseService;
import com.bean.TableBean;
import com.exception.PcException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.UUIDTool;

import java.util.ArrayList;
import java.util.List;

public class SysRolesService extends BaseService {

    private static String[] columnNameArr = {"id", "name", "remark", "updatedate", "state"};
    private static String[] columnTypeArr = {"VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "INT"};
    private static String[] columnCommentArr = {"", "", "", "", ""};

    private SysAuthService sysAuthService = enhance(SysAuthService.class);

    public SysRolesService() {
        super("s_sys_roles", new TableBean("s_sys_roles", columnNameArr, columnTypeArr, columnCommentArr));
    }

    @Override
    public Record findById(String id) throws PcException{
        Record result = super.findById(id);
        Record record = new Record();
        record.set("role_id", id);
        List<Record> menuList = sysAuthService.list(record);
        if(menuList != null && menuList.size() > 0){
            List<String> menuIdList = new ArrayList<>(menuList.size());
            for(Record r : menuList){
                menuIdList.add(r.get("menu_id"));
            }
            result.set("menu_ids", menuIdList);
        }
        return result;
    }

    @Override
    public int delete(String... ids) throws PcException {
        if(ids != null){
            String sysUserSelect = "select count(*) from s_sys_user where role_id in({{mark}})";
            String mark = "";
            for(int i = 0; i < ids.length; i++){
                mark += "?,";
            }
            mark = mark.substring(0, mark.length() - 1);
            sysUserSelect = sysUserSelect.replace("{{mark}}", mark);
            int count = Db.queryInt(sysUserSelect, ids);
            if(count > 0){
                throw new PcException(DELETE_EXCEPTION, "该角色关联了用户，不能删除！");
            }
            String deleteAuthSql = "delete from s_sys_auth where role_id in({{mark}})".replace("{{mark}}", mark);
            Db.delete(deleteAuthSql, ids);
        }else{
            throw new PcException(DELETE_EXCEPTION, "删除记录的id没有传递！");
        }
        return super.delete(ids);
    }

    @Override
    public String add(Record record) throws PcException {
        try {
            String roleId = UUIDTool.getUUID();
            record.set("id", roleId);
            String[] menuIdArr = record.get("menu_ids");
            if(super.add(record) != null){
                boolean result = sysAuthService.addAssignRoleAuth(roleId, menuIdArr);
                if(result){
                    return roleId;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new PcException(ADD_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public boolean updateById(Record record) throws PcException {
        try {
            String roleId = record.getStr("id");
            String[] menuIdArr = record.get("menu_ids");
            boolean result = sysAuthService.addAssignRoleAuth(roleId, menuIdArr) && super.updateById(record);
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new PcException(ADD_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public List<Record> listBeforeReturn(List<Record> list) {
        return list;
    }

    @Override
    public Page<Record> queryBeforeReturn(Page<Record> page) {
        return page;
    }

}
