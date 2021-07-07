package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.TestActiveProfilesResolver;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.user.Grade;
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
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
class CrewRunningControllerTest {

    private static final String TEAM_MEMBER_RESOURCE_PATTERN = "http://[^/]+/api/crew-running/teams/[0-9]+/members/[0-9]+";
    private static final String TEAM_ID = "1";

    MockMvc mockMvc;

    @Autowired WebApplicationContext ctx;
    @Autowired JwtProvider provider;
    @Autowired UserService userService;
    @Autowired CrewUserRepository crewUserRepository;
    @Autowired ObjectMapper mapper;

    User leaderUser;
    String leaderToken;
    CrewUser leaderCrewUser;

    private static final String WITH_CREW_USER_EMAIL = "you@you.com";
    private static final String DIFFERENT_CREW_EMAIL = "sung@sung.com";

    private static final String TEAM_TITLE = "my team";
    private static final String FIRST_MEMBER_EMAIL = "one@gmail.com";
    private static final String SECOND_MEMBER_EMAIL = "two@gmail.com";
    private static final double TEAM_GOAL_DISTANCE = 5.0d;
    private static final int TEAM_GOAL_TIME = 1500;
    private static final String TEAM_START_TIME = "2021-06-29 23:30:30";

    private static final String INVITED_MEMBER_EMAIL = "three@gmail.com";

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
    @DisplayName("초기 초대 멤버 없이 팀 생성 요청은 201 Created 응답을 받고 응답 본문의 data 속성은 생성된 팀의 정보의 members가 비어 있는 배열로 구성")
    void When_CreateTeam_WithNoInitialMember_Expect_StatusCreated_BodyDataIsEmptyArray() throws Exception {
        String requestBody = "{\n" +
                "  \"leader_id\": " + leaderCrewUser.getId() + ", \n" +
                "  \"title\": \"" + TEAM_TITLE + "\", \n" +
                "  \"emails\": [], \n" +
                "  \"goal\": {\n" +
                "    \"distance\": " + TEAM_GOAL_DISTANCE + ", \n" +
                "    \"running_time\": " + TEAM_GOAL_TIME + "\n" +
                "  },\n" +
                "  \"start_time\" : \"" + TEAM_START_TIME + "\"" + "\n" +
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
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title", is(TEAM_TITLE)))
                .andExpect(jsonPath("$.data.members").isArray())
                .andExpect(jsonPath("$.data.members.length()", is(0)))
                .andExpect(jsonPath("$.data.goal.distance", is(TEAM_GOAL_DISTANCE)))
                .andExpect(jsonPath("$.data.goal.running_time", is(TEAM_GOAL_TIME)))
                .andExpect(jsonPath("$.data.goal.pace", is("00:05:00")))
                .andExpect(jsonPath("$.data.start_time").exists());
    }

