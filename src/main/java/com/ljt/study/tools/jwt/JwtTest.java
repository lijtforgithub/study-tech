package com.ljt.study.tools.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author LiJingTang
 * @date 2021-06-25 09:10
 */
@Slf4j
class JwtTest {

    private static final String USER_NAME = "userName";
    private static final String ACCOUNT_ID = "1";
    private static final String SECRET = "123456";

    @Test
    @SneakyThrows
    void create() {
        String token = createToken();
        log.info(token);
    }

    @SneakyThrows
    private String createToken() {
        long time = System.currentTimeMillis() + (60 * 2) * 1000;
        // 默认 SignatureAlgorithm.HS256
        return JWT.create()
                .withSubject(ACCOUNT_ID)
                .withClaim(USER_NAME, "璟瑜")
                .withExpiresAt(new Date(time))
                .sign(Algorithm.HMAC256(SECRET));
    }

    @Test
    @SneakyThrows
    void createWithHeader() {
        long time = System.currentTimeMillis() + (60 * 2) * 1000;
        // 默认 SignatureAlgorithm.HS256
        String token = JWT.create()
                .withHeader(ImmutableMap.of("alg", "HS512"))
                .withSubject(ACCOUNT_ID)
                .withClaim(USER_NAME, "璟瑜")
                .withExpiresAt(new Date(time))
                .sign(Algorithm.HMAC512(SECRET));
        log.info(token);
    }

    @Test
    void decode() {
        String token = createToken();
        DecodedJWT decode = JWT.decode(token);
        log.info(decode.getSubject());
        log.info(decode.getClaim(USER_NAME).asString());
    }

    @Test
    @SneakyThrows
    void verify() {
        String token = createToken();
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        Verification verification = JWT.require(algorithm);
        try {
            verification.build().verify(token);
            log.info("校验成功");
        } catch (JWTVerificationException e) {
            log.error("校验失败", e);
        }
    }


}
