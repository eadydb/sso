package com.db2.sso.server.service;

import com.db2.sso.server.model.Credential;
import com.db2.sso.server.model.DemoLoginUser;
import com.db2.sso.server.model.LoginUser;

import java.util.Set;

/**
 * 示例性的鉴权处理器，当用户名和密码都为admin时授权通过，返回的也是一个示例性Credential实例
 * Created by db2 on 2016/2/21.
 */
public class DemoAuthenticationHandler implements IAuthenticationHandler {

    @Override
    public LoginUser authenticate(Credential credential) throws Exception {
        if ("admin".equals(credential.getParameter("name"))
                && "admin".equals(credential.getParameter("passwd"))) {
            DemoLoginUser loginUser = new DemoLoginUser();
            loginUser.setLoginName("admin");
            return loginUser;
        } else {
            credential.setError("error");
            return null;
        }
    }

    @Override
    public Set<String> authedSystemIds(LoginUser loginUser) throws Exception {
        return null;
    }
}
