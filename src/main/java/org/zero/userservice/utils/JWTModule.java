package org.zero.userservice.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class JWTModule {

    @Value("${jwt.sessionDuration}")
    private int sessionDuration;
    @Value("${jwt.refreshDuration}")
    private int refreshDuration;
    @Value("${jwt.sessionSecretKey}")
    private String sessionSecretKey;
    @Value("${jwt.refreshSecretKey}")
    private String refreshSecretKey;

    public String issueSession(String userId, Boolean refreshed, String roles) {
        var now = Instant.now();

        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(now)
                .withClaim("rwt", refreshed)
                .withClaim("rls", roles)
                .withExpiresAt(now.plus(Duration.ofSeconds(sessionDuration)))
                .sign(Algorithm.HMAC256(sessionSecretKey));
    }

    public String issueRefresh(String userId) {
        var now = Instant.now();

        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(Duration.ofDays(refreshDuration)))
                .sign(Algorithm.HMAC256(refreshSecretKey));
    }
    public DecodedJWT decodeSession(String token) throws JWTVerificationException {
        return this.decode(token, sessionSecretKey);
    }
    public DecodedJWT decodeRefresh(String token) throws JWTVerificationException {
        return this.decode(token, refreshSecretKey);
    }

    private DecodedJWT decode(String token, String secretKey) throws JWTVerificationException {
        var decoded = JWT.require(Algorithm.HMAC256(secretKey)).build();
        return decoded.verify(token);
    }
}
