package com.db2.sso.server.service;


import com.db2.sso.server.model.DemoLoginUser;
import com.db2.sso.server.model.LoginUser;

/**
 * 序列化DemoLoginUser
 * 
 * @author db2
 *
 */
public class DemoUserSerializer extends UserSerializer {

    @Override
    protected void translate(LoginUser loginUser, UserData userData) throws Exception {
        
        // 实现类型已知，可强制转换
        DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;
        userData.setId(demoLoginUser.getLoginName());
        userData.setProperty("name", demoLoginUser.getLoginName());
        userData.setProperty("dept", "信息部");
        userData.setProperty("post", "IT管理员");
    }

}
