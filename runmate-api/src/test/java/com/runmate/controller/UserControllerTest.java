package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.TestActiveProfilesResolver;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.user.User;
import com.runmate.repository.user.UserRepository;
import com.runmate.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.net.URL;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
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

    String token;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilter(new JwtAuthenticationFilter(jwtProvider))
                .addFilter(new CharacterEncodingFilter("UTF-8"))
                .build();

        user = userRepository.findByEmail(ADDRESS).orElseThrow(NotFoundUserEmailException::new);

        String jsonBody = "{\n" +
                "\t\"email\":\"you@you.com\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";

        MvcResult result = mockMvc.perform(post("/api/auth/local/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated())
                .andReturn();

        token = result.getResponse().getHeader("Authorization").replace("Bearer ", "");
    }

    @Test
    public void When_Modify_UserDomain_Expect_Status_OK() throws Exception {
        String requestBody = "{\n" +
                "\t\"id\":1,\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"username\":\"you\",\n" +
                "\t\"region\":{\n" +
                "\t\t\"si\":\"seoul\",\n" +
                "\t\t\"gu\":\"nowon\",\n" +
                "\t\t\"gun\":null\n" +
                "\t},\n" +
                "\t\"introduction\":\"메일 뛰자!\"\n" +
                "}";

        mockMvc.perform(put("/api/users/" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.username", is("you")))
                .andExpect(jsonPath("$.data.region.si", is("seoul")))
                .andExpect(jsonPath("$.data.region.gu", is("nowon")))
                .andExpect(jsonPath("$.data.region.gun", nullValue()))
                .andExpect(jsonPath("$.data.introduction", is("메일 뛰자!")));
    }

    @Test
    public void When_Modify_InvalidUserDomain_Expect_Status_ClientError_Body_ErrorMessage() throws Exception {
        String invalidRequestBody = "{\n" +
                "\t\"id\":1,\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"region\":null,\n" +
                "\t\"introduction\":\"메일 뛰자!\"\n" +
                "}";

        mockMvc.perform(put("/api/users/" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void When_Get_User_Expect_Status_OK_Body_UserJson() throws Exception {
        mockMvc.perform(get("/api/users/" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    //can be access s3
    @Test
    public void When_UploadImage_Expect_Status_OK() throws Exception {
        final String requestUrl = "/api/users/" + user.getEmail() + "/thumbnail";
        final String keyName = "";

        URL url = new URL("https://blog.kakaocdn.net/dn/cfcwdg/btq9E5yyy5I/17QtKpp8A7Ylg4XGUo3XN0/img.jpg");
        MockMultipartFile file = new MockMultipartFile("file",
                "sky.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                url.openStream());

        MvcResult result = mockMvc.perform(multipart(requestUrl)
                .file(file)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertTrue(body.contains(keyName));
    }
}
