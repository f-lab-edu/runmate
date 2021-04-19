package com.runmate.security.jwtUtils;

import com.runmate.configure.jwt.JwtUtils;
import com.runmate.domain.user.CrewRole;
import com.runmate.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtUtilsTests {

    @Test
    public void createTokenAndValidate(){
        User user=new User();
        user.setCrewRole(CrewRole.NO);
        user.setEmail("anny@anny.com");

        String token=JwtUtils.createToken(user.getEmail());
        System.out.println(token);

        assertEquals(JwtUtils.validate(token),true);
    }
    @Test
    public void getClaim(){
        User user=new User();
        user.setCrewRole(CrewRole.NO);
        user.setEmail("anny@anny.com");

        String token=JwtUtils.createToken(user.getEmail());
        assertEquals(JwtUtils.getClaim(token),user.getEmail());
    }
}
