package com.runmate.configure.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expire}")
    private Long expire;

    public String createToken(String email) {
        if(email==null)
            throw new IllegalArgumentException();
        return JWT
                .create()
                .withSubject("runner")
                .withExpiresAt(new Date(System.currentTimeMillis()+ expire))
                .withClaim("userEmail", email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public boolean validate(String token) throws JWTVerificationException {
        String userEmail=
                JWT
                .require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token).getClaim("userEmail").asString();
        return userEmail!=null;
    }
    public String getClaim(String token){
        Map<String, Claim> claims=JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token).getClaims();
        return claims.get("userEmail").asString();
    }
}
