package com.db2.sso.server.token;

import com.db2.sso.server.config.Config;
import com.db2.sso.server.model.ClientSystem;
import com.db2.sso.server.model.LoginUser;
import com.db2.sso.server.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储VT_USER信息，并提供操作方法
 * <p>
 * Created by db2 on 2016/2/21.
 */
public class TokenManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenManager.class);

    private static final Timer timer = new Timer(true);

    private static final Config config = SpringContextUtil.getBean(Config.class);


    static {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Map.Entry<String, Token> entry : DATA_MAP.entrySet()) {
                    String vt = entry.getKey();
                    Token token = entry.getValue();
                    Date expired = token.expired;
                    Date now = new Date();

                    // 当前时间大于过期时间
                    if (now.compareTo(expired) > 0) {
                        // 因为令牌支持自动延期服务，并且应用客户端缓存机制后，
                        // 令牌最后访问时间是存储在客户端的，所以服务端向所有客户端发起一次timeout通知，
                        // 客户端根据lastAccessTime + tokenTimeout计算是否过期，<br>
                        // 若未过期，用各客户端最大有效期更新当前过期时间
                        List<ClientSystem> clientSystems = config.getClientSystems();
                        Date maxClientExpired = expired;
                        for (ClientSystem clientSystem : clientSystems) {
                            Date clientExpired = clientSystem.noticeTimeout(vt, config.getTokenTimeout());
                            if (clientExpired != null
                                    && clientExpired.compareTo(now) > 0) {
                                maxClientExpired = maxClientExpired.compareTo(clientExpired) < 0 ? clientExpired : maxClientExpired;
                            }
                        }

                        if (maxClientExpired.compareTo(now) > 0) {
                            LOGGER.debug("更新过期时间,time={}", maxClientExpired);
                            token.expired = maxClientExpired;
                        } else {
                            LOGGER.debug("清除过期时间,vt={}", vt);
                            // 已过期，清除对应的token
                            DATA_MAP.remove(vt);
                        }

                    }
                }
            }
        }, 60 * 1000, 60 * 1000);
    }


    private TokenManager() {
    }

    // 复合结构
    private static class Token {
        private LoginUser loginUser;
        private Date expired;
    }

    // 令牌存储结构
    private static final Map<String, Token> DATA_MAP = new ConcurrentHashMap<>();


    /**
     * 验证令牌有效性
     *
     * @param vt
     * @return
     */
    public static LoginUser validate(String vt) {
        Token token = DATA_MAP.get(vt);
        return token == null ? null : token.loginUser;
    }

    /**
     * 用户授权成功后将授权信息存入
     *
     * @param vt
     * @param loginUser
     */
    public static void addToken(String vt, LoginUser loginUser) {
        Token token = new Token();
        token.loginUser = loginUser;

        //非自动登录时的处理
        token.expired = new Date(new Date().getTime() + config.getTokenTimeout() * 60 * 1000);

        // TODO 自动登录时，有效处理
        DATA_MAP.put(vt, token);
    }

    public static void invalid(String vt) {
        if (vt != null) {
            DATA_MAP.remove(vt);
        }
    }

}

