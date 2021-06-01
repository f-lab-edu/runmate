package com.runmate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.user.User;
import com.runmate.dto.AuthRequest;
import com.runmate.service.crew.CrewJoinRequestService;
import com.runmate.service.crew.CrewService;
import com.runmate.service.crew.CrewUserService;
import com.runmate.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

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
        mockMvc.perform(
                post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + noCrewToken)
        )
                //then
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("success"));
    }

    @Test
    @DisplayName("크루에 이미 속한 회원의 크루 생성 요청은 403 Forbidden 응답을 받음")
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
        mockMvc.perform(
                post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        )
                //then
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("자신의 등급보다 높은 등급 제한을 가진 크루 생성 요청은 403 Forbidden 응답을 받음")
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
        mockMvc.perform(
                post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + withCrewToken)
        )
                //then
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("크루장의 크루 삭제 요청은 204 No Content 응답을 받음")
    void When_DeleteCrew_AdminUserRequest_Expect_Status_NoContent() throws Exception {
        //given
        Long ownCrewId = withCrewUser.getCrewUser().getCrew().getId();

        //when
        mockMvc.perform(
                delete("/api/crews/" + ownCrewId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(WITH_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + withCrewToken)
        )
                //then
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("일반 크루원의 크루 삭제 요청은 403 Forbidden 응답을 받음")
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
        mockMvc.perform(
                delete("/api/crews/" + crewId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(NO_CREW_USER_EMAIL)
                        .header("Authorization", "Bearer " + noCrewToken)
        )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("서울시 검색 조건으로 크루 목록 조회시 200 OK 응답을 받고, 서울에 존재하는 크루만 출력됨")
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
                //첫번째 크루 조회 결과
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].name", is("강북크루")))
                .andExpect(jsonPath("$.data[0].total_distance", is(84.39)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(29820)))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //두번째
                .andExpect(jsonPath("$.data[1].id", is(3)))
                .andExpect(jsonPath("$.data[1].name", is("노원크루")))
                .andExpect(jsonPath("$.data[1].total_distance", is(213.03)))
                .andExpect(jsonPath("$.data[1].total_running_seconds", is(69792)))
                .andExpect(jsonPath("$.data[1].created_at").exists())
                //세번째
                .andExpect(jsonPath("$.data[2].id", is(2)))
                .andExpect(jsonPath("$.data[2].name", is("강남크루")))
                .andExpect(jsonPath("$.data[2].total_distance", is(328.39)))
                .andExpect(jsonPath("$.data[2].total_running_seconds", is(116310)))
                .andExpect(jsonPath("$.data[2].created_at").exists());

    }

    @Test
    @DisplayName("서울시 검색 조건과 결과 크기 제한 2로 크루 목록 조회시 200 OK 응답을 받고, 서울에 존재하는 2개의 크루만 출력됨")
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
                //첫번째 크루 조회 결과
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].name", is("강북크루")))
                .andExpect(jsonPath("$.data[0].total_distance", is(84.39)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(29820)))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //두번째
                .andExpect(jsonPath("$.data[1].id", is(3)))
                .andExpect(jsonPath("$.data[1].name", is("노원크루")))
                .andExpect(jsonPath("$.data[1].total_distance", is(213.03)))
                .andExpect(jsonPath("$.data[1].total_running_seconds", is(69792)))
                .andExpect(jsonPath("$.data[1].created_at").exists());
    }

    @Test
    @DisplayName("서울시 강남구 조건으로 크루 목록 조회시 200 OK 응답을 받고, 서울시 강남구에 존재하는 크루만 조회됨")
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
                .andExpect(jsonPath("$.data[0].name", is("강남크루")))
                .andExpect(jsonPath("$.data[0].total_distance", is(328.39)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(116310)))
                .andExpect(jsonPath("$.data[0].created_at").exists());
    }

    @Test
    @DisplayName("달린 시간 내림차순 조건으로 크루 목록 조회시 200 OK 응답을 받고, total_running_seconds의 내림차순으로 정렬되어 조회됨")
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
                //첫번째
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("강남크루")))
                .andExpect(jsonPath("$.data[0].total_distance", is(328.39)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(116310)))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //두번째
                .andExpect(jsonPath("$.data[1].id", is(3)))
                .andExpect(jsonPath("$.data[1].name", is("노원크루")))
                .andExpect(jsonPath("$.data[1].total_distance", is(213.03)))
                .andExpect(jsonPath("$.data[1].total_running_seconds", is(69792)))
                .andExpect(jsonPath("$.data[1].created_at").exists())
                //세번째
                .andExpect(jsonPath("$.data[2].id", is(1)))
                .andExpect(jsonPath("$.data[2].name", is("강북크루")))
                .andExpect(jsonPath("$.data[2].total_distance", is(84.39)))
                .andExpect(jsonPath("$.data[2].total_running_seconds", is(29820)))
                .andExpect(jsonPath("$.data[2].created_at").exists());
    }

    @Test
    @DisplayName("어느 크루에도 해당되지 않는 지역 조건으로 크루 목록 조회시 200 OK 응답을 받고, 결과 크기가 0으로 조회됨")
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
    @DisplayName("지정 크루의 크루원 조회시 200 OK 응답을 받고, 설정한 정렬 조건에 따라 정렬되어 조회됨 [정렬 조건: running_time DESC]")
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
                .andExpect(jsonPath("$.data.length()", is(3)))
                //첫번째 멤버 출력
                .andExpect(jsonPath("$.data[0].id", is(4)))
                .andExpect(jsonPath("$.data[0].username", is("one")))
                .andExpect(jsonPath("$.data[0].role", is("NORMAL")))
                .andExpect(jsonPath("$.data[0].total_distance", is(194.33)))
                .andExpect(jsonPath("$.data[0].total_running_seconds", is(64415)))
                .andExpect(jsonPath("$.data[0].created_at").exists())
                //두번째 멤버 출력
                .andExpect(jsonPath("$.data[1].id", is(3)))
                .andExpect(jsonPath("$.data[1].username", is("you")))
                .andExpect(jsonPath("$.data[1].role", is("ADMIN")))
                .andExpect(jsonPath("$.data[1].total_distance", is(18.7)))
                .andExpect(jsonPath("$.data[1].total_running_seconds", is(5377)))
                .andExpect(jsonPath("$.data[1].created_at").exists())
                //세번째 멤버 출력
                .andExpect(jsonPath("$.data[2].id", is(5)))
                .andExpect(jsonPath("$.data[2].username", is("two")))
                .andExpect(jsonPath("$.data[2].role", is("NORMAL")))
                .andExpect(jsonPath("$.data[2].total_distance", is(0.0)))
                .andExpect(jsonPath("$.data[2].total_running_seconds", is(0)))
                .andExpect(jsonPath("$.data[2].created_at").exists());
    }


}