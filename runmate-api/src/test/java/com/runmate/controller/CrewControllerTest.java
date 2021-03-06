package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.TestActiveProfilesResolver;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.user.User;
import com.runmate.dto.AuthRequest;
import com.runmate.dto.crew.JoinRequestApproveDto;
import com.runmate.service.crew.CrewJoinRequestService;
import com.runmate.service.crew.CrewService;
import com.runmate.service.crew.CrewUserService;
import com.runmate.service.user.UserService;
import org.hamcrest.core.StringStartsWith;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(inheritProfiles = false, resolver = TestActiveProfilesResolver.class)
class CrewControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext ctx;
    @Autowired
    JwtProvider provider;

    @Autowired
    CrewService crewService;
    @Autowired
    CrewUserService crewUserService;
    @Autowired
    CrewJoinRequestService crewJoinRequestService;

    User noCrewUser;
    User withCrewUser;

    static final String WITH_CREW_USER_EMAIL = "you@you.com";
    static final String NO_CREW_USER_EMAIL = "min@gmail.com";

    @Autowired
    UserService userService;

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

        noCrewUser = userService.findByEmail(NO_CREW_USER_EMAIL);
        withCrewUser = userService.findByEmail(WITH_CREW_USER_EMAIL);

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
                .andExpect(status().isCreated())
                .andExpect(header().exists("Authorization"))
                .andReturn();
    }

    private String getToken(MvcResult result) {
        return Objects.requireNonNull(result.getResponse().getHeader("Authorization"))
                .replace("Bearer ", "");
    }

    @Test
    @DisplayName("????????? ????????? ?????? ????????? ???????????? ?????? ?????? ????????? ???????????? 201 Created ????????? ??????")
    void When_CreateCrew_WithNotBelongToAnyCrew_Expect_Status_Created_Body_success() throws Exception {
        //given
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

        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + noCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("success"))
                .andExpect(redirectedUrlPattern("**/api/crews/*"));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ????????? ?????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_CreateCrew_WithBelongToCrew_Expect_Status_Forbidden() throws Exception {
        //given
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

        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").isString());

    }

    @Test
    @DisplayName("????????? ???????????? ?????? ?????? ????????? ?????? ?????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_CreateCrew_SettingGradeLimit_WithUpperThanOwnGrade_Expect_Status_Forbidden() throws Exception {
        //given
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

        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ????????? 204 No Content ????????? ??????")
    void When_DeleteCrew_AdminUserRequest_Expect_Status_NoContent() throws Exception {
        //given
        Long ownCrewId = withCrewUser.getCrewUser().getCrew().getId();

        //when
        ResultActions result = mockMvc.perform(
                delete("/api/crews/" + ownCrewId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(WITH_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //result
        result.andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_DeleteCrew_NormalUserRequest_Expect_Status_Forbidden() throws Exception {
        //given
        CrewJoinRequest joinRequest = noCrewUser.getJoinRequests().stream()
                .filter(request -> withCrewUser.isRequestOfBelongingCrew(request))
                .findAny()
                .orElse(null);

        Long crewId = withCrewUser.getCrewUser().getCrew().getId();

        assertThat(joinRequest).isNotNull();
        crewJoinRequestService.approveJoinRequest(WITH_CREW_USER_EMAIL, crewId, joinRequest.getId());

        //when
        ResultActions result = mockMvc.perform(
                delete("/api/crews/" + crewId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(NO_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + noCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? ?????? ????????? 200 OK ????????? ??????, ????????? ???????????? ????????? ?????????")
    void When_CrewsWithLocation_InSeoulSi_Expect_Status_OK_Body_CrewsInSeoul() throws Exception {
        //given
        String requestBody = "{\n" +
                "  \"location\": {\n" +
                "      \"si\" : \"seoul\",\n" +
                "      \"gu\" : null,\n" +
                "      \"gun\" : null\n" +
                "    },\n" +
                "  \"sort_by\" : \"distance\",\n" +
                "  \"is_ascending\" : true\n" +
                "}";

        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews?pageNumber=1&limitCount=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(3)))
                //????????? ?????? ?????? ??????
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].name", is("????????????")))
                .andExpect(jsonPath("$.data[0].total_distance", is(84.39)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(29820)))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //?????????
                .andExpect(jsonPath("$.data[1].id", is(3)))
                .andExpect(jsonPath("$.data[1].name", is("????????????")))
                .andExpect(jsonPath("$.data[1].total_distance", is(213.03)))
                .andExpect(jsonPath("$.data[1].total_running_seconds", is(69792)))
                .andExpect(jsonPath("$.data[1].created_at").exists())
                //?????????
                .andExpect(jsonPath("$.data[2].id", is(2)))
                .andExpect(jsonPath("$.data[2].name", is("????????????")))
                .andExpect(jsonPath("$.data[2].total_distance", is(328.39)))
                .andExpect(jsonPath("$.data[2].total_running_seconds", is(116310)))
                .andExpect(jsonPath("$.data[2].created_at").exists());

    }

    @Test
    @DisplayName("????????? ?????? ????????? ?????? ?????? ?????? 2??? ?????? ?????? ????????? 200 OK ????????? ??????, ????????? ???????????? 2?????? ????????? ?????????")
    void When_CrewsWithLocation_WithLimitCount_2_Expect_Status_OK_Body_TwoResults() throws Exception {
        //given
        String requestBody = "{\n" +
                "  \"location\": {\n" +
                "      \"si\" : \"seoul\",\n" +
                "      \"gu\" : null,\n" +
                "      \"gun\" : null\n" +
                "    },\n" +
                "  \"sort_by\" : \"distance\",\n" +
                "  \"is_ascending\" : true\n" +
                "}";

        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews?pageNumber=1&limitCount=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(2)))
                //????????? ?????? ?????? ??????
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].name", is("????????????")))
                .andExpect(jsonPath("$.data[0].total_distance", is(84.39)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(29820)))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //?????????
                .andExpect(jsonPath("$.data[1].id", is(3)))
                .andExpect(jsonPath("$.data[1].name", is("????????????")))
                .andExpect(jsonPath("$.data[1].total_distance", is(213.03)))
                .andExpect(jsonPath("$.data[1].total_running_seconds", is(69792)))
                .andExpect(jsonPath("$.data[1].created_at").exists());
    }

    @Test
    @DisplayName("????????? ????????? ???????????? ?????? ?????? ????????? 200 OK ????????? ??????, ????????? ???????????? ???????????? ????????? ?????????")
    void When_CrewsWithLocation_InSeoulSiGangNamGu_Expect_Status_OK_Body_CrewsInSeoulAndGangNam() throws Exception {
        //given
        String requestBody = "{\n" +
                "  \"location\": {\n" +
                "      \"si\" : \"seoul\",\n" +
                "      \"gu\" : \"gangnam\",\n" +
                "      \"gun\" : null\n" +
                "    },\n" +
                "  \"sort_by\" : \"distance\",\n" +
                "  \"is_ascending\" : true\n" +
                "}";

        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews?pageNumber=1&limitCount=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("????????????")))
                .andExpect(jsonPath("$.data[0].total_distance", is(328.39)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(116310)))
                .andExpect(jsonPath("$.data[0].created_at").exists());
    }

    @Test
    @DisplayName("?????? ?????? ???????????? ???????????? ?????? ?????? ????????? 200 OK ????????? ??????, total_running_seconds??? ?????????????????? ???????????? ?????????")
    void When_CrewsWithLocation_WithSortingCondition_Expect_Status_OK_Body_ProperlySortingCrews() throws Exception {
        //given
        String requestBody = "{\n" +
                "  \"location\": {\n" +
                "      \"si\" : \"seoul\",\n" +
                "      \"gu\" : null,\n" +
                "      \"gun\" : null\n" +
                "    },\n" +
                "  \"sort_by\" : \"running_time\",\n" +
                "  \"is_ascending\" : false\n" +
                "}";

        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews?pageNumber=1&limitCount=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(3)))
                //?????????
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("????????????")))
                .andExpect(jsonPath("$.data[0].total_distance", is(328.39)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(116310)))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //?????????
                .andExpect(jsonPath("$.data[1].id", is(3)))
                .andExpect(jsonPath("$.data[1].name", is("????????????")))
                .andExpect(jsonPath("$.data[1].total_distance", is(213.03)))
                .andExpect(jsonPath("$.data[1].total_running_seconds", is(69792)))
                .andExpect(jsonPath("$.data[1].created_at").exists())
                //?????????
                .andExpect(jsonPath("$.data[2].id", is(1)))
                .andExpect(jsonPath("$.data[2].name", is("????????????")))
                .andExpect(jsonPath("$.data[2].total_distance", is(84.39)))
                .andExpect(jsonPath("$.data[2].total_running_seconds", is(29820)))
                .andExpect(jsonPath("$.data[2].created_at").exists());
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ?????? ?????? ???????????? ?????? ?????? ????????? 200 OK ????????? ??????, ?????? ????????? 0?????? ?????????")
    void When_CrewsWithLocation_WithNoResultsLocationCondition_Expect_Status_OK_Body_ResultSizeIsZero() throws Exception {
        //given
        String requestBody = "{\n" +
                "  \"location\": {\n" +
                "      \"si\" : \"incheon\",\n" +
                "      \"gu\" : null,\n" +
                "      \"gun\" : null\n" +
                "    },\n" +
                "  \"sort_by\" : \"running_time\",\n" +
                "  \"is_ascending\" : false\n" +
                "}";

        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews?pageNumber=1&limitCount=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(0)));
    }

    @Test
    @DisplayName("?????? ????????? ????????? ????????? 200 OK ????????? ??????, ????????? ?????? ????????? ?????? ???????????? ????????? [?????? ??????: running_time DESC]")
    void When_findAllCrewMembers_WithRunningTimeDESC_Expect_Status_OK_Body_SortingByRunningTime() throws Exception {
        //given
        String requestBody = "{\n" +
                "  \"sort_by\" : \"running_time\",\n" +
                "  \"is_ascending\" : false\n" +
                "}";

        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews/3/members?pageNumber=1&limitCount=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(4)))
                //????????? ?????? ??????
                .andExpect(jsonPath("$.data[0].id", is(4)))
                .andExpect(jsonPath("$.data[0].username", is("one")))
                .andExpect(jsonPath("$.data[0].role", is("NORMAL")))
                .andExpect(jsonPath("$.data[0].total_distance", is(194.33)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(64415)))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //????????? ?????? ??????
                .andExpect(jsonPath("$.data[1].id", is(3)))
                .andExpect(jsonPath("$.data[1].username", is("you")))
                .andExpect(jsonPath("$.data[1].role", is("ADMIN")))
                .andExpect(jsonPath("$.data[1].total_distance", is(18.7)))
                .andExpect(jsonPath("$.data[1].total_running_seconds", is(5377)))
                .andExpect(jsonPath("$.data[1].created_at").exists())
                //????????? ?????? ??????
                .andExpect(jsonPath("$.data[2].id", is(5)))
                .andExpect(jsonPath("$.data[2].username", is("two")))
                .andExpect(jsonPath("$.data[2].role", is("NORMAL")))
                .andExpect(jsonPath("$.data[2].total_distance", is(0.0)))
                .andExpect(jsonPath("$.data[2].total_running_seconds", is(0)))
                .andExpect(jsonPath("$.data[2].created_at").exists())
                //????????? ?????? ??????
                .andExpect(jsonPath("$.data[3].id", is(6)))
                .andExpect(jsonPath("$.data[3].username", is("three")))
                .andExpect(jsonPath("$.data[3].role", is("NORMAL")))
                .andExpect(jsonPath("$.data[3].total_distance", is(0.0)))
                .andExpect(jsonPath("$.data[3].total_running_seconds", is(0)))
                .andExpect(jsonPath("$.data[3].created_at").exists());
    }

    @Test
    @DisplayName("????????? ???????????? ?????? ?????? ?????? ?????? ????????? 200 OK ????????? ??????, ??? ????????? ???????????? ?????? ?????? ????????? ??????????????? ??????????????? ?????????")
    void When_findAllJoinRequests_WithAdminEmail_Expect_Status_OK_Body_RequestsInRecentOrder() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews/3/requests?pageNumber=1&limitCount=5")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(WITH_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", is(2)))
                //?????????
                .andExpect(jsonPath("$.data[0].id", is(4)))
                .andExpect(jsonPath("$.data[0].email", is("jan@naver.com")))
                .andExpect(jsonPath("$.data[0].location.si", is("seoul")))
                .andExpect(jsonPath("$.data[0].location.gu", is("gangnam")))
                .andExpect(jsonPath("$.data[0].location.gun", nullValue()))
                .andExpect(jsonPath("$.data[0].grade", is("BRONZE")))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //?????????
                .andExpect(jsonPath("$.data[1].id", is(1)))
                .andExpect(jsonPath("$.data[1].email", is("min@gmail.com")))
                .andExpect(jsonPath("$.data[1].location.si", is("seoul")))
                .andExpect(jsonPath("$.data[1].location.gu", is("nowon")))
                .andExpect(jsonPath("$.data[1].location.gun", nullValue()))
                .andExpect(jsonPath("$.data[1].grade", is("BRONZE")))
                .andExpect(jsonPath("$.data[1].created_at").exists());
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? ?????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_findAllJoinRequests_WithNormalEmail_Expect_Status_Forbidden() throws Exception {
        //given
        String notAdminEmail = "one@gmail.com";
        String notAdminPassword = "1234";
        String loginRequestBody = mapper.writeValueAsString(new AuthRequest(notAdminEmail, notAdminPassword));
        MvcResult authorization = getAuthorization(loginRequestBody);
        String token = getToken(authorization);

        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews/3/requests?pageNumber=1&limitCount=5")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(notAdminEmail)
                        .header("Authorization", "Bearer " + token)
        );

        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", is(StringStartsWith.startsWith("you can't browse for"))));
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ???????????? ?????? ?????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_findAllJoinRequests_WithNotCrewUserEmail_Expect_Status_Forbidden() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews/3/requests?pageNumber=1&limitCount=5")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(NO_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + noCrewToken)
        );

        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", is(StringStartsWith.startsWith("you can't browse for"))));
    }

    @Test
    @DisplayName("???????????? ?????? ?????? id??? ?????? ?????? ?????? ????????? 404 Not Found ????????? ??????")
    void When_findAllJoinRequests_WithNotExistsCrewId_Expect_Status_NotFound() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                get("/api/crews/3000/requests?pageNumber=1&limitCount=5")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(WITH_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", is("cannot found for such crew")));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? 201 Created ????????? ?????? ?????? ???????????? Redirected URL??? ??????")
    void When_sendJoinRequest_ForExistingCrew_Expect_Status_Created() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews/1/requests")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(NO_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + noCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("success"))
                .andExpect(redirectedUrlPattern("**/api/crews/1/requests/*"));
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ????????? ?????? ????????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_sendJoinRequest_ToCrewHasGradeLimitUpperThanOwnGrade_Status_Forbidden() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews/2/requests")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(NO_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + noCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", is("User's Grade lower than Crew's Grade Limit")));
    }

    @Test
    @DisplayName("?????? ?????? ????????? ?????? ????????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_sendJoinRequest_ToDuplicateCrew_Status_Forbidden() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews/3/requests")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(NO_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + noCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", startsWith("You have already sent a join request:")));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ???????????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_sendJoinRequest_UserBelongingToOtherCrew_Status_Forbidden() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews/1/requests")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(WITH_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", startsWith("you have already belong to crew:")));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? 201 Created ????????? ?????? ?????? ?????? ?????? ???????????? Redirected URL??? ??????")
    void When_approveJoinRequest_ByAdminEmail_Status_Created_WithRedirectedURL() throws Exception {
        //given
        String jsonBody = mapper.writeValueAsString(new JoinRequestApproveDto(WITH_CREW_USER_EMAIL, 1L));

        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews/3/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("success"))
                .andExpect(redirectedUrlPattern("**/api/crews/3/members/*"));
    }

    @Test
    @DisplayName("???????????? ?????? ???????????? ?????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_approveJoinRequest_ByNormalUser_Status_Forbidden() throws Exception {
        //given
        String jsonBody = mapper.writeValueAsString(new JoinRequestApproveDto("one@gmail.com", 1L));

        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews/3/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", is("you can't handle join requests for this crew")));
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ?????? ????????? ?????? ????????? 404 Not Found ????????? ??????")
    void When_approveJoinRequest_ForUnmatchedCrew_Then_Status_NotFound() throws Exception {
        //given
        String jsonBody = mapper.writeValueAsString(new JoinRequestApproveDto(WITH_CREW_USER_EMAIL, 1L));

        //when
        ResultActions result = mockMvc.perform(
                post("/api/crews/1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", is("request is not matched to crew")));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ????????? 204 No Content ????????? ??????")
    void When_cancelJoinRequest_ByAdminEmail_Then_Status_NoContent() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                delete("/api/crews/3/requests/1")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(WITH_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("???????????? ?????? ???????????? ?????? ?????? ????????? 403 Forbidden ????????? ??????")
    void When_cancelJoinRequest_ByNormalUser_Then_Status_Forbidden() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                delete("/api/crews/3/requests/1")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("one@gmail.com")
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", is("you can't handle join requests for this crew")));
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ?????? ????????? ?????? ????????? 404 Not Found ????????? ??????")
    void When_cancelJoinRequest_ForUnmatchedCrew_Then_Status_NotFound() throws Exception {
        //when
        ResultActions result = mockMvc.perform(
                delete("/api/crews/1/requests/1")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(WITH_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", is("request is not matched to crew")));
    }

    @Test
    @DisplayName("???????????? ?????? ???????????? ?????? ?????? ????????? 204 No Content ????????? ??????")
    void When_deleteCrewUser_ByAdminEmail_Then_Status_NoContent() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/crews/3/members/4")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(WITH_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + withCrewToken)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("?????? ???????????? ????????? ?????? ????????? ?????? ?????? ?????? ????????? 204 No Content ????????? ??????")
    void When_deleteCrewUser_ByOwnEmailForBelongingCrew_Then_Status_NoContent() throws Exception {
        //given
        String notAdminEmail = "one@gmail.com";
        String notAdminPassword = "1234";
        String loginRequestBody = mapper.writeValueAsString(new AuthRequest(notAdminEmail, notAdminPassword));
        MvcResult authorization = getAuthorization(loginRequestBody);
        String token = getToken(authorization);

        //when
        ResultActions result = mockMvc.perform(
                delete("/api/crews/3/members/4")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(notAdminEmail)
                        .header("Authorization", "Bearer " + token)
        );

        //then
        result.andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("?????? ???????????? ????????? ?????? ???????????? ?????? ?????? ????????? ?????? 403 Forbidden ????????? ??????")
    void When_deleteCrewUser_ByOtherUserEmail_Then_Status_Forbidden() throws Exception {
        //given
        String notAdminEmail = "one@gmail.com";
        String notAdminPassword = "1234";
        String loginRequestBody = mapper.writeValueAsString(new AuthRequest(notAdminEmail, notAdminPassword));
        MvcResult authorization = getAuthorization(loginRequestBody);
        String token = getToken(authorization);

        //when
        ResultActions result = mockMvc.perform(
                delete("/api/crews/3/members/5")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(notAdminEmail)
                        .header("Authorization", "Bearer " + token)
        );

        //then
        result.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.data", nullValue()))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error", startsWith("not authorized to delete ")));
    }
}