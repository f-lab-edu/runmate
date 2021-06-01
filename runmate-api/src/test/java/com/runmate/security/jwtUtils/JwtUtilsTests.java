package com.runmate.security.jwtUtils;

import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JwtUtilsTests {
    @Autowired
    JwtProvider jwtProvider;

    @Test
    public void createTokenAndValidate() {
        User user = User.of()
                .email("anny@anny.com")
                .build();

        String token = jwtProvider.createToken(user.getEmail());
        System.out.println(token);

        assertEquals(jwtProvider.validate(token), true);
    }

    @Test
    public void getClaim() {
        User user = User.of()
                .email("anny@anny.com")
                .build();

        String token = jwtProvider.createToken(user.getEmail());
        assertEquals(jwtProvider.getClaim(token), user.getEmail());
    }
}
