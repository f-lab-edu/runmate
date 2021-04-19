package com.runmate.configure.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.runmate.domain.user.CrewRole;
import com.runmate.domain.user.User;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static final String secretKey="3q$.5pdz4563!2aghkz";
    private static final Long validateTime=1000*60*10L;

    public static String createToken(String email) {
        if(email==null)
            throw new IllegalArgumentException();
        return JWT
                .create()
                .withSubject("runner")
                .withExpiresAt(new Date(System.currentTimeMillis()+validateTime))
                .withClaim("userEmail", email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public static boolean validate(String token) throws JWTVerificationException {
        String userEmail=
                JWT
                .require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token).getClaim("userEmail").asString();
        return userEmail!=null;
    }
    public static String getClaim(String token){
        Map<String, Claim> claims=JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token).getClaims();
        return claims.get("userEmail").asString();
    }
}
