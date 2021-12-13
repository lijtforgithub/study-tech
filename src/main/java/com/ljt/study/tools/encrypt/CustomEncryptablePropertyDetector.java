package com.ljt.study.tools.encrypt;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2021-12-13 9:18
 */
@Slf4j
class CustomEncryptablePropertyDetector implements EncryptablePropertyDetector {

    private static final String PREFIX = "encrypt.";

    @Override
    public boolean isEncrypted(String s) {
        boolean flag = Objects.nonNull(s) && s.startsWith(PREFIX);
        log.info("是否需要解密: {} = {}", s, flag);
        return flag;
    }

    @Override
    public String unwrapEncryptedValue(String s) {
        return s.substring(PREFIX.length());
    }

}
