package com.ljt.study.shiro.client;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 认证
 *
 * @author LiJingTang
 * @date 2021-06-21 09:01
 */
@Slf4j
class AuthenticationTest {

    private static final String SYS = "system";
    private static final String ADMIN = "admin";
    private static final String LIJT = "lijt";
    private static final IniRealm INI_REALM = new IniRealm("classpath:shiro/client.ini");

    @Test
    @SneakyThrows
    void test() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealm(INI_REALM);

        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();

        Session session = subject.getSession();
        final String key = "key";
        session.setAttribute(key, "session-value");

        UsernamePasswordToken token = new UsernamePasswordToken(SYS, SYS);
        token.setRememberMe(true);

        if (!subject.isAuthenticated()) {
            try {
                subject.login(token);
                log.info("登陆成功");
            } catch (UnknownAccountException e) {
                log.error("登陆失败：未知账号");
            } catch (IncorrectCredentialsException e) {
                log.error("登陆失败：密码错误");
            } catch (LockedAccountException e) {
                log.error("登陆失败：账号锁定");
            } catch (AuthenticationException e) {
                log.error("登陆失败", e);
            }
        }

        log.info(subject.getPrincipal().toString());
        log.info(subject.getPrincipals().toString());
        log.info("有system角色：{}", subject.hasRole(SYS));
        log.info("有user角色：{}", subject.hasRole("user"));

        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            log.info(session.getAttribute(key).toString());
            latch.countDown();
        }).start();

        latch.await();
        subject.logout();
    }

    @Test
    void multiRealm() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        List<Realm> realms = Lists.newArrayList(INI_REALM, new AdminRealm(), new CustomRealm());
        securityManager.setRealms(realms);
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(LIJT, LIJT);
        subject.login(token);

        log.info(subject.getPrincipal().toString());
    }

    @Test
    void strategy() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        List<Realm> realms = Lists.newArrayList(new AdminRealm(), new CustomRealm(), INI_REALM);

        FirstSuccessfulStrategy firstSuccessfulStrategy = new FirstSuccessfulStrategy();
        firstSuccessfulStrategy.setStopAfterFirstSuccess(true);
        // 设置策略 默认 AtLeastOneSuccessfulStrategy
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(firstSuccessfulStrategy);
        authenticator.setRealms(realms);
        securityManager.setAuthenticator(authenticator);
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(ADMIN, ADMIN);
        subject.login(token);

        log.info(subject.getPrincipal().toString());
        log.info(subject.getPrincipals().toString());
    }


    private static class AdminRealm implements Realm {

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public boolean supports(AuthenticationToken token) {
            return token instanceof UsernamePasswordToken;
        }

        @Override
        public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
            log.info("AdminRealm 开始认证");
            String username = (String) token.getPrincipal();
            String password = new String((char[]) token.getCredentials());
            if (!ADMIN.equals(username)) {
                throw new UnknownAccountException();
            }
            if (!ADMIN.equals(password)) {
                throw new IncorrectCredentialsException();
            }
            return new SimpleAuthenticationInfo(username, password, getName());
        }
    }

    private static class CustomRealm implements Realm {

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public boolean supports(AuthenticationToken token) {
            return token instanceof UsernamePasswordToken;
        }

        @Override
        public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
            log.info("CustomRealm 开始认证");
            return new SimpleAuthenticationInfo(LIJT, LIJT, getName());
        }
    }

}
