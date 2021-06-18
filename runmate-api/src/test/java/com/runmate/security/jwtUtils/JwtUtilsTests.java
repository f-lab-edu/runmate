package com.runmate.security.jwtUtils;

import com.runmate.TestActiveProfilesResolver;
import com.runmate.configure.jwt.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
public class JwtUtilsTests {
    @Autowired
    JwtProvider jwtProvider;

    @Test
    public void createTokenAndValidate() {
        final String email = "anny@anny.com";
        String token = jwtProvider.createToken(email);
        assertEquals(jwtProvider.validate(token), true);
    }

    @Test
    public void getClaim() {
        final String email = "anny@anny.com";
        String token = jwtProvider.createToken(email);
        assertEquals(jwtProvider.getClaim(token), email);
    }
}
