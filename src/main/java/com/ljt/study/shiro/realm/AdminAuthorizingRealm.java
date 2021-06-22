package com.ljt.study.shiro.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import static com.ljt.study.shiro.realm.LoginTypeEnum.ADMIN;
import static com.ljt.study.shiro.realm.PasswordUtils.encode;

/**
 * @author LiJingTang
 * @date 2021-06-21 11:35
 */
@Slf4j
class AdminAuthorizingRealm extends AuthorizingRealm {

    /**
     * 模拟调试用
     */
    static final String AUTH_PWD = "admin";

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof CustomAuthToken && ADMIN == ((CustomAuthToken) token).getLoginTypeEnum();
    }

    /**
     * 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("开始认证：{}", this.getClass().getSimpleName());
        CustomAuthToken authToken = (CustomAuthToken) token;
        final String username = authToken.getUsername();
        // 根据username去后台系统数据库查询
        return new SimpleAuthenticationInfo(username, encode(AUTH_PWD), getName());
    }

    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        super.setCredentialsMatcher(new CustomCredentialsMatcher());
    }

}
