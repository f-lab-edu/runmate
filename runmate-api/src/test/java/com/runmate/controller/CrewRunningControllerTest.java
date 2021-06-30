package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.TestActiveProfilesResolver;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.User;
import com.runmate.dto.AuthRequest;
import com.runmate.exception.NotFoundCrewUserException;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
class CrewRunningControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext ctx;
    @Autowired
    JwtProvider provider;

    User leaderUser;
    String leaderToken;
    CrewUser leaderCrewUser;

    static final String WITH_CREW_USER_EMAIL = "you@you.com";

    @Autowired
    UserService userService;
    @Autowired
    CrewUserRepository crewUserRepository;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void createValidToken() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .addFilter(new JwtAuthenticationFilter(provider))
                .addFilter(new CharacterEncodingFilter("utf8"))
                .build();

        leaderUser = userService.findByEmail(WITH_CREW_USER_EMAIL);
        AuthRequest withCrewRequest = new AuthRequest(leaderUser.getEmail(), leaderUser.getPassword());
        String withCrewRequestBody = mapper.writeValueAsString(withCrewRequest);
        MvcResult withCrewResult = getAuthorization(withCrewRequestBody);

        leaderCrewUser = crewUserRepository.findByUser(leaderUser).orElseThrow(NotFoundCrewUserException::new);
        leaderToken = getToken(withCrewResult);
    }

    private MvcResult getAuthorization(String requestBody) throws Exception {
        return mockMvc.perform(post("/api/auth/local/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Authorization"))
                .andReturn();
    }

    private String getToken(MvcResult result) {
        return Objects.requireNonNull(result.getResponse().getHeader("Authorization"))
                .replace("Bearer ", "");
    }

    @Test
    @DisplayName("초기 초대 멤버 없이 팀 생성 요청은 201 Created 응답을 받고 응답 본문의 data 속성이 비어있음")
    void When_CreateTeam_WithNoInitialMember_Expect_StatusCreated_BodyDataIsEmptyArray() throws Exception {
        String requestBody = "{\n" +
                "  \"leader_id\": " + leaderCrewUser.getId() + ", \n" +
                "  \"title\": \"my team\", \n" +
                "  \"emails\": [], \n" +
                "  \"goal\": {\n" +
                "    \"distance\": 5.0, \n" +
                "    \"running_time\": 1500\n" +
                "  },\n" +
                "  \"start_time\" : \"2021-06-29 23:30:30\"" + "\n" +
                "}";

        ResultActions result = mockMvc.perform(
                post("/api/crew-running/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + leaderToken)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/api/crew-running/teams/*"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("초기 초대 멤버를 지닌 팀 생성 요청은 201 Created 응답을 받고 응답 본문의 data 속성은 생성된 팀멤버 객체의 id에 대한 uri의 배열로 구성됨")
    void When_CreateTeam_WithInitialMembers_Expect_StatusCreated_BodyDataIsArrayContainingUris() throws Exception {
        String requestBody = "{\n" +
                "  \"leader_id\": " + leaderCrewUser.getId() + ", \n" +
                "  \"title\": \"my team\", \n" +
                "  \"emails\": [" +
                "  \"one@gmail.com\", \"two@gmail.com\"" +
                "  ], \n" +
                "  \"goal\": {\n" +
                "    \"distance\": 5.0, \n" +
                "    \"running_time\": 1500\n" +
                "  },\n" +
                "  \"start_time\" : \"2021-06-29 23:30:30\"" + "\n" +
                "}";

        ResultActions result = mockMvc.perform(
                post("/api/crew-running/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + leaderToken)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/api/crew-running/teams/*"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0]").isString())
                .andExpect(jsonPath("$.data[1]").isString());
    }

    @Test
    @DisplayName("초기 초대 멤버의 이메일이 같은 크루 멤버의 것이 아니라면 404 Not Found 응답을 받음")
    void When_CreateTeam_WithNotSameCrewMemberEmail_Expect_StatusNotFound() throws Exception {
        String requestBody = "{\n" +
                "  \"leader_id\": " + leaderCrewUser.getId() + ", \n" +
                "  \"title\": \"my team\", \n" +
                "  \"emails\": [" +
                "  \"one@gmail.com\", \"sdfg@gmail.com\"" +
                "  ], \n" +
                "  \"goal\": {\n" +
                "    \"distance\": 5.0, \n" +
                "    \"running_time\": 1500\n" +
                "  },\n" +
                "  \"start_time\" : \"2021-06-29 23:30:30\"" + "\n" +
                "}";

        ResultActions result = mockMvc.perform(
                post("/api/crew-running/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + leaderToken)
        );

        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").isString());
    }
}
