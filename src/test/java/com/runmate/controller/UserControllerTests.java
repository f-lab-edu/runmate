package com.runmate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.domain.user.CrewRole;
import com.runmate.domain.user.Region;
import com.runmate.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper=new ObjectMapper();
    @Test
    public void join() throws Exception {
        User user=new User();
        user.setEmail("anny@anny.com");
        user.setPassword("1234");
        user.setUsername("yousung");
        user.setRegion(new Region("zipcode","address1","address2"));
        user.setHeight(178);
        user.setWeight(30);
        user.setCrewRole(CrewRole.NO);

        String body=mapper.writeValueAsString(user);

        System.out.println(body);

        mockMvc.perform(post("/api/auth/local/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }
}
