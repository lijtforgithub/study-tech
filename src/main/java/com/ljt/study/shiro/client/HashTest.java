package com.ljt.study.shiro.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.jupiter.api.Test;

/**
 * @author LiJingTang
 * @date 2021-06-21 11:25
 */
@Slf4j
class HashTest {

    @Test
    void testMd5(){
        String password = "admin";
        String salt = "salt";
        String result = new Md5Hash(password, salt, 1).toString();
        log.info(result);
    }

    @Test
    void testSimpleHash() {
        String password = "admin";
        String salt = "salt";
        String result = new SimpleHash("MD5", password, salt, 1).toString();
        log.info(result);
    }

}
