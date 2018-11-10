package com.works.pc.sys.controllers;

import com.common.controllers.BaseCtrl;
import com.constants.DictionaryConstants;
import com.constants.KEY;
import com.jfinal.plugin.activerecord.Record;
import com.utils.DateUtil;
import com.utils.HanyuPinyinHelper;
import com.utils.JsonHashMap;
import com.utils.UserSessionUtil;
import com.works.pc.sys.services.SysUserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

public class SysUserCtrl extends BaseCtrl<SysUserService> {

    public SysUserCtrl() {
        super(SysUserService.class);
    }

    private SysUserService service = enhance(SysUserService.class);
    /**
     * 修改自己密码
     */
    public void modifyMyPwd(){
        JsonHashMap jhm=new JsonHashMap();
        try{
            String currentPwd=getPara("currentPwd");
            String confirmPwd=getPara("confirmPwd");

            UserSessionUtil usu=new UserSessionUtil(getRequest());
            Record record = new Record();
            record.set("id", usu.getSysUserId());
            record.set("password", currentPwd);
            Record r= service.findOne(record);
            if(r!=null){
                r.set("password", confirmPwd);
                boolean flag = service.updateById(r);
                if(flag){
                    jhm.putCode(1);
                    jhm.putMessage("更新成功！");
                }else{
                    jhm.putCode(-1);
                    jhm.putMessage("更新失败！");
                }
            }else{
                jhm.putCode(-1);
                jhm.putMessage("密码错误！");
            }
        }catch(Exception e){
            e.printStackTrace();
            jhm.putCode(-1);
            jhm.putMessage(e.toString());
        }
        renderJson(jhm);
    }

    public void showMyDetail(){
        JsonHashMap jhm = new JsonHashMap();
        UserSessionUtil usu = new UserSessionUtil(getRequest());
        if(pleaseLogin(usu, jhm)){
            return;
        }
        try{
            Record r= service.findById(usu.getSysUserId());
            r.remove("password");
            jhm.putCode(1).put("data",r);
        }catch (Exception e){
            e.printStackTrace();
            jhm.putCode(-1).putMessage(e.toString());
        }
        renderJson(jhm);
    }

    public void getUserInfo(){
        JsonHashMap jhm = new JsonHashMap();
        try {
            UserSessionUtil usu = new UserSessionUtil(getRequest());
            if(pleaseLogin(usu, jhm)){
                return;
            }
            jhm.put("id",usu.getSysUserId());
            jhm.put("nickname",usu.getNickname());
            jhm.put("role_id",usu.getUserBean().get("role_id"));
            jhm.putCode(1);
            jhm.putMessage("");
        }catch(Exception e){
            e.printStackTrace();
            jhm.putCode(-1);
            jhm.putMessage(e.toString());

        }
        renderJson(jhm);
    }

    public void loginout(){
        JsonHashMap jhm=new JsonHashMap();
        try {
            HttpSession session = getSession();
            session.removeAttribute(KEY.SESSION_USER);
            session.invalidate();
            session = null;
            //清空cookie
            Cookie cookies[] = getCookieObjects();
            for(int i=0;i<cookies.length;i++){
                cookies[i].setMaxAge(0);
                setCookie(cookies[i]);
            }
            jhm.putCode(1);
        }catch (Exception e){
            e.printStackTrace();
            jhm.putCode(-1).putMessage(e.toString());
        }
        renderJson(jhm);
    }

    private boolean pleaseLogin(UserSessionUtil usu, JsonHashMap jhm){
        if(!usu.isLogin()){
            jhm.putCode(-1);
            jhm.putMessage("请先登录！");
            renderJson(jhm);
            return true;
        }
        return false;
    }

    @Override
    public void handleRecord(Record record) {
        if(record != null){
            String state_text = DictionaryConstants.DICT_STRING_MAP.get(DictionaryConstants.STATE).get(record.getStr("state"));
            record.set("state_text", state_text);
            String sexText = "0".equals(record.getStr("sex")) ? "女" : "男";
            record.set("sex_text", sexText);
            record.remove("password");
        }
    }

    @Override
    public void handleAddRecord(Record record) {
        record.set("state", 1);
        String pinyin = HanyuPinyinHelper.getFirstLettersLo(record.getStr("nickname"));
        record.set("pinyin", pinyin);
        record.set("updatedate", DateUtil.GetDateTime());
    }

    @Override
    public void handleUpdateRecord(Record record) {
        String pinyin = HanyuPinyinHelper.getFirstLettersLo(record.getStr("nickname"));
        record.set("pinyin", pinyin);
        record.set("updatedate", DateUtil.GetDateTime());
    }

    @Override
    public void createRecordBeforeSelect(Record record) {
        String keyword = this.getPara("keyword");
        if(keyword != null && keyword.trim().length() > 0){
            String keywords_key = "$all$and#username$like$or#pinyin$like$or#nickname$like$and";
            String[] keywords_value = {keyword, keyword, keyword};
            record.set(keywords_key, keywords_value);
        }
    }
}
