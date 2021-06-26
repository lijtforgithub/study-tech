package com.ljt.study.shiro.realm;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ljt.study.shiro.realm.LoginWayEnum.CODE;
import static com.ljt.study.shiro.realm.LoginWayEnum.PWD;

/**
 * @author LiJingTang
 * @date 2021-06-22 09:15
 */
@Slf4j
class RealmTest {

    @BeforeAll
    static void beforeAll() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        List<Realm> realms = Lists.newArrayList(new UserAuthorizingRealm(), new AdminAuthorizingRealm());

        FirstSuccessfulStrategy firstSuccessfulStrategy = new FirstSuccessfulStrategy();
        firstSuccessfulStrategy.setStopAfterFirstSuccess(true);
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(firstSuccessfulStrategy);
        authenticator.setRealms(realms);
        securityManager.setAuthenticator(authenticator);

        SecurityUtils.setSecurityManager(securityManager);
    }

    @Test
    void user() {
        Subject subject = SecurityUtils.getSubject();
        subject.login(CustomAuthToken.newUserInstance(PWD, "user", UserAuthorizingRealm.AUTH_PWD));
        log.info(subject.getPrincipal().toString());
    }

    @Test
    void admin() {
        Subject subject = SecurityUtils.getSubject();
        subject.login(CustomAuthToken.newAdminInstance(CODE, "admin", CustomCredentialsMatcher.AUTH_CODE));
        log.info(subject.getPrincipal().toString());
    }

}
