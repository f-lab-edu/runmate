package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.user.User;
import com.runmate.dto.AuthRequest;
import com.runmate.repository.user.UserRepository;
import com.runmate.service.crew.CrewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class CrewControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext ctx;
    @Autowired
    JwtProvider provider;

    @Autowired
    CrewService crewService;

    User noCrewUser;
    User withCrewUser;

    static final String WITH_CREW_USER_EMAIL = "you@you.com";
    static final String NO_CREW_USER_EMAIL = "min@gmail.com";

    @Autowired
    UserRepository userRepository;

    final ObjectMapper mapper = new ObjectMapper();

    String noCrewToken;
    String withCrewToken;

    @BeforeEach
    void createValidToken() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilter(new JwtAuthenticationFilter(provider))
                .addFilter(new CharacterEncodingFilter("utf8"))
                .build();

        noCrewUser = userRepository.findByEmail(NO_CREW_USER_EMAIL);
        withCrewUser = userRepository.findByEmail(WITH_CREW_USER_EMAIL);

        AuthRequest noCrewRequest = new AuthRequest(noCrewUser.getEmail(), noCrewUser.getPassword());
        AuthRequest withCrewRequest = new AuthRequest(withCrewUser.getEmail(), withCrewUser.getPassword());

        String noCrewRequestBody = mapper.writeValueAsString(noCrewRequest);
        String withCrewRequestBody = mapper.writeValueAsString(withCrewRequest);

        MvcResult noCrewResult = getAuthorization(noCrewRequestBody);
        MvcResult withCrewResult = getAuthorization(withCrewRequestBody);

        noCrewToken = getToken(noCrewResult);
        withCrewToken = getToken(withCrewResult);
    }

    private MvcResult getAuthorization(String requestBody) throws Exception {
        return mockMvc.perform(post("/api/auth/local/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn();
    }

    private String getToken(MvcResult result) {
        return Objects.requireNonNull(result.getResponse().getHeader("Authorization"))
                .replace("Bearer ", "");
    }

    @Test
    @DisplayName("크루에 속하지 않은 회원의 정상적인 크루 생성 요청은 처리되고 201 Created 응답을 받음")
    void When_CreateCrew_WithNotBelongToAnyCrew_Expect_Status_Created_Body_success() throws Exception {
        String requestBody = "{\n" +
                "  \"email\": \"" + NO_CREW_USER_EMAIL + "\", \n" +
                "  \"data\": {\n" +
                "    \"name\" : \"my crew\",\n" +
                "    \"description\" : \"first crew\",\n" +
                "    \"region\" : {\n" +
                "      \"si\" : \"seoul\",\n" +
                "      \"gu\" : \"nowon\",\n" +
                "      \"gun\" : null\n" +
                "    },\n" +
                "    \"grade_limit\" : \"UNRANKED\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(
                post("/api/crews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer " + noCrewToken)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("success"));
    }

    @Test
    @DisplayName("크루에 이미 속한 회원의 크루 생성 요청은 403 Forbidden 응답을 받음")
    void When_CreateCrew_WithBelongToCrew_Expect_Status_Forbidden() throws Exception {
        String requestBody = "{\n" +
                "  \"email\": \"" + WITH_CREW_USER_EMAIL + "\", \n" +
                "  \"data\": {\n" +
                "    \"name\" : \"your crew\",\n" +
                "    \"description\" : \"with crew\",\n" +
                "    \"region\" : {\n" +
                "      \"si\" : \"seoul\",\n" +
                "      \"gu\" : \"nowon\",\n" +
                "      \"gun\" : null\n" +
                "    },\n" +
                "    \"grade_limit\" : \"UNRANKED\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(
                post("/api/crews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer " + withCrewToken)
        )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("자신의 등급보다 높은 등급 제한을 가진 크루 생성 요청은 403 Forbidden 응답을 받음")
    void When_CreateCrew_SettingGradeLimit_WithUpperThanOwnGrade_Expect_Forbidden() throws Exception {
        String requestBody = "{\n" +
                "  \"email\": \"" + NO_CREW_USER_EMAIL + "\", \n" +
                "  \"data\": {\n" +
                "    \"name\" : \"upper crew\",\n" +
                "    \"description\" : \"upper than own grade crew\",\n" +
                "    \"region\" : {\n" +
                "      \"si\" : \"seoul\",\n" +
                "      \"gu\" : \"nowon\",\n" +
                "      \"gun\" : null\n" +
                "    },\n" +
                "    \"grade_limit\" : \"GOLD\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(
                post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void delete() {
    }
}