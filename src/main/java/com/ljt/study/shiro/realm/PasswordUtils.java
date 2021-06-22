package com.ljt.study.shiro.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.util.Assert;

/**
 * @author LiJingTang
 * @date 2021-06-22 09:58
 */
class PasswordUtils {

    private static final String SALT = "shiro";

    private PasswordUtils() {}

    static String encode(String content) {
        Assert.isTrue(StringUtils.isNotBlank(content), "内容为空");
        return new Md5Hash(content.trim(), SALT, 1).toString();
    }

    static boolean match(String content, String password) {
        return encode(content).equals(password.trim());
    }

}
