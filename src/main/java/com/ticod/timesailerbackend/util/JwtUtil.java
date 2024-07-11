package com.ticod.timesailerbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    public static String createToken(String userId, SecretKey key, long expireTime) {
        return Jwts.builder()
                .claim("userId", userId)
                .subject(userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public static Claims getClaimsFrom(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key).build()
                .parseEncryptedClaims(token)
                .getPayload();
    }

    public static String getUserIdFrom(String token, SecretKey key) {
        return getClaimsFrom(token, key)
                .get("userId", String.class);
    }

    public static boolean isExpired(String token, SecretKey key) {
        return getClaimsFrom(token, key)
                .getExpiration()
                .before(new Date());
    }

}
