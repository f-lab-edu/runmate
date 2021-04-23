package com.runmate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.dto.AuthRequest;
import com.runmate.domain.user.CrewRole;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext ctx;
    @Autowired
    JwtProvider provider;

    @Autowired
    UserRepository userRepository;

    ObjectMapper mapper=new ObjectMapper();
    User user,another;
    String token,anotherToken;

    @BeforeEach
    public void setUp() throws Exception {
        userRepository.deleteAll();
        mockMvc= MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilter(new JwtAuthenticationFilter(provider))
                .addFilter(new CharacterEncodingFilter("UTF-8"))
                .build();

        user=new User();
        user.setEmail("anny@anny.com");
        user.setPassword("1234");
        user.setUsername("yousung");
        user.setRegion(new Region("zipcode1","address1","address2"));
        user.setHeight(178);
        user.setWeight(30);
        user.setCrewRole(CrewRole.NO);

        another=new User();
        another.setEmail("ppap@ppap.com");
        another.setUsername("ppap");
        another.setPassword("123");

        token= joinAndLogin(user);
        anotherToken= joinAndLogin(another);
    }
    public String joinAndLogin(User user) throws Exception {
        String body=mapper.writeValueAsString(user);
        AuthRequest authRequest=new AuthRequest();

        authRequest.setEmail(user.getEmail());
        authRequest.setPassword(user.getPassword());

        mockMvc.perform(post("/api/auth/local/new")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();


        body=mapper.writeValueAsString(authRequest);
        MvcResult result=mockMvc.perform(post("/api/auth/local/login")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader("Authorization").replace("Bearer ","");
    }

    @Test
    public void modifyShouldAuthorized() throws Exception {
        user.setWeight(133);
        user.setHeight(100);
        user.setUsername("small");
        user.setRegion(new Region("zipcode2","myaddress1","myaddress2"));

        String body=mapper.writeValueAsString(user);
        mockMvc.perform(put("/api/users/"+user.getEmail())  //update가 아니라 insert 쿼리가 실행된다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer "+token))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().string("success"));
    }
    @Test
    public void modifyShouldUnAuthorized() throws Exception {
        another.setHeight(999);
        String body=mapper.writeValueAsString(another);

        mockMvc.perform(put("/api/users/"+user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header("Authorization","Bearer "+anotherToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("it's not your email"));
    }
}
