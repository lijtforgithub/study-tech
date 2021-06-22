package com.ljt.study.shiro.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

import static com.ljt.study.shiro.realm.LoginWayEnum.CODE;
import static com.ljt.study.shiro.realm.LoginWayEnum.PWD;
import static com.ljt.study.shiro.realm.PasswordUtils.match;

/**
 * @author LiJingTang
 * @date 2021-06-22 10:23
 */
@Slf4j
class CustomCredentialsMatcher implements CredentialsMatcher {

    /**
     * 模拟验证码 应该去redis查询
     */
    static final String AUTH_CODE = "123456";

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        if (!(token instanceof CustomAuthToken)) {
            log.warn("未知认证Token：{}", token.getClass().toString());
            return false;
        }

        CustomAuthToken authToken = (CustomAuthToken) token;
        if (CODE == authToken.getLoginWayEnum()) {
            return AUTH_CODE.equals(authToken.getCode());
        } else if (PWD == authToken.getLoginWayEnum()) {
            return match(new String(authToken.getPassword()), info.getCredentials().toString());
        }

        return false;
    }

}