    @Test
    @DisplayName("초기 초대 멤버를 지닌 팀 생성 요청은 201 Created 응답을 받고 응답 본문의 data 속성은 생성된 팀의 정보를 TeamCreationResponse에 TeamMemberCreationResponse와 함께 구성함")
    void When_CreateTeam_WithInitialMembers_Expect_StatusCreated_BodyDataIsMapWithTeamInformation() throws Exception {
        String requestBody = "{\n" +
                "  \"leader_id\": " + leaderCrewUser.getId() + ", \n" +
                "  \"title\": \"" + TEAM_TITLE + "\", \n" +
                "  \"emails\": [" +
                "  \"" + FIRST_MEMBER_EMAIL + "\", \"" + SECOND_MEMBER_EMAIL + "\"" +
                "  ], \n" +
                "  \"goal\": {\n" +
                "    \"distance\": " + TEAM_GOAL_DISTANCE + ", \n" +
                "    \"running_time\": " + TEAM_GOAL_TIME + "\n" +
                "  },\n" +
                "  \"start_time\" : \"" + TEAM_START_TIME + "\"" + "\n" +
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
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title", is(TEAM_TITLE)))
                .andExpect(jsonPath("$.data.members").isArray())
                .andExpect(jsonPath("$.data.members.length()", is(2)))
                // first member
                .andExpect(jsonPath("$.data.members[0]").isMap())
                .andExpect(jsonPath("$.data.members[0].id").exists())
                .andExpect(jsonPath("$.data.members[0].name", is("one")))
                .andExpect(jsonPath("$.data.members[0].email", is(FIRST_MEMBER_EMAIL)))
                .andExpect(jsonPath("$.data.members[0].grade", is(Grade.SILVER.getValue())))
                .andExpect(jsonPath("$.data.members[0].uri", is(matchesPattern(Pattern.compile(TEAM_MEMBER_RESOURCE_PATTERN)))))
                // second member
                .andExpect(jsonPath("$.data.members[" + TEAM_ID + "]").isMap())
                .andExpect(jsonPath("$.data.members[" + TEAM_ID + "].id").exists())
                .andExpect(jsonPath("$.data.members[" + TEAM_ID + "].name", is("two")))
                .andExpect(jsonPath("$.data.members[" + TEAM_ID + "].email", is(SECOND_MEMBER_EMAIL)))
                .andExpect(jsonPath("$.data.members[" + TEAM_ID + "].grade", is(Grade.UNRANKED.getValue())))
                .andExpect(jsonPath("$.data.members[" + TEAM_ID + "].uri", is(matchesPattern(Pattern.compile(TEAM_MEMBER_RESOURCE_PATTERN)))))
                .andExpect(jsonPath("$.data.goal.distance", is(TEAM_GOAL_DISTANCE)))
                .andExpect(jsonPath("$.data.goal.running_time", is(TEAM_GOAL_TIME)))
                .andExpect(jsonPath("$.data.goal.pace", is("00:05:00")))
                .andExpect(jsonPath("$.data.start_time").exists());
    }

    @Test
    @DisplayName("초기 초대 멤버의 이메일이 같은 크루 멤버의 것이 아니라면 404 Not Found 응답을 받음")
    void When_CreateTeam_WithNotSameCrewMemberEmail_Expect_StatusNotFound() throws Exception {
        String requestBody = "{\n" +
                "  \"leader_id\": " + leaderCrewUser.getId() + ", \n" +
                "  \"title\": \"" + TEAM_TITLE + "\", \n" +
                "  \"emails\": [" +
                "  \"" + FIRST_MEMBER_EMAIL + "\", \"sdfg@gmail.com\"" +
                "  ], \n" +
                "  \"goal\": {\n" +
                "    \"distance\": " + TEAM_GOAL_DISTANCE + ", \n" +
                "    \"running_time\": " + TEAM_GOAL_TIME + "\n" +
                "  },\n" +
                "  \"start_time\" : \"" + TEAM_START_TIME + "\"" + "\n" +
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

    @Test
    @DisplayName("같은 크루 멤버에게로의 초대 요청은 201 Created 응답을 받고 응답 본문의 data 속성은 생성된 멤버의 정보를 TeamMemberCreationResponse로 구성함")
    void When_InviteMember_WithSameCrew_Expect_StatusCreated_BodyDataIsMapWithTeamMemberInformation() throws Exception {
        String url = "/api/crew-running/teams/" + TEAM_ID + "/members";

        ResultActions result = mockMvc.perform(
                post(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(INVITED_MEMBER_EMAIL)
                        .header("Authorization", "Bearer " + leaderToken)
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/api/crew-running/teams/*/members/*"))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data.name", is("three")))
                .andExpect(jsonPath("$.data.email", is("three@gmail.com")))
                .andExpect(jsonPath("$.data.grade", is("UNRANKED")))
                .andExpect(jsonPath("$.data.uri", is(matchesPattern(Pattern.compile(TEAM_MEMBER_RESOURCE_PATTERN)))))
                .andExpect(jsonPath("$.error", nullValue()));
    }

    @Test
    @DisplayName("다른 크루 멤버에게로의 초대 요청은 404 Not Found 응답을 받음")
    void When_InviteMember_WithDifferentCrew_Expect_StatusNotFound() throws Exception {
        String url = "/api/crew-running/teams/" + TEAM_ID + "/members";

        ResultActions result = mockMvc.perform(
                post(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(DIFFERENT_CREW_EMAIL)
                        .header("Authorization", "Bearer " + leaderToken)
        );

        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").isString());
    }
}
