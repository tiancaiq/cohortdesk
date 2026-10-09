package com.github.tiancaiq.cohortdesk.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static final byte[] SIGN_KEY = signingKey();
    private static final long EXPIRE = 43200000L;

    private static byte[] signingKey() {
        String configured = System.getenv("COHORTDESK_JWT_SECRET");
        if (configured != null && !configured.isBlank()) {
            byte[] key = configured.getBytes(StandardCharsets.UTF_8);
            if (key.length < 32) {
                throw new IllegalStateException("COHORTDESK_JWT_SECRET must be at least 32 bytes.");
            }
            return key;
        }
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public static String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SIGN_KEY)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .compact();
    }

    public static Claims parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(SIGN_KEY)
                .parseClaimsJws(jwt)
                .getBody();
    }
}
