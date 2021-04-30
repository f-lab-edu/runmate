package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.dto.AuthRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext ctx;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    UserRepository userRepository;
    User user;
    ObjectMapper mapper = new ObjectMapper();
    static String ADDRESS = "you@you.com";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilter(new JwtAuthenticationFilter(jwtProvider))
                .addFilter(new CharacterEncodingFilter("UTF-8"))
                .build();

        user = userRepository.findByEmail(ADDRESS);
    }

    @Test
    public void When_Modify_InvalidUserDomain_Expect_Status_ClientError_Body_ErrorMessage() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user.getEmail());
        authRequest.setPassword(user.getPassword());

        String jsonBody = mapper.writeValueAsString(authRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/local/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getHeader("Authorization").replace("Bearer ", "");

        String invalidRequestBody = " {\t\t\t\t \t\n" +
                "\t\"id\":1,\n" +
                "\t\"email\":\"you@you.com\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"username\":null,\n" +
                "\t\"region\":null,\n" +
                "\t\"introduction\":\"메일 뛰자!\",\"grade\":\"UNRANKED\",\n" +
                "\t\"createdAt\":\"2020-08-22 12:30:30\"\n" +
                " }";

        jsonBody = mapper.writeValueAsString(user);
        mockMvc.perform(put("/api/users/" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
