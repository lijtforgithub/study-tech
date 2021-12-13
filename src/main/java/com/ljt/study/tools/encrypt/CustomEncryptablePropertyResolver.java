package com.ljt.study.tools.encrypt;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

/**
 * @author LiJingTang
 * @date 2021-12-13 9:24
 */
@Slf4j
public class CustomEncryptablePropertyResolver implements EncryptablePropertyResolver {

    private final EncryptablePropertyDetector encryptablePropertyDetector;

    public CustomEncryptablePropertyResolver() {
        this.encryptablePropertyDetector = new CustomEncryptablePropertyDetector();
    }

    @Override
    public String resolvePropertyValue(String s) {
        if (encryptablePropertyDetector.isEncrypted(s)) {
            log.info("解密配置属性: {}", s);
            return new String(Base64.getDecoder().decode(encryptablePropertyDetector.unwrapEncryptedValue(s)));
        }

        return s;
    }

}
