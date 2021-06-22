package com.ljt.study.shiro.realm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.util.Assert;

import java.util.Objects;

import static com.ljt.study.shiro.realm.LoginTypeEnum.ADMIN;
import static com.ljt.study.shiro.realm.LoginTypeEnum.USER;
import static com.ljt.study.shiro.realm.LoginWayEnum.CODE;
import static com.ljt.study.shiro.realm.LoginWayEnum.PWD;

/**
 * @author LiJingTang
 * @date 2021-06-21 20:21
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
class CustomAuthToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 6956496934470156422L;

    private LoginTypeEnum loginTypeEnum;
    private LoginWayEnum loginWayEnum;

    private String code;

    private CustomAuthToken(LoginTypeEnum loginTypeEnum, LoginWayEnum loginWayEnum, String username, String password, String code) {
        this.loginTypeEnum = loginTypeEnum;
        this.loginWayEnum = loginWayEnum;
        this.code = code;
        setUsername(username);
        setPassword(Objects.nonNull(password) ? password.toCharArray() : null);
    }

    static CustomAuthToken newAdminInstance(LoginWayEnum loginWayEnum, String username, String pwdOrCode) {
        return newInstance(ADMIN, loginWayEnum, username, pwdOrCode);
    }

    private static CustomAuthToken newInstance(LoginTypeEnum loginTypeEnum, LoginWayEnum loginWayEnum, String username, String pwdOrCode) {
        Assert.notNull(loginWayEnum, "登录方式为空");

        String password = null;
        String code = null;

        if (PWD == loginWayEnum) {
            password = pwdOrCode;
        }
        if (CODE == loginWayEnum) {
            code = pwdOrCode;
        }

        return new CustomAuthToken(loginTypeEnum, loginWayEnum, username, password, code);
    }

    static CustomAuthToken newUserInstance(LoginWayEnum loginWayEnum, String username, String pwdOrCode) {
        return newInstance(USER, loginWayEnum, username, pwdOrCode);
    }

}
